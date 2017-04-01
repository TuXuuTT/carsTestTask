package com.automation.browserClient;

import com.automation.environment.EnvironmentConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;

public class BrowserClient {

    public static final int TIME_WAIT_SECONDS = 20;
    protected static final Logger LOGGER = LogManager.getLogger(BrowserClient.class);
    protected static EnvironmentConfigurator environmentConfigurator;
    private RemoteWebDriver webDriver;

    public BrowserClient() {
        environmentConfigurator = EnvironmentConfigurator.getInstance();
    }

    public static void maximizeWindow(WebDriver wd) {
        if (!OSUtils.isWindows()) {
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            Point position = new Point(0, 0);
            wd.manage().window().setPosition(position);
            Dimension maximizedScreenSize =
                    new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
            wd.manage().window().setSize(maximizedScreenSize);
        } else {
            wd.manage().window().maximize();
        }
    }

    public RemoteWebDriver getDriver(String clientName) throws MalformedURLException {
        BrowserClientType browserClientType = BrowserClientType.valueOf(clientName.toUpperCase());
        switch (browserClientType) {
            case IE:
                this.webDriver = startInternetExplorer();
                break;
            case FF:
                this.webDriver = startFirefox();
                break;
            case GC:
                this.webDriver = startChrome();
                break;
            default:
                this.webDriver = startChrome();
                break;
        }
        if (environmentConfigurator.isGridUsed()) {
            this.webDriver.setFileDetector(new LocalFileDetector());
        }
        maximizeWindow(this.webDriver);
        this.webDriver.manage().deleteAllCookies();
        return this.webDriver;
    }

    /**
     * Launches Internet Explorer natively or on the remote machine according to settings
     */
    private RemoteWebDriver startInternetExplorer() throws MalformedURLException {

        DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
        cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        cap.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
        cap.setCapability("ensureCleanSession", true);
        cap.setCapability("enableElementCacheCleanup", true);
        cap.setCapability("nativeEvents", true);
        cap.setCapability("enablePersistentHover", true);


        if (environmentConfigurator.isGridUsed()) {
            this.webDriver = new RemoteWebDriver(new URL("http://" + environmentConfigurator.getSeleniumHub() + "/wd/hub"), cap);
        } else {
            if (System.getProperty("webdriver.ie.driver") == null) {
                String driverPath = currentThread().getContextClassLoader().getResource("IEDriverServer.exe").getPath();
                LOGGER.warn("webdriver.ie.driver is not set. will now try to use [" + driverPath + "]");
                System.setProperty("webdriver.ie.driver", driverPath);
            }
            this.webDriver = new InternetExplorerDriver(cap);
        }
        return this.webDriver;
    }

    /**
     * Launches Chrome natively or on the remote machine according to settings
     */
    private RemoteWebDriver startChrome() throws MalformedURLException {
        OSUtils.killProcess("chromedriver.exe");
        if (environmentConfigurator.isGridUsed()) {
            DesiredCapabilities cap = DesiredCapabilities.chrome();
            this.webDriver = (new RemoteWebDriver(new URL("http://" + environmentConfigurator.getSeleniumHub() + "/wd/hub"), cap));
            System.setProperty("webdriver.chrome.logfile", System.getProperty("user.dir") + "/chromedriver.log");
        } else {
            if (System.getProperty("webdriver.chrome.driver") == null) {
                String chromeDriverName;
                String chromedriverPath;
                if (OSUtils.isWindows()) {
                    chromeDriverName = "chromedriver.exe";
                } else chromeDriverName = "chromedriver";
                {
//                there is a bug that sometimes compiles resources with worng permissions during MAC OS maven run
//                https://issues.apache.org/jira/browse/MRESOURCES-132
//                    chromedriverPath = currentThread().getContextClassLoader().getResource(chromeDriverName).getPath();
                }
                chromedriverPath = System.getProperty("user.dir") + "/src/main/resources/" + chromeDriverName;
                LOGGER.warn("webdriver.chrome.driver is not set. will now try to use [" + chromedriverPath + "]");
                System.setProperty("webdriver.chrome.driver", chromedriverPath);
            }
            this.webDriver = new ChromeDriver();
        }
        return this.webDriver;
    }

    /**
     * Launches Firefox natively or on the remote machine according to settings
     */
    private RemoteWebDriver startFirefox() throws MalformedURLException {
        FirefoxBinary fb = new FirefoxBinary();
        fb.setTimeout(SECONDS.toMillis(TIME_WAIT_SECONDS * 2));
        FirefoxProfile profile = new FirefoxProfile();
        if (!environmentConfigurator.isGridUsed()) {
            try {
                File firebugFile = new File(System.getProperty("user.dir") + "/firebug-2.0.4.xpi");
                File firepathFile = new File(System.getProperty("user.dir") + "/firepath-0.9.7-fx.xpi");
                if (firebugFile.canRead() && firepathFile.canRead()) {
                    profile.addExtension(firebugFile);
                    profile.addExtension(firepathFile);
                } else {
                    LOGGER.info("Firefox extensions: [firebug] [firepath] are not available");
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        profile.setEnableNativeEvents(false);
        profile.setPreference("dom.successive_dialog_time_limit", 0);
        profile.setPreference("dom.popup_maximum", 200000);
        profile.setPreference("app.update.enabled", false);

        try {
            if (environmentConfigurator.isGridUsed()) {
                DesiredCapabilities cap = DesiredCapabilities.firefox();
                cap.setCapability(FirefoxDriver.PROFILE, profile);
                this.webDriver = new RemoteWebDriver(new URL("http://" + environmentConfigurator.getSeleniumHub() + "/wd/hub"), cap);
            } else {
                this.webDriver = new FirefoxDriver(fb, profile);
            }
        } catch (WebDriverException e) {
            LOGGER.error("", e);
        }
        return this.webDriver;
    }

    public RemoteWebDriver getWebDriver() {
        return webDriver;
    }

}