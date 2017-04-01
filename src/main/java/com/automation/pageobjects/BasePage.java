package com.automation.pageobjects;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.automation.browserClient.BrowserClient;
import com.automation.environment.EnvironmentConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;


public abstract class BasePage {
    protected final Logger LOGGER = LogManager.getLogger(this);
    private final WebDriver wd;

    public BasePage() {
        wd = WebDriverRunner.getWebDriver();
    }

    public static String getPageURL() {
        return EnvironmentConfigurator.getInstance().getApiUrl();
    }

    protected WebDriver getWebDriverCurrent() {
        return wd;
    }

    protected void openUrl(String url) {
        Selenide.open(url);
        LOGGER.info("Loading page: {}", url);
    }

    public void setWebDriverWindowSize(int width, int height) {
        wd.manage().window().setPosition(new Point(0, 0));
        wd.manage().window().setSize(new Dimension(width, height));
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Dimension getWebDriverWindowsSize() {
        return wd.manage().window().getSize();
    }

    protected Object executeJS(final String script, final Object... params) {
        return ((JavascriptExecutor) wd).executeScript(script, params);
    }

    public int getPageBodyWidth() {
        return Integer.parseInt(executeJS("return document.body.clientWidth").toString());
    }

    public void maximizeWebdriverWindow() {
        BrowserClient.maximizeWindow(wd);
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void waitForElementStopMoving(WebElement element) {
        Point a;
        Point b;
        do {
            a = element.getLocation();
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            b = element.getLocation();
        } while (!a.equals(b));
    }
}
