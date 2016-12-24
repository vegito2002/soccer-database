//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.soccerDatabase;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class SoccerService {

    private Sql2o db;

    private final Logger logger = LoggerFactory.getLogger(SoccerService.class);

    private final String SQL_CREATETABLE_MATCHRECORDS ="CREATE TABLE IF NOT EXISTS MatchRecords ( "
            + " dateString TEXT,  "
            + " homeTeam TEXT,  "
            + " awayTeam TEXT,  "
            + " fullTimeResult TEXT,  "
            + " halfTimeResult TEXT,  "
            + " referee TEXT, "
            + " dateNumber INTEGER,  "
            + " fthg INTEGER,  "
            + " ftag INTEGER,  "
            + " hthg INTEGER,  "
            + " htag INTEGER, "
            + " homeShot INTEGER,  "
            + " awayShot INTEGER,  "
            + " hst INTEGER,  "
            + " ast INTEGER,  "
            + " hf INTEGER,  "
            + " af INTEGER, "
            + " hc INTEGER,  "
            + " ac INTEGER,  "
            + " hy INTEGER,  "
            + " ay INTEGER,  "
            + " hr INTEGER,  "
            + " ar INTEGER, "
            + " PRIMARY KEY(dateNumber, homeTeam, awayTeam)); ";
    private final String SQL_CREATETABLE_UPDATELOG = " CREATE TABLE IF NOT EXISTS updatelog "
            + " (sourcename TEXT NOT NULL, "
            + " updatecount INTEGER NOT NULL, "
            + " PRIMARY KEY(sourcename)); ";
    private final String SQL_CREATETABLE_ENGLANDTEAM = "CREATE TABLE IF NOT EXISTS EnglandTeam "
            + " (id INTEGER NOT NULL, "
            + " teamName TEXT, "
            + " teamCode TEXT, "
            + " city TEXT, "
            + " PRIMARY KEY(id));";
    private final String SQL_CREATETABLE_ENGLAND_TEAM_ATTRIBUTES = " CREATE TABLE IF NOT EXISTS EnglandTeamAttributes "
            + " (id INTEGER NOT NULL,  "
            + " buildUpPlaySpeedClass TEXT, "
            + " buildUpPlayDribblingClass TEXT, "
            + " buildUpPlayPassingClass TEXT, "
            + " buildUpPlayPositioningClass TEXT, "
            + " chanceCreationPassingClass TEXT, "
            + " chanceCreationCrossingClass TEXT, "
            + " chanceCreationShootingClass TEXT, "
            + " chanceCreationPositioningClass TEXT, "
            + " defencePressureClass TEXT, "
            + " defenceAggressionClass TEXT, "
            + " defenceTeamWidthClass TEXT, "
            + " defenceDefenderLineClass TEXT, "
            + " PRIMARY KEY(id)); ";

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    public SoccerService(DataSource dataSource) throws SoccerServiceException {
        db = new Sql2o(dataSource);

        //        try (Connection conn = db.open()) {
        //            String sql = "CREATE TABLE IF NOT "
        //        }

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            conn.createQuery(SQL_CREATETABLE_MATCHRECORDS).executeUpdate();
            conn.createQuery(SQL_CREATETABLE_UPDATELOG).executeUpdate();
            conn.createQuery(SQL_CREATETABLE_ENGLANDTEAM).executeUpdate();
            conn.createQuery(SQL_CREATETABLE_ENGLAND_TEAM_ATTRIBUTES).executeUpdate();
            conn.createQuery(" DROP TABLE IF EXISTS Match; ").executeUpdate();
            conn.createQuery(" VACUUM; ").executeUpdate();

        } catch (Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new SoccerServiceException("Failed to create schema at startup", ex);
        }
    }

    /**
     * Fetch all todo entries in the list
     *
     * @return List of all SoccerData entries
     */
    public List<SoccerData> findAll() throws SoccerServiceException {
        String sql = "SELECT * FROM item";
        try (Connection conn = db.open()) {
            List<SoccerData> soccerDatas = conn.createQuery(sql)
                    .addColumnMapping("item_id", "id")
                    .addColumnMapping("created_on", "createdOn")
                    .executeAndFetch(SoccerData.class);
            return soccerDatas;
        } catch (Sql2oException ex) {
            logger.error("SoccerService.findAll: Failed to query database", ex);
            throw new SoccerServiceException("SoccerService.findAll: Failed to query database", ex);
        }
    }

    /**
     * Create a new SoccerData entry.
     */
    public void createNewTodo(String body) throws SoccerServiceException {
        SoccerData soccerData = new Gson().fromJson(body, SoccerData.class);

        String sql = "INSERT INTO item (title, done, created_on) " +
                "             VALUES (:title, :done, :createdOn)";

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .bind(soccerData)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("SoccerService.createNewTodo: Failed to create new entry", ex);
            throw new SoccerServiceException("SoccerService.createNewTodo: Failed to create new entry", ex);
        }
    }

    /**
     * Find a todo entry given an Id.
     *
     * @param id The id for the SoccerData entry
     * @return The SoccerData corresponding to the id if one is found, otherwise null
     */
    public SoccerData find(String id) throws SoccerServiceException {
        String sql = "SELECT * FROM item WHERE item_id = :itemId ";

        try (Connection conn = db.open()) {
            return conn.createQuery(sql)
                    .addParameter("itemId", Integer.parseInt(id))
                    .addColumnMapping("item_id", "id")
                    .addColumnMapping("created_on", "createdOn")
                    .executeAndFetchFirst(SoccerData.class);
        } catch (Sql2oException ex) {
            logger.error(String.format("SoccerService.find: Failed to query database for id: %s", id), ex);
            throw new SoccerServiceException(String.format("SoccerService.find: Failed to query database for id: %s", id), ex);
        }
    }

    /**
     * Update the specified SoccerData entry with new information
     */
    public SoccerData update(String todoId, String body) throws SoccerServiceException {
        SoccerData soccerData = new Gson().fromJson(body, SoccerData.class);

        String sql = "UPDATE item SET title = :title, done = :done, created_on = :createdOn WHERE item_id = :itemId ";
        try (Connection conn = db.open()) {
            //Update the item
            conn.createQuery(sql)
                    .bind(soccerData)  // one-liner to map all SoccerData object fields to query parameters :title etc
                    .addParameter("itemId", Integer.parseInt(todoId))
                    .executeUpdate();

            //Verify that we did indeed update something
            if (getChangedRows(conn) != 1) {
                logger.error(String.format("SoccerService.update: Update operation did not update rows. Incorrect id(?): %s", todoId));
                throw new SoccerServiceException(String.format("SoccerService.update: Update operation did not update rows. Incorrect id (?): %s", todoId), null);
            }
        } catch (Sql2oException ex) {
            logger.error(String.format("SoccerService.update: Failed to update database for id: %s", todoId), ex);
            throw new SoccerServiceException(String.format("SoccerService.update: Failed to update database for id: %s", todoId), ex);
        }

        return find(todoId);
    }

    /**
     * Delete the entry with the specified id
     */
    public void delete(String todoId) throws SoccerServiceException {
        String sql = "DELETE FROM item WHERE item_id = :itemId";
        try (Connection conn = db.open()) {
            //Delete the item
            conn.createQuery(sql)
                    .addParameter("itemId", Integer.parseInt(todoId))
                    .executeUpdate();

            //Verify that we did indeed change something
            if (getChangedRows(conn) != 1) {
                logger.error(String.format("SoccerService.delete: Delete operation did not delete rows. Incorrect id(?): %s", todoId));
                throw new SoccerServiceException(String.format("SoccerService.delete: Delete operation did not delete rows. Incorrect id(?): %s", todoId), null);
            }
        } catch (Sql2oException ex) {
            logger.error(String.format("SoccerService.update: Failed to delete id: %s", todoId), ex);
            throw new SoccerServiceException(String.format("SoccerService.update: Failed to delete id: %s", todoId), ex);
        }
    }


    public static class SoccerServiceException extends Exception {
        public SoccerServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * This Sqlite specific method returns the number of rows changed by the most recent
     * INSERT, UPDATE, DELETE operation. Note that you MUST use the same connection to get
     * this information
     */
    private int getChangedRows(Connection conn) throws Sql2oException {
        return conn.createQuery("SELECT changes()").executeScalar(Integer.class);
    }


    public void readCSV() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if(!checkUpdatePermission("CSV", conn)) return;

            CSVReader reader = new CSVReader();
            List<EnglandMatch> matches = reader.readMatchData();

            insertMatchRecords(matches, conn);

            conn.createQuery("vacuum; ").executeUpdate();

            updateLog("CSV", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to update MatchRecords table", ex);
            throw new SoccerServiceException("Failed to update MatchRecords table", ex);
        }
    }


    public void initializeTeam() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if(!checkUpdatePermission("Team", conn)) return;

            String sqlFetchEnglandMatches = " SELECT * from MatchRecords; ";

            List<EnglandMatch> englandMatches = conn.createQuery(sqlFetchEnglandMatches)
                    .executeAndFetch(EnglandMatch.class);

            String sqlFetchAllTeams = "SELECT team_api_id, team_long_name, team_short_name, ' ' as city FROM Team; ";
            List<EnglandTeam> allTeams =conn.createQuery(sqlFetchAllTeams)
                    .addColumnMapping("team_api_id", "id")
                    .addColumnMapping("team_long_name", "teamName")
                    .addColumnMapping("team_short_name", "teamCode")
                    .executeAndFetch(EnglandTeam.class);

            clearTable("MatchRecords", conn);

            for (EnglandMatch eachMatch : englandMatches ) {
                eachMatch.expandTeamNames(allTeams);
            }

            insertMatchRecords(englandMatches, conn);

            List<EnglandTeam> englandTeams = new ArrayList<EnglandTeam>();
            Map<String, String> teamCities = manualSetTeamCity();

            for (EnglandTeam eachTeam : allTeams ) {
                if(isTeamInEngland(eachTeam.getTeamName(), englandMatches)) {
                    englandTeams.add(eachTeam);
                    eachTeam.setCity(teamCities.get(eachTeam.getTeamName()));
                }
            }

            String sqlInsertEnglandTeam = " INSERT INTO EnglandTeam VALUES ( "
                    + " :id, :teamName, :teamCode, :city); ";

            for (EnglandTeam eachTeam : englandTeams ) {
                conn.createQuery(sqlInsertEnglandTeam)
                        .bind(eachTeam)
                        .executeUpdate();
            }

            updateLog("Team", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to initialize Team table", ex);
            throw new SoccerServiceException("Failed to initialize Team table", ex);
        }
    }

    public void initializeTeamAttributes() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if (!checkUpdatePermission("TeamAttributes", conn)) return;

            String sqlFetchTeamAttributes = " SELECT team_api_id,  "
                    + " buildUpPlaySpeedClass, "
                    + " buildUpPlayDribblingClass, "
                    + " buildUpPlayPassingClass, "
                    + " buildUpPlayPositioningClass, "
                    + " chanceCreationPassingClass, "
                    + " chanceCreationCrossingClass, "
                    + " chanceCreationShootingClass, "
                    + " chanceCreationPositioningClass, "
                    + " defencePressureClass, "
                    + " defenceAggressionClass, "
                    + " defenceTeamWidthClass, "
                    + " defenceDefenderLineClass " +
                    "FROM Team_Attributes T1 " +
                    "WHERE id = (SELECT max(id) as max_id " +
                    "            FROM Team_Attributes T2 " +
                    "            WHERE T2.team_api_id=T1.team_api_id " +
                    "            GROUP BY team_api_id);  ";

            List<EnglandTeamAttributes> allAttributes = conn.createQuery(sqlFetchTeamAttributes)
                    .addColumnMapping("team_api_id", "id")
                    .executeAndFetch(EnglandTeamAttributes.class);

            String sqlFetchEnglandTeam = " SELECT * FROM EnglandTeam; ";
            List<EnglandTeam> englandTeams = conn.createQuery(sqlFetchEnglandTeam)
                    .executeAndFetch(EnglandTeam.class);

            //            for (EnglandTeam eachTeam : englandTeams ) System.out.println(eachTeam);

            List<EnglandTeamAttributes> englandAttributes = new ArrayList<>();

            for (EnglandTeamAttributes eachAttributes : allAttributes ) {
                for(EnglandTeam eachTeam : englandTeams ) {
                    if( eachAttributes.getId() == eachTeam.getId()) {
                        englandAttributes.add(eachAttributes);
                    }
                }
            }

            //            for (EnglandTeamAttributes eachAttributes : englandAttributes ) System.out.println(eachAttributes);
            String sqlInsertEnglandTeamAttributes = " INSERT INTO EnglandTeamAttributes VALUES "
                    + " (:id, "
                    + " :buildUpPlaySpeedClass, "
                    + " :buildUpPlayDribblingClass, "
                    + " :buildUpPlayPassingClass, "
                    + " :buildUpPlayPositioningClass, "
                    + " :chanceCreationPassingClass, "
                    + " :chanceCreationCrossingClass, "
                    + " :chanceCreationShootingClass, "
                    + " :chanceCreationPositioningClass, "
                    + " :defencePressureClass, "
                    + " :defenceAggressionClass, "
                    + " :defenceTeamWidthClass, "
                    + " :defenceDefenderLineClass ); ";
            for (EnglandTeamAttributes eachAttributes : englandAttributes ) {
                conn.createQuery(sqlInsertEnglandTeamAttributes)
                        .bind(eachAttributes)
                        .executeUpdate();
            }

            updateLog("TeamAttributes", conn);


        } catch (Sql2oException ex) {
            logger.error("Failed to initialize EnglandTeamAttributes table", ex);
            throw new SoccerServiceException("Failed to initialize EnglandTeamAttributes table", ex);
        }
    }

    private boolean checkUpdatePermission(String sourceName, Connection conn) throws SoccerServiceException {
        String sqlQueryLog = String.format(" SELECT updatecount FROM updatelog WHERE sourcename= '%s' ; ", sourceName);

        System.out.println(sqlQueryLog);

        Integer updateCount = conn.createQuery(sqlQueryLog)
                .executeScalar(Integer.class);

        System.out.println("count fetched");

        if (updateCount == null) {
            System.out.println("No update on this source yet, permission granted");
            return true;
        }
        System.out.println("This source already updated, permission denied");
        return false;
    }

    private void updateLog(String sourceName, Connection conn) throws SoccerServiceException {
        System.out.printf("updating log for source %s%n", sourceName);

        String sqlUpdateLog = String.format(" INSERT INTO updatelog VALUES('%s', 1); ",sourceName);
        conn.createQuery(sqlUpdateLog).executeUpdate();
    }

    private void insertMatchRecords(List<EnglandMatch> matches, Connection conn) {
        String sql = " INSERT INTO MatchRecords VALUES "
                + " ( :dateString, :homeTeam, :awayTeam, :fullTimeResult, :halfTimeResult, :referee, "
                + " :dateNumber, :fthg, :ftag, :hthg, :htag, :homeShot, :awayShot, :hst, :ast, :hf, :af, :hc, :ac, :hy, :ay, :hr, :ar); ";
        for (EnglandMatch eachMatch : matches) {
            conn.createQuery(sql)
                    .bind(eachMatch)
                    .executeUpdate();
        }
    }

    private void clearTable(String tableName, Connection conn) {
        String sqlClearTableMatches = String.format(" DELETE FROM %s; ", tableName);
        conn.createQuery(sqlClearTableMatches).executeUpdate();
    }

    private boolean isTeamInEngland(String teamName, List<EnglandMatch> matches) {
        System.out.printf("Trying to find %s's match in England%n", teamName);

        for (EnglandMatch eachMatch: matches ) {
            if ( eachMatch.getHomeTeam() == teamName || eachMatch.getAwayTeam() == teamName ) {
                System.out.printf("Found a match that contains team %s%n", teamName);
                return true;
            }
        }

        System.out.printf("Team %s has no match in England%n", teamName);

        return false;
    }

    private Map<String, String> manualSetTeamCity() {
        Map<String, String> result = new HashMap<>();
        result.put("Manchester United", "Manchester");
        result.put("Arsenal", "London");
        result.put("West Bromwich Albion", "West Bromwich");
        result.put("Sunderland", "Sunderland");
        result.put("Liverpool", "Liverpool");
        result.put("West Ham United", "London");
        result.put("Manchester City", "Manchester");
        result.put("Everton", "Liverpool");
        result.put("Middlesbrough", "Middlesbrough");
        result.put("Tottenham Hotspur", "London");
        result.put("Stoke City", "Stoke-on-Trent");
        result.put("Hull City", "Hull");
        result.put("Chelsea", "London");
        result.put("Burnley", "Burnley");
        result.put("Swansea City", "Swansea");
        result.put("Southampton", "Southampton");
        result.put("Crystal Palace", "London");
        result.put("Leicester City", "Leicester");
        result.put("Bournemouth", "Bournemouth");
        result.put("Watford", "Watford");

        return result;
    }

    private Map<String, List<String>> manualSetPlayerMembership() {
        Map<String, List<String>> result = new HashMap<String,List<String>>();

        result.put("Manchester United", Arrays.asList("David de Gea",
                "Sergio Romero",
                "Sam Johnstone",
                "Eric Bailly",
                "Daley Blind",
                "Luke Shaw",
                "Chris Smalling",
                "Marcos Rojo",
                "Matteo Darmian",
                "Phil Jones",
                "Antonio Valencia",
                "Paul Pogba",
                "Henrikh Mkhitaryan",
                "Juan Mata",
                "Ander Herrera",
                "Morgan Schneiderlin",
                "Marouane Fellaini",
                "Ashley Young",
                "Jesse Lingard",
                "Bastian Scheweinsteiger",
                "Michael Carrick",
                "Timothy Fosu-Mensah",
                "Anthony Martial",
                "Wayne Rooney",
                "Memphis Depay",
                "Zlatan Ibrahimovic",
                "Marcus Dashford"));

        result.put("Burnley", Arrays.asList("Tom Heaton",
                "Nick Pope",
                "Paul Robinson",
                "Micheal Keane",
                "James Tarkowski",
                "Kevin Long",
                "Ben Mee",
                "Stephen Ward",
                "Jon Flanagan",
                "Matthew Lowton",
                "Tendayi Darikwa",
                "Steven Defour",
                "Jeff Hendrick",
                "Dean Marney",
                "George Boyd",
                "Scott Arfield",
                "Micheal Kightly",
                "Johann Berg Gudmundsson",
                "Andre Gray",
                "Patrick Bamford",
                "Sam Vokes",
                "Ashley Barnes"));

        result.put("Arsenal", Arrays.asList("Petr Cech",
                "David Ospina",
                "Emiliano Martinez",
                "Matt Macey",
                "Shkodran Mustafi",
                "Laurent Koscielny",
                "Gabriel Paulista",
                "Per Mertesacker",
                "Rob Holding",
                "Nacho Monreal",
                "Kieran Gibbs",
                "Hector Bellerin",
                "Carl Jenkinson",
                "Mathieu Debuchy",
                "Francis Coquelin",
                "Mohamed Elneny",
                "Aaron Ramsey",
                "Granit Xhaka",
                "Santi Cazorla",
                "Mesut Oezil",
                "Alex Iwobi",
                "Alex Oxlade-Chamberlain",
                "Alexis Sanchez",
                "Theo Walcott",
                "Olivier Giroud",
                "Lucas Perez",
                "Danny Welbeck",
                "Yaya Sanogo",
                "Chuba Akpom"));

        result.put("Leicester City", Arrays.asList("Kasper Schmeichel",
                "Ron-Robert Zieler",
                "Ben Hamer",
                "Robert Huth",
                "Wes Morgan",
                "Yohan Benalouane",
                "Marcin Wasilewski",
                "Jeffrey Schlupp",
                "Christian Fuchs",
                "Ben Chilwell",
                "Danny Simpson",
                "Luis Hernandez",
                "Nampalys Mendy",
                "Daniel Amartey",
                "Danny Drinkwater",
                "Andy King",
                "Matty James",
                "Marc Albrighton",
                "Bartosz Kapustka",
                "Demarai Gray",
                "Riyad Mahrez",
                "Islam Slimani",
                "Jamie Vardy",
                "Ahmed Musa",
                "Shinji Okazaki",
                "Leonardo Ulloa"));

        result.put("Chelsea", Arrays.asList("Thibaut Courtois",
                "Asmir Begovic",
                "Eduardo",
                "David Luiz",
                "Kurt Zouma",
                "Gary Cahill",
                "John Terry",
                "Cesar Azpilicueta",
                "Marcos Alonso",
                "Kenedy",
                "Branislav Ivanovic",
                "Todd Kane",
                "Ola Aina",
                "Nemanja Matic",
                "N'Golo Kante",
                "John Mikel Obi",
                "Nathaniel Chalobah",
                "Cesc Fabregas",
                "Marco van Ginkel",
                "Ruben Loftus-Cheek",
                "Oscar",
                "Eden Hazard",
                "Willian",
                "Pedro",
                "Victor Moses",
                "Diego Costa",
                "Michy Batshuayi"));

        result.put("Manchester City", Arrays.asList());



        return result;
    }




}







