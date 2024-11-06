package com.test.automation.uiAutomation.testBase;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.test.automation.uiAutomation.configLoader.ConfigLoader;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.OutputType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


import javax.imageio.ImageIO;


public class TestBase {

	public static final Logger log = Logger.getLogger(TestBase.class.getName());

	public WebDriver driver;
	Excel_Reader excel;

	public WebEventListener eventListener;
	private Properties OR = new Properties();
	private Properties languageProperties = new Properties();
	public static ExtentReports extent;
	protected static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	public ITestResult result;

	public WebDriver getDriver() {
		return driver;
	}

	static {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		extent = new ExtentReports(System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/report/test" + formater.format(calendar.getTime()) + ".html", false);
	}





	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void init(String browser) throws IOException {

		// Configure Log4j
		String log4jConfPath = "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

		// Load main configuration properties file from class ConfigLoader
		ConfigLoader configLoader = new ConfigLoader();
		String configPath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/config/config.properties";
		Properties configProperties = configLoader.loadMainConfig(configPath);

		// Load language-specific properties file
		String language = configProperties.getProperty("language");
		languageProperties = configLoader.loadLanguageProperties(language);

		// Get URL from the main config properties
		// Select and initialize the browser
		selectBrowser(browser); // Use the browser directly from testng.xml
		getUrl(configProperties.getProperty("url"));

		log.info("Initialization is successful...!");
	}
	// Overloaded method that defaults to Chrome
	public void selectBrowser() {
		selectBrowser("chrome");
	}

	public void selectBrowser(String browser) {
		if (browser.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().window().maximize();


		} else if (browser.equals("firefox")) {
			FirefoxOptions options = new FirefoxOptions();
			options.setBinary("C:\\Users\\163639\\AppData\\Local\\Mozilla Firefox\\firefox.exe");
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/drivers/geckodriver.exe");
			driver = new FirefoxDriver(options);
			driver.manage().window().maximize();
			eventListener = new WebEventListener();
		}
	}

	public void getUrl(String url) {
		log.info("navigating to :-" + url);
		log.info("Language text fetched :-" + languageProperties.getProperty("ShopCatsLinkText"));

		driver.get(url);
		log.info("Browser Opened");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	public String[][] getData(String excelName, String sheetName) {
		String path = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/data/" + excelName;
		excel = new Excel_Reader(path);
		String[][] data = excel.getDataFromSheet(sheetName, excelName);
		return data;
	}

	public void waitForElement(WebDriver driver, java.time.Duration timeOutInSeconds, WebElement element) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(element));
		highlightMe(driver,element);
	}

