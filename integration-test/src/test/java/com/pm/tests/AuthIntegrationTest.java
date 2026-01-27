package com.pm.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

class AuthIntegrationTest {

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI="http://localhost:8080";
    }

    @Test
    void shouldReturnOkWithValidToken(){
        //setup
        //Act
        //Assert

        String loginPayload = """
                {
                    "email": "testuser@test.com",
                     "password": "password123"
                }
                """;
        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token",notNullValue())
                .extract().response();

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    void shouldReturnUnauthorizedOnValidToken(){
        //arrange
        //Act
        //Assert

        String loginPayload = """
                {
                    "email": "wronguser@test.com",
                     "password": "invalidpassword"
                }
                """;
        RestAssured
                .given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);

      }
}
