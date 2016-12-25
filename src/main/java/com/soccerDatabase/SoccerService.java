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

import javax.sql.ConnectionEvent;
import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SoccerService {

    private Sql2o db;

    private final Logger logger = LoggerFactory.getLogger(SoccerService.class);

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
            StaticQueryGenerator generator = new StaticQueryGenerator();
            conn.createQuery(generator.SQL_CREATETABLE_MATCHRECORDS).executeUpdate();
            conn.createQuery(generator.SQL_CREATETABLE_UPDATELOG).executeUpdate();
            conn.createQuery(generator.SQL_CREATETABLE_ENGLANDTEAM).executeUpdate();
            conn.createQuery(generator.SQL_CREATETABLE_ENGLAND_TEAM_ATTRIBUTES).executeUpdate();
            conn.createQuery(generator.SQL_CREATETABLE_ENGLAND_MEMBERSHIP).executeUpdate();
            conn.createQuery(generator.SQL_CREATETABLE_ENGLAND_PLAYER).executeUpdate();
            conn.createQuery(generator.SQL_CREATETABLE_ENGLAND_PLAYER).executeUpdate();
            conn.createQuery(generator.SQL_CREATETABLE_ENGLAND_PLAYER_ATTRIBUTES).executeUpdate();
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

            System.out.println("Starting to initialize data from CSV");

            CSVReader reader = new CSVReader();
            List<EnglandMatch> matches = reader.readMatchData();

            insertMatchRecords(matches, conn);

            conn.createQuery("vacuum; ").executeUpdate();

            System.out.println("Data from E0.CSV sucessfully initialized into database.sqlite");

            updateLog("CSV", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to update MatchRecords table", ex);
            throw new SoccerServiceException("Failed to update MatchRecords table", ex);
        }
    }


    public void initializeTeam() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if(!checkUpdatePermission("Team", conn)) return;
            System.out.println("Starting to initialize table EnglandTeam");

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
            Map<String, String> teamCities = new ManualDataGenerator().manualSetTeamCity();

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

            System.out.printf("%d tuples put back into table EnglandTeam in database.sqlite. Initialization complete %n", englandTeams.size());

            updateLog("Team", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to initialize Team table", ex);
            throw new SoccerServiceException("Failed to initialize Team table", ex);
        }
    }

    public void initializeTeamAttributes() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if (!checkUpdatePermission("TeamAttributes", conn)) return;
            System.out.println("Starting to initialize table EnglandTeamAttributes");

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
            System.out.printf("%d tuples put back into table EnglandTeamAttributes, initialization complete%n", englandAttributes.size());
            updateLog("TeamAttributes", conn);


        } catch (Sql2oException ex) {
            logger.error("Failed to initialize EnglandTeamAttributes table", ex);
            throw new SoccerServiceException("Failed to initialize EnglandTeamAttributes table", ex);
        }
    }

    public void initializePlayer() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if(!checkUpdatePermission("Player", conn)) return;
            System.out.println("Starting to initialize table EnglandPlayer");

            String sqlUpdateTableEnglandPlayer = " INSERT OR REPLACE INTO EnglandPlayer" +
                    " SELECT E1.playerId AS id, P1.player_name AS name, P1.height AS height, P1.weight AS weight, P1.birthday AS birthday, 0 AS birthdayNumber " +
                    "                    FROM EnglandMembership AS E1, Player AS P1 " +
                    "                    WHERE E1.playerId=P1.player_api_id;  ";
            conn.createQuery(sqlUpdateTableEnglandPlayer).executeUpdate();

            String sqlFetchEnglandPlayers = " SELECT * FROM EnglandPlayer; ";
            List<EnglandPlayer> englandPlayers = conn.createQuery(sqlFetchEnglandPlayers)
                    .executeAndFetch(EnglandPlayer.class);

            conn.createQuery( " DELETE FROM EnglandPlayer; ").executeUpdate();

            String sqlInsertPlayer = " INSERT INTO EnglandPlayer VALUES " +
                    " ( :id, :name, :height, :weight, :birthday, :birthdayNumber ); ";

            for(EnglandPlayer eachPlayer : englandPlayers) {
                eachPlayer.calculateDateNumber();
                conn.createQuery(sqlInsertPlayer)
                        .bind(eachPlayer)
                        .executeUpdate();
            }

            System.out.printf("%d tuples put back into table EnglandPlayer with birthdayNumber generated%n", englandPlayers.size());

            updateLog("Player", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to initialize EnglandPlayer table", ex);
            throw new SoccerServiceException("Failed to initialize EnglandPlayer table", ex);
        }
    }

    public void initializePlayerAttributes() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if(!checkUpdatePermission("PlayerAttributes", conn)) return;
            System.out.println("Starting to initialize table EnglandPlayerAttributes");

            String sqlFetchAllPlayerAttributes = " SELECT id AS counter, "
                    + " player_api_id AS id, "
                    + " overall_rating AS rating, "
                    + " potential, "
                    + " preferred_foot as foot"
                    + " FROM Player_Attributes; ";

            List<AnyPlayerAttributes> allPlayerAttributes = conn.createQuery(sqlFetchAllPlayerAttributes)
                    .executeAndFetch(AnyPlayerAttributes.class);

            System.out.printf("%d tuples fetched into memory%n", allPlayerAttributes.size());

            List<EnglandPlayerAttributes> englandPlayerAttributes = new ArrayList<>();

            Map<Integer, Integer> maxCounters = new HashMap<>();

            Integer tempCounter;

            for (AnyPlayerAttributes eachAttributes : allPlayerAttributes) {

                tempCounter = maxCounters.get(eachAttributes.getId());

                if (tempCounter == null) {
                    maxCounters.put(eachAttributes.getId(), 0);
                    tempCounter = 0;
                }

                if (eachAttributes.getCounter() > tempCounter && checkPlayerAttributesEntryEligibility(eachAttributes)) {
                    maxCounters.put(eachAttributes.getId(), eachAttributes.getCounter());
                }
            }

            System.out.printf("%d entries currently in the map%n", maxCounters.keySet().size());

            for (AnyPlayerAttributes eachAttributes : allPlayerAttributes ) {
                if(maxCounters.get(eachAttributes.getId()) == eachAttributes.getCounter()) {
                    englandPlayerAttributes.add(new EnglandPlayerAttributes(eachAttributes.getId(),eachAttributes.getRating(),eachAttributes.getPotential(),eachAttributes.getFoot()));
                }
            }

            System.out.printf("%d tuples with the maximum counter for each playerId are selected %n", englandPlayerAttributes.size());

            String sqlInsertPlayerAttributes = " INSERT INTO EnglandPlayerAttributes VALUES (:id, "
                    + " :rating, "
                    + " :potential, "
                    + " :foot); ";

            for (EnglandPlayerAttributes eachAttributes : englandPlayerAttributes ) {
                conn.createQuery(sqlInsertPlayerAttributes)
                        .bind(eachAttributes)
                        .executeUpdate();
            }

            System.out.printf("%d tuples put back into table EnglandPlayerAttributes with birthdayNumber generated%n", englandPlayerAttributes.size());

            updateLog("PlayerAttributes", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to initialize EnglandPlayerAttributes table", ex);
            throw new SoccerServiceException("Failed to initialize EnglandPlayerAttributes table", ex);
        }
    }

    private boolean checkPlayerAttributesEntryEligibility(AnyPlayerAttributes arg) {
        return (arg.getRating()>0 ) && (arg.getPotential()>0 ) && (arg.getFoot()!=null );
    }

    public void initializeTeamMemberShip() throws SoccerServiceException {
        try (Connection conn = db.open()) {

            if (!checkUpdatePermission("TeamMembership", conn)) return;

            System.out.println("Starting initializing table EnglandMembership");

            Map<String, List<String>> rawMembership = new ManualDataGenerator().manualSetPlayerMembership();
            List<EnglandTeam> englandTeams = conn.createQuery(" SELECT * FROM EnglandTeam; ").executeAndFetch(EnglandTeam.class);

            List<EnglandMembership> englandMemberships = new ArrayList<>();

            int tempTeamId;
            Integer tempPlayerId;

            for (Map.Entry eachMembership : rawMembership.entrySet() ) {

                System.out.printf("Trying to create roster for team %s%n",(String)eachMembership.getKey());

                tempTeamId = getTeamIdByName((String)eachMembership.getKey(), englandTeams);

                System.out.printf("The id for this team is %d%n",tempTeamId);

                for (String eachPlayerName : (List<String>)eachMembership.getValue()) {

                    System.out.printf("Found a player %s for team %d, trying to find his playerId%n", eachPlayerName,tempTeamId);

                    String sqlFetchPlayerIdByName = " SELECT player_api_id FROM Player WHERE player_name = :nameParam; ";
                    tempPlayerId = conn.createQuery(sqlFetchPlayerIdByName)
                            .addParameter("nameParam", eachPlayerName)
                            .executeScalar(Integer.class);
                    if (tempPlayerId == null) {
                        System.out.printf("Player %s is not found%n", eachPlayerName);
                        return;
                    }
                    System.out.printf("id for player %s is %d%n", eachPlayerName, tempPlayerId);
                    englandMemberships.add(new EnglandMembership(tempTeamId, tempPlayerId));

                }
            }

            String sqlInsertMembership = " INSERT INTO EnglandMembership VALUES( :teamId, :playerId); ";
            for (EnglandMembership eachMembership : englandMemberships ) {
                conn.createQuery(sqlInsertMembership)
                        .bind(eachMembership)
                        .executeUpdate();
            }

            System.out.printf("%d tuples put back into table EnglandMembership in database.sqlite file%n", englandMemberships.size());

            updateLog("TeamMembership", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to initialize EnglandMembership table", ex);
            throw new SoccerServiceException("Failed to initialize EnglandMembership table", ex);
        }
    }

    private int getTeamIdByName(String teamName, List<EnglandTeam> teams) {
        for (EnglandTeam eachTeam : teams ) {
            if (eachTeam.getTeamName().equals(teamName)) return eachTeam.getId();
        }
        return -1;
    }

    private boolean checkUpdatePermission(String sourceName, Connection conn) throws SoccerServiceException {
        System.out.printf("Checking permission to update from source %s%n", sourceName);

        String sqlQueryLog = String.format(" SELECT updatecount FROM updatelog WHERE sourcename= '%s' ; ", sourceName);

        Integer updateCount = conn.createQuery(sqlQueryLog)
                .executeScalar(Integer.class);

        System.out.printf("Historical update count for the source %s is fetched from the database%n", sourceName);

        if (updateCount == null) {
            System.out.printf("No update has been performed on the source of %s, permission granted%n", sourceName);
            return true;
        }
        System.out.printf("The source of %s already updated, permission denied%n", sourceName);
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




}







