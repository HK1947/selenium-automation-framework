package com.automation.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage - Page Object for Login functionality.
 *
 * Design Decisions:
 * 1. @FindBy annotations - Clean locator definition
 * 2. Meaningful method names - Reflects user actions
 * 3. Fluent interface - Methods return page objects for chaining
 * 4. Single Responsibility - Only handles login-related actions
 *
 * Example Usage:
 *   LoginPage loginPage = new LoginPage();
 *   loginPage.enterUsername("user")
 *            .enterPassword("pass")
 *            .clickLogin();
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public class LoginPage extends BasePage {

    // ==================== LOCATORS ====================
    // Using @FindBy for clean, maintainable locators
    // CSS selectors preferred for performance

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = ".error-message")
    private WebElement errorMessage;

    @FindBy(css = ".flash.success")
    private WebElement successMessage;

    @FindBy(linkText = "Logout")
    private WebElement logoutLink;

    // Alternative locators for the-internet.herokuapp.com
    @FindBy(css = "#username, input[name='username']")
    private WebElement usernameField;

    @FindBy(css = "#password, input[name='password']")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit'], .radius")
    private WebElement submitButton;

    @FindBy(css = "#flash, .flash")
    private WebElement flashMessage;

    // ==================== PAGE ACTIONS ====================

    /**
     * Enter username into the username field.
     *
     * @param username Username to enter
     * @return LoginPage for method chaining
     */
    public LoginPage enterUsername(String username) {
        logger.info("Entering username: {}", username);
        type(usernameField, username);
        return this;
    }

    /**
     * Enter password into the password field.
     *
     * @param password Password to enter
     * @return LoginPage for method chaining
     */
    public LoginPage enterPassword(String password) {
        logger.info("Entering password: ****");
        type(passwordField, password);
        return this;
    }

    /**
     * Click the login button.
     *
     * @return LoginPage for further assertions
     */
    public LoginPage clickLogin() {
        logger.info("Clicking login button");
        click(submitButton);
        waitForPageLoad();
        return this;
    }

    /**
     * Perform complete login action.
     * Convenience method combining all login steps.
     *
     * @param username Username
     * @param password Password
     * @return LoginPage for assertions
     */
    public LoginPage login(String username, String password) {
        logger.info("Performing login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }

    /**
     * Perform logout action.
     *
     * @return LoginPage after logout
     */
    public LoginPage logout() {
        logger.info("Performing logout");
        click(logoutLink);
        waitForPageLoad();
        return this;
    }

    // ==================== VERIFICATION METHODS ====================

    /**
     * Get error message text.
     *
     * @return Error message text
     */
    public String getErrorMessage() {
        waitForVisibility(flashMessage);
        String message = getText(flashMessage);
        logger.debug("Error message: {}", message);
        return message;
    }

    /**
     * Get success message text.
     *
     * @return Success message text
     */
    public String getSuccessMessage() {
        waitForVisibility(flashMessage);
        String message = getText(flashMessage);
        logger.debug("Success message: {}", message);
        return message;
    }

    /**
     * Check if error message is displayed.
     *
     * @return true if error is visible
     */
    public boolean isErrorDisplayed() {
        try {
            String message = getText(flashMessage);
            return message.toLowerCase().contains("invalid") ||
                   message.toLowerCase().contains("error");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if login was successful.
     *
     * @return true if success message or secure area is visible
     */
    public boolean isLoginSuccessful() {
        try {
            String message = getText(flashMessage);
            return message.toLowerCase().contains("logged in") ||
                   message.toLowerCase().contains("success");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if logout link is visible (indicates logged in state).
     *
     * @return true if logout link is visible
     */
    public boolean isLoggedIn() {
        return isDisplayed(logoutLink);
    }

    /**
     * Check if login page is displayed.
     *
     * @return true if on login page
     */
    public boolean isLoginPageDisplayed() {
        return isDisplayed(usernameField) && isDisplayed(passwordField);
    }

    /**
     * Get page title.
     *
     * @return Page title
     */
    public String getLoginPageTitle() {
        return getPageTitle();
    }
}
