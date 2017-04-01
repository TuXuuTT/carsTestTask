//package com.ui.automation.api;
//
//import com.bmc.myservice.testautomation.api.restGuiApp.ServiceTypes;
//import com.bmc.myservice.testautomation.businessobjects.*;
//import com.bmc.myservice.testautomation.businessobjects.service.Service;
//import com.bmc.myservice.testautomation.businessobjects.service.ServicePricingModelOneTimePayment;
//import com.bmc.myservice.testautomation.businessobjects.service.ServicePricingModelRecurringPayment;
//import com.bmc.myservice.testautomation.environment.EnvironmentConfigurator;
//import com.jayway.restassured.response.ValidatableResponse;
//
//import java.io.File;
//import java.util.*;
//
//import static com.jayway.restassured.path.json.JsonPath.from;
//
//public class RestApiExecutor extends RestApiBasicClient {
//
//    public static final String PROCESS_DEFINITION_CREATE_URL = API_HOST + "/api/rx/application/process/processdefinition";
//    public static final String RATINGS_REQUEST_URL = API_HOST + "/api/sbe/ratings";
//    public static final String REVIEWS_REQUEST_URL = API_HOST + "/api/sbe/reviews";
//    public static final String SERVICE_REQUEST_URL = API_HOST + "/api/sbe/services"; //used only wit id /services/{serviceId}
//    public static final String SERVICES_SEARCH_REQUEST_URL = API_HOST + "/api/sbe/services/search/";
//    public static final String CATEGORIES_REQUEST_URL = API_HOST + "/api/sbe/categories";
//    public static final String VIRTUAL_MP_REQUEST_URL = API_HOST + "/api/sbe/virtualmarketplaces";
//    public static final String SUPPLIERS_REQUEST_URI = BASE_GUI_URI + "/companies.json";
//    public static final String SERVICES_REQUEST_URI = BASE_GUI_URI + "/products";
//    public static final String SERVICES_REVISION_LOGO_URI = BASE_GUI_URI + "/revision_logos";
//    public static final String SERVICES_ATTACHED_MEDIA_URI = BASE_GUI_URI + "/media";
//    public static final String PRODUCT_TYPES_URI = BASE_GUI_URI + "/product_types";
//    public static final String FIELD_SERVICE_ID = "serviceId";
//    public static final String FIELD_TITLE = "title";
//    public static final String FIELD_CONTENT = "content";
//    public static final String FIELD_USER_ID = "userId";
//    public static final String SIMPLE_WORKFLOW_JSON = "/testdata/myservice/json/simple_wokflow.json";
//    protected static final String QUESTIONNAIRE_JSON_FOR_SIMPLE_WORKFLOW = "/testdata/myservice/json/questionnaire_for_simple_workflow.json";
//    private static final String PATH_TO_WORKFLOW_JSON_FILE = "/data/json/simple_wokflow.json";
//    private static final String PATH_TO_QUESTIONNAIRE_JSON_FILE = "/data/json/questionnaire_for_simple_workflow.json";
//    private static volatile RestApiExecutor restApiExecutor;
//    private User adminUser;
//    private User supplierAdminUser;
//    private User assetManagerUser;
//
//    private RestApiExecutor() {
//        super();
//        adminUser = new User(EnvironmentConfigurator.getInstance().getAdminLogin(), EnvironmentConfigurator.getInstance().getPassword()).setRole(UserRole.ADMINISTRATOR.getValue());
//        supplierAdminUser = new User(EnvironmentConfigurator.getInstance().getSupplierAdminLogin(), EnvironmentConfigurator.getInstance().getPassword()).setRole(UserRole.SERVICE_SUPPLIER.getValue());
//        assetManagerUser = new User(EnvironmentConfigurator.getInstance().getAssetManagerLogin(), EnvironmentConfigurator.getInstance().getPassword()).setRole(UserRole.ASSET_MANAGER.getValue());
//        loginAllRequiredUsers();
//    }
//
//    public static RestApiExecutor getInstance() {
//
//        RestApiExecutor sysProps = restApiExecutor;
//        if (sysProps == null) {
//            synchronized (RestApiExecutor.class) {
//                sysProps = restApiExecutor;
//                if (sysProps == null) {
//                    restApiExecutor = sysProps = new RestApiExecutor();
//                }
//            }
//        }
//        return sysProps;
//    }
//
//    public void loginAllRequiredUsers() {
//        executeLogin(adminUser);
//        executeLogin(supplierAdminUser);
//        executeLogin(assetManagerUser);
//        executeLoginGuiApp(adminUser);
//        executeLoginGuiApp(supplierAdminUser);
//        executeLoginGuiApp(assetManagerUser);
//    }
//
//    /**
//     * @param user              takes a logged in user as argument to operate with his cookies and auth tokens
//     * @param service           takes created service entity. May contain different name, description, logo, attachments images, video links, price and categories settings. Whatever service entity can contain will be present in result of this service creation by api
//     * @param shouldBeSubmitted specify 'true' in most of cases. If 'false' - only draft service without content will be created
//     * @return gives back service entity which is created, filled with specified data and submitted and\or published
//     */
//
//    public Service createService(User user, Service service, boolean shouldBeSubmitted) {
//        Map<String, String> query = new HashMap<>();
//        query.put("format", "json");
//        query.put("myservice", "true");
//
//        String serviceTypeId = getServiceTypeId(user, service, query);
//
//        String newServiceBodyText = String.format("{\"product\":{\"name\":\"%s\",\"product_type_id\":\"%s\"},\"product_version\":{\"name\":\"%s\"}}", service.getName(), serviceTypeId, service.getVersion());
//        ValidatableResponse responseCreateDraft = executePostRequestGuiApp(SERVICES_REQUEST_URI, user, newServiceBodyText, query).statusCode(200);
//
//        service.setServiceID(responseCreateDraft.extract().jsonPath().getString("resource.id"));
//        service.setServiceVersionId(responseCreateDraft.extract().jsonPath().getString("resource.new_product_version_id"));
//        service.setServiceRevisionID(responseCreateDraft.extract().jsonPath().getString("resource.new_product_version_revision_id"));
//
//        String serviceLogoId = getServiceLogoId(user, service, query);
//
//        String servicePaymentTypeJsonString = "\"Free\",\"price\":null,\"currency\":null,\"payment_schedule\":null";
//        if (!service.getServicePricingModel().getServicePricingModelName().toLowerCase().equals("free")) {
//            if (service.getServicePricingModel().getServicePricingModelName().toLowerCase().equals("once")) {
//                servicePaymentTypeJsonString = String.format("\"%s\",\"price\":\"%s\",\"currency\":\"%s\",\"payment_schedule\":null", service.getServicePricingModel().getServicePricingModelName(), ((ServicePricingModelOneTimePayment) service.getServicePricingModel()).getSummOfPayment(), ((ServicePricingModelOneTimePayment) service.getServicePricingModel()).getCurrency());
//            } else if (service.getServicePricingModel().getServicePricingModelName().toLowerCase().equals("recurring")) {
//                servicePaymentTypeJsonString = String.format("\"%s\",\"price\":\"%s\",\"currency\":\"%s\",\"payment_schedule\":\"%s\"", service.getServicePricingModel().getServicePricingModelName(), ((ServicePricingModelRecurringPayment) service.getServicePricingModel()).getSummOfPayment(), ((ServicePricingModelRecurringPayment) service.getServicePricingModel()).getCurrency(), ((ServicePricingModelRecurringPayment) service.getServicePricingModel()).getPeriod());
//            }
//        }
//
//        ValidatableResponse responseGetEditServiceBeforeSave = getServiceEditForm(user, service);
//        String servicePricesAttr = responseGetEditServiceBeforeSave.extract().jsonPath().getString("resource.payments.id[0]");
//        String excerptCatId = responseGetEditServiceBeforeSave.extract().jsonPath().getString("form.fields[0].id");
//        String descriptionCatId;
//
//        if (service.getType().equals(ServiceTypes.IT_REQUEST.getTypeName())) {
//            descriptionCatId = responseGetEditServiceBeforeSave.extract().jsonPath().getString("form.fields[1].id");
//        } else {
//            descriptionCatId = responseGetEditServiceBeforeSave.extract().jsonPath().getString("form.fields[2].id");
//        }
//
//        String saveServiceBodyText = String.format("{\"id\":\"%s\",\"product_version_revision\":{\"logo_id\":\"%s\",\"product_type_field_values_attributes\":{\"" + excerptCatId + "\":{\"product_type_field_id\":\"" + excerptCatId + "\",\"id\":null,\"value\":[\"\"]},\"" + descriptionCatId + "\":{\"product_type_field_id\":\"" + descriptionCatId + "\",\"id\":null,\"value\":[\"\"]}},\"revision_media_attributes\":{},\"document_ids\":[\"\"],\"category_ids\":[%s],\"prices_attributes\":[{\"id\":\"%s\",\"payment_type\":" + servicePaymentTypeJsonString + "}],\"tags\":[],\"connector_id\":null}}",
//                service.getServiceRevisionID(),
//                serviceLogoId,
//                getCategoriesIdsAsStringFromEnv(user, service),
//                servicePricesAttr);
//        RestApiExecutor.getInstance().executePutRequestGuiApp(String.format(SERVICES_REQUEST_URI + "/%s/product_versions/%s/product_version_revisions/%s", service.getServiceID(), service.getServiceVersionId(), service.getServiceRevisionID()), user, saveServiceBodyText, query).statusCode(200);
//
//        ValidatableResponse responseGetEditServiceAfterSave;
//        String serviceExcerptValueId;
//        int i = 0;
//        do {
//            responseGetEditServiceAfterSave = getServiceEditForm(user, service);
//            serviceExcerptValueId = responseGetEditServiceAfterSave.extract().jsonPath().getString("form.fields[0].values.id");
//            i++;
//        }
//        while (serviceExcerptValueId == null && i < 5);
//
//        String serviceDescriptionValueId;
//        if (service.getType().equals(ServiceTypes.IT_REQUEST.getTypeName())) {
//            serviceDescriptionValueId = responseGetEditServiceAfterSave.extract().jsonPath().getString("form.fields[1].values.id");
//        } else {
//            serviceDescriptionValueId = responseGetEditServiceAfterSave.extract().jsonPath().getString("form.fields[2].values.id");
//        }
//
//        String submitServiceBodyText = String.format("{\"product_version_revision\":{\"logo_id\":\"%s\"," +
//                        "\"product_type_field_values_attributes\":{\"" + excerptCatId + "\":{\"product_type_field_id\":\"" + excerptCatId + "\",\"id\":\"" + serviceExcerptValueId + "\", \"value\":[\"service excerpt\"]},\"" + descriptionCatId + "\":{\"product_type_field_id\":\"" + descriptionCatId + "\",\"id\":\"" + serviceDescriptionValueId + "\",\"value\":[\"<p>service description</p>\"]}},\"revision_media_attributes\":{},\"document_ids\":[\"\"]," +
//                        "\"category_ids\":[%s]," +
//                        "\"prices_attributes\":[{\"id\":\"%s\",\"payment_type\":" + servicePaymentTypeJsonString + "}],\"tags\":[],\"connector_id\":null}}",
//                serviceLogoId,
//                getCategoriesIdsAsStringFromEnv(user, service),
//                servicePricesAttr);
//
//
//        if (service.getImageFiles().size() > 0 || service.getVideoLinks().size() > 0) {
//            List<String> mediaAttributesObjs = new ArrayList<>();
//            List<String> mediaList = getAttachmentMediaIds(user, service);
//            for (String mediaId : mediaList) {
//                mediaAttributesObjs.add(String.format("\"%s\":{\"id\":null,\"medium_id\":\"%s\",\"position\":\"%s\"}", mediaList.indexOf(mediaId), mediaId, mediaList.indexOf(mediaId) + 1));
//            }
//            String mediaAttribute = String.format("%s", String.join(",", mediaAttributesObjs));
//            String updatedServiceBodyText = String.format("{\"id\":\"%s\",\"product_version_revision\":{\"logo_id\":\"%s\",\"product_type_field_values_attributes\":{\"" + excerptCatId + "\":{\"product_type_field_id\":\"" + excerptCatId + "\",\"id\":\"%s\",\"value\":[\"excerpt here\"]},\"" + descriptionCatId + "\":{\"product_type_field_id\":\"" + descriptionCatId + "\",\"id\":\"%s\",\"value\":[\"description here\"]}},\"revision_media_attributes\":{" + mediaAttribute + "},\"document_ids\":[\"\"],\"category_ids\":[%s],\"prices_attributes\":[{\"id\":\"%s\",\"payment_type\":" + servicePaymentTypeJsonString + "}],\"tags\":[],\"connector_id\":null}}", service.getServiceRevisionID(), serviceLogoId, serviceExcerptValueId, serviceDescriptionValueId, getCategoriesIdsAsStringFromEnv(user, service), servicePricesAttr);
//            ValidatableResponse saveUpdatedBodyText = RestApiExecutor.getInstance().executePutRequestGuiApp(String.format(SERVICES_REQUEST_URI + "/%s/product_versions/%s/product_version_revisions/%s", service.getServiceID(), service.getServiceVersionId(), service.getServiceRevisionID()), user, updatedServiceBodyText, query);
//            saveUpdatedBodyText.statusCode(200);
//        }
//
////        attachWorkflowToService(service, workflow);
////        saveQuestionnaireForService(service, questionnaire);
//
//        if (shouldBeSubmitted) {
//            submitServiceBodyText = submitServiceBodyText.replace("\"connector_id\":null", "\"connector_id\":null,\"state\":\"pending\"");
//            ValidatableResponse responseSavedService = RestApiExecutor.getInstance().executePutRequestGuiApp(String.format(SERVICES_REQUEST_URI + "/%s/product_versions/%s/product_version_revisions/%s", service.getServiceID(), service.getServiceVersionId(), service.getServiceRevisionID()), user, submitServiceBodyText, query);
//            responseSavedService.statusCode(200);
//            service.setState("PENDING");
//        }
//        return service;
//    }
//
//    public SupplierCompany createSupplier(SupplierCompany supplierCompany) {
//        Map<String, String> query = new HashMap<>();
//        query.put("format", "json");
//        query.put("myservice", "true");
//
//        supplierCompany.setId(
//                executePostRequestGuiApp(GUI_COMPANIES, getAdminUser(), String.format("{\"name\":\"%s\"}", supplierCompany.getName()))
//                        .extract().jsonPath().getString("resource.id")
//        );
//
//        String serviceLogoId = getCompanyLogoId(getAdminUser(), supplierCompany, query);
//        executePutRequestGuiApp(
//                GUI_COMPANIES + "/" + supplierCompany.getId(),
//                getAdminUser(),
//                String.format("{\"id\":\"%s\",\"logo_id\":\"%s\",\"name\":\"%s\",\"description\":\"description\"}", supplierCompany.getId(), serviceLogoId, supplierCompany.getName()),
//                query);
//
//        return supplierCompany;
//    }
//
//    public Service createPublishedService(User serviceSubmitter, Service service, Workflow workflow, Questionnaire questionnaire) {
//        RestApiExecutor.getInstance().createService(serviceSubmitter, service, true);
//        RestApiExecutor.getInstance().attachWorkflowToService(service, workflow);
//        RestApiExecutor.getInstance().saveQuestionnaireForService(service, questionnaire);
//        RestApiExecutor.getInstance().publishService(getAdminUser(), service);
//        return service;
//    }
//
//    public Service createPublishedService(User serviceSubmitter, Service service) {
//        Workflow workflow = new Workflow();
//        workflow.setWorkflowDefinition(SIMPLE_WORKFLOW_JSON);
//        RestApiExecutor.getInstance().createWorkflow(workflow);
//
//        Questionnaire questionnaire = new Questionnaire();
//        questionnaire.setQuestionnaireDefinition(QUESTIONNAIRE_JSON_FOR_SIMPLE_WORKFLOW);
//
//        RestApiExecutor.getInstance().createService(serviceSubmitter, service, true);
//        RestApiExecutor.getInstance().attachWorkflowToService(service, workflow);
//        RestApiExecutor.getInstance().saveQuestionnaireForService(service, questionnaire);
//        RestApiExecutor.getInstance().publishService(getAdminUser(), service);
//        return service;
//    }
//
//    public Service createSubmittedService(User serviceSubmitter, Service service) {
//        RestApiExecutor.getInstance().createService(serviceSubmitter, service, true);
//        return service;
//    }
//
//    private String getCategoriesIdsAsStringFromEnv(User user, Service service) {
//        List<String> serviceCategoriesIds = new ArrayList<>();
//
//        for (Categories category : getCategoryList(user)) {
//            if (service.getCategoriesNames().contains(category.getName())) {
//                serviceCategoriesIds.add(category.getId());
//            }
//        }
//        return String.format("\"%s\"", String.join("\",\"", serviceCategoriesIds));
//    }
//
//    private List<Categories> getCategoryList(User user) {
//        return RestApiExecutor.getInstance()
//                .executeGetRequest(CATEGORIES_REQUEST_URL, user)
//                .statusCode(200).extract().as(CategoriesList.class).getCategories();
//    }
//
//    public ValidatableResponse getServiceEditForm(User user, Service service) {
//        Map<String, String> query = new HashMap<>();
//        query.put("format", "json");
//        query.put("myservice", "true");
//        return RestApiExecutor.getInstance().executeGetRequestGuiApp(String.format(SERVICES_REQUEST_URI + "/%s/product_versions/%s/product_version_revisions/%s/edit", service.getServiceID(), service.getServiceVersionId(), service.getServiceRevisionID()), user, query).statusCode(200);
//    }
//
//    public void publishService(User user, Service service) {
//        Map<String, String> query = new HashMap<>();
//        query.put("format", "json");
//        query.put("myservice", "true");
//        String approveBodyText = String.format("{\"revisionIds\":[\"%s\"],\"newState\":\"approved\",\"bundleStatusChanges\":null}", service.getServiceRevisionID());
//        ValidatableResponse responseApprovePublishService = RestApiExecutor.getInstance().executePutRequestGuiApp(SERVICE_REQUEST_URL + "/changeState", user, approveBodyText, query);
//        responseApprovePublishService.statusCode(204);
//        service.setState("PUBLISHED");
//    }
//
//    private String getServiceLogoId(User user, Service service, Map<String, String> query) {
//        ValidatableResponse responseAddLogo = RestApiExecutor.getInstance().executePostFileGuiApp(SERVICES_REVISION_LOGO_URI, user, query, service.getServiceLogoFile(), "logo").statusCode(200);
//        return responseAddLogo.extract().jsonPath().getString("resource.id");
//    }
//
//    private String getCompanyLogoId(User user, SupplierCompany company, Map<String, String> query) {
//        ValidatableResponse responseAddLogo = RestApiExecutor.getInstance().executePostFileGuiApp(GUI_COMPANY_LOGO, user, query, company.getLogo(), "logo").statusCode(200);
//        return responseAddLogo.extract().jsonPath().getString("resource.id");
//    }
//
//    private List<String> getAttachmentMediaIds(User user, Service service) {
//        Map<String, String> query = new HashMap<>();
//        query.put("format", "json");
//        query.put("myservice", "true");
//        List<String> attachmentIds = new ArrayList<>();
//        for (File image : service.getImageFiles()) {
//            ValidatableResponse responseAddImages = RestApiExecutor.getInstance().executePostFileGuiApp(SERVICES_ATTACHED_MEDIA_URI, user, query, image, "medium[image]").statusCode(200);
//            attachmentIds.add(responseAddImages.extract().jsonPath().getString("resource.id"));
//        }
//        for (String link : service.getVideoLinks()) {
//            String bodyText = String.format("{\"video_url\":\"%s\"}", link);
//            ValidatableResponse responseAddVideos = RestApiExecutor.getInstance().executePostRequestGuiApp(SERVICES_ATTACHED_MEDIA_URI, user, bodyText, query).statusCode(200);
//            attachmentIds.add(responseAddVideos.extract().jsonPath().getString("resource.id"));
//        }
//        return attachmentIds;
//    }
//
//    private String getServiceTypeId(User user, Service service, Map<String, String> query) {
//        ValidatableResponse responseGetServiceTypes = executeGetRequestGuiApp(PRODUCT_TYPES_URI, user, query).statusCode(200);
//        String serviceTypeId = "";
//
//        for (Object resource : responseGetServiceTypes.extract().jsonPath().getList("resources")) {
//            if (((Map) resource).get("name").toString().toLowerCase().equals(service.getType().toLowerCase())) {
//                serviceTypeId = ((Map) resource).get("id").toString();
//                break;
//            }
//        }
//        if (serviceTypeId.equals("")) {
//            throw new IllegalArgumentException("Specified service type name is not defined");
//        }
//        return serviceTypeId;
//    }
//
//    public Workflow createWorkflow(Workflow workflow) {
//        //save workflow
//        getInstance()
//                .executePostRequest(PROCESS_DEFINITION_CREATE_URL, adminUser, workflow.getWorkflowJsonDefinition())
//                .statusCode(201);
//        // deploy workflow
//        RestApiExecutor.getInstance().executePostRequest(AUTH_URL, adminUser, String.format("{\"resourceType\":\"com.bmc.arsys.rx.application.process.command.DeployProcessDefinitionCommand\",\"processName\":\"rxn:/myit-sb/%s\"}", workflow.getName()))
//                .statusCode(204);
//        return workflow;
//    }
//
//    public void saveQuestionnaireForService(Service service, Questionnaire questionnaire) {
//        questionnaire.setId(RestApiExecutor.getInstance().executePostRequest(SERVICE_REQUEST_URL + "/" + service.getServiceVersionId() + "/revisions/" + service.getServiceRevisionID() + "/questionnaire", adminUser, questionnaire.getQuestionnaireDefinition()).statusCode(201).extract().path("id").toString());
//        service.setQuestionnaire(questionnaire);
//    }
//
//    public void attachWorkflowToService(Service service, Workflow workflow) {
//        String bodyText =
//                String.format("{\"workflowId\":\"%s\"}", workflow.getId());
//
//        RestApiExecutor.getInstance()
//                .executePutRequest(SERVICE_REQUEST_URL + "/" + service.getServiceVersionId() + "/revisions/" + service.getServiceRevisionID() + "/workflow", adminUser, bodyText)
//                .statusCode(200);
//
//    }
//
//    public List<String> getSupplierUsers(User user) {
//        Map queryParams = new HashMap<>();
//        queryParams.put("format", "json");
//        queryParams.put("myservice", "true");
//        queryParams.put("order", "full_name ASC");
//        queryParams.put("page", "1");
//        queryParams.put("per_page", "1000");
//        queryParams.put("q", "");
//        queryParams.put("role[]", "customer");
//        return from(RestApiExecutor.getInstance()
//                .executeGetRequestGuiApp(RestApiExecutor.BASE_GUI_URI + "/users", user, queryParams)
//                .statusCode(200)
//                .extract().body().asString()).get("resources.email");
//    }
//
//    public Integer getLastServiceIndexByNamePattern(List<User> users, String namePattern) {
//        Map queryParams = new HashMap<>();
//        queryParams.put("format", "json");
//        queryParams.put("myservice", "true");
//        queryParams.put("order", "updated_at desc");
//        queryParams.put("page", "1");
//        queryParams.put("per_page", "10");
//        queryParams.put("q", namePattern);
//
//
//        HashSet<String> serviceNames = new HashSet<>();
//
//        for (User user : users) {
//            serviceNames.addAll(from(RestApiExecutor.getInstance()
//                    .executeGetRequestGuiApp(RestApiExecutor.BASE_GUI_URI + "/products/", user, queryParams)
//                    .statusCode(200)
//                    .extract().body().asString()).get("resources.name"));
//            if (user.getRole().equals(UserRole.ADMINISTRATOR.getValue())) {
//                serviceNames.addAll(
//                        from(RestApiExecutor.getInstance()
//                                .executeGetRequestGuiApp(RestApiExecutor.BASE_GUI_URI + "/products/revisions", user, queryParams)
//                                .statusCode(200)
//                                .extract().body().asString()).get("resources.name"));
//            }
//        }
//        if (serviceNames.isEmpty()) {
//            return 0;
//        }
//
//        List<Integer> serviceIndexes = new ArrayList<>();
//        for (String serviceName : serviceNames) {
//            serviceIndexes.add(Integer.parseInt(serviceName.replace(namePattern, "").toString()));
//        }
//        if (serviceIndexes.isEmpty()) {
//            return 0;
//        }
//
//        return Integer.valueOf(Collections.max(serviceIndexes));
//    }
//
//    public Integer getLastSupplierIndexByNamePattern(String namePattern) {
//        Map queryParams = new HashMap<>();
//        queryParams.put("format", "json");
//        queryParams.put("myservice", "true");
//        queryParams.put("order", "created_at desc");
//        queryParams.put("page", "1");
//        queryParams.put("per_page", "100");
//        queryParams.put("q", namePattern);
//
//        HashSet<String> supplierNames = new HashSet<>();
//        supplierNames.addAll(from(RestApiExecutor.getInstance()
//                .executeGetRequestGuiApp(GUI_COMPANIES, getAdminUser(), queryParams)
//                .statusCode(200)
//                .extract().body().asString()).get("resources.name"));
//
//        if (supplierNames.isEmpty()) {
//            return 0;
//        }
//
//        List<Integer> supplierIndexes = new ArrayList<>();
//        for (String serviceName : supplierNames) {
//            try {
//                supplierIndexes.add(Integer.parseInt(serviceName.replace(namePattern, "").toString()));
//            } catch (NumberFormatException nfe) {
//                continue;
//            }
//        }
//        if (supplierIndexes.isEmpty()) {
//            return 0;
//        }
//        return Integer.valueOf(Collections.max(supplierIndexes));
//    }
//
//
//    public List<String> getPublishedServices(User user) {
//        Map queryParams = new HashMap<>();
//        queryParams.put("format", "json");
//        queryParams.put("myservice", "true");
//        queryParams.put("order", "name ASC");
//        queryParams.put("page", "1");
//        queryParams.put("per_page", "1000");
//        queryParams.put("q", "");
//        queryParams.put("small", "true");
//        queryParams.put("state", "published");
//        return from(RestApiExecutor.getInstance()
//                .executeGetRequestGuiApp(RestApiExecutor.BASE_GUI_URI + "/products/revisions", user, queryParams)
//                .statusCode(200)
//                .extract().body().asString()).get("resources.id");
//
//    }
//
//    public VirtualMarketPlace createVirtualMarketPlace(User user) {
//        return RestApiExecutor.getInstance()
//                .executePostRequest(VIRTUAL_MP_REQUEST_URL, user, String.format("{\"name\": \"%s\"}", "Vm" + new Date().getTime()))
//                .statusCode(201)
//                .extract().as(VirtualMarketPlace.class);
//    }
//
//    public void addUsersToVirtualMarketPlace(User user, VirtualMarketPlace virtualMarketPlace) {
//        RestApiExecutor.getInstance()
//                .executePostRequest(VIRTUAL_MP_REQUEST_URL + "/" + virtualMarketPlace.getId() + "/users", user,
//                        "{\"userIDs\":[\"" + String.join("\",\"", RestApiExecutor.getInstance().getSupplierUsers(user)) + "\"]}")
//                .statusCode(200);
//    }
//
//
//    public void addServicesToVirtualMarketPlace(User user, VirtualMarketPlace virtualMarketPlace) {
//        RestApiExecutor.getInstance()
//                .executePostRequest(VIRTUAL_MP_REQUEST_URL + "/" + virtualMarketPlace.getId() + "/services", user,
//                        "{\"serviceIDs\":[\"" + String.join("\",\"", RestApiExecutor.getInstance().getPublishedServices(user)) + "\"]}")
//                .statusCode(200);
//    }
//
//
//    public User getAdminUser() {
//        return adminUser;
//    }
//
//    public void setAdminUser(User adminUser) {
//        this.adminUser = adminUser;
//    }
//
//    public User getSupplierAdminUser() {
//        return supplierAdminUser;
//    }
//
//    public void setSupplierAdminUser(User supplierAdminUser) {
//        this.supplierAdminUser = supplierAdminUser;
//    }
//
//    public User getAssetManagerUser() {
//        return assetManagerUser;
//    }
//
//    public void setAssetManagerUser(User assetManagerUser) {
//        this.assetManagerUser = assetManagerUser;
//    }
//}
