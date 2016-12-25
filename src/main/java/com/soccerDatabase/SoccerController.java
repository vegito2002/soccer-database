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
        post(API_CONTEXT + "/todos", "application/json", (request, response) -> {
            try {
                soccerService.createNewTodo(request.body());
                response.status(201);
            } catch (SoccerService.SoccerServiceException ex) {
                logger.error("Failed to create new entry");
                response.status(500);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        get(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                return soccerService.find(request.params(":id"));
            } catch (SoccerService.SoccerServiceException ex) {
                logger.error(String.format("Failed to find object with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/todos", "application/json", (request, response)-> {
            try {
                return soccerService.findAll() ;
            } catch  (SoccerService.SoccerServiceException ex) {
                logger.error("Failed to fetch the list of todos");
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        put(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                return soccerService.update(request.params(":id"), request.body());
            } catch (SoccerService.SoccerServiceException ex) {
                logger.error(String.format("Failed to update todo with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        delete(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                soccerService.delete(request.params(":id"));
                response.status(200);
            } catch (SoccerService.SoccerServiceException ex) {
                logger.error(String.format("Failed to delete todo with id: %s", request.params(":id")));
                response.status(500);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        get(API_CONTEXT + "/initialize", "application/json", (request, response)-> {
            try {
                soccerService.readCSV();
                soccerService.initializeTeam();
                soccerService.initializeTeamAttributes();
                soccerService.initializeTeamMemberShip();
                soccerService.initializePlayer();
                soccerService.initializePlayerAttributes();
                return "Initialization Complete";
            } catch  (SoccerService.SoccerServiceException ex) {
                logger.error("Failed to fetch the list of todos");
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());    }
}
