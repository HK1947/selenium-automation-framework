package com.automation.tests;

import com.automation.config.ConfigReader;
import com.automation.driver.DriverFactory;
import com.automation.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest - Abstract base class for all test classes.
 *
 * Design Decisions:
 * 1. Template Method Pattern - Common setup/teardown for all tests
 * 2. Centralized Configuration - Single source for test config
 * 3. Automatic Screenshots - Capture on failure
 * 4. Thread Safety - Each test thread has isolated driver
 *
 * Why extend this class?
 * - Automatic WebDriver lifecycle management
 * - Consistent test setup across all tests
 * - Built-in failure handling
 * - Access to common utilities
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public abstract class BaseTest {

    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected ConfigReader config;

    /**
     * Suite-level setup - runs once before all tests in suite.
     * Used for global initialization like test data setup.
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("========== TEST SUITE STARTED ==========");
        // Clean old screenshots before test run
        ScreenshotUtils.cleanOldScreenshots(7);
    }

    /**
     * Test-level setup - runs before each test method.
     * Initializes WebDriver and navigates to base URL.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        logger.info("---------- Test Setup Started ----------");

        // Initialize configuration
        config = ConfigReader.getInstance();

        // Initialize WebDriver
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();

        // Navigate to base URL
        String baseUrl = config.get("base.url");
        if (baseUrl != null && !baseUrl.isEmpty()) {
            driver.get(baseUrl);
            logger.info("Navigated to base URL: {}", baseUrl);
        }

        logger.info("---------- Test Setup Completed ----------");
    }

    /**
     * Test-level teardown - runs after each test method.
     * Captures screenshot on failure and quits driver.
     *
     * @param result TestNG test result for failure detection
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        logger.info("---------- Test Teardown Started ----------");

        try {
            // Capture screenshot on failure
            if (result.getStatus() == ITestResult.FAILURE) {
                String testName = result.getMethod().getMethodName();
                logger.error("TEST FAILED: {}", testName);
                logger.error("Failure reason: {}", result.getThrowable().getMessage());

                // Capture and log screenshot path
                String screenshotPath = ScreenshotUtils.captureOnFailure(testName);
                if (screenshotPath != null) {
                    logger.info("Screenshot saved: {}", screenshotPath);
                }
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                logger.info("TEST PASSED: {}", result.getMethod().getMethodName());
            } else if (result.getStatus() == ITestResult.SKIP) {
                logger.warn("TEST SKIPPED: {}", result.getMethod().getMethodName());
            }
        } finally {
            // Always quit driver
            DriverFactory.quitDriver();
        }

        logger.info("---------- Test Teardown Completed ----------");
    }

    /**
     * Suite-level teardown - runs once after all tests in suite.
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("========== TEST SUITE COMPLETED ==========");
    }

    // ==================== HELPER METHODS ====================

    /**
     * Navigate to specific URL.
     *
     * @param url URL to navigate to
     */
    protected void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to: {}", url);
    }

    /**
     * Navigate to relative path (appended to base URL).
     *
     * @param path Relative path
     */
    protected void navigateToPath(String path) {
        String baseUrl = config.get("base.url");
        String fullUrl = baseUrl + (path.startsWith("/") ? path : "/" + path);
        navigateTo(fullUrl);
    }

    /**
     * Get current page URL.
     *
     * @return Current URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get current page title.
     *
     * @return Page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Capture screenshot during test (for debugging).
     *
     * @param name Screenshot name
     * @return Path to screenshot
     */
    protected String captureScreenshot(String name) {
        return ScreenshotUtils.captureScreenshot(name);
    }

    /**
     * Get configuration value.
     *
     * @param key Config key
     * @return Config value
     */
    protected String getConfig(String key) {
        return config.get(key);
    }

    /**
     * Get configuration value with default.
     *
     * @param key Config key
     * @param defaultValue Default if not found
     * @return Config value or default
     */
    protected String getConfig(String key, String defaultValue) {
        return config.get(key, defaultValue);
    }
}
