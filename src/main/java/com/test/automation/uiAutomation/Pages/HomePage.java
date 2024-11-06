package com.test.automation.uiAutomation.Pages;

import com.test.automation.uiAutomation.configLoader.ConfigLoader;
import com.test.automation.uiAutomation.googleVision.GoogleVisionApi;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.test.automation.uiAutomation.testBase.TestBase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.regex.Pattern;

public class HomePage extends TestBase {

	public static final Logger log = Logger.getLogger(HomePage.class.getName());

	WebDriver driver;
	String credentialsPath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/googleVision/googleconfig.json";


	@FindBy(xpath = "//a[contains(@href, '/collections/cats')]/button")
	WebElement ShopCatsLink;

	public HomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public boolean isShopCatsLinkTextDisplayed() {
		try {
			// Load main configuration properties file from class ConfigLoader
			ConfigLoader configLoader = new ConfigLoader();

			String configPath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/config/config.properties";
			Properties configProperties = configLoader.loadMainConfig(configPath);
			String language = configProperties.getProperty("language");
			log.info("language : " + language);

			// Load language-specific properties file
			Properties languageProperties = configLoader.loadLanguageProperties(language);
			String expectedText = languageProperties.getProperty("ShopCatsLinkText");
			log.info("expectedText : " + expectedText);

			if (expectedText == null) {
				log.info("Expected text for 'ShopCatsLinkText' not found in language properties.");
				return false;
			}

			String actualText = ShopCatsLink.getText();
			if (actualText == null) {
				log.info("Actual text of ShopCatsLink is null.");
				return false;
			}

			if (actualText.equals(expectedText)) {
				log.info("ShopCatsLink text is displayed as expected: " + actualText);
				return true;
			} else {
				log.info("ShopCatsLink text does not match. Expected: " + expectedText + ", but got: " + actualText);
				return false;
			}

		} catch (Exception e) {
			log.error("Error occurred while checking ShopCatsLink text: ", e);
			return false;
		}
	}

	public boolean isShopCatsLinkTextDisplayedUsingVision() {
		try {
			// Load main configuration properties file from class ConfigLoader
			ConfigLoader configLoader = new ConfigLoader();
			String configPath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/config/config.properties";
			Properties configProperties = configLoader.loadMainConfig(configPath);
			String language = configProperties.getProperty("language");
			log.info("language : " + language);

			// Load language-specific properties file
			Properties languageProperties = configLoader.loadLanguageProperties(language);
			String expectedText = languageProperties.getProperty("ShopCatsLinkText");
			log.info("expectedText : " + expectedText);

			if (expectedText == null) {
				log.info("Expected text for 'ShopCatsLinkText' not found in language properties.");
				return false;
			}

			// Take screenshot of the ShopCatsLink element and save it

			getElementScreenshot(driver,ShopCatsLink, "ShopCatsLink"); // Calls the method to take a screenshot of the element
			//File will be saved with the name eg  "ShopCatsLink_06_11_2024_16_59_59.png"

			// Get the path of the saved image using the same name with timestamp
			String imagePath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/resources/ShopCatsLink_" + new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss").format(Calendar.getInstance().getTime()) + ".png";
		//	String imagePath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/resources/ShopCatsLink_" +".png";

			GoogleVisionApi visionAPI = new GoogleVisionApi(credentialsPath);
			String actualTextFromImage = visionAPI.detectTextFromImage(imagePath);

			if (actualTextFromImage == null) {
				log.info("No text detected from the screenshot.");
				return false;
			}

			log.info("Actual text from ShopCatsLink screenshot: " + actualTextFromImage);

			// Split the actual text to get the first phrase (assuming phrases are separated by spaces)
			String[] words = expectedText.trim().split("\\s+");
			String firstPhrase = String.join(" ", words);  // Join the expected text words as a phrase

			// Build a dynamic regex pattern based on the expected phrase
			String regexPattern = "(" + Pattern.quote(firstPhrase) + "\\s*)+";

			// Normalize the actual text by replacing repeated occurrences of the phrase
			String normalizedText = actualTextFromImage.replaceAll(regexPattern, firstPhrase).trim();

			// Compare the normalized text with the expected text
			boolean result =normalizedText.equals(expectedText.trim());
			if(result)
			{
				log.info("Result match successfully : normalized Text = " + normalizedText + " , expected Text = " +expectedText);
			}
			else {
				log.info("Result did not matched : normalized Text = " + normalizedText + " , expected Text = " +expectedText);
			}
			return result;

		} catch (Exception e) {
			log.error("Error occurred while checking ShopCatsLink text using Vision: ", e);
			return false;
		}
	}

}
