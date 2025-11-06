package com.automation.utils;

import com.automation.driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * WaitUtils - Centralized utility class for all wait operations.
 *
 * Design Decisions:
 * 1. Static Methods - No instantiation needed, utility pattern
 * 2. Explicit Waits Only - No Thread.sleep() for reliability
 * 3. Configurable Timeouts - Flexible wait durations
 * 4. Custom ExpectedConditions - Framework-specific wait conditions
 *
 * Why Explicit Waits?
 * - More reliable than implicit waits
 * - Waits for specific conditions, not arbitrary time
 * - Better for dynamic web applications
 * - Easier to debug failed waits
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public final class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    // Default timeout constants
    public static final int DEFAULT_TIMEOUT = 15;
    public static final int SHORT_TIMEOUT = 5;
    public static final int LONG_TIMEOUT = 30;
    public static final int POLLING_INTERVAL = 500;

    // Private constructor - utility class
    private WaitUtils() {
        throw new UnsupportedOperationException("WaitUtils is a utility class");
    }

    // ==================== BASIC WAIT METHODS ====================

    /**
     * Wait for element to be visible.
     *
     * @param locator Element locator
     * @return WebElement once visible
     */
    public static WebElement waitForVisibility(By locator) {
        return waitForVisibility(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for element to be visible with custom timeout.
     */
    public static WebElement waitForVisibility(By locator, int timeoutSeconds) {
        logger.debug("Waiting for visibility: {}, timeout: {}s", locator, timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be clickable.
     */
    public static WebElement waitForClickable(By locator) {
        return waitForClickable(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for element to be clickable with custom timeout.
     */
    public static WebElement waitForClickable(By locator, int timeoutSeconds) {
        logger.debug("Waiting for clickable: {}, timeout: {}s", locator, timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for element presence in DOM.
     */
    public static WebElement waitForPresence(By locator) {
        return waitForPresence(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for element presence with custom timeout.
     */
    public static WebElement waitForPresence(By locator, int timeoutSeconds) {
        logger.debug("Waiting for presence: {}, timeout: {}s", locator, timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait for element to disappear.
     */
    public static boolean waitForInvisibility(By locator) {
        return waitForInvisibility(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for element to disappear with custom timeout.
     */
    public static boolean waitForInvisibility(By locator, int timeoutSeconds) {
        logger.debug("Waiting for invisibility: {}, timeout: {}s", locator, timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ==================== TEXT WAIT METHODS ====================

    /**
     * Wait for text to be present in element.
     */
    public static boolean waitForTextPresent(By locator, String text) {
        return waitForTextPresent(locator, text, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for text to be present with custom timeout.
     */
    public static boolean waitForTextPresent(By locator, String text, int timeoutSeconds) {
        logger.debug("Waiting for text '{}' in: {}", text, locator);
        return getWait(timeoutSeconds).until(
                ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Wait for text to be present in element value attribute.
     */
    public static boolean waitForTextInValue(By locator, String text) {
        logger.debug("Waiting for text '{}' in value of: {}", text, locator);
        return getWait(DEFAULT_TIMEOUT).until(
                ExpectedConditions.textToBePresentInElementValue(locator, text));
    }

    // ==================== URL/TITLE WAIT METHODS ====================

    /**
     * Wait for URL to contain text.
     */
    public static boolean waitForUrlContains(String urlPart) {
        return waitForUrlContains(urlPart, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for URL to contain text with custom timeout.
     */
    public static boolean waitForUrlContains(String urlPart, int timeoutSeconds) {
        logger.debug("Waiting for URL to contain: {}", urlPart);
        return getWait(timeoutSeconds).until(ExpectedConditions.urlContains(urlPart));
    }

    /**
     * Wait for exact URL match.
     */
    public static boolean waitForUrl(String url) {
        logger.debug("Waiting for URL: {}", url);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.urlToBe(url));
    }

    /**
     * Wait for title to contain text.
     */
    public static boolean waitForTitleContains(String titlePart) {
        logger.debug("Waiting for title to contain: {}", titlePart);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.titleContains(titlePart));
    }

    /**
     * Wait for exact title match.
     */
    public static boolean waitForTitle(String title) {
        logger.debug("Waiting for title: {}", title);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.titleIs(title));
    }

    // ==================== ELEMENT STATE WAIT METHODS ====================

    /**
     * Wait for element attribute to contain value.
     */
    public static boolean waitForAttributeContains(By locator, String attribute, String value) {
        logger.debug("Waiting for attribute '{}' to contain '{}'", attribute, value);
        return getWait(DEFAULT_TIMEOUT).until(
                ExpectedConditions.attributeContains(locator, attribute, value));
    }

    /**
     * Wait for element attribute to equal value.
     */
    public static boolean waitForAttributeToBe(By locator, String attribute, String value) {
        logger.debug("Waiting for attribute '{}' to be '{}'", attribute, value);
        return getWait(DEFAULT_TIMEOUT).until(
                ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    /**
     * Wait for element to be selected.
     */
    public static boolean waitForSelected(By locator) {
        logger.debug("Waiting for element to be selected: {}", locator);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.elementToBeSelected(locator));
    }

    /**
     * Wait for specific number of elements.
     */
    public static List<WebElement> waitForNumberOfElements(By locator, int expectedCount) {
        logger.debug("Waiting for {} elements: {}", expectedCount, locator);
        return getWait(DEFAULT_TIMEOUT).until(
                ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }

    /**
     * Wait for at least N elements.
     */
    public static List<WebElement> waitForMinimumElements(By locator, int minCount) {
        logger.debug("Waiting for at least {} elements: {}", minCount, locator);
        return getWait(DEFAULT_TIMEOUT).until(
                ExpectedConditions.numberOfElementsToBeMoreThan(locator, minCount - 1));
    }

    // ==================== ADVANCED WAIT METHODS ====================

    /**
     * Wait for page load complete using JavaScript.
     */
    public static void waitForPageLoad() {
        waitForPageLoad(LONG_TIMEOUT);
    }

    /**
     * Wait for page load with custom timeout.
     */
    public static void waitForPageLoad(int timeoutSeconds) {
        logger.debug("Waiting for page load complete");
        getWait(timeoutSeconds).until((ExpectedCondition<Boolean>) driver ->
                ((JavascriptExecutor) driver).executeScript("return document.readyState")
                        .equals("complete"));
    }

    /**
     * Wait for jQuery/AJAX to complete.
     */
    public static void waitForAjax() {
        waitForAjax(DEFAULT_TIMEOUT);
    }

    /**
     * Wait for jQuery/AJAX with custom timeout.
     */
    public static void waitForAjax(int timeoutSeconds) {
        logger.debug("Waiting for AJAX to complete");
        getWait(timeoutSeconds).until((ExpectedCondition<Boolean>) driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (Boolean) js.executeScript(
                    "return (typeof jQuery === 'undefined') ? true : jQuery.active == 0");
        });
    }

    /**
     * Wait for Angular to finish rendering.
     */
    public static void waitForAngular() {
        logger.debug("Waiting for Angular to stabilize");
        getWait(DEFAULT_TIMEOUT).until((ExpectedCondition<Boolean>) driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (Boolean) js.executeScript(
                    "return (typeof angular === 'undefined') ? true : " +
                    "angular.element(document.body).injector().get('$http').pendingRequests.length === 0");
        });
    }

    /**
     * Wait for element staleness (useful after dynamic updates).
     */
    public static boolean waitForStaleness(WebElement element) {
        logger.debug("Waiting for element staleness");
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.stalenessOf(element));
    }

    /**
     * Wait for frame and switch to it.
     */
    public static WebDriver waitForFrameAndSwitch(By locator) {
        logger.debug("Waiting for frame: {}", locator);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    /**
     * Wait for frame by name/id and switch.
     */
    public static WebDriver waitForFrameAndSwitch(String nameOrId) {
        logger.debug("Waiting for frame: {}", nameOrId);
        return getWait(DEFAULT_TIMEOUT).until(
                ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
    }

    /**
     * Wait for alert to be present.
     */
    public static Alert waitForAlert() {
        logger.debug("Waiting for alert");
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.alertIsPresent());
    }

    // ==================== FLUENT WAIT ====================

    /**
     * Create fluent wait with custom polling and ignored exceptions.
     */
    public static <T> T fluentWait(Function<WebDriver, T> condition, int timeoutSeconds,
                                   int pollingMillis, Class<? extends Throwable>... exceptions) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(DriverFactory.getDriver())
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis));

        for (Class<? extends Throwable> exception : exceptions) {
            fluentWait.ignoring(exception);
        }

        return fluentWait.until(condition);
    }

    /**
     * Fluent wait with default ignored exceptions (NoSuchElement, Stale).
     */
    @SuppressWarnings("unchecked")
    public static <T> T fluentWait(Function<WebDriver, T> condition) {
        return fluentWait(condition, DEFAULT_TIMEOUT, POLLING_INTERVAL,
                NoSuchElementException.class, StaleElementReferenceException.class);
    }

    // ==================== CUSTOM CONDITIONS ====================

    /**
     * Custom condition: Wait for element to have specific CSS class.
     */
    public static ExpectedCondition<Boolean> elementHasClass(By locator, String className) {
        return driver -> {
            WebElement element = driver.findElement(locator);
            String classes = element.getAttribute("class");
            return classes != null && classes.contains(className);
        };
    }

    /**
     * Custom condition: Wait for element count to change.
     */
    public static ExpectedCondition<Boolean> elementCountChanged(By locator, int originalCount) {
        return driver -> driver.findElements(locator).size() != originalCount;
    }

    /**
     * Custom condition: Wait for no loading spinner.
     */
    public static ExpectedCondition<Boolean> noLoadingSpinner(By spinnerLocator) {
        return driver -> {
            List<WebElement> spinners = driver.findElements(spinnerLocator);
            return spinners.isEmpty() || !spinners.get(0).isDisplayed();
        };
    }

    // ==================== HELPER METHODS ====================

    /**
     * Create WebDriverWait with specified timeout.
     */
    private static WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(DriverFactory.getDriver(),
                Duration.ofSeconds(timeoutSeconds),
                Duration.ofMillis(POLLING_INTERVAL));
    }

    /**
     * Safe wait that returns null instead of throwing exception.
     */
    public static WebElement safeWaitForVisibility(By locator, int timeoutSeconds) {
        try {
            return waitForVisibility(locator, timeoutSeconds);
        } catch (TimeoutException e) {
            logger.warn("Element not visible within timeout: {}", locator);
            return null;
        }
    }

    /**
     * Check if element is present without throwing exception.
     */
    public static boolean isElementPresent(By locator, int timeoutSeconds) {
        try {
            waitForPresence(locator, timeoutSeconds);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
