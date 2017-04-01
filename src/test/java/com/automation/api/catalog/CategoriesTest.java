//package com.automation.api.catalog;
///**
// * Created by vasyl.stasiuk on 6/5/2015.
// */
//
//import com.bmc.myservice.testautomation.api.BaseApiTest;
//import com.bmc.myservice.testautomation.api.RestApiExecutor;
//import org.testng.annotations.Test;
//import ru.yandex.qatools.allure.annotations.Features;
//import ru.yandex.qatools.allure.annotations.Stories;
//
//import static com.bmc.myservice.testautomation.api.RestApiExecutor.CATEGORIES_REQUEST_URL;
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.CoreMatchers.hasItems;
//
//public class CategoriesTest extends BaseApiTest {
//
//    String[] categories = {"Business", "Storage", "Social", "Collaboration", "Education", "Finance", "Health", "Productivity", "Travel", "News"};
//
//    @Features("Catalog API")
//    @Stories({"MS-TC-466:get : List all categories"})
//    @Test(groups = {"bvt"}, description = "MS-TC-466")
//    public void getListAllCategoriesByAdmin() {
//        RestApiExecutor.getInstance()
//                .executeGetRequest(CATEGORIES_REQUEST_URL, getAdminUser())
//                .statusCode(200)
//                .body("pageSize", equalTo(-1), "total", equalTo(10),
//                        "categories.name", hasItems(categories));
//    }
//
//    @Features("Catalog API")
//    @Stories({"MS-TC-466:get : List all categories"})
//    @Test(groups = {"bvt"}, description = "MS-TC-466")
//    public void getListAllCategoriesBySupplier() {
//        RestApiExecutor.getInstance()
//                .executeGetRequest(CATEGORIES_REQUEST_URL, getSupplierAdminUser())
//                .statusCode(200)
//                .body("pageSize", equalTo(-1), "total", equalTo(10),
//                        "categories.name", hasItems(categories));
//    }
//
//
//    @Features("Catalog API")
//    @Stories({"MS-TC-466:get : List all categories"})
//    @Test(groups = {"bvt"}, description = "MS-TC-466")
//    public void getListAllCategoriesByAssetManager() {
//        RestApiExecutor.getInstance()
//                .executeGetRequest(CATEGORIES_REQUEST_URL, getAssetManagerUser())
//                .statusCode(200)
//                .body("pageSize", equalTo(-1), "total", equalTo(10),
//                        "categories.name", hasItems(categories));
//    }
//
//    @Features("Catalog API")
//    @Stories({"MS-TC-466:get : List all categories"})
//    @Test(groups = {"smoke"}, description = "MS-TC-466")
//    public void tryGetCategoryByIdByAdminWithWrongUrl() {
//        RestApiExecutor.getInstance()
//                .executeGetRequest(CATEGORIES_REQUEST_URL + "/2", getAdminUser())
//                .statusCode(422)
//                .body("message", equalTo("HTTP 404 Not Found"));
//    }
//
//
//}