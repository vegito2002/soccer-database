//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.soccerDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class SoccerController {

    private static final String API_CONTEXT = "/api/v1";

    private final SoccerService soccerService;

    private final Logger logger = LoggerFactory.getLogger(SoccerController.class);

    public SoccerController(SoccerService soccerService) {
        this.soccerService = soccerService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        get(API_CONTEXT + "/initialize", "application/json", (request, response)-> {
            try {
                soccerService.readCSV();
                soccerService.initializeTeam();
                soccerService.initializeTeamAttributes();
                soccerService.initializeTeamMemberShip();
                soccerService.initializePlayer();
                soccerService.initializePlayerAttributes();
                soccerService.initializeReferee();
                soccerService.initializeManager();
                response.status(201);
            } catch  (SoccerService.SoccerServiceException ex) {
                logger.error("Failed to initialize database");
                response.status(500);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        get(API_CONTEXT + "/teambyid", "application/json", (request, response) -> {
            try {
                return soccerService.findTeamById(request.queryParams("id"));
            } catch (SoccerService.SoccerServiceException ex) {
                logger.error(String.format("Failed to find object with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/teambycode", "application/json", (request, response) -> {
            try {
                return soccerService.findTeamByCode(request.queryParams("code"));
            } catch (SoccerService.SoccerServiceException ex) {
                logger.error(String.format("Failed to find object with code: %s", request.params(":code")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/teambyname", "application/json", (request, response) -> {
            try {
                return soccerService.findTeamByName(request.queryParams("name"));
            } catch (SoccerService.SoccerServiceException ex) {
                logger.error(String.format("Failed to find object with name: %s", request.params(":name")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

    }
}
