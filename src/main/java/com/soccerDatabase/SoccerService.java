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
            conn.createQuery(generator.SQL_CREATETABLE_ENGLAND_REFEREE).executeUpdate();
            conn.createQuery(generator.SQL_CREATETABLE_ENGLAND_MANAGER).executeUpdate();

            //The table Match is bulky and not needed, we delete it from the db file
            conn.createQuery(" DROP TABLE IF EXISTS Match; ").executeUpdate();

            //Compress the db file
            conn.createQuery(" VACUUM; ").executeUpdate();

        } catch (Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new SoccerServiceException("Failed to create schema at startup", ex);
        }
    }

    /**
     * Return an EnglandTeam by the input String that is the team's id
     * @param id
     * @return Found EnglandTeam, or null
     * @throws SoccerServiceException
     */
    public EnglandTeam findTeamById(String id) throws SoccerServiceException {
        int idInt = Integer.parseInt(id);
        try (Connection conn = db.open()) {
            String sqlFetchTeamById = " SELECT * FROM EnglandTeam WHERE id= :idParam ; ";

            return conn.createQuery(sqlFetchTeamById)
                    .addParameter("idParam", idInt)
                    .executeAndFetchFirst(EnglandTeam.class);

        } catch (Sql2oException ex) {
            logger.error(String.format("SoccerService.findTeamById: Failed to query database for id: %s", id), ex);
            throw new SoccerServiceException(String.format("SoccerService.findTeamById: Failed to query database for id: %s", id), ex);
        }
    }

    /**
     * Return an EnglandTeam by the input String that is the team's code
     * @param code
     * @return the found team or null
     * @throws SoccerServiceException
     */
    public EnglandTeam findTeamByCode(String code) throws SoccerServiceException {
        try (Connection conn = db.open()) {
            String sqlFetchTeamById = " SELECT * FROM EnglandTeam WHERE teamCode= :codeParam ; ";

            return conn.createQuery(sqlFetchTeamById)
                    .addParameter("codeParam", code)
                    .executeAndFetchFirst(EnglandTeam.class);

        } catch (Sql2oException ex) {
            logger.error(String.format("SoccerService.findTeamById: Failed to query database for code: %s", code), ex);
            throw new SoccerServiceException(String.format("SoccerService.findTeamById: Failed to query database for code: %s", code), ex);
        }
    }

    /**
     * Return an EnglandTeam by the input String that is the incomplete of complete name of a team, case in sensitive
     * @param name
     * @return the found EnglandTeam or null
     * @throws SoccerServiceException
     */
    public EnglandTeam findTeamByName(String name) throws SoccerServiceException {
        try (Connection conn = db.open()) {
            List<EnglandTeam> teams = conn.createQuery(" SELECT * FROM EnglandTeam; ")
                    .executeAndFetch(EnglandTeam.class);

            for (EnglandTeam eachTeam : teams ) {
                if (eachTeam
                        .getTeamName()
                        .toUpperCase()
                        .contains(name
                                .toUpperCase())) return eachTeam;
            }

            return null;

        } catch (Sql2oException ex) {
            logger.error(String.format("SoccerService.findTeamById: Failed to query database for name: %s", name), ex);
            throw new SoccerServiceException(String.format("SoccerService.findTeamById: Failed to query database for name: %s", name), ex);
        }
    }

    /**
     * Read data into database.sqlite from E0.CSV file
     * @throws SoccerServiceException
     */
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

    /**
     * Initialize the table EnglandTeam
     * @throws SoccerServiceException
     */
    public void initializeTeam() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if(!checkUpdatePermission("Team", conn)) return;
            System.out.println("Starting to initialize table EnglandTeam");

            String sqlFetchEnglandMatches = " SELECT * from MatchRecords; ";

            //Fetch all MatchRecords data into memory to avoid I/O overhead
            List<EnglandMatch> englandMatches = conn.createQuery(sqlFetchEnglandMatches)
                    .executeAndFetch(EnglandMatch.class);

            //Fetch all Team data into memory. Leave the column city blank awaiting processing
            String sqlFetchAllTeams = "SELECT team_api_id, team_long_name, team_short_name, ' ' as city FROM Team; ";
            List<EnglandTeam> allTeams =conn.createQuery(sqlFetchAllTeams)
                    .addColumnMapping("team_api_id", "id")
                    .addColumnMapping("team_long_name", "teamName")
                    .addColumnMapping("team_short_name", "teamCode")
                    .executeAndFetch(EnglandTeam.class);

            clearTable("MatchRecords", conn);

            //Expand all the team names in the MatchRecords table to full names, that can be found in table Team
            for (EnglandMatch eachMatch : englandMatches ) {
                eachMatch.expandTeamNames(allTeams);
            }

            insertMatchRecords(englandMatches, conn);

            List<EnglandTeam> englandTeams = new ArrayList<EnglandTeam>();

            //Introducing manual data, since there is no API available to determine a team's city
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

    /**
     * Initialize the table EnglandTeamAttributes
     * @throws SoccerServiceException
     */
    public void initializeTeamAttributes() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if (!checkUpdatePermission("TeamAttributes", conn)) return;
            System.out.println("Starting to initialize table EnglandTeamAttributes");

            //First fetch all data from table Team_Attributes to avoid I/O overhead
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

            //Fetch all data from table EnglandTeam into memory
            String sqlFetchEnglandTeam = " SELECT * FROM EnglandTeam; ";
            List<EnglandTeam> englandTeams = conn.createQuery(sqlFetchEnglandTeam)
                    .executeAndFetch(EnglandTeam.class);

            List<EnglandTeamAttributes> englandAttributes = new ArrayList<>();

            //an easy one-pass to find the right All_Attributes tuple for each EnglandTeam
            for (EnglandTeamAttributes eachAttributes : allAttributes ) {
                for(EnglandTeam eachTeam : englandTeams ) {
                    if( eachAttributes.getId() == eachTeam.getId()) {
                        englandAttributes.add(eachAttributes);
                    }
                }
            }

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

    /**
     * Initialize the table EnglandMembership
     * @throws SoccerServiceException
     */
    public void initializeTeamMemberShip() throws SoccerServiceException {
        try (Connection conn = db.open()) {

            if (!checkUpdatePermission("TeamMembership", conn)) return;

            System.out.println("Starting initializing table EnglandMembership");

            //Introducitn manual data
            Map<String, List<String>> rawMembership = new ManualDataGenerator().manualSetPlayerMembership();
            List<EnglandTeam> englandTeams = conn.createQuery(" SELECT * FROM EnglandTeam; ").executeAndFetch(EnglandTeam.class);

            List<EnglandMembership> englandMemberships = new ArrayList<>();

            //The manual data for team membership is a little complicated, so we use two temp variables here
            int tempTeamId;
            Integer tempPlayerId;

            for (Map.Entry eachMembership : rawMembership.entrySet() ) {

                System.out.printf("Trying to create roster for team %s%n",(String)eachMembership.getKey());

                tempTeamId = getTeamIdByName((String)eachMembership.getKey(), englandTeams);

                System.out.printf("The id for this team is %d%n",tempTeamId);

                //Simple one-pass two-dimentional loop to find all the playerIds for each teamId
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

    public void initializePlayer() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if(!checkUpdatePermission("Player", conn)) return;
            System.out.println("Starting to initialize table EnglandPlayer");

            //I realized that finding in table Player_Attributes all the tuples that has a player_api_id corresponding to some value in EnglandMembership is easier to do in SQL, so this is just running an SQL query
            String sqlUpdateTableEnglandPlayer = " INSERT OR REPLACE INTO EnglandPlayer" +
                    " SELECT E1.playerId AS id, P1.player_name AS name, P1.height AS height, P1.weight AS weight, P1.birthday AS birthday, 0 AS birthdayNumber " +
                    "                    FROM EnglandMembership AS E1, Player AS P1 " +
                    "                    WHERE E1.playerId=P1.player_api_id;  ";
            conn.createQuery(sqlUpdateTableEnglandPlayer).executeUpdate();

            //Select everything of EnglandPlayer into memory to add birthdayNumber to it
            String sqlFetchEnglandPlayers = " SELECT * FROM EnglandPlayer; ";
            List<EnglandPlayer> englandPlayers = conn.createQuery(sqlFetchEnglandPlayers)
                    .executeAndFetch(EnglandPlayer.class);

            conn.createQuery( " DELETE FROM EnglandPlayer; ").executeUpdate();

            String sqlInsertPlayer = " INSERT INTO EnglandPlayer VALUES " +
                    " ( :id, :name, :height, :weight, :birthday, :birthdayNumber ); ";

            for(EnglandPlayer eachPlayer : englandPlayers) {
                //Calculate the number formatted birthday for each player. The mechanism used is similar to that of CSV reader
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

    /**
     * Initializes EnglandPlayerAttributes table
     * @throws SoccerServiceException
     */
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

            List<AnyPlayerAttributes> allPlayerAttributesDuplicated = conn.createQuery(sqlFetchAllPlayerAttributes)
                    .executeAndFetch(AnyPlayerAttributes.class);

            System.out.printf("%d tuples fetched into memory%n", allPlayerAttributesDuplicated.size());

            //I have to fetch every tuple of table Player_Attributes into memory first
            List<EnglandPlayerAttributes> allPlayerAttributes = new ArrayList<>();
            List<EnglandPlayerAttributes> englandPlayerAttributes = new ArrayList<>();

            /*
            Each playerId may have multiple tuples in Player_Attributes corresponding to different years. I created
            this map to save, for each playerId, the max counter id(an automatically generated counter by the Player_Attributes
            table). With this map fully calculated, I will be able to identify uniquely the tuple in Player_Attributes
            that corresponds to a certain playerId.
             */
            Map<Integer, Integer> maxCounters = new HashMap<>();

            Integer tempCounter;

            //One pass to calculate the maxCounters map
            for (AnyPlayerAttributes eachAttributes : allPlayerAttributesDuplicated) {

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

            //With this map in hand, a simple one pass throught all tuples of Player_Attributes, and fetch one unique attributes tuple for a playerId
            for (AnyPlayerAttributes eachAttributes : allPlayerAttributesDuplicated ) {
                if(maxCounters.get(eachAttributes.getId()) == eachAttributes.getCounter()) {
                    allPlayerAttributes.add(new EnglandPlayerAttributes(eachAttributes.getId(),eachAttributes.getRating(),eachAttributes.getPotential(),eachAttributes.getFoot()));
                }
            }

            /*
            At this point, we have a list of tuples of Player_Attributes that is unique for playerId, but there still are too many playerIds. We only
            want to collect those playIds that are in UK Premier League 16/17, or those already in EnglandPlayer, given that
            initializePlayer is performed before this initializePlayerAttributes.
             */
            System.out.printf("%d tuples with the maximum counter for each playerId are selected %n", allPlayerAttributes.size());

            List<EnglandPlayer> englandPlayers = conn.createQuery(" SELECT * FROM EnglandPlayer; ").executeAndFetch(EnglandPlayer.class);

            for (EnglandPlayerAttributes eachAttributes : allPlayerAttributes ) {
                for (EnglandPlayer eachPlayer : englandPlayers ) {
                    if (eachPlayer.getId()==eachAttributes.getId()) {
                        englandPlayerAttributes.add(eachAttributes);
                        break;
                    }
                }
            }

            String sqlInsertPlayerAttributes = " INSERT INTO EnglandPlayerAttributes VALUES (:id, "
                    + " :rating, "
                    + " :potential, "
                    + " :foot); ";

            for (EnglandPlayerAttributes eachAttributes : englandPlayerAttributes ) {
                conn.createQuery(sqlInsertPlayerAttributes)
                        .bind(eachAttributes)
                        .executeUpdate();
            }

            System.out.printf("%d tuples put back into table EnglandPlayerAttributes with birthdayNumber generated%n", allPlayerAttributes.size());

            updateLog("PlayerAttributes", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to initialize EnglandPlayerAttributes table", ex);
            throw new SoccerServiceException("Failed to initialize EnglandPlayerAttributes table", ex);
        }
    }

    /**
     * Simple method to check whether a certain entry is complete in columns.
     * @param arg
     * @return
     */
    private boolean checkPlayerAttributesEntryEligibility(AnyPlayerAttributes arg) {
        return (arg.getRating()>0 ) && (arg.getPotential()>0 ) && (arg.getFoot()!=null );
    }

    /**
     * Initialize the table EnglandReferee
     * @throws SoccerServiceException
     */
    public void initializeReferee() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if (!checkUpdatePermission("Referee", conn)) return;
            System.out.println("Starting to initialize table EnglandReferee");

            //Introducing manual data
            ManualDataGenerator generator = new ManualDataGenerator();
            Map<String, List<String>> referees = generator.manualSetReferee();

            List<EnglandReferee> englandReferees = new ArrayList<>();

            for (Map.Entry eachReferee : referees.entrySet() ) {

                List<String> eachRefereeProfile = (List<String>) eachReferee.getValue();
                String[] eachRefereeProfileEntries = (String[]) eachRefereeProfile.toArray();

                int dateNumber = 86400 * (int) (LocalDate.parse(eachRefereeProfileEntries[2],DateTimeFormatter.ISO_LOCAL_DATE)).toEpochDay();
                englandReferees.add(new EnglandReferee(eachRefereeProfileEntries[0],
                        eachRefereeProfileEntries[1],
                        eachRefereeProfileEntries[2],
                        eachRefereeProfileEntries[3],
                        dateNumber));
            }

            String sqlInsertEnglandReferee = " INSERT INTO EnglandReferee VALUES "
                    + " ( :name, "
                    + " :fullName, "
                    + " :birthday, "
                    + " :birthCity, "
                    + " :birthdayNumber); ";

            for (EnglandReferee eachReferee : englandReferees ) {
                conn.createQuery(sqlInsertEnglandReferee)
                        .bind(eachReferee)
                        .executeUpdate();
            }

            System.out.printf("%d tuples put back into table EnglandReferee%n", englandReferees.size());

            updateLog("Referee", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to initialize EnglandReferee table", ex);
            throw new SoccerServiceException("Failed to initialize EnglandReferee table", ex);
        }
    }

    /**
     * Initialize the table EnglandManager
     * @throws SoccerServiceException
     */
    public void initializeManager() throws SoccerServiceException {
        try (Connection conn = db.open()) {
            if (!checkUpdatePermission("Manager", conn)) return;
            System.out.println("Starting to initialize table EnglandManager");

            //Introducing manual data
            ManualDataGenerator generator = new ManualDataGenerator();
            Map<Integer, List<String>> managerProfiles = generator.manualSetManager();

            List<EnglandManager> englandManagers = new ArrayList<>();

            for (Map.Entry eachManagerProfile : managerProfiles.entrySet() ) {

                List<String> eachManagerProfileList = (List<String>) eachManagerProfile.getValue();
                String[] managerProfileEntries = (String[]) eachManagerProfileList.toArray();

                int teamIdToAdd = (int) eachManagerProfile.getKey();

                int dateNumber = 86400 * (int) (LocalDate.parse(managerProfileEntries[1],DateTimeFormatter.ISO_LOCAL_DATE)).toEpochDay();

                englandManagers.add(new EnglandManager(managerProfileEntries[0],
                        teamIdToAdd,
                        managerProfileEntries[1],
                        managerProfileEntries[2],
                        dateNumber));
            }

            String sqlInsertManager = " INSERT INTO EnglandManager VALUES(:name, "
                    + " :teamId, "
                    + " :birthday, "
                    + " :birthCity, "
                    + " :birthdayNumber); ";

            for (EnglandManager eachManager : englandManagers) {
                conn.createQuery(sqlInsertManager)
                        .bind(eachManager)
                        .executeUpdate();
            }

            System.out.printf("%d tuples put back into table EnglandManager%n", englandManagers.size());

            updateLog("Manager", conn);

        } catch (Sql2oException ex) {
            logger.error("Failed to initialize EnglandManager table", ex);
            throw new SoccerServiceException("Failed to initialize Manager table", ex);
        }
    }

    /**
     * Helper method to find an EnglandTeam in a list of EnglandTeams with the give name
     * @param teamName The name to find in the list
     * @param teams the list to be searched
     * @return
     */
    private int getTeamIdByName(String teamName, List<EnglandTeam> teams) {
        for (EnglandTeam eachTeam : teams ) {
            if (eachTeam.getTeamName().equals(teamName)) return eachTeam.getId();
        }
        return -1;
    }

    /**
     * Query the 'updatelog' table to see whether a certain update has been performed on a certain source.
     * If not, then permission granted. Otherwise, permission denied.
     * @param sourceName The source of the update to check
     * @param conn The connection passed in to create query and execute them
     * @return a boolean value to indicate permission status
     * @throws SoccerServiceException
     */
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

    /**
     * Change the updatecount in the updatelog table, so that future attempts to update a source can know whether
     * the update permission should be granted
     * @param sourceName update source name
     * @param conn connection for queries
     * @throws SoccerServiceException
     */
    private void updateLog(String sourceName, Connection conn) throws SoccerServiceException {
        System.out.printf("updating log for source %s%n", sourceName);

        String sqlUpdateLog = String.format(" INSERT INTO updatelog VALUES('%s', 1); ",sourceName);
        conn.createQuery(sqlUpdateLog).executeUpdate();
    }


    /**
     * Simple helper method to insert a list of values into the MatchRecords table
     * @param matches
     * @param conn
     */
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

    /**
     * Simple helper method to clean up a table
     * @param tableName
     * @param conn
     */
    private void clearTable(String tableName, Connection conn) {
        String sqlClearTableMatches = String.format(" DELETE FROM %s; ", tableName);
        conn.createQuery(sqlClearTableMatches).executeUpdate();
    }

    /**
     * Simple helper method to check there is a team in UK Premier League 16/17 that is called a certain name
     * @param teamName the team name to check in England matches
     * @param matches all the england matches to search
     * @return boolean to indicate whether found or not
     */
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

}







