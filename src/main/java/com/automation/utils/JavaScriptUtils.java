package com.automation.utils;

import com.automation.driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

/**
 * JavaScriptUtils - Utility class for JavaScript-based operations.
 *
 * Design Decisions:
 * 1. Encapsulated JS execution - Clean API hiding JS complexity
 * 2. Common operations - Scroll, highlight, DOM manipulation
 * 3. Performance - Direct DOM access bypasses WebDriver overhead
 * 4. Fallback support - When native Selenium methods fail
 *
 * When to use JavaScript:
 * - Click fails due to overlapping elements
 * - Need to access shadow DOM
 * - Scroll operations
 * - Getting/setting element properties
 * - Performance-critical DOM queries
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public final class JavaScriptUtils {

    private static final Logger logger = LogManager.getLogger(JavaScriptUtils.class);

    // Private constructor - utility class
    private JavaScriptUtils() {
        throw new UnsupportedOperationException("JavaScriptUtils is a utility class");
    }

    // ==================== CORE EXECUTION ====================

    /**
     * Execute JavaScript and return result.
     *
     * @param script JavaScript code to execute
     * @param args Arguments to pass to script
     * @return Result of script execution
     */
    public static Object executeScript(String script, Object... args) {
        JavascriptExecutor js = getJsExecutor();
        Object result = js.executeScript(script, args);
        logger.debug("Executed JS: {}", script.substring(0, Math.min(50, script.length())));
        return result;
    }

    /**
     * Execute asynchronous JavaScript.
     *
     * @param script JavaScript code to execute
     * @param args Arguments to pass to script
     * @return Result of script execution
     */
    public static Object executeAsyncScript(String script, Object... args) {
        JavascriptExecutor js = getJsExecutor();
        return js.executeAsyncScript(script, args);
    }

    // ==================== CLICK OPERATIONS ====================

    /**
     * Click element using JavaScript.
     */
    public static void click(WebElement element) {
        executeScript("arguments[0].click();", element);
        logger.info("JS click on element");
    }

    /**
     * Click element by locator using JavaScript.
     */
    public static void click(By locator) {
        WebElement element = WaitUtils.waitForPresence(locator);
        click(element);
    }

    /**
     * Click at specific coordinates.
     */
    public static void clickAtCoordinates(int x, int y) {
        executeScript(
            "document.elementFromPoint(arguments[0], arguments[1]).click();", x, y);
        logger.info("JS click at coordinates: ({}, {})", x, y);
    }

    // ==================== INPUT OPERATIONS ====================

    /**
     * Set value of input element.
     */
    public static void setValue(WebElement element, String value) {
        executeScript("arguments[0].value = arguments[1];", element, value);
        logger.info("JS set value on element");
    }

    /**
     * Set value and trigger input event (for React/Angular inputs).
     */
    public static void setValueWithEvent(WebElement element, String value) {
        executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            element, value);
        logger.info("JS set value with events on element");
    }

    /**
     * Clear input field.
     */
    public static void clearValue(WebElement element) {
        executeScript("arguments[0].value = '';", element);
    }

    /**
     * Get value of input element.
     */
    public static String getValue(WebElement element) {
        return (String) executeScript("return arguments[0].value;", element);
    }

    // ==================== SCROLL OPERATIONS ====================

    /**
     * Scroll element into view.
     */
    public static void scrollIntoView(WebElement element) {
        executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
            element);
    }

    /**
     * Scroll element into view by locator.
     */
    public static void scrollIntoView(By locator) {
        WebElement element = WaitUtils.waitForPresence(locator);
        scrollIntoView(element);
    }

    /**
     * Scroll to top of page.
     */
    public static void scrollToTop() {
        executeScript("window.scrollTo(0, 0);");
        logger.info("Scrolled to top");
    }

    /**
     * Scroll to bottom of page.
     */
    public static void scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.info("Scrolled to bottom");
    }

    /**
     * Scroll by specific pixels.
     */
    public static void scrollBy(int x, int y) {
        executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    /**
     * Scroll to specific position.
     */
    public static void scrollTo(int x, int y) {
        executeScript("window.scrollTo(arguments[0], arguments[1]);", x, y);
    }

    /**
     * Get current scroll position.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Long> getScrollPosition() {
        return (Map<String, Long>) executeScript(
            "return {x: window.pageXOffset, y: window.pageYOffset};");
    }

    /**
     * Get page height.
     */
    public static Long getPageHeight() {
        return (Long) executeScript("return document.body.scrollHeight;");
    }

    // ==================== ELEMENT OPERATIONS ====================

    /**
     * Highlight element (for debugging).
     */
    public static void highlightElement(WebElement element) {
        executeScript(
            "arguments[0].style.border='3px solid red';" +
            "arguments[0].style.backgroundColor='yellow';",
            element);
    }

    /**
     * Highlight element temporarily.
     */
    public static void flashElement(WebElement element, int durationMs) {
        executeScript(
            "var elem = arguments[0];" +
            "var originalStyle = elem.getAttribute('style');" +
            "elem.style.border='3px solid red';" +
            "elem.style.backgroundColor='yellow';" +
            "setTimeout(function() {" +
            "  elem.setAttribute('style', originalStyle || '');" +
            "}, arguments[1]);",
            element, durationMs);
    }

    /**
     * Remove element from DOM.
     */
    public static void removeElement(WebElement element) {
        executeScript("arguments[0].remove();", element);
        logger.info("Removed element from DOM");
    }

    /**
     * Hide element.
     */
    public static void hideElement(WebElement element) {
        executeScript("arguments[0].style.display='none';", element);
    }

    /**
     * Show element.
     */
    public static void showElement(WebElement element) {
        executeScript("arguments[0].style.display='block';", element);
    }

    /**
     * Check if element is in viewport.
     */
    public static boolean isInViewport(WebElement element) {
        return (Boolean) executeScript(
            "var rect = arguments[0].getBoundingClientRect();" +
            "return (" +
            "  rect.top >= 0 &&" +
            "  rect.left >= 0 &&" +
            "  rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&" +
            "  rect.right <= (window.innerWidth || document.documentElement.clientWidth)" +
            ");",
            element);
    }

    // ==================== ATTRIBUTE OPERATIONS ====================

    /**
     * Get element attribute.
     */
    public static String getAttribute(WebElement element, String attribute) {
        return (String) executeScript(
            "return arguments[0].getAttribute(arguments[1]);", element, attribute);
    }

    /**
     * Set element attribute.
     */
    public static void setAttribute(WebElement element, String attribute, String value) {
        executeScript(
            "arguments[0].setAttribute(arguments[1], arguments[2]);",
            element, attribute, value);
    }

    /**
     * Remove element attribute.
     */
    public static void removeAttribute(WebElement element, String attribute) {
        executeScript("arguments[0].removeAttribute(arguments[1]);", element, attribute);
    }

    /**
     * Get element inner text.
     */
    public static String getInnerText(WebElement element) {
        return (String) executeScript("return arguments[0].innerText;", element);
    }

    /**
     * Get element innerHTML.
     */
    public static String getInnerHTML(WebElement element) {
        return (String) executeScript("return arguments[0].innerHTML;", element);
    }

    /**
     * Set element innerHTML.
     */
    public static void setInnerHTML(WebElement element, String html) {
        executeScript("arguments[0].innerHTML = arguments[1];", element, html);
    }

    // ==================== PAGE OPERATIONS ====================

    /**
     * Get page title.
     */
    public static String getPageTitle() {
        return (String) executeScript("return document.title;");
    }

    /**
     * Get page URL.
     */
    public static String getPageUrl() {
        return (String) executeScript("return window.location.href;");
    }

    /**
     * Navigate to URL.
     */
    public static void navigateTo(String url) {
        executeScript("window.location.href = arguments[0];", url);
    }

    /**
     * Refresh page.
     */
    public static void refreshPage() {
        executeScript("location.reload();");
    }

    /**
     * Go back in history.
     */
    public static void goBack() {
        executeScript("window.history.back();");
    }

    /**
     * Go forward in history.
     */
    public static void goForward() {
        executeScript("window.history.forward();");
    }

    /**
     * Get document ready state.
     */
    public static String getReadyState() {
        return (String) executeScript("return document.readyState;");
    }

    /**
     * Check if page is fully loaded.
     */
    public static boolean isPageLoaded() {
        return "complete".equals(getReadyState());
    }

    // ==================== LOCAL STORAGE ====================

    /**
     * Set local storage item.
     */
    public static void setLocalStorage(String key, String value) {
        executeScript("localStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    /**
     * Get local storage item.
     */
    public static String getLocalStorage(String key) {
        return (String) executeScript("return localStorage.getItem(arguments[0]);", key);
    }

    /**
     * Remove local storage item.
     */
    public static void removeLocalStorage(String key) {
        executeScript("localStorage.removeItem(arguments[0]);", key);
    }

    /**
     * Clear all local storage.
     */
    public static void clearLocalStorage() {
        executeScript("localStorage.clear();");
    }

    // ==================== SESSION STORAGE ====================

    /**
     * Set session storage item.
     */
    public static void setSessionStorage(String key, String value) {
        executeScript("sessionStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    /**
     * Get session storage item.
     */
    public static String getSessionStorage(String key) {
        return (String) executeScript("return sessionStorage.getItem(arguments[0]);", key);
    }

    /**
     * Clear all session storage.
     */
    public static void clearSessionStorage() {
        executeScript("sessionStorage.clear();");
    }

    // ==================== SHADOW DOM ====================

    /**
     * Find element in shadow DOM.
     */
    public static WebElement findElementInShadowRoot(WebElement shadowHost, String cssSelector) {
        return (WebElement) executeScript(
            "return arguments[0].shadowRoot.querySelector(arguments[1]);",
            shadowHost, cssSelector);
    }

    /**
     * Find elements in shadow DOM.
     */
    @SuppressWarnings("unchecked")
    public static List<WebElement> findElementsInShadowRoot(WebElement shadowHost, String cssSelector) {
        return (List<WebElement>) executeScript(
            "return arguments[0].shadowRoot.querySelectorAll(arguments[1]);",
            shadowHost, cssSelector);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Get JavascriptExecutor from current driver.
     */
    private static JavascriptExecutor getJsExecutor() {
        WebDriver driver = DriverFactory.getDriver();
        return (JavascriptExecutor) driver;
    }
}
