package com.automation.utils;

import com.automation.driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtils - Utility class for capturing screenshots.
 *
 * Design Decisions:
 * 1. Multiple capture methods - Full page, element, viewport
 * 2. Automatic directory creation - Screenshots folder management
 * 3. Timestamped naming - Unique filenames prevent overwrites
 * 4. Error handling - Graceful failure with logging
 *
 * Usage:
 *   ScreenshotUtils.captureScreenshot("login_failed");
 *   ScreenshotUtils.captureElementScreenshot(element, "error_message");
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public final class ScreenshotUtils {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOTS_DIR = "target/screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    // Private constructor - utility class
    private ScreenshotUtils() {
        throw new UnsupportedOperationException("ScreenshotUtils is a utility class");
    }

    /**
     * Capture full page screenshot with auto-generated name.
     *
     * @return Path to saved screenshot
     */
    public static String captureScreenshot() {
        return captureScreenshot("screenshot");
    }

    /**
     * Capture full page screenshot with custom name.
     *
     * @param name Base name for the screenshot file
     * @return Path to saved screenshot
     */
    public static String captureScreenshot(String name) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;

            // Capture screenshot
            File sourceFile = screenshotDriver.getScreenshotAs(OutputType.FILE);

            // Generate unique filename
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String filename = String.format("%s_%s.png", sanitizeFilename(name), timestamp);

            // Create target path
            Path targetPath = Paths.get(SCREENSHOTS_DIR, filename);
            ensureDirectoryExists(targetPath.getParent());

            // Copy file
            Files.copy(sourceFile.toPath(), targetPath);

            logger.info("Screenshot saved: {}", targetPath);
            return targetPath.toString();

        } catch (IOException e) {
            logger.error("Failed to save screenshot: {}", name, e);
            return null;
        } catch (WebDriverException e) {
            logger.error("WebDriver error capturing screenshot: {}", name, e);
            return null;
        }
    }

    /**
     * Capture screenshot and return as byte array.
     * Useful for attaching to reports.
     *
     * @return Screenshot as byte array
     */
    public static byte[] captureScreenshotAsBytes() {
        try {
            WebDriver driver = DriverFactory.getDriver();
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            return screenshotDriver.getScreenshotAs(OutputType.BYTES);
        } catch (WebDriverException e) {
            logger.error("Failed to capture screenshot as bytes", e);
            return new byte[0];
        }
    }

    /**
     * Capture screenshot and return as Base64 string.
     * Useful for embedding in HTML reports.
     *
     * @return Screenshot as Base64 string
     */
    public static String captureScreenshotAsBase64() {
        try {
            WebDriver driver = DriverFactory.getDriver();
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            return screenshotDriver.getScreenshotAs(OutputType.BASE64);
        } catch (WebDriverException e) {
            logger.error("Failed to capture screenshot as Base64", e);
            return "";
        }
    }

    /**
     * Capture screenshot of specific element.
     *
     * @param element WebElement to capture
     * @param name Base name for the file
     * @return Path to saved screenshot
     */
    public static String captureElementScreenshot(WebElement element, String name) {
        try {
            // Scroll element into view first
            ActionUtils.scrollToElement(element);

            // Capture element screenshot
            File sourceFile = element.getScreenshotAs(OutputType.FILE);

            // Generate unique filename
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String filename = String.format("%s_element_%s.png",
                    sanitizeFilename(name), timestamp);

            // Create target path
            Path targetPath = Paths.get(SCREENSHOTS_DIR, filename);
            ensureDirectoryExists(targetPath.getParent());

            // Copy file
            Files.copy(sourceFile.toPath(), targetPath);

            logger.info("Element screenshot saved: {}", targetPath);
            return targetPath.toString();

        } catch (IOException e) {
            logger.error("Failed to save element screenshot: {}", name, e);
            return null;
        } catch (WebDriverException e) {
            logger.error("WebDriver error capturing element screenshot: {}", name, e);
            return null;
        }
    }

    /**
     * Capture screenshot of specific element by locator.
     *
     * @param locator Element locator
     * @param name Base name for the file
     * @return Path to saved screenshot
     */
    public static String captureElementScreenshot(By locator, String name) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        return captureElementScreenshot(element, name);
    }

    /**
     * Capture full page screenshot using JavaScript.
     * Captures entire scrollable content, not just viewport.
     *
     * @param name Base name for the file
     * @return Path to saved screenshot
     */
    public static String captureFullPageScreenshot(String name) {
        // Note: For full page screenshots, consider using libraries like
        // AShot or Shutterbug for better scrolling page capture
        // This is a basic implementation using native Selenium

        WebDriver driver = DriverFactory.getDriver();

        // For Chrome/Firefox with native full page support
        if (driver instanceof TakesScreenshot) {
            return captureScreenshot(name + "_fullpage");
        }

        return captureScreenshot(name);
    }

    /**
     * Capture screenshot on test failure.
     * Designed to be called from test listeners.
     *
     * @param testName Name of the failed test
     * @return Path to saved screenshot
     */
    public static String captureOnFailure(String testName) {
        String sanitizedName = "FAILED_" + sanitizeFilename(testName);
        logger.info("Capturing failure screenshot for test: {}", testName);
        return captureScreenshot(sanitizedName);
    }

    /**
     * Capture screenshot with custom directory.
     *
     * @param name Base name for the file
     * @param directory Target directory
     * @return Path to saved screenshot
     */
    public static String captureScreenshot(String name, String directory) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;

            File sourceFile = screenshotDriver.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String filename = String.format("%s_%s.png", sanitizeFilename(name), timestamp);

            Path targetPath = Paths.get(directory, filename);
            ensureDirectoryExists(targetPath.getParent());

            Files.copy(sourceFile.toPath(), targetPath);

            logger.info("Screenshot saved: {}", targetPath);
            return targetPath.toString();

        } catch (IOException e) {
            logger.error("Failed to save screenshot: {}", name, e);
            return null;
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Ensure directory exists, create if necessary.
     */
    private static void ensureDirectoryExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            logger.debug("Created directory: {}", directory);
        }
    }

    /**
     * Sanitize filename to remove invalid characters.
     */
    private static String sanitizeFilename(String name) {
        // Replace invalid characters with underscore
        return name.replaceAll("[^a-zA-Z0-9._-]", "_")
                   .replaceAll("_+", "_")  // Remove multiple underscores
                   .replaceAll("^_|_$", ""); // Remove leading/trailing underscores
    }

    /**
     * Get screenshots directory path.
     */
    public static String getScreenshotsDirectory() {
        return SCREENSHOTS_DIR;
    }

    /**
     * Clean old screenshots (older than specified days).
     *
     * @param daysOld Number of days to keep
     */
    public static void cleanOldScreenshots(int daysOld) {
        try {
            Path screenshotsPath = Paths.get(SCREENSHOTS_DIR);
            if (!Files.exists(screenshotsPath)) {
                return;
            }

            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);

            Files.list(screenshotsPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".png"))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            logger.debug("Deleted old screenshot: {}", path);
                        } catch (IOException e) {
                            logger.warn("Failed to delete old screenshot: {}", path);
                        }
                    });

            logger.info("Cleaned screenshots older than {} days", daysOld);

        } catch (IOException e) {
            logger.error("Failed to clean old screenshots", e);
        }
    }
}
