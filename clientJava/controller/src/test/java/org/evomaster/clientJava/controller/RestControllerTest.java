package org.evomaster.clientJava.controller;

import io.restassured.RestAssured;
import org.evomaster.clientJava.controllerApi.Formats;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class RestControllerTest {

    private static final String SWAGGER_URL = "localhost:9999/swagger.json";

    private static class FakeRestController extends RestController{

        public boolean running;

        @Override
        public String startSut() {
            running = true;
            return null;
        }

        @Override
        public String startInstrumentedSut() {
            running = true;
            return null;
        }

        @Override
        public boolean isSutRunning() {
            return running;
        }

        @Override
        public void stopSut() {
            running = false;
        }

        @Override
        public void resetStateOfSUT() {

        }

        @Override
        public String getUrlOfSwaggerJSON() {
            return SWAGGER_URL;
        }


    }

    private static RestController restController = new FakeRestController();

    @BeforeAll
    public static void initClass(){
        restController.setControllerPort(0);
        restController.startTheControllerServer();

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = restController.getControllerServerJettyPort();
        RestAssured.basePath = "/controller/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterAll
    public static void tearDown(){
        restController.stopSut();
    }

    @BeforeEach
    public void initTest(){
        if(restController.isSutRunning()){
            restController.stopSut();
        }
    }


    @Test
    public void testNotRunning(){
        assertTrue(! restController.isSutRunning());

        given().accept(Formats.JSON_V1)
                .get("/infoSUT")
                .then()
                .statusCode(200)
                .body("isSutRunning", is(false));
    }

    @Test
    public void testStartDirect(){

        restController.startInstrumentedSut();
        assertTrue(restController.isSutRunning());

        given().accept(Formats.JSON_V1)
                .get("/infoSUT")
                .then()
                .statusCode(200)
                .body("isSutRunning", is(true));
    }

    @Test
    public void testStartRest(){

        given().post("/startSUT")
                .then()
                .statusCode(204);

        assertTrue(restController.isSutRunning());

        given().accept(Formats.JSON_V1)
                .get("/infoSUT")
                .then()
                .statusCode(200)
                .body("isSutRunning", is(true));
    }


    @Test
    public void testGetSwaggerUrl(){

        given().accept(Formats.JSON_V1)
                .get("/infoSUT")
                .then()
                .statusCode(200)
                .body("swaggerJsonUrl", is(SWAGGER_URL));
    }
}