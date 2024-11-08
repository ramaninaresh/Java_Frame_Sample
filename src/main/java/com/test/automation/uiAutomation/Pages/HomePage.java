package com.test.automation.uiAutomation.Pages;

import com.test.automation.uiAutomation.configLoader.ConfigLoader;
import com.test.automation.uiAutomation.googleVision.GoogleVisionApi;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.test.automation.uiAutomation.testBase.TestBase;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

public class HomePage extends TestBase {

	public static final Logger log = Logger.getLogger(HomePage.class.getName());
	private GoogleVisionApi visionAPI;

	WebDriver HomePagedriver;
	String credentialsPath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/googleVision/auth.json";


	@FindBy(xpath = "//a[contains(@href, '/collections/cats')]/button")
	WebElement ShopCatsLink;

	public HomePage(WebDriver BaseDriver) throws Exception {
		this.HomePagedriver = BaseDriver;
		PageFactory.initElements(HomePagedriver, this);
		String credentialsPath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/googleVision/auth.json";
		log.info("Using Google Vision credentials from: " + credentialsPath);
		this.visionAPI = new GoogleVisionApi(credentialsPath);
		log.info("HomePageDriver Initialized...!");
	}

	public void openHomePage() throws InterruptedException {
		try {
			// Load the main configuration to get the home URL
			ConfigLoader configLoader = new ConfigLoader();
			Properties configProperties = configLoader.loadMainConfig(getConfigPath());
			String homeUrl = configProperties.getProperty("url");

			// Log and navigate to the home URL
			log.info("Navigating to the home page: " + homeUrl);
			HomePagedriver.get(homeUrl);

			// Wait for the page to load completely using TestBase's method
			waitForPageLoad();

		} catch (IOException e) {
			log.error("Error loading home page URL from config: ", e);
		}
	}

	public boolean isShopCatsLinkTextDisplayed() {
		try {
			// Wait for the ShopCatsLink to be visible
			waitForElementVisibility(ShopCatsLink);

			// Load language-specific properties using reusable method from TestBase
			Properties languageProperties = loadLanguageSpecificProperties();
			if (languageProperties == null) {
				log.info("Language properties could not be loaded.");
				return false;
			}

			// Get the expected Text
			String expectedText = languageProperties.getProperty("ShopCatsLinkText");
			log.info("Expected text: " + expectedText);

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
			// Wait for the ShopCatsLink to be visible
			waitForElementVisibility(ShopCatsLink);

			// Load language-specific properties using reusable method from TestBase
			Properties languageProperties = loadLanguageSpecificProperties();
			if (languageProperties == null) {
				log.info("Language properties could not be loaded.");
				return false;
			}

			// Get the expected Text
			String expectedText = languageProperties.getProperty("ShopCatsLinkText");
			log.info("Expected text: " + expectedText);

			if (expectedText == null) {
				log.info("Expected text for 'ShopCatsLinkText' not found in language properties.");
				return false;
			}

			// Take screenshot of the ShopCatsLink element and save it
			String imagePath=getElementScreenshot(HomePagedriver, ShopCatsLink, "ShopCatsLink");

			log.info("Image Path = " +imagePath );

			String actualTextFromImage = visionAPI.detectTextFromImage(imagePath);

			if (actualTextFromImage == null) {
				log.info("No text detected from the screenshot.");
				return false;
			}

			log.info("Actual text from ShopCatsLink screenshot: " + actualTextFromImage);

			// Normalize the actual text and compare
			String regexPattern = "(" + Pattern.quote(expectedText) + "\\s*)+";
			String normalizedText = actualTextFromImage.replaceAll(regexPattern, expectedText).trim();

			boolean result = normalizedText.equals(expectedText.trim());
			if (result) {
				log.info("Text matches successfully: Normalized text = " + normalizedText + ", Expected text = " + expectedText);
			} else {
				log.info("Text did not match: Normalized text = " + normalizedText + ", Expected text = " + expectedText);
			}
			return result;

		} catch (Exception e) {
			log.error("Error occurred while checking ShopCatsLink text using Vision: ", e);
			return false;
		}
	}

	public boolean clickAndVerifyShopCatsLinkUrl() {
		try {
			// Wait for the ShopCatsLink button to be clickable
			waitForElementToBeClickable(ShopCatsLink);

			// Click the ShopCatsLink button
			ShopCatsLink.click();
			log.info("Clicked on ShopCatsLink button");

			// Wait for navigation (you may adjust the wait time if needed)
			Thread.sleep(3000);

			// Verify if the URL is as expected
			String expectedUrl = "https://www.edgardcooper.com/fr/collections/cats/";
			String currentUrl = HomePagedriver.getCurrentUrl();

			log.info("Expected URL: " + expectedUrl);
			log.info("Actual URL: " + currentUrl);

			// Return true if the current URL matches the expected URL
			return currentUrl.equals(expectedUrl);

		} catch (Exception e) {
			log.error("Error occurred while clicking on ShopCatsLink and verifying URL: ", e);
			return false;
		}
	}

	public boolean isCatlinkPresentUsingGoogleVisionOCR() {
		// Take a screenshot of the `ShopCatsLink` element
		log.info("Taking a snapshot of the element");
		String elementImagePath = getElementScreenshot(HomePagedriver, ShopCatsLink, "ShopCatsLink");

		log.info("Element image Path found as = " + elementImagePath);

		// Take a full-screen screenshot for reference
		String screenImagePath = getScreenshot(HomePagedriver,"fullScreen", "GoogleVision");

		try {
			// Check for the presence of the element image on the screen using Google Vision API
			return visionAPI.isElementImagePresentOCR(elementImagePath, screenImagePath);
		} catch (IOException e) {
			log.error("Error checking presence of ShopCatsLink using Google Vision: ", e);
			return false;
		}
	}

}
