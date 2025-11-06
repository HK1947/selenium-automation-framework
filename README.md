# Selenium Automation Framework

A production-ready Selenium WebDriver automation framework built with Java, following industry best practices and design patterns.

## 🏗️ Architecture

```
selenium-automation-framework/
├── src/
│   ├── main/
│   │   ├── java/com/automation/
│   │   │   ├── config/           # Configuration management
│   │   │   ├── driver/           # WebDriver factory
│   │   │   ├── pages/            # Page Object classes
│   │   │   └── utils/            # Utility classes
│   │   └── resources/
│   │       ├── config/           # Environment configs
│   │       └── log4j2.xml        # Logging configuration
│   └── test/
│       ├── java/com/automation/
│       │   ├── tests/            # Test classes
│       │   └── listeners/        # TestNG listeners
│       └── resources/
│           └── testng.xml        # TestNG suite configuration
├── pom.xml                       # Maven dependencies
└── README.md
```

## 🛠️ Tech Stack

- **Language**: Java 11+
- **Build Tool**: Maven
- **Testing Framework**: TestNG
- **Browser Automation**: Selenium 4
- **Driver Management**: WebDriverManager
- **Logging**: Log4j2
- **Assertions**: AssertJ

## 🎯 Key Features

### Design Patterns
- **Page Object Model (POM)** - Separation of page structure from tests
- **Factory Pattern** - WebDriver creation
- **Singleton Pattern** - Configuration management
- **Template Method** - Base test/page classes

### Framework Capabilities
- ✅ Multi-browser support (Chrome, Firefox, Edge)
- ✅ Headless execution for CI/CD
- ✅ Environment-based configuration
- ✅ Parallel test execution
- ✅ Screenshot on failure
- ✅ Automatic retry for flaky tests
- ✅ Comprehensive logging
- ✅ Explicit waits (no Thread.sleep)

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Chrome/Firefox/Edge browser installed

### Installation

```bash
# Clone repository
git clone <repository-url>
cd selenium-automation-framework

# Install dependencies
mvn clean install -DskipTests
```

### Running Tests

```bash
# Run all tests
mvn test

# Run with specific browser
mvn test -Dbrowser=firefox

# Run in headless mode
mvn test -Dheadless=true

# Run specific test groups
mvn test -Dgroups=smoke

# Run with specific environment
mvn test -Denv=qa

# Combined example
mvn test -Dbrowser=chrome -Dheadless=true -Denv=qa
```

### Maven Profiles

```bash
# Chrome profile
mvn test -Pchrome

# Firefox profile
mvn test -Pfirefox

# Headless profile
mvn test -Pheadless

# QA environment
mvn test -Pqa
```

## 📁 Key Components

### ConfigReader
Thread-safe singleton for reading YAML configuration files.
```java
String baseUrl = ConfigReader.getInstance().get("base.url");
int timeout = ConfigReader.getInstance().getInt("timeout.explicit", 15);
```

### DriverFactory
Thread-safe WebDriver factory with ThreadLocal storage.
```java
DriverFactory.initDriver();
WebDriver driver = DriverFactory.getDriver();
DriverFactory.quitDriver();
```

### BasePage
Abstract base class providing common page actions.
```java
public class LoginPage extends BasePage {
    public void enterUsername(String username) {
        type(usernameInput, username);
    }
}
```

### WaitUtils
Comprehensive explicit wait utilities.
```java
WebElement element = WaitUtils.waitForVisibility(locator);
WaitUtils.waitForClickable(locator);
WaitUtils.waitForPageLoad();
```

## 📝 Writing Tests

### Test Structure (AAA Pattern)
```java
@Test
public void testValidLogin() {
    // Arrange
    String username = "validuser";
    String password = "validpass";

    // Act
    loginPage.login(username, password);

    // Assert
    assertThat(loginPage.isLoginSuccessful()).isTrue();
}
```

### Data-Driven Tests
```java
@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return new Object[][] {
        {"user1", "pass1"},
        {"user2", "pass2"}
    };
}

@Test(dataProvider = "loginData")
public void testLogin(String username, String password) {
    loginPage.login(username, password);
}
```

## 🔧 Configuration

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `browser` | Browser to use | chrome |
| `headless` | Headless mode | false |
| `env` | Environment | dev |

### Config Files
- `config.yaml` - Default configuration
- `config-dev.yaml` - Development overrides
- `config-qa.yaml` - QA environment settings

## 📊 Reports & Logs

- **Screenshots**: `target/screenshots/`
- **Logs**: `target/logs/automation.log`
- **TestNG Reports**: `target/surefire-reports/`

## 🧪 Test Groups

| Group | Description |
|-------|-------------|
| smoke | Critical path tests |
| regression | Full regression suite |
| negative | Negative test cases |
| ui | UI validation tests |

## 👨‍💻 Author

**Harsha Kumar**

## 📄 License

This project is licensed under the MIT License.
