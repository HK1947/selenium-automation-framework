package com.automation.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer - Automatic retry mechanism for flaky tests.
 *
 * Design Decisions:
 * 1. Configurable retry count - Default 2, can be overridden
 * 2. Thread-safe counter - Uses ThreadLocal for parallel execution
 * 3. Selective retry - Only retries on actual failures
 * 4. Logging - Tracks retry attempts for debugging
 *
 * Why use RetryAnalyzer?
 * - Handles flaky tests due to network issues
 * - Reduces false negatives in CI/CD pipelines
 * - Improves test reliability without ignoring real failures
 *
 * Usage:
 * @Test(retryAnalyzer = RetryAnalyzer.class)
 * public void flakyTest() { ... }
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);

    // Maximum number of retry attempts
    private static final int MAX_RETRY_COUNT = 2;

    // ThreadLocal counter for parallel execution safety
    private static final ThreadLocal<Integer> retryCount = ThreadLocal.withInitial(() -> 0);

    /**
     * Determines whether a test should be retried.
     *
     * @param result The result of the test method that just ran
     * @return true if the test should be retried, false otherwise
     */
    @Override
    public boolean retry(ITestResult result) {
        int currentRetry = retryCount.get();

        if (currentRetry < MAX_RETRY_COUNT) {
            currentRetry++;
            retryCount.set(currentRetry);

            String testName = result.getMethod().getMethodName();
            String className = result.getTestClass().getName();

            logger.warn("⟳ RETRY ATTEMPT {}/{} for test: {}.{}",
                    currentRetry, MAX_RETRY_COUNT, className, testName);

            // Log the failure reason
            if (result.getThrowable() != null) {
                logger.warn("Failure reason: {}", result.getThrowable().getMessage());
            }

            return true;
        }

        // Reset counter for next test
        retryCount.remove();
        return false;
    }

    /**
     * Get current retry count.
     *
     * @return Current retry attempt number
     */
    public static int getCurrentRetryCount() {
        return retryCount.get();
    }

    /**
     * Reset retry counter (useful for manual reset).
     */
    public static void resetRetryCount() {
        retryCount.remove();
    }

    /**
     * Check if test is being retried.
     *
     * @return true if currently in retry
     */
    public static boolean isRetrying() {
        return retryCount.get() > 0;
    }
}
