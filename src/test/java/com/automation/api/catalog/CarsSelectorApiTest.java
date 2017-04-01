package com.automation.api.catalog;

import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.HashMap;

import static com.automation.api.RestApiBasicClient.getInstance;


public class CarsSelectorApiTest {

    private static final String MANUFACTURERS_QUERY = "/v1/car-types/manufacturer";

    @Features("Catalog API")
    @Stories({"MS-TC-466:get : List all categories"})
    @Test(groups = {"bvt"}, description = "MS-TC-466")
    public void getListAllCategoriesByAdmin() {
        getInstance()
                .executeGetRequest(MANUFACTURERS_QUERY,new HashMap<>())
                .statusCode(200)
                .log().all(true);
    }

}

