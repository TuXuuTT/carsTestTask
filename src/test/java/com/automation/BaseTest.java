//package com.automation;
//
//import com.automation.browserClient.BrowserClient;
//import com.codeborne.selenide.Configuration;
//import com.codeborne.selenide.WebDriverRunner;
//import com.automation.environment.EnvironmentConfigurator;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.remote.RemoteWebDriver;
//import org.testng.IHookCallBack;
//import org.testng.IHookable;
//import org.testng.ITestResult;
//import org.testng.annotations.*;
//import ru.yandex.qatools.allure.annotations.Attachment;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.util.Random;
//
//public class BaseTest implements IHookable {
//    private static RemoteWebDriver wd = null;
//    protected final Logger LOGGER = LogManager.getLogger(this);
//
//    protected static void setSelenideConfigurations() {
//        Configuration.screenshots = false;
//        Configuration.timeout = 10000;
//    }
//
//    public RemoteWebDriver getPureWdInstance() {
//        return wd;
//    }
//
//    @BeforeClass(alwaysRun = true)
//    public void beforeClass() {
//        setSelenideConfigurations();
//        try {
//            wd = new BrowserClient().getDriver(EnvironmentConfigurator.getInstance().getBrowserClient());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            LOGGER.error(e);
//        }
//        WebDriverRunner.setWebDriver(wd);
//    }
//
//    @AfterClass(alwaysRun = true)
//    public void afterClass() {
//        if (wd != null) {
//            wd.quit();
//        }
//    }
//
//    @BeforeMethod(alwaysRun = true)
//    public void beforeMethodInit() throws MalformedURLException {
//    }
//
//    @AfterMethod(alwaysRun = true)
//    public void afterMethodStop(ITestResult testResult) throws IOException {
//        LOGGER.info("Test '{}' finished its execution", testResult.getMethod().getMethodName());
//    }
//
//    @AfterSuite(alwaysRun = true)
//    public void afterSuiteStop() {
//    }
//
//    @Override
//    public void run(IHookCallBack callBack, ITestResult testResult) {
//        callBack.runTestMethod(testResult);
//        if (testResult.getThrowable() != null) {
//            try {
//                takeScreenShot(testResult.getMethod().getMethodName());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Attachment(value = "Failure in method {0}", type = "image/png")
//    private byte[] takeScreenShot(String failureReason) throws IOException {
//        LOGGER.info(String.format("Taking screenshot due to fail in method %s", failureReason));
//        return ((TakesScreenshot) wd).getScreenshotAs(OutputType.BYTES);
//    }
//
//    protected String getRandomString(int length) {
//        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        Random rnd = new Random();
//
//        StringBuilder sb = new StringBuilder(length);
//        for (int i = 0; i < length; i++)
//            sb.append(AB.charAt(rnd.nextInt(AB.length())));
//        return sb.toString();
//    }
//}