/*package com.bybud.authservice.Integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

public class AuthApiIntegrationTest {

    private static String accessToken;
    private static String refreshToken;

    @BeforeAll
    public static void setup() {
        // Base URI of your auth service
        RestAssured.baseURI = "http://localhost:8081/api/auth";

        // Register a user
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", "testuser")
                .formParam("email", "testuser@example.com")
                .formParam("password", "password123")
                .formParam("fullName", "Test User")
                .formParam("dateOfBirth", "1990-01-01")
                .formParam("role", "ROLE_CUSTOMER")
                .post("/register");

        // Log in and retrieve tokens
        Response loginResponse = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("usernameOrEmail", "testuser")
                .formParam("password", "password123")
                .post("/login");

        accessToken = loginResponse.jsonPath().getString("accessToken");
        refreshToken = loginResponse.jsonPath().getString("refreshToken");
    }

    @Test
    public void testRegisterUser() {
        Response registerResponse = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", "testuser2")
                .formParam("email", "testuser2@example.com")
                .formParam("password", "password123")
                .formParam("fullName", "Test User")
                .formParam("dateOfBirth", "1990-01-01")
                .formParam("role", "ROLE_CUSTOMER")
                .post("/register");

        registerResponse.then()
                .statusCode(200)
                .body(containsString("User registered successfully"));
    }

    @Test
    public void testLoginUser() {
        Response loginResponse = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("usernameOrEmail", "testuser")
                .formParam("password", "password123")
                .post("/login");

        loginResponse.then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("username", equalTo("testuser"));

        // Extract access and refresh tokens for subsequent tests
        accessToken = loginResponse.jsonPath().getString("accessToken");
        refreshToken = loginResponse.jsonPath().getString("refreshToken");
    }

    @Test
    public void testGetUserDetails() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("usernameOrEmail", "testuser")
                .get("/user")
                .then()
                .statusCode(200)
                .body("username", equalTo("testuser"))
                .body("email", equalTo("testuser@example.com"));
    }

    @Test
    public void testRefreshToken() {
        assertNotNull(refreshToken, "Refresh token is null. Ensure login test passes first.");

        Response refreshTokenResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/x-www-form-urlencoded")
                .formParam("refreshToken", refreshToken)
                .post("/refresh-token");

        System.out.println("Response: " + refreshTokenResponse.body().asString());

        refreshTokenResponse
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }
}

/*
To run the integration tests, you need to start the auth service locally on port 8081 and uncomment the import statement at the top of the class.
 */
