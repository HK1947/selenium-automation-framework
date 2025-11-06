package com.automation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConfigReader - Thread-safe singleton configuration reader.
 *
 * Design Decisions:
 * 1. Singleton Pattern - Ensures single source of truth for configuration
 * 2. Environment-based loading - Supports dev/qa/prod environments
 * 3. YAML format - More readable than properties, supports nested configs
 * 4. Caching - Prevents repeated file reads
 * 5. Thread-safe - Uses ConcurrentHashMap for parallel execution support
 *
 * Usage:
 *   ConfigReader.getInstance().get("browser")
 *   ConfigReader.getInstance().get("base.url")
 *
 * @author Harsha Kumar
 * @version 1.0
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    // Volatile ensures visibility across threads
    private static volatile ConfigReader instance;

    // Thread-safe cache for configuration values
    private final Map<String, Object> configCache = new ConcurrentHashMap<>();

    // Environment configuration
    private final String environment;

    // Private constructor for singleton
    private ConfigReader() {
        // Priority: System property > Environment variable > Default
        this.environment = Optional.ofNullable(System.getProperty("env"))
                .orElse(Optional.ofNullable(System.getenv("ENV"))
                .orElse("dev"));

        loadConfiguration();
        logger.info("ConfigReader initialized for environment: {}", environment);
    }

    /**
     * Thread-safe singleton instance getter using double-checked locking.
     *
     * Why double-checked locking?
     * - First check avoids unnecessary synchronization after initialization
     * - Second check inside synchronized block prevents race conditions
     * - Volatile keyword ensures proper initialization visibility
     *
     * @return ConfigReader singleton instance
     */
    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    /**
     * Load configuration from YAML files.
     * Loads default config first, then environment-specific config to override.
     */
    @SuppressWarnings("unchecked")
    private void loadConfiguration() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        // Load default configuration
        loadConfigFile(mapper, "config/config.yaml");

        // Load environment-specific configuration (overrides defaults)
        String envConfigPath = String.format("config/config-%s.yaml", environment);
        loadConfigFile(mapper, envConfigPath);

        logger.debug("Configuration loaded: {}", configCache);
    }

    /**
     * Load a single configuration file and merge into cache.
     */
    @SuppressWarnings("unchecked")
    private void loadConfigFile(ObjectMapper mapper, String resourcePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream != null) {
                Map<String, Object> config = mapper.readValue(inputStream, Map.class);
                flattenAndCache(config, "");
                logger.info("Loaded configuration from: {}", resourcePath);
            } else {
                logger.warn("Configuration file not found: {}", resourcePath);
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration from: {}", resourcePath, e);
            throw new ConfigurationException("Failed to load config: " + resourcePath, e);
        }
    }

    /**
     * Flatten nested YAML structure into dot-notation keys.
     * Example: {browser: {name: chrome}} -> "browser.name" = "chrome"
     *
     * This allows accessing nested properties with simple dot notation:
     * ConfigReader.getInstance().get("browser.name")
     */
    @SuppressWarnings("unchecked")
    private void flattenAndCache(Map<String, Object> map, String prefix) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenAndCache((Map<String, Object>) value, key);
            } else {
                configCache.put(key, value);
            }
        }
    }

    /**
     * Get configuration value as String.
     *
     * @param key Configuration key (supports dot notation for nested values)
     * @return Configuration value or null if not found
     */
    public String get(String key) {
        // Check system property first (allows runtime override)
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }

        Object value = configCache.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    /**
     * Get configuration value with default fallback.
     *
     * @param key Configuration key
     * @param defaultValue Default value if key not found
     * @return Configuration value or default
     */
    public String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Get configuration value as Integer.
     */
    public Integer getInt(String key) {
        String value = get(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    /**
     * Get configuration value as Integer with default.
     */
    public int getInt(String key, int defaultValue) {
        Integer value = getInt(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Get configuration value as Boolean.
     */
    public Boolean getBoolean(String key) {
        String value = get(key);
        return value != null ? Boolean.parseBoolean(value) : null;
    }

    /**
     * Get configuration value as Boolean with default.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Get configuration value as Long.
     */
    public Long getLong(String key) {
        String value = get(key);
        return value != null ? Long.parseLong(value) : null;
    }

    /**
     * Get current environment.
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Reload configuration (useful for dynamic config updates).
     */
    public void reload() {
        configCache.clear();
        loadConfiguration();
        logger.info("Configuration reloaded");
    }

    /**
     * Custom exception for configuration errors.
     */
    public static class ConfigurationException extends RuntimeException {
        public ConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
