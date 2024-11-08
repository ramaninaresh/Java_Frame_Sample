package com.test.automation.uiAutomation.HomePage_test;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.uiAutomation.testBase.TestBase;
import com.test.automation.uiAutomation.Pages.HomePage;


public class TC002 extends TestBase {

	@Test
	public void TC__2() throws Exception {
		HomePage homepage = new HomePage(driver);
		homepage.openHomePage();
		boolean isTextDisplayed = homepage.isShopCatsLinkTextDisplayed();
		Assert.assertTrue(isTextDisplayed, "ShopCatsLink text is not displayed as expected.");

	}
}
