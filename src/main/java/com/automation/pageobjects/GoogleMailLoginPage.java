package com.automation.pageobjects;

import com.codeborne.selenide.Condition;
import com.automation.environment.EnvironmentConfigurator;
import org.openqa.selenium.By;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Selenide.*;

public class GoogleMailLoginPage extends BasePage {

    EnvironmentConfigurator environmentConfigurator = EnvironmentConfigurator.getInstance();
    private By inputEmail = By.cssSelector("#Email");
    private By buttonNext = By.cssSelector("#next");
    private By inputPassword = By.cssSelector("#Passwd");
    private By buttonSignIn = By.cssSelector("#signIn");

    private By divSecurityForm = By.cssSelector("div[token='security/interstitials/recoveryoptions']");
    private By buttonDone = By.cssSelector("div[role='button']");

    @Step
    public GmailInboxPage loginAsExistingUser() {
        performLogin();
        return proceedToInbox();
    }

    protected void performLogin() {
//        $(inputEmail).setValue(environmentConfigurator.getLogin()).pressEnter();
        $(buttonNext).shouldNotBe(Condition.visible);

//        $(inputPassword).setValue(environmentConfigurator.getPassword());
        $(buttonSignIn).click();
        $$(buttonSignIn).shouldHaveSize(0);

    }

    protected GmailInboxPage proceedToInbox() {
        if ($$(divSecurityForm).filter(Condition.visible).size() != 0) {
            $(divSecurityForm).$(buttonDone).click();
        }
        return page(GmailInboxPage.class);
    }

    public static class GmailInboxPage extends GoogleMailLoginPage {

        private By inboxButtonLink = By.cssSelector("a[href='https://mail.google.com/mail/u/0/#inbox']");

        @Step
        public void verifyInboxDisplayed() {
            $(inboxButtonLink).shouldBe(Condition.visible);
        }

    }
}