	public void getScreenShot(String name) {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		try {
			String reportDirectory = new File(System.getProperty("user.dir")).getAbsolutePath() + "/src/main/java/com/test/automation/uiAutomation/screenshot/";
			File destFile = new File((String) reportDirectory + name + "_" + formater.format(calendar.getTime()) + ".png");
			FileUtils.copyFile(scrFile, destFile);
			// This will help us to link the screen shot in testNG report
			Reporter.log("<a href='" + destFile.getAbsolutePath() + "'> <img src='" + destFile.getAbsolutePath() + "' height='100' width='100'/> </a>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getElementScreenshot(WebDriver driver, WebElement element, String name) {
		if (driver == null) {
			log.error("Driver is not initialized. Cannot take screenshot.");
			return;
		}

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

		try {
			// Capture the element screenshot directly
			File elementScreenshot = element.getScreenshotAs(OutputType.FILE);

			// Define the destination file path
			String resourceDirectory = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/resources/";
			File destFile = new File(resourceDirectory + name + "_" + formatter.format(calendar.getTime()) + ".png");

			// Copy the element screenshot to the destination
			Files.copy(elementScreenshot.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			log.info("Screenshot of element saved as: " + destFile.getAbsolutePath());

		} catch (IOException e) {
			log.error("Error occurred while saving element screenshot: ", e);
		}
	}

	public static void highlightMe(WebDriver driver, WebElement element) throws InterruptedException {
		// Creating JavaScriptExecuter Interface
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// Execute javascript
		js.executeScript("arguments[0].style.border='4px solid yellow'", element);
		Thread.sleep(3000);
		js.executeScript("arguments[0].style.border=''", element);
	}

	public Iterator<String> getAllWindows() {
		Set<String> windows = driver.getWindowHandles();
		Iterator<String> itr = windows.iterator();
		return itr;
	}

	public void getScreenShot(WebDriver driver, ITestResult result, String folderName) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

		String methodName = result.getName();

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			String reportDirectory = new File(System.getProperty("user.dir")).getAbsolutePath() + "/src/main/java/com/test/automation/uiAutomation/";
			File destFile = new File((String) reportDirectory + "/" + folderName + "/" + methodName + "_" + formater.format(calendar.getTime()) + ".png");

			FileUtils.copyFile(scrFile, destFile);

			Reporter.log("<a href='" + destFile.getAbsolutePath() + "'> <img src='" + destFile.getAbsolutePath() + "' height='100' width='100'/> </a>");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getScreenShotOnSucess(WebDriver driver, ITestResult result) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

		String methodName = result.getName();

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			String reportDirectory = new File(System.getProperty("user.dir")).getAbsolutePath() + "/src/main/java/com/test/automation/uiAutomation/";
			File destFile = new File((String) reportDirectory + "/failure_screenshots/" + methodName + "_" + formater.format(calendar.getTime()) + ".png");

			FileUtils.copyFile(scrFile, destFile);

			Reporter.log("<a href='" + destFile.getAbsolutePath() + "'> <img src='" + destFile.getAbsolutePath() + "' height='100' width='100'/> </a>");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String captureScreen(String fileName) {
		if (fileName == "") {
			fileName = "blank";
		}
		File destFile = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		try {
			String reportDirectory = new File(System.getProperty("user.dir")).getAbsolutePath() + "/src/main/java/com/test/automation/uiAutomation/screenshot/";
			destFile = new File((String) reportDirectory + fileName + "_" + formater.format(calendar.getTime()) + ".png");
			FileUtils.copyFile(scrFile, destFile);
			// This will help us to link the screen shot in testNG report
			Reporter.log("<a href='" + destFile.getAbsolutePath() + "'> <img src='" + destFile.getAbsolutePath() + "' height='100' width='100'/> </a>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFile.toString();
	}



	public void getresult(ITestResult result) {
		if (result.getStatus() == ITestResult.SUCCESS) {
			test.get().log(LogStatus.PASS, result.getName() + " test is pass");
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.get().log(LogStatus.SKIP, result.getName() + " test is skipped and skip reason is:-" + result.getThrowable());
		} else if (result.getStatus() == ITestResult.FAILURE) {
			test.get().log(LogStatus.ERROR, result.getName() + " test is failed" + result.getThrowable());
			String screen = captureScreen("");
			test.get().log(LogStatus.FAIL, test.get().addScreenCapture(screen));
		} else if (result.getStatus() == ITestResult.STARTED) {
			test.get().log(LogStatus.INFO, result.getName() + " test is started");
		}
	}

	@AfterMethod()
	public void afterMethod(ITestResult result) {
		log.info("AfterMethod Started : ");
		getresult(result);
		log.info("AfterMethod Ended : ");
	}

	@BeforeMethod
	public void beforeMethod(Method result) {
		log.info("@BeforeMethod Started : ");
		test.set(extent.startTest(result.getName())); // Correcting the syntax for setting the ThreadLocal variable
		test.get().log(LogStatus.INFO, result.getName() + " test Started");
		log.info("@BeforeMethod Ended : ");
	}


	@AfterClass(alwaysRun = true)
	public void endTest() {
		log.info("AfterClass Started : ");
		closeBrowser();
		log.info("AfterClass Ended : ");
	}

	public void closeBrowser() {
		driver.close();
		log.info("browser closed");

		extent.endTest(test.get());
		extent.flush();
	}

	public WebElement waitForElement(WebDriver driver, WebElement element, java.time.Duration timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		return element;
	}


	@BeforeClass
	@Parameters("browser") // Get the browser name from testng.xml
	public void setUp(@Optional("chrome") String browser) throws IOException {
		log.info("@BeforeClass Started : ");
		if (browser == null || browser.isEmpty()) {
			browser = "chrome"; // Default to Chrome if not specified
		}
		init(browser); // Pass the browser name to the init method
		log.info("@BeforeClass Ended : ");
	}


}
