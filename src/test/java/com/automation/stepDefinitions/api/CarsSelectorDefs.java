package com.automation.stepDefinitions.api;

import cucumber.api.java.en.Given;

import java.util.HashMap;

import static com.automation.api.RestApiBasicClient.getInstance;

public class CarsSelectorDefs {

    private static final String MANUFACTURERS_QUERY = "/v1/car-types/manufacturer";


    @Given("^cars manufacturers list is grabbed$")
    public void getMapOfManufacturers(){
        getInstance()
                .executeGetRequest(MANUFACTURERS_QUERY,new HashMap<>())
                .statusCode(200);
    }
}
