package com.test.automation.uiAutomation.testBase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Properties;

import com.test.automation.uiAutomation.configLoader.ConfigLoader;
import com.test.automation.uiAutomation.googleVision.GoogleVisionApi;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.test.automation.uiAutomation.customListner.WebEventListener;
import com.test.automation.uiAutomation.excelReader.Excel_Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TestBase {
	public static final int DEFAULT_WAIT_TIME = 10; // 10 seconds as default wait time

	public static final Logger log = Logger.getLogger(TestBase.class.getName());
	public WebDriver driver;
	Excel_Reader excel;
	public WebEventListener eventListener;
	private Properties OR = new Properties();
	private Properties languageProperties = new Properties();
	public static ExtentReports extent;
	protected static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	public ITestResult result;


	static {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		extent = new ExtentReports(System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/report/test" + formater.format(calendar.getTime()) + ".html", false);
		log.info("Suite has Started...!");

	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public Properties loadLanguageSpecificProperties() {
		try {
			ConfigLoader configLoader = new ConfigLoader();

			// Load main configuration properties
			String configPath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/config/config.properties";
			Properties configProperties = configLoader.loadMainConfig(configPath);

			// Get the language setting from main configuration
			String language = configProperties.getProperty("language");

			// Load and return language-specific properties
			return configLoader.loadLanguageProperties(language);

		} catch (Exception e) {
			log.error("Error occurred while loading language-specific properties: ", e);
			return null;
		}
	}

	public void init(String browser) throws IOException {
		log.info("Project Initialization Started...!");
		configureLogging();
		ConfigLoader configLoader = new ConfigLoader();
		Properties configProperties = configLoader.loadMainConfig(getConfigPath());
		log.info("Project Configuration file loaded");

		languageProperties = configLoader.loadLanguageProperties(configProperties.getProperty("language"));
		log.info("Language specific Data file loaded");

		selectBrowser(browser);
		log.info("Browser Opened");
		getUrl(configProperties.getProperty("url"));
		log.info("Website Opened");
	}





	private void configureLogging() {
		String log4jConfPath = "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		log.info("Log4J configuration loaded");
	}

	protected String getConfigPath() {
		return System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/config/config.properties";
	}

	public void selectBrowser(String browser) {
		log.info(("Initialzing Base Driver"));
		if (browser.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
			driver = new ChromeDriver();
		} else if (browser.equals("firefox")) {
			FirefoxOptions options = new FirefoxOptions();
			options.setBinary("C:\\Users\\163639\\AppData\\Local\\Mozilla Firefox\\firefox.exe");
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/drivers/geckodriver.exe");
			driver = new FirefoxDriver(options);
		}
		log.info(("Base Driver is initialized"));
		driver.manage().window().maximize();
		log.info(("Miximizing Window"));
	}

	public void getUrl(String url) {
		log.info("Navigating to: " + url);
		driver.get(url);
	}

	public String[][] getData(String excelName, String sheetName) {
		String path = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/data/" + excelName;
		excel = new Excel_Reader(path);
		return excel.getDataFromSheet(sheetName, excelName);
	}

	public String getScreenshot(WebDriver driver,String screenshotName, String folderName) {
		log.info("Taking Full Screen Screenshot");
		if (driver == null) {
			log.error("Screenshot capture failed. WebDriver is not initialized.");
			return null;
		}

		String relativePath = "/src/main/java/com/test/automation/uiAutomation/screenshot/" + folderName + "/" + screenshotName + "_" + System.currentTimeMillis() + ".png";
		String absolutePath = System.getProperty("user.dir") + relativePath;

		try {
			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcFile, new File(absolutePath));
			log.info("Screenshot saved at: " + absolutePath);

			// Use the absolute path in URL format for Extent Report compatibility
			String reportPath = "file:///" + absolutePath.replace("\\", "/");

			// Add the screenshot to Extent Report with the full file path
		//	test.get().log(LogStatus.INFO, "Screenshot -> " + test.get().addScreenCapture(reportPath));

			return absolutePath;

		} catch (IOException e) {
			log.error("Error saving screenshot: " + e.getMessage());
			return null;
		}
	}


	public String  getElementScreenshot(WebDriver driver,WebElement element, String name) {
		if (driver == null) {
			log.error("Driver is not initialized. Cannot take screenshot.");
			return null;
		}
		String timestamp = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss").format(Calendar.getInstance().getTime());
		try {
			File elementScreenshot = element.getScreenshotAs(OutputType.FILE);
			String resourceDirectory = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/resources/";
			File destFile = new File(resourceDirectory + name + "_" + timestamp + ".png");
			Files.copy(elementScreenshot.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			log.info("Screenshot of element saved as: " + destFile.getAbsolutePath());
			return destFile.getAbsolutePath();
		} catch (IOException e) {
			log.error("Error occurred while saving element screenshot: ", e);
			return null;
		}
	}

	public static void highlightElement(WebDriver driver, WebElement element) throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].style.border='4px solid yellow'", element);
		Thread.sleep(3000);
		js.executeScript("arguments[0].style.border=''", element);
	}

	@BeforeTest
	public void TestSetup() throws IOException {
		log.info("Test Started");
	}

	@BeforeSuite
	public void SuiteSetup() throws IOException {
		log.info("Suite Started");
	}

	@BeforeClass
	@Parameters("browser")
	public void setUp(@Optional("chrome") String browser) throws IOException {
		init(browser);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
		extent.endTest(test.get());
		log.info("Browser Closed and Extent Report updated with Test Result");
	}


	@BeforeMethod
	public void startTest(Method result) {
		ExtentTest extentTest = extent.startTest(result.getName());
		test.set(extentTest);
		test.get().log(LogStatus.INFO, result.getName() + " test Started");
		log.info("Test Started: " + result.getName());
	}

	@AfterMethod
	public void updateTestResult(ITestResult result) {
		String screenshotPath = getScreenshot(driver,result.getName(), "Test_Status_screenshots");

		if (result.getStatus() == ITestResult.SUCCESS) {
			test.get().log(LogStatus.PASS, result.getName() + " Test has passed");
			if (screenshotPath != null) {
				test.get().log(LogStatus.PASS, test.get().addScreenCapture(screenshotPath));
			}
			log.info("Test passed: " + result.getName());
			Reporter.log("Test passed: " + result.getMethod());
		} else if (result.getStatus() == ITestResult.FAILURE) {
			test.get().log(LogStatus.FAIL, result.getName() + " test has failed", result.getThrowable());
			if (screenshotPath != null) {
				test.get().log(LogStatus.FAIL, test.get().addScreenCapture(screenshotPath));
			}
			log.info("Test failed: " + result.getName());
			Reporter.log("Test failed: " + result.getMethod());
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.get().log(LogStatus.SKIP, result.getName() + " test has been skipped");
			log.info("Test skipped: " + result.getName());
			Reporter.log("Test skipped: " + result.getMethod());
		}

		extent.endTest(test.get());
		extent.flush();
		log.info("Extent Report updated with Test method result..!");
	}

	// Explicit Wait for element to be visible
	public void waitForElementVisibility(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	// Explicit Wait for element to be clickable
	public void waitForElementToBeClickable(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME));
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	// Explicit Wait for element to be present in DOM
	public void waitForElementPresence(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME));
		wait.until(ExpectedConditions.presenceOfElementLocated((By) element));
	}

	// Wait for page to load completely
	public void waitForPageLoad() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME));
		/*wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
	*/
		Thread.sleep(5000);
	}

}
