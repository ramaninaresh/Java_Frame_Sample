package com.test.automation.uiAutomation.HomePage_test;

import org.testng.annotations.Test;

import com.test.automation.uiAutomation.testBase.TestBase;
import com.test.automation.uiAutomation.Pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TC001 extends TestBase{
	HomePage homepage;



	@Test
	public void TC__1() throws InterruptedException {
		try {
			log.info("=======Starting TC1 test========");
			homepage = new HomePage(driver);
			Thread.sleep(3000);
			boolean isTextDisplayed = homepage.isShopCatsLinkTextDisplayedUsingVision();
			Assert.assertTrue(isTextDisplayed, "ShopCatsLink text is not displayed as expected.");

			log.info("=======Finished TC1 test========");
			Thread.sleep(10000);
			getScreenShot("TC1_Pass");

		} catch (AssertionError e) {
			log.error("Assertion Error in TC1: " + e.getMessage());
			getScreenShot("Assertion_Error_TC__1");
			throw e;
		} catch (Exception e) {
			log.error("Exception in TC1: " + e.getMessage());
			getScreenShot("Exception_Error_TC__1");
			throw e;
		}
	}



}
