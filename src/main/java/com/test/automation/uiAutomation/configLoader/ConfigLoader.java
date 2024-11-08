package com.test.automation.uiAutomation.configLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigLoader {

    private static final Logger log = Logger.getLogger(ConfigLoader.class);

    public Properties loadMainConfig(String configFilePath) throws IOException {
        Properties configProperties = new Properties();
        try (FileInputStream configStream = new FileInputStream(new File(configFilePath))) {
            configProperties.load(configStream);

        } catch (IOException e) {
            log.error("Failed to load main config file at: " + configFilePath, e);
            throw e;
        }
        return configProperties;
    }

    public Properties loadLanguageProperties(String language) throws IOException {
        Properties languageProperties = new Properties();
        String languageFilePath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/Language/" + language + ".properties";
        File languageFile = new File(languageFilePath);

        if (!languageFile.exists()) {
            log.error("Language properties file does not exist: " + languageFilePath);
            throw new IOException("Language properties file not found: " + languageFilePath);
        }

        try (FileInputStream langStream = new FileInputStream(languageFile)) {
            languageProperties.load(langStream);
        } catch (IOException e) {
            log.error("Failed to load language properties file: " + languageFilePath, e);
            throw e;
        }

        return languageProperties;
    }
}

