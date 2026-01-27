package com.pm.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll
     static void setUp(){
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void shouldReturnPatientsWithValidToken(){
        //setup
        //Act
        //Assert

        String loginPayload = """
                {
                    "email": "testuser@test.com",
                     "password": "password123"
                }
                """;
        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");

        List<Map<String, Object>> patients = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue())
                .extract()
                .jsonPath()
                .getList("");

        // Print all patients with formatting
        System.out.println("Total patients: " + patients.size());
        System.out.println("======================================");

        for (int i = 0; i < patients.size(); i++) {
            Map<String, Object> patient = patients.get(i);
            System.out.println("Patient #" + (i + 1) + ":");
            System.out.println("  ID: " + patient.get("id"));
            System.out.println("  Name: " + patient.get("name"));
            System.out.println("  Email: " + patient.get("email"));
            System.out.println("  Date of Birth: " + patient.get("dateOfBirth"));
            System.out.println("  Address: " + patient.get("address"));
            System.out.println("--------------------------------------");
        }
    }
}
