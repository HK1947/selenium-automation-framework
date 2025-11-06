package com.automation.listeners;

import com.automation.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestListener - TestNG listener for test lifecycle events.
 *
 * Design Decisions:
 * 1. ITestListener - Standard TestNG listener interface
 * 2. Centralized logging - All test events logged uniformly
 * 3. Screenshot capture - Automatic capture on failures
 * 4. Report integration - Can be extended for Extent/Allure
 *
 * This listener handles:
 * - Test start/finish events
 * - Test pass/fail/skip events
 * - Suite start/finish events
 * - Screenshot capture on failure
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    /**
     * Invoked before any test method is run.
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);
        logger.info("========== STARTING TEST: {} ==========", testName);
    }

    /**
     * Invoked when a test passes.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        logger.info("✅ PASSED: {} ({}ms)", testName, duration);
    }

    /**
     * Invoked when a test fails.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();

        logger.error("❌ FAILED: {} ({}ms)", testName, duration);
        logger.error("Failure Reason: {}", result.getThrowable().getMessage());

        // Capture screenshot
        try {
            String screenshotPath = ScreenshotUtils.captureOnFailure(testName);
            if (screenshotPath != null) {
                logger.info("Screenshot captured: {}", screenshotPath);
                // Store screenshot path in result for report access
                result.setAttribute("screenshot", screenshotPath);
            }
        } catch (Exception e) {
            logger.warn("Failed to capture screenshot: {}", e.getMessage());
        }

        // Log stack trace for debugging
        if (logger.isDebugEnabled()) {
            logger.debug("Stack trace:", result.getThrowable());
        }
    }

    /**
     * Invoked when a test is skipped.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestName(result);
        logger.warn("⚠️ SKIPPED: {}", testName);

        if (result.getThrowable() != null) {
            logger.warn("Skip Reason: {}", result.getThrowable().getMessage());
        }
    }

    /**
     * Invoked when a test fails but is within the success percentage.
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = getTestName(result);
        logger.warn("⚠️ FAILED (within success %): {}", testName);
    }

    /**
     * Invoked when a test fails with timeout.
     */
    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        String testName = getTestName(result);
        logger.error("⏱️ TIMEOUT: {}", testName);
        onTestFailure(result); // Treat as failure for screenshot
    }

    /**
     * Invoked before the test suite starts.
     */
    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        logger.info("╔══════════════════════════════════════════╗");
        logger.info("║        TEST SUITE STARTED                ║");
        logger.info("║  Suite: {}", padRight(suiteName, 32) + "║");
        logger.info("╚══════════════════════════════════════════╝");
    }

    /**
     * Invoked after all tests in the suite have run.
     */
    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;

        logger.info("╔══════════════════════════════════════════╗");
        logger.info("║        TEST SUITE COMPLETED              ║");
        logger.info("║  Total:   {}                             ║", padLeft(String.valueOf(total), 5));
        logger.info("║  Passed:  {} ✅                          ║", padLeft(String.valueOf(passed), 5));
        logger.info("║  Failed:  {} ❌                          ║", padLeft(String.valueOf(failed), 5));
        logger.info("║  Skipped: {} ⚠️                          ║", padLeft(String.valueOf(skipped), 5));
        logger.info("╚══════════════════════════════════════════╝");

        // Log failed test names for quick reference
        if (failed > 0) {
            logger.error("Failed Tests:");
            context.getFailedTests().getAllResults().forEach(result ->
                logger.error("  - {}", getTestName(result))
            );
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Get formatted test name (class.method).
     */
    private String getTestName(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        String methodName = result.getMethod().getMethodName();
        return className + "." + methodName;
    }

    /**
     * Pad string to the right.
     */
    private String padRight(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }
        return text + " ".repeat(length - text.length());
    }

    /**
     * Pad string to the left.
     */
    private String padLeft(String text, int length) {
        if (text.length() >= length) {
            return text;
        }
        return " ".repeat(length - text.length()) + text;
    }
}
