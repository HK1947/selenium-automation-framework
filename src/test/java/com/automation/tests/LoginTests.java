package com.automation.tests;

import com.automation.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginTests - Test class for Login functionality.
 *
 * Design Decisions:
 * 1. AAA Pattern - Arrange, Act, Assert structure
 * 2. DataProvider - Parameterized tests for multiple scenarios
 * 3. Descriptive names - Test names describe expected behavior
 * 4. Single responsibility - Each test validates one thing
 *
 * Test Strategy:
 * - Positive tests: Valid login scenarios
 * - Negative tests: Invalid credentials, empty fields
 * - Edge cases: Special characters, long inputs
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public class LoginTests extends BaseTest {

    private LoginPage loginPage;

    /**
     * Initialize page object before each test.
     * Ensures clean state for every test.
     */
    @BeforeMethod
    public void initPage() {
        loginPage = new LoginPage();
    }

    // ==================== POSITIVE TESTS ====================

    /**
     * Test: Successful login with valid credentials.
     *
     * Given: User is on login page
     * When: User enters valid username and password
     * Then: User is logged in successfully
     */
    @Test(description = "Verify successful login with valid credentials",
          groups = {"smoke", "regression"})
    public void testValidLogin() {
        // Arrange
        String username = config.get("login.valid.username", "tomsmith");
        String password = config.get("login.valid.password", "SuperSecretPassword!");

        // Act
        loginPage.login(username, password);

        // Assert
        assertThat(loginPage.isLoginSuccessful())
                .as("Login should be successful with valid credentials")
                .isTrue();

        logger.info("Valid login test passed");
    }

    /**
     * Test: Successful logout after login.
     *
     * Given: User is logged in
     * When: User clicks logout
     * Then: User is logged out and sees login page
     */
    @Test(description = "Verify successful logout",
          groups = {"smoke", "regression"},
          dependsOnMethods = "testValidLogin")
    public void testLogout() {
        // Arrange - Login first
        String username = config.get("login.valid.username", "tomsmith");
        String password = config.get("login.valid.password", "SuperSecretPassword!");
        loginPage.login(username, password);

        // Act
        loginPage.logout();

        // Assert
        assertThat(loginPage.isLoginPageDisplayed())
                .as("Login page should be displayed after logout")
                .isTrue();

        logger.info("Logout test passed");
    }

    // ==================== NEGATIVE TESTS ====================

    /**
     * Test: Login fails with invalid username.
     *
     * Given: User is on login page
     * When: User enters invalid username with valid password
     * Then: Error message is displayed
     */
    @Test(description = "Verify error message for invalid username",
          groups = {"regression", "negative"})
    public void testInvalidUsername() {
        // Arrange
        String invalidUsername = "invaliduser";
        String validPassword = config.get("login.valid.password", "SuperSecretPassword!");

        // Act
        loginPage.login(invalidUsername, validPassword);

        // Assert
        assertThat(loginPage.isErrorDisplayed())
                .as("Error should be displayed for invalid username")
                .isTrue();

        String errorMessage = loginPage.getErrorMessage();
        assertThat(errorMessage.toLowerCase())
                .as("Error message should indicate invalid username")
                .contains("invalid");

        logger.info("Invalid username test passed");
    }

    /**
     * Test: Login fails with invalid password.
     *
     * Given: User is on login page
     * When: User enters valid username with invalid password
     * Then: Error message is displayed
     */
    @Test(description = "Verify error message for invalid password",
          groups = {"regression", "negative"})
    public void testInvalidPassword() {
        // Arrange
        String validUsername = config.get("login.valid.username", "tomsmith");
        String invalidPassword = "wrongpassword";

        // Act
        loginPage.login(validUsername, invalidPassword);

        // Assert
        assertThat(loginPage.isErrorDisplayed())
                .as("Error should be displayed for invalid password")
                .isTrue();

        logger.info("Invalid password test passed");
    }

    /**
     * Test: Login fails with empty credentials.
     *
     * Given: User is on login page
     * When: User clicks login without entering credentials
     * Then: Error message is displayed
     */
    @Test(description = "Verify error message for empty credentials",
          groups = {"regression", "negative"})
    public void testEmptyCredentials() {
        // Arrange - no credentials needed

        // Act
        loginPage.clickLogin();

        // Assert
        assertThat(loginPage.isErrorDisplayed())
                .as("Error should be displayed for empty credentials")
                .isTrue();

        logger.info("Empty credentials test passed");
    }

    // ==================== DATA-DRIVEN TESTS ====================

    /**
     * DataProvider for invalid login scenarios.
     * Tests multiple invalid credential combinations.
     */
    @DataProvider(name = "invalidCredentials")
    public Object[][] getInvalidCredentials() {
        return new Object[][] {
            {"", "", "Both empty"},
            {"validuser", "", "Empty password"},
            {"", "validpass", "Empty username"},
            {"user@#$%", "pass", "Special characters in username"},
            {"a".repeat(100), "pass", "Very long username"},
            {"user", "a".repeat(100), "Very long password"},
            {"admin", "admin", "Common weak credentials"},
            {" tomsmith", "SuperSecretPassword!", "Username with leading space"},
            {"tomsmith ", "SuperSecretPassword!", "Username with trailing space"}
        };
    }

    /**
     * Test: Parameterized test for various invalid login scenarios.
     *
     * @param username Test username
     * @param password Test password
     * @param scenario Description of test scenario
     */
    @Test(dataProvider = "invalidCredentials",
          description = "Verify login fails for invalid credential combinations",
          groups = {"regression", "negative"})
    public void testInvalidLoginScenarios(String username, String password, String scenario) {
        logger.info("Testing invalid login scenario: {}", scenario);

        // Act
        loginPage.login(username, password);

        // Assert - Login should fail (error shown OR not logged in)
        boolean loginFailed = loginPage.isErrorDisplayed() || !loginPage.isLoggedIn();
        assertThat(loginFailed)
                .as("Login should fail for scenario: " + scenario)
                .isTrue();

        logger.info("Invalid login scenario '{}' test passed", scenario);
    }

    // ==================== UI VALIDATION TESTS ====================

    /**
     * Test: Verify login page elements are displayed.
     *
     * Given: User navigates to login page
     * Then: All login elements should be visible
     */
    @Test(description = "Verify login page elements are displayed",
          groups = {"smoke", "ui"})
    public void testLoginPageElementsDisplayed() {
        // Assert
        assertThat(loginPage.isLoginPageDisplayed())
                .as("Login page elements should be displayed")
                .isTrue();

        logger.info("Login page elements test passed");
    }

    /**
     * Test: Verify page title on login page.
     */
    @Test(description = "Verify login page title",
          groups = {"ui"})
    public void testLoginPageTitle() {
        // Assert
        String pageTitle = loginPage.getLoginPageTitle();
        assertThat(pageTitle)
                .as("Page title should not be empty")
                .isNotEmpty();

        logger.info("Login page title: {}", pageTitle);
    }
}
