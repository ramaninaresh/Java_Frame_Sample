package com.test.automation.uiAutomation.customListner;

import com.relevantcodes.extentreports.LogStatus;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import com.test.automation.uiAutomation.testBase.TestBase;

public class Listener extends TestBase implements ITestListener {

	@Override
	public void onFinish(ITestContext context) {
		Reporter.log("======= Execution Finished: " + context.getName() + " =======");
		log.info("======= Execution Finished: " + context.getName() + " =======");
	}

	@Override
	public void onStart(ITestContext context) {
		Reporter.log("======= Execution Started: " + context.getName() + " =======");
		log.info("======= Execution Started: " + context.getName() + " =======");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// Optional to implement, left empty
	}

	@Override
	public void onTestFailure(ITestResult result) {
	}

	@Override
	public void onTestSkipped(ITestResult result) {

	}

	@Override
	public void onTestStart(ITestResult result) {
		Reporter.log("======= Test Started: " + result.getName() + " =======");
		log.info("======= Test Started: " + result.getName() + " =======");
	}

	@Override
	public void onTestSuccess(ITestResult result) {

	}
}
