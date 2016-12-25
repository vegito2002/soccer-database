//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//


package com.soccerDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bootstrap {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 8080;

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Exception {
        //Check if the database file exists in the current directory. Abort if not
        DataSource soccerSource = configureDataSource("database.sqlite");
        if (soccerSource == null) {
            System.out.printf("Could not find database file in the current directory (%s). Terminating\n",
                    Paths.get(".").toAbsolutePath().normalize());
            System.exit(1);
        }

        //Specify the IP address and Port at which the server should be run
        ipAddress(IP_ADDRESS);
        port(PORT);

        //Specify the sub-directory from which to serve static resources (like html and css)
//        staticFileLocation("/public");

        //Create the model instance and then configure and start the web service
        try {
            SoccerService model = new SoccerService(soccerSource);
            new SoccerController(model);
        } catch (SoccerService.SoccerServiceException ex) {
            logger.error("Failed to create a SoccerService instance. Aborting");
        }
    }

    /**
     * Check if the database file exists in the current directory. If it does
     * create a DataSource instance for the file and return it.
     * @return javax.sql.DataSource corresponding to the todo database
     */
    private static DataSource configureDataSource(String fileName) {
        Path soccerPath = Paths.get(".", fileName);
        if ( !(Files.exists(soccerPath) )) {
            try { Files.createFile(soccerPath); }
            catch (java.io.IOException ex) {
                logger.error("Failed to create database.sqlite file in current directory. Aborting");
            }
        }

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + fileName);
        return dataSource;

    }
}
