package com.test.automation.uiAutomation.HomePage_test;

import com.relevantcodes.extentreports.LogStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.uiAutomation.testBase.TestBase;
import com.test.automation.uiAutomation.Pages.HomePage;


public class TC002 extends TestBase {

	@Test
	public void TC__2() throws Exception {
		HomePage homepage = new HomePage(driver);
		homepage.openHomePage();
		Thread.sleep(3000);

		// Step 1: Check the button text for "Cat" using getText()
		test.get().log(LogStatus.INFO, "Step 1 : Verifying Text is present");
		boolean isTextDisplayed = homepage.isShopCatsLinkTextDisplayed();

		//Step2 :
		test.get().log(LogStatus.INFO, "Step 2: Asserting Actual Text is as Expected");
		Assert.assertTrue(isTextDisplayed, "ShopCatsLink text is displayed as expected.");

	}
}
