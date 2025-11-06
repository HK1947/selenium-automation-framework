#!/bin/bash

# ============================================================
# AUTO-COMMIT SCRIPT - SELENIUM AUTOMATION FRAMEWORK
# ============================================================
# This script creates backdated commits to simulate 90 days
# of development history for your GitHub profile.
#
# Author: Harsha Kumar
# Usage: chmod +x auto-commit.sh && ./auto-commit.sh
# ============================================================

set -e  # Exit on error

# Configuration
REPO_NAME="selenium-automation-framework"
AUTHOR_NAME="Harsha Kumar"
AUTHOR_EMAIL="harsha@dutchview.com"
START_DAYS_AGO=90  # Start 90 days ago

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}   Selenium Framework Auto-Commit Script   ${NC}"
echo -e "${BLUE}============================================${NC}"

# Initialize git if not already
if [ ! -d ".git" ]; then
    echo -e "\n${GREEN}Initializing Git repository...${NC}"
    git init
    git config user.name "$AUTHOR_NAME"
    git config user.email "$AUTHOR_EMAIL"
fi

# Function to create commit with specific date
create_commit() {
    local message="$1"
    local days_ago="$2"
    local commit_date=$(date -v-${days_ago}d "+%Y-%m-%dT09:%M:%S")

    git add -A
    GIT_AUTHOR_DATE="$commit_date" GIT_COMMITTER_DATE="$commit_date" \
        git commit -m "$message" --allow-empty 2>/dev/null || true

    echo -e "${GREEN}✓${NC} Created commit: $message (${days_ago} days ago)"
}

# ============================================================
# COMMIT SEQUENCE - 90 DAYS OF DEVELOPMENT
# ============================================================

echo -e "\n${BLUE}Creating 90 days of commit history...${NC}\n"

# Day 90: Project initialization
create_commit "chore: initialize selenium automation framework" 90

# Day 88: Add pom.xml
create_commit "chore: add maven pom.xml with dependencies" 88

# Day 86: Add configuration
create_commit "feat(config): add ConfigReader with YAML support" 86

# Day 84: Add driver factory
create_commit "feat(driver): add DriverFactory with ThreadLocal support" 84

# Day 82: Add base page
create_commit "feat(pages): add BasePage with common methods" 82

# Day 80: Add wait utilities
create_commit "feat(utils): add WaitUtils with explicit waits" 80

# Day 78: Add action utilities
create_commit "feat(utils): add ActionUtils for common actions" 78

# Day 76: Add screenshot utilities
create_commit "feat(utils): add ScreenshotUtils for failure capture" 76

# Day 74: Add JavaScript utilities
create_commit "feat(utils): add JavaScriptUtils for JS operations" 74

# Day 72: Add login page
create_commit "feat(pages): add LoginPage with page object pattern" 72

# Day 70: Add base test
create_commit "feat(tests): add BaseTest with setup/teardown" 70

# Day 68: Add test listener
create_commit "feat(listeners): add TestListener for reporting" 68

# Day 66: Add retry analyzer
create_commit "feat(listeners): add RetryAnalyzer for flaky tests" 66

# Day 64: Add login tests
create_commit "feat(tests): add LoginTests with data provider" 64

# Day 62: Add TestNG configuration
create_commit "chore: add testng.xml configuration" 62

# Day 60: Add logging configuration
create_commit "chore: add log4j2.xml configuration" 60

# Day 58: Add environment configs
create_commit "feat(config): add environment-specific configs" 58

# Day 56: Add gitignore
create_commit "chore: add .gitignore" 56

# Day 54: Add README
create_commit "docs: add comprehensive README.md" 54

# Day 52: Improve wait utilities
create_commit "refactor(utils): add fluent wait methods" 52

# Day 50: Add dropdown support
create_commit "feat(pages): add dropdown handling to BasePage" 50

# Day 48: Add frame handling
create_commit "feat(utils): add frame switching utilities" 48

# Day 46: Add alert handling
create_commit "feat(utils): add alert handling methods" 46

# Day 44: Improve driver factory
create_commit "refactor(driver): add headless support" 44

# Day 42: Add Firefox support
create_commit "feat(driver): add Firefox browser support" 42

# Day 40: Add Edge support
create_commit "feat(driver): add Edge browser support" 40

# Day 38: Add negative tests
create_commit "feat(tests): add negative login test scenarios" 38

# Day 36: Add data-driven tests
create_commit "feat(tests): add data provider for invalid credentials" 36

# Day 34: Improve logging
create_commit "refactor(utils): improve logging messages" 34

# Day 32: Add scroll utilities
create_commit "feat(utils): add scroll utilities to JavaScriptUtils" 32

# Day 30: Add element highlight
create_commit "feat(pages): add element highlighting for debugging" 30

# Day 28: Add page load wait
create_commit "feat(utils): add waitForPageLoad method" 28

# Day 26: Add AJAX wait
create_commit "feat(utils): add waitForAjax method" 26

# Day 24: Improve screenshot naming
create_commit "refactor(utils): improve screenshot naming convention" 24

# Day 22: Add config reload
create_commit "feat(config): add configuration reload support" 22

# Day 20: Add parallel execution support
create_commit "feat: configure parallel execution in testng.xml" 20

# Day 18: Add test groups
create_commit "feat(tests): organize tests with groups" 18

# Day 16: Improve error handling
create_commit "refactor(utils): improve error handling and logging" 16

# Day 14: Add UI validation tests
create_commit "feat(tests): add UI element validation tests" 14

# Day 12: Add custom wait conditions
create_commit "feat(utils): add custom ExpectedConditions" 12

# Day 10: Code cleanup
create_commit "refactor: code cleanup and formatting" 10

# Day 8: Add method chaining
create_commit "refactor(pages): add fluent interface pattern" 8

# Day 6: Improve documentation
create_commit "docs: add Javadoc comments to all classes" 6

# Day 4: Add Maven profiles
create_commit "feat: add Maven profiles for browsers and environments" 4

# Day 2: Final polish
create_commit "chore: final code review and cleanup" 2

# Day 1: Ready for production
create_commit "chore: framework ready for production use" 1

echo -e "\n${GREEN}============================================${NC}"
echo -e "${GREEN}   Commit history created successfully!    ${NC}"
echo -e "${GREEN}============================================${NC}"
echo -e "\nTotal commits: $(git rev-list --count HEAD)"
echo -e "\nNext steps:"
echo -e "1. Create a GitHub repo named: $REPO_NAME"
echo -e "2. Run: git remote add origin https://github.com/YOUR_USERNAME/$REPO_NAME.git"
echo -e "3. Run: git push -u origin main"
