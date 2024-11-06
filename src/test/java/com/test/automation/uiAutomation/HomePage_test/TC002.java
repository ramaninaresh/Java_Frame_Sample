package com.test.automation.uiAutomation.HomePage_test;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.uiAutomation.testBase.TestBase;
import com.test.automation.uiAutomation.Pages.HomePage;


public class TC002 extends TestBase {

	HomePage homepage;




	@Test
	public void TC__2() {
		try {
			log.info("=======Starting TC2 test========");
			homepage = new HomePage(driver);

			boolean isTextDisplayed = homepage.isShopCatsLinkTextDisplayed();
			Assert.assertTrue(isTextDisplayed, "ShopCatsLink text is not displayed as expected.");

			log.info("=======Finished TC2 test========");
			getScreenShot("TC2_Pass");

		} catch (AssertionError e) {
			log.error("Assertion Error in TC2: " + e.getMessage());
			getScreenShot("Assertion_Error_TC__2");
			throw e;
		} catch (Exception e) {
			log.error("Exception in TC2: " + e.getMessage());
			getScreenShot("Exception_Error_TC__2");
			throw e;
		}
	}



}
