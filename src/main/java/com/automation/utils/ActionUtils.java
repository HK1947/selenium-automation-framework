package com.automation.utils;

import com.automation.driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ActionUtils - Reusable utility class for common Selenium actions.
 *
 * Design Decisions:
 * 1. Static Methods - Utility pattern, no state needed
 * 2. Retry Logic - Handle flaky elements with retries
 * 3. JavaScript Fallbacks - Alternative when native actions fail
 * 4. Comprehensive Logging - Debug support for failures
 *
 * This class encapsulates all common user interactions:
 * - Click actions (regular, JS, double, right-click)
 * - Input actions (type, clear, upload)
 * - Mouse actions (hover, drag-drop)
 * - Keyboard actions
 * - Frame/Window handling
 * - Dropdown handling
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public final class ActionUtils {

    private static final Logger logger = LogManager.getLogger(ActionUtils.class);
    private static final int MAX_RETRIES = 3;

    // Private constructor - utility class
    private ActionUtils() {
        throw new UnsupportedOperationException("ActionUtils is a utility class");
    }

    // ==================== CLICK ACTIONS ====================

    /**
     * Click with retry logic for handling stale elements.
     */
    public static void clickWithRetry(By locator) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                WebElement element = WaitUtils.waitForClickable(locator);
                element.click();
                logger.info("Clicked element: {}", locator);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                logger.warn("Stale element, attempt {}/{}: {}", attempts, MAX_RETRIES, locator);
                if (attempts == MAX_RETRIES) {
                    throw e;
                }
            }
        }
    }

    /**
     * Click using JavaScript (fallback when native click fails).
     * Useful for hidden/overlapped elements.
     */
    public static void clickWithJs(By locator) {
        WebElement element = WaitUtils.waitForPresence(locator);
        clickWithJs(element);
    }

    /**
     * Click using JavaScript (WebElement version).
     */
    public static void clickWithJs(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript("arguments[0].click();", element);
        logger.info("JS clicked element");
    }

    /**
     * Double click element.
     */
    public static void doubleClick(By locator) {
        WebElement element = WaitUtils.waitForClickable(locator);
        new Actions(DriverFactory.getDriver())
                .doubleClick(element)
                .perform();
        logger.info("Double-clicked element: {}", locator);
    }

    /**
     * Right-click (context click) element.
     */
    public static void rightClick(By locator) {
        WebElement element = WaitUtils.waitForClickable(locator);
        new Actions(DriverFactory.getDriver())
                .contextClick(element)
                .perform();
        logger.info("Right-clicked element: {}", locator);
    }

    /**
     * Click and hold element.
     */
    public static void clickAndHold(By locator) {
        WebElement element = WaitUtils.waitForClickable(locator);
        new Actions(DriverFactory.getDriver())
                .clickAndHold(element)
                .perform();
        logger.info("Click and hold on element: {}", locator);
    }

    /**
     * Release held element.
     */
    public static void release() {
        new Actions(DriverFactory.getDriver()).release().perform();
        logger.info("Released held element");
    }

    // ==================== INPUT ACTIONS ====================

    /**
     * Type text with clear first.
     */
    public static void typeText(By locator, String text) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
        logger.info("Typed text into element: {}", locator);
    }

    /**
     * Type text using JavaScript (for stubborn inputs).
     */
    public static void typeWithJs(By locator, String text) {
        WebElement element = WaitUtils.waitForPresence(locator);
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript("arguments[0].value = arguments[1];", element, text);
        logger.info("JS typed text into element: {}", locator);
    }

    /**
     * Clear input field.
     */
    public static void clearField(By locator) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        element.clear();
        logger.info("Cleared field: {}", locator);
    }

    /**
     * Clear field using Ctrl+A, Delete (more reliable).
     */
    public static void clearFieldWithKeys(By locator) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        logger.info("Cleared field with keys: {}", locator);
    }

    /**
     * Upload file to input element.
     */
    public static void uploadFile(By locator, String filePath) {
        WebElement element = WaitUtils.waitForPresence(locator);
        element.sendKeys(filePath);
        logger.info("Uploaded file: {} to element: {}", filePath, locator);
    }

    // ==================== MOUSE ACTIONS ====================

    /**
     * Hover over element.
     */
    public static void hoverOver(By locator) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        new Actions(DriverFactory.getDriver())
                .moveToElement(element)
                .perform();
        logger.info("Hovered over element: {}", locator);
    }

    /**
     * Drag and drop from source to target.
     */
    public static void dragAndDrop(By source, By target) {
        WebElement sourceElement = WaitUtils.waitForVisibility(source);
        WebElement targetElement = WaitUtils.waitForVisibility(target);
        new Actions(DriverFactory.getDriver())
                .dragAndDrop(sourceElement, targetElement)
                .perform();
        logger.info("Dragged from {} to {}", source, target);
    }

    /**
     * Drag element by offset.
     */
    public static void dragAndDropByOffset(By locator, int xOffset, int yOffset) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        new Actions(DriverFactory.getDriver())
                .dragAndDropBy(element, xOffset, yOffset)
                .perform();
        logger.info("Dragged element by offset: ({}, {})", xOffset, yOffset);
    }

    /**
     * Move mouse to coordinates.
     */
    public static void moveToOffset(int xOffset, int yOffset) {
        new Actions(DriverFactory.getDriver())
                .moveByOffset(xOffset, yOffset)
                .perform();
    }

    // ==================== KEYBOARD ACTIONS ====================

    /**
     * Press keyboard key.
     */
    public static void pressKey(Keys key) {
        new Actions(DriverFactory.getDriver())
                .sendKeys(key)
                .perform();
        logger.info("Pressed key: {}", key.name());
    }

    /**
     * Press Enter key on element.
     */
    public static void pressEnter(By locator) {
        WaitUtils.waitForVisibility(locator).sendKeys(Keys.ENTER);
        logger.info("Pressed Enter on element: {}", locator);
    }

    /**
     * Press Tab key.
     */
    public static void pressTab() {
        new Actions(DriverFactory.getDriver())
                .sendKeys(Keys.TAB)
                .perform();
        logger.info("Pressed Tab key");
    }

    /**
     * Press Escape key.
     */
    public static void pressEscape() {
        new Actions(DriverFactory.getDriver())
                .sendKeys(Keys.ESCAPE)
                .perform();
        logger.info("Pressed Escape key");
    }

    /**
     * Keyboard shortcut (e.g., Ctrl+C).
     */
    public static void keyboardShortcut(CharSequence modifier, CharSequence key) {
        new Actions(DriverFactory.getDriver())
                .keyDown(modifier)
                .sendKeys(key)
                .keyUp(modifier)
                .perform();
        logger.info("Executed keyboard shortcut");
    }

    // ==================== DROPDOWN ACTIONS ====================

    /**
     * Select dropdown option by visible text.
     */
    public static void selectByText(By locator, String text) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        new Select(element).selectByVisibleText(text);
        logger.info("Selected '{}' from dropdown: {}", text, locator);
    }

    /**
     * Select dropdown option by value.
     */
    public static void selectByValue(By locator, String value) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        new Select(element).selectByValue(value);
        logger.info("Selected value '{}' from dropdown: {}", value, locator);
    }

    /**
     * Select dropdown option by index.
     */
    public static void selectByIndex(By locator, int index) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        new Select(element).selectByIndex(index);
        logger.info("Selected index {} from dropdown: {}", index, locator);
    }

    /**
     * Get all dropdown options.
     */
    public static List<String> getDropdownOptions(By locator) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        Select select = new Select(element);
        List<String> options = new ArrayList<>();
        for (WebElement option : select.getOptions()) {
            options.add(option.getText());
        }
        return options;
    }

    /**
     * Get selected option text.
     */
    public static String getSelectedOption(By locator) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        return new Select(element).getFirstSelectedOption().getText();
    }

    // ==================== FRAME ACTIONS ====================

    /**
     * Switch to frame by locator.
     */
    public static void switchToFrame(By locator) {
        WaitUtils.waitForFrameAndSwitch(locator);
        logger.info("Switched to frame: {}", locator);
    }

    /**
     * Switch to frame by name or ID.
     */
    public static void switchToFrame(String nameOrId) {
        WaitUtils.waitForFrameAndSwitch(nameOrId);
        logger.info("Switched to frame: {}", nameOrId);
    }

    /**
     * Switch to frame by index.
     */
    public static void switchToFrame(int index) {
        DriverFactory.getDriver().switchTo().frame(index);
        logger.info("Switched to frame by index: {}", index);
    }

    /**
     * Switch back to main content.
     */
    public static void switchToDefaultContent() {
        DriverFactory.getDriver().switchTo().defaultContent();
        logger.info("Switched to default content");
    }

    /**
     * Switch to parent frame.
     */
    public static void switchToParentFrame() {
        DriverFactory.getDriver().switchTo().parentFrame();
        logger.info("Switched to parent frame");
    }

    // ==================== WINDOW ACTIONS ====================

    /**
     * Get current window handle.
     */
    public static String getCurrentWindowHandle() {
        return DriverFactory.getDriver().getWindowHandle();
    }

    /**
     * Get all window handles.
     */
    public static Set<String> getAllWindowHandles() {
        return DriverFactory.getDriver().getWindowHandles();
    }

    /**
     * Switch to window by handle.
     */
    public static void switchToWindow(String handle) {
        DriverFactory.getDriver().switchTo().window(handle);
        logger.info("Switched to window: {}", handle);
    }

    /**
     * Switch to new window (latest opened).
     */
    public static void switchToNewWindow() {
        String currentHandle = getCurrentWindowHandle();
        Set<String> handles = getAllWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(currentHandle)) {
                switchToWindow(handle);
                break;
            }
        }
    }

    /**
     * Close current window and switch back.
     */
    public static void closeCurrentWindow(String originalHandle) {
        DriverFactory.getDriver().close();
        switchToWindow(originalHandle);
        logger.info("Closed window and switched back to original");
    }

    /**
     * Open new tab.
     */
    public static void openNewTab() {
        DriverFactory.getDriver().switchTo().newWindow(WindowType.TAB);
        logger.info("Opened new tab");
    }

    /**
     * Open new window.
     */
    public static void openNewWindow() {
        DriverFactory.getDriver().switchTo().newWindow(WindowType.WINDOW);
        logger.info("Opened new window");
    }

    // ==================== ALERT ACTIONS ====================

    /**
     * Accept alert (click OK).
     */
    public static void acceptAlert() {
        WaitUtils.waitForAlert().accept();
        logger.info("Accepted alert");
    }

    /**
     * Dismiss alert (click Cancel).
     */
    public static void dismissAlert() {
        WaitUtils.waitForAlert().dismiss();
        logger.info("Dismissed alert");
    }

    /**
     * Get alert text.
     */
    public static String getAlertText() {
        String text = WaitUtils.waitForAlert().getText();
        logger.info("Alert text: {}", text);
        return text;
    }

    /**
     * Type in alert prompt.
     */
    public static void typeInAlert(String text) {
        Alert alert = WaitUtils.waitForAlert();
        alert.sendKeys(text);
        logger.info("Typed in alert: {}", text);
    }

    // ==================== SCROLL ACTIONS ====================

    /**
     * Scroll to element.
     */
    public static void scrollToElement(By locator) {
        WebElement element = WaitUtils.waitForPresence(locator);
        scrollToElement(element);
    }

    /**
     * Scroll to element (WebElement version).
     */
    public static void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        logger.info("Scrolled to element");
    }

    /**
     * Scroll to top of page.
     */
    public static void scrollToTop() {
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript("window.scrollTo(0, 0);");
        logger.info("Scrolled to top");
    }

    /**
     * Scroll to bottom of page.
     */
    public static void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.info("Scrolled to bottom");
    }

    /**
     * Scroll by pixels.
     */
    public static void scrollByPixels(int x, int y) {
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }
}
