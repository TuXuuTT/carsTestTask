package com.automation.testCucmbrRunners.api;


import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(features = "src/test/resources/features/CarsApiTest.feature",
        glue = "com.automation.stepDefinitions.api",
        plugin = {"html:build/reports/cucumber-report", "pretty"},
        strict = true,
        snippets = SnippetType.CAMELCASE,
        tags = {/*"@DEV"*/}) //TODO Remove tags before release, its for dev purposes only)
public class TestCarsSelectorRunner extends AbstractTestNGCucumberTests {
}
