package com.test.automation.uiAutomation.HomePage_test;

import org.testng.annotations.Test;
import com.test.automation.uiAutomation.testBase.TestBase;
import com.test.automation.uiAutomation.Pages.HomePage;
import org.testng.Assert;

public class TC001 extends TestBase {

	@Test
	public void TC__1_1() throws Exception {
		HomePage homepage = new HomePage(driver);
		Thread.sleep(3000);

		// Step 1: Check the button text for "Cat" using Google Vision by sending element image and getting the OCT text
		boolean isTextDisplayed = homepage.isShopCatsLinkTextDisplayedUsingVision();
		Assert.assertTrue(isTextDisplayed, "ShopCatsLink text is not displayed as expected.");

		// Step 2: Click the button and verify the URL
		boolean isButtonClicked = homepage.clickAndVerifyShopCatsLinkUrl();
		Assert.assertTrue(isButtonClicked, "Button click did not navigate to the expected URL.");
	}

	@Test
	public void TC__1_2() throws Exception {
		HomePage homepage = new HomePage(driver);
		homepage.openHomePage();
		Thread.sleep(3000);

		// Step 1: Check the button text for "Cat" using getText()
		boolean isTextDisplayed = homepage.isShopCatsLinkTextDisplayed();
		Assert.assertTrue(isTextDisplayed, "ShopCatsLink text is not displayed as expected.");
	}

	@Test
	public void TC__1_3() throws Exception {
		HomePage homepage = new HomePage(driver);
		homepage.openHomePage();
		Thread.sleep(3000);

		// Step 1:  Check if the Cat link is displayed on the screen using Google Vision by finding image with in the Screen
		boolean isTextDisplayed = homepage.isCatlinkPresentUsingGoogleVisionOCR();
		Assert.assertTrue(isTextDisplayed, "Cat link is present on the screen.");

	}

}
