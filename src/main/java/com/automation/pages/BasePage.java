package com.automation.pages;

import com.automation.driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage - Abstract base class for all Page Objects.
 *
 * Design Decisions:
 * 1. Template Method Pattern - Common wait/action methods for all pages
 * 2. Explicit Waits Only - No Thread.sleep() for reliability
 * 3. Encapsulation - WebDriver interactions hidden from tests
 * 4. Fluent Interface - Methods return page objects for chaining
 *
 * Why Abstract?
 * - Forces concrete pages to extend this class
 * - Provides common functionality without instantiation
 * - Defines contract for all page objects
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public abstract class BasePage {

    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // Default timeout values
    private static final int DEFAULT_TIMEOUT = 15;
    private static final int POLLING_INTERVAL = 500;

    /**
     * Constructor initializes WebDriver and WebDriverWait.
     * Uses PageFactory for @FindBy annotations support.
     */
    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT),
                Duration.ofMillis(POLLING_INTERVAL));
        PageFactory.initElements(driver, this);
        logger.debug("Initialized page: {}", this.getClass().getSimpleName());
    }

    // ==================== WAIT METHODS ====================

    /**
     * Wait for element to be visible.
     *
     * @param locator Element locator
     * @return WebElement once visible
     */
    protected WebElement waitForVisibility(By locator) {
        logger.debug("Waiting for visibility of element: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be visible with custom timeout.
     */
    protected WebElement waitForVisibility(By locator, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be clickable.
     */
    protected WebElement waitForClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for element to be clickable (WebElement version).
     */
    protected WebElement waitForClickable(WebElement element) {
        logger.debug("Waiting for element to be clickable");
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for element presence in DOM (not necessarily visible).
     */
    protected WebElement waitForPresence(By locator) {
        logger.debug("Waiting for presence of element: {}", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait for element to disappear.
     */
    protected boolean waitForInvisibility(By locator) {
        logger.debug("Waiting for invisibility of element: {}", locator);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Wait for text to be present in element.
     */
    protected boolean waitForTextPresent(By locator, String text) {
        logger.debug("Waiting for text '{}' in element: {}", text, locator);
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Wait for URL to contain specific text.
     */
    protected boolean waitForUrlContains(String urlPart) {
        logger.debug("Waiting for URL to contain: {}", urlPart);
        return wait.until(ExpectedConditions.urlContains(urlPart));
    }

    /**
     * Wait for page title to contain text.
     */
    protected boolean waitForTitleContains(String titlePart) {
        logger.debug("Waiting for title to contain: {}", titlePart);
        return wait.until(ExpectedConditions.titleContains(titlePart));
    }

    // ==================== ACTION METHODS ====================

    /**
     * Click element with wait for clickable.
     */
    protected void click(By locator) {
        WebElement element = waitForClickable(locator);
        highlightElement(element);
        element.click();
        logger.info("Clicked element: {}", locator);
    }

    /**
     * Click element (WebElement version).
     */
    protected void click(WebElement element) {
        waitForClickable(element);
        highlightElement(element);
        element.click();
        logger.info("Clicked element");
    }

    /**
     * Type text into element with clear.
     */
    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        highlightElement(element);
        element.clear();
        element.sendKeys(text);
        logger.info("Typed '{}' into element: {}", text, locator);
    }

    /**
     * Type text into element (WebElement version).
     */
    protected void type(WebElement element, String text) {
        waitForVisibility(element);
        highlightElement(element);
        element.clear();
        element.sendKeys(text);
        logger.info("Typed '{}' into element", text);
    }

    /**
     * Wait for WebElement to be visible.
     */
    protected WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Get text from element.
     */
    protected String getText(By locator) {
        WebElement element = waitForVisibility(locator);
        String text = element.getText();
        logger.debug("Got text '{}' from element: {}", text, locator);
        return text;
    }

    /**
     * Get text from element (WebElement version).
     */
    protected String getText(WebElement element) {
        waitForVisibility(element);
        return element.getText();
    }

    /**
     * Get attribute value from element.
     */
    protected String getAttribute(By locator, String attribute) {
        WebElement element = waitForVisibility(locator);
        return element.getAttribute(attribute);
    }

    /**
     * Check if element is displayed.
     */
    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisibility(locator, 5).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Check if element is displayed (WebElement version).
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Check if element is enabled.
     */
    protected boolean isEnabled(By locator) {
        return waitForVisibility(locator).isEnabled();
    }

    /**
     * Check if element is selected (checkbox/radio).
     */
    protected boolean isSelected(By locator) {
        return waitForVisibility(locator).isSelected();
    }

    // ==================== DROPDOWN METHODS ====================

    /**
     * Select dropdown option by visible text.
     */
    protected void selectByVisibleText(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        Select select = new Select(element);
        select.selectByVisibleText(text);
        logger.info("Selected '{}' from dropdown: {}", text, locator);
    }

    /**
     * Select dropdown option by value.
     */
    protected void selectByValue(By locator, String value) {
        WebElement element = waitForVisibility(locator);
        Select select = new Select(element);
        select.selectByValue(value);
        logger.info("Selected value '{}' from dropdown: {}", value, locator);
    }

    /**
     * Select dropdown option by index.
     */
    protected void selectByIndex(By locator, int index) {
        WebElement element = waitForVisibility(locator);
        Select select = new Select(element);
        select.selectByIndex(index);
        logger.info("Selected index {} from dropdown: {}", index, locator);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Find multiple elements.
     */
    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Get current page URL.
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get current page title.
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Navigate to URL.
     */
    protected void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to: {}", url);
    }

    /**
     * Refresh current page.
     */
    protected void refreshPage() {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }

    /**
     * Scroll element into view using JavaScript.
     */
    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    /**
     * Scroll element into view (By locator version).
     */
    protected void scrollIntoView(By locator) {
        WebElement element = waitForPresence(locator);
        scrollIntoView(element);
    }

    /**
     * Highlight element for visual debugging.
     * Useful for debugging and demo purposes.
     */
    protected void highlightElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.border='2px solid red'", element);
        } catch (Exception e) {
            // Ignore highlight failures
        }
    }

    /**
     * Execute JavaScript and return result.
     */
    protected Object executeJavaScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    /**
     * Wait for page load complete.
     */
    protected void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
        logger.debug("Page load complete");
    }
}
