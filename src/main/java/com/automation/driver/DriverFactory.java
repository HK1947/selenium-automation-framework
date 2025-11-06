package com.automation.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.automation.config.ConfigReader;

import java.time.Duration;
import java.util.Optional;

/**
 * DriverFactory - Thread-safe WebDriver factory implementing Factory Pattern.
 *
 * Design Decisions:
 * 1. ThreadLocal - Ensures thread safety for parallel execution
 * 2. Factory Pattern - Centralized driver creation logic
 * 3. Strategy Pattern - Browser-specific options handled via enum
 * 4. WebDriverManager - Automatic driver binary management
 *
 * Why ThreadLocal?
 * - Each test thread gets its own WebDriver instance
 * - Prevents race conditions in parallel execution
 * - Automatic cleanup when thread terminates
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    // ThreadLocal ensures each thread has its own WebDriver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // Supported browser types
    public enum BrowserType {
        CHROME, FIREFOX, EDGE
    }

    // Private constructor - utility class pattern
    private DriverFactory() {
        throw new UnsupportedOperationException("DriverFactory is a utility class");
    }

    /**
     * Initialize WebDriver based on configuration.
     * Reads browser type from config or system property.
     */
    public static void initDriver() {
        if (driverThreadLocal.get() != null) {
            logger.warn("Driver already initialized for this thread");
            return;
        }

        ConfigReader config = ConfigReader.getInstance();

        // Determine browser - Priority: System Property > Config > Default
        String browserName = Optional.ofNullable(System.getProperty("browser"))
                .orElse(config.get("browser", "chrome"));

        boolean headless = Boolean.parseBoolean(
                Optional.ofNullable(System.getProperty("headless"))
                        .orElse(config.get("headless", "false"))
        );

        BrowserType browserType = BrowserType.valueOf(browserName.toUpperCase());
        WebDriver driver = createDriver(browserType, headless);

        // Configure timeouts from config
        int implicitWait = config.getInt("timeout.implicit", 0);
        int pageLoadTimeout = config.getInt("timeout.pageLoad", 30);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        // Maximize window unless headless
        if (!headless) {
            driver.manage().window().maximize();
        }

        driverThreadLocal.set(driver);
        logger.info("WebDriver initialized: browser={}, headless={}, threadId={}",
                browserType, headless, Thread.currentThread().getId());
    }

    /**
     * Create WebDriver instance based on browser type.
     * Uses Factory Pattern to centralize driver creation.
     *
     * @param browserType Type of browser to create
     * @param headless Whether to run in headless mode
     * @return Configured WebDriver instance
     */
    private static WebDriver createDriver(BrowserType browserType, boolean headless) {
        switch (browserType) {
            case CHROME:
                return createChromeDriver(headless);
            case FIREFOX:
                return createFirefoxDriver(headless);
            case EDGE:
                return createEdgeDriver(headless);
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType);
        }
    }

    /**
     * Create Chrome WebDriver with optimized options.
     */
    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Standard options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");

        // Headless mode for CI/CD
        if (headless) {
            options.addArguments("--headless=new"); // New headless mode in Chrome 109+
            logger.info("Chrome running in headless mode");
        }

        return new ChromeDriver(options);
    }

    /**
     * Create Firefox WebDriver with optimized options.
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("-headless");
            logger.info("Firefox running in headless mode");
        }

        return new FirefoxDriver(options);
    }

    /**
     * Create Edge WebDriver with optimized options.
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();

        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless");
            logger.info("Edge running in headless mode");
        }

        return new EdgeDriver(options);
    }

    /**
     * Get WebDriver instance for current thread.
     *
     * @return WebDriver instance
     * @throws IllegalStateException if driver not initialized
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver not initialized. Call initDriver() first. ThreadId: "
                            + Thread.currentThread().getId());
        }
        return driver;
    }

    /**
     * Quit WebDriver and remove from ThreadLocal.
     * Always call this in @AfterMethod to prevent memory leaks.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully for threadId: {}",
                        Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error("Error quitting WebDriver", e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Check if driver is initialized for current thread.
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
}
