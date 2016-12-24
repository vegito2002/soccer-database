package com.soccerDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sqlite.SQLiteDataSource;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

import org.junit.*;
import static org.junit.Assert.*;

public class TestTodoServer {

    //------------------------------------------------------------------------//
    // Setup
    //------------------------------------------------------------------------//

    @Before
    public void setup() throws Exception {
        //Clear the database and then start the server
        clearDB();

        //Start the main server
        Bootstrap.main(null);
        Spark.awaitInitialization();
    }

    @After
    public void tearDown() {
        //Stop the server
        clearDB();
        Spark.stop();
    }

    //------------------------------------------------------------------------//
    // Tests
    //------------------------------------------------------------------------//

    @Test
    public void testAdd() throws Exception {
        
        //Add a few elements
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SoccerData[] entries = new SoccerData[] {
          new SoccerData(null, "Test-1", false, df.parse("2015-04-23T23:10:15-0700")),
          new SoccerData(null, "Test-2", true, df.parse("2015-03-07T01:10:20-0530")),
          new SoccerData(null, "Test-3", false, df.parse("2010-02-19T13:25:43-0530"))
        };
        
        for (SoccerData t : entries) {
            Response radd = request("POST", "/api/v1/todos", t);
            assertEquals("Failed to add", 201, radd.httpStatus);
        }

        //Get them back
        Response r = request("GET", "/api/v1/todos", null);
        assertEquals("Failed to get todos", 200, r.httpStatus);
        List<SoccerData> results = getTodos(r);
        
        //Verify that we got the right element back
        assertEquals("Number of todo entries differ", entries.length, results.size());
        
        for (int i = 0; i < results.size(); i++) {
            SoccerData actual = results.get(i);
            assertEquals(String.format("Index %d: Mismatch in title", i), entries[i].getTitle(), actual.getTitle());
            assertEquals(String.format("Index %d: Mismatch in creation date", i), entries[i].getCreatedOn(), actual.getCreatedOn());
            assertEquals(String.format("Index %d: Mismatch in done state", i), entries[i].isDone(), actual.isDone());
        }
    }
    
    @Test
    public void testUpdate() throws Exception {
        
        //Add a single element
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SoccerData expected = new SoccerData(null, "Test-1", false, df.parse("2015-04-23T23:10:15-0700"));
        Response r1 = request("POST", "/api/v1/todos", expected);
        assertEquals("Failed to add", 201, r1.httpStatus);

        //Get it back so that we know its ID
        Response r2 = request("GET", "/api/v1/todos", null);
        assertEquals("Failed to get todos", 200, r2.httpStatus);
        SoccerData t = getTodos(r2).get(0);
        
        //Send out an update with a changed title and state
        SoccerData updated = new SoccerData(t.getId(), t.getTitle(), !t.isDone(), t.getCreatedOn());
        Response r3 = request("PUT", "/api/v1/todos/" + t.getId(), updated);
        assertEquals("Failed to update", 200, r3.httpStatus);
        
        //Get stuff back again
        Response r4 = request("GET", "/api/v1/todos", null);
        assertEquals("Failed to get todos", 200, r4.httpStatus);
        List<SoccerData> results = getTodos(r4);
        
        //Verify that we got the right element back
        assertEquals(1, results.size());
        
        SoccerData actual = results.get(0);
        assertEquals("Mismatch in Id", updated.getId(), actual.getId());
        assertEquals("Mismatch in title", updated.getTitle(), actual.getTitle());
        assertEquals("Mismatch in creation date", updated.getCreatedOn(), actual.getCreatedOn());
        assertEquals("Mismatch in done state", updated.isDone(), actual.isDone());
    }
    
    @Test
    public void testDelete() throws Exception {
        
        //Add a few elements
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SoccerData[] entries = new SoccerData[] {
          new SoccerData(null, "Test-1", false, df.parse("2015-04-23T23:10:15-0700")),
          new SoccerData(null, "Test-2", true, df.parse("2015-03-07T01:10:20-0530")),
          new SoccerData(null, "Test-3", false, df.parse("2010-02-19T13:25:43-0530"))
        };
        
        for (SoccerData t : entries) {
            Response radd = request("POST", "/api/v1/todos", t);
            assertEquals("Failed to add", 201, radd.httpStatus);
        }

        //Get them back so that we know our ids
        Response r1 = request("GET", "/api/v1/todos", null);
        assertEquals("Failed to get todos", 200, r1.httpStatus);
        List<SoccerData> data = getTodos(r1);
        
        //Delete an entry
        int indexToDelete = 1;
        Response r2 = request("DELETE", "/api/v1/todos/" + data.get(indexToDelete).getId(), null);
        assertEquals("Failed to delete todo", 200, r2.httpStatus);
        
        //Get it back again
        Response r3 = request("GET", "/api/v1/todos", null);
        assertEquals("Failed to get todos", 200, r3.httpStatus);
        List<SoccerData> results = getTodos(r3);
        
        //Verify that we got the right element back
        assertEquals("Number of todo entries differ", entries.length - 1, results.size());
        
        //Make a new list of expected Todos with some Java 8 functional foo :)
        List<SoccerData> expected = IntStream.range(0, entries.length)
            .filter(i -> i != indexToDelete)
            .mapToObj(i -> entries[i])
            .collect(Collectors.toList());

        //And check
        for (int i = 0; i < results.size(); i++) {
            SoccerData actual = results.get(i);
            assertEquals(String.format("Index %d: Mismatch in title", i), expected.get(i).getTitle(), actual.getTitle());
            assertEquals(String.format("Index %d: Mismatch in creation date", i), expected.get(i).getCreatedOn(), actual.getCreatedOn());
            assertEquals(String.format("Index %d: Mismatch in done state", i), expected.get(i).isDone(), actual.isDone());
        }
    }
 
    //------------------------------------------------------------------------//
    // Generic Helper Methods and classes
    //------------------------------------------------------------------------//
    
    private Response request(String method, String path, Object content) {
        try {
			URL url = new URL("http", Bootstrap.IP_ADDRESS, Bootstrap.PORT, path);
            System.out.println(url);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoInput(true);
            if (content != null) {
                String contentAsJson = new Gson().toJson(content);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter output = new OutputStreamWriter(http.getOutputStream());
                output.write(contentAsJson);
                output.flush();
                output.close();
            }

            String responseBody = IOUtils.toString(http.getInputStream());
			return new Response(http.getResponseCode(), responseBody);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}
    }

        
    private static class Response {

		public String content;
        
		public int httpStatus;

		public Response(int httpStatus, String content) {
			this.content = content;
            this.httpStatus = httpStatus;
		}

        public <T> T getContentAsObject(Type type) {
            return new Gson().fromJson(content, type);
        }
	}

    //------------------------------------------------------------------------//
    // TodoApp Specific Helper Methods and classes
    //------------------------------------------------------------------------//

    private void clearDB() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:todo.db");

        Sql2o db = new Sql2o(dataSource);

        try (Connection conn = db.open()) {
            String sql = "DROP TABLE IF EXISTS item" ;
            conn.createQuery(sql).executeUpdate();
        }
    }

    private List<SoccerData> getTodos(Response r) {
        //Getting a useful Type instance for a *generic* container is tricky given Java's type erasure.
        //The technique below is documented in the documentation of com.google.gson.reflect.TypeToken.
        Type type = (new TypeToken<ArrayList<SoccerData>>() { }). getType();
        return r.getContentAsObject(type);
    }

}