package org.evomaster.e2etests.spring.rest.mongo.customer;

import com.foo.customer.CustomerEmbeddedController;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.evomaster.client.java.controller.api.Formats;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.internal.db.StandardOutputTracker;
import org.evomaster.e2etests.spring.rest.mongo.SpringRestMongoTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerManualTest extends SpringRestMongoTestBase {

    private static final CustomerEmbeddedController sutController = new CustomerEmbeddedController();

    @BeforeAll
    public static void init() throws Exception {
        SpringRestMongoTestBase.initClass(sutController);
    }

    @BeforeEach
    public void turnOnTracker() {
        StandardOutputTracker.setTracker(true, sutController);
    }

    @AfterEach
    public void turnOffTracker() {
        StandardOutputTracker.setTracker(false, sutController);
    }

    @Test
    public void testSwaggerJSON() {

        SutInfoDto dto = remoteController.getSutInfo();

        String swaggerJson = given().accept(Formats.JSON_V1)
                .get(dto.restProblem.swaggerJsonUrl)
                .then()
                .statusCode(200)
                .extract().asString();

        Swagger swagger = new SwaggerParser().parse(swaggerJson);

        assertEquals("/", swagger.getBasePath());
        assertEquals(2, swagger.getPaths().size());
    }

    @Test
    public void testPostAndThenGet() {
        String url = baseUrlOfSut + "/api/mongodb/customer";

        given().post(url)
                .then()
                .statusCode(200);

        given().get(url)
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetOnEmpty() {
        String url = baseUrlOfSut + "/api/mongodb/customer";

        given()
                .get(url)
                .then()
                .statusCode(404);
    }

    @Test
    public void testPostGetDeleteGet() {
        String url = baseUrlOfSut + "/api/mongodb/customer";

        given().post(url)
                .then()
                .statusCode(200);

        given().get(url)
                .then()
                .statusCode(200);

        given().delete(url)
                .then()
                .statusCode(200);

        given().get(url)
                .then()
                .statusCode(404);

    }

    @Test
    public void testDeleteOnEmpty() {
        String url = baseUrlOfSut + "/api/mongodb/customer";

        given()
                .delete(url)
                .then()
                .statusCode(404);
    }


    @Test
    public void testDuplicatePost() {
        String url = baseUrlOfSut + "/api/mongodb/customer";

        given().post(url)
                .then()
                .statusCode(200);

        given().post(url)
                .then()
                .statusCode(400);
    }

}