package koyaltesting.ExtentReports;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportsDemo {

    public WebDriver driver;
    public ExtentReports extent;
    public ExtentTest test;

    // Define WebElements using @FindBy annotations
    @FindBy(className = "react-select__control")
    WebElement className;

    @FindBy(xpath = "//div[text()='Zong']")
    WebElement xpath;

    @FindBy(css = ".chakra-button")
    WebElement cssSelector;

    @FindBy(className = "iti__tel-input")
    WebElement className1;

    @FindBy(className = "chakra-button")
    WebElement className2;

    @FindBy(className = "underline")
    WebElement className3;

    @FindBy(xpath = "//*[@id=\"pin-input-:r14:-0\"]")
    WebElement pinInput1;

    @FindBy(xpath = "//*[@id=\"pin-input-:r14:-1\"]")
    WebElement pinInput2;

    @FindBy(xpath = "//*[@id=\"pin-input-:r14:-2\"]")
    WebElement pinInput3;

    @FindBy(xpath = "//*[@id=\"pin-input-:r14:-3\"]")
    WebElement pinInput4;

    @FindBy(name = "phone")
    WebElement phoneNumberInput;

    @FindBy(className = "chakra-button")
    WebElement submitButton;

    @BeforeTest
    public void config() {
        // Setting up Extent Reports
        String path = System.getProperty("user.dir") + "/ExtentReport.html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
        reporter.config().setReportName("Web Automation Results");
        reporter.config().setDocumentTitle("Koyal Automation Results");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Tester", "Noreen");

        // Initialize WebDriver
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\noreen.iqbal\\Downloads\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Initialize PageFactory elements
        PageFactory.initElements(driver, this);
    }

    @Test(priority = 1)
    public void initialdemo() {
        test = extent.createTest("InitialDemo");
        driver.get("https://koyal.pk/login");
        test.info("Navigated to the login page");
        test.info("Page title is: " + driver.getTitle());
        try {
            String screenshotPath = getScreenshot("InitialDemo");
            test.addScreenCaptureFromPath(screenshotPath, "InitialDemo Screenshot");
        } catch (IOException e) {
            test.warning("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void LandingPage() {
        test = extent.createTest("LandingPage");
        test.info("LandingPage elements initialized using PageFactory.");
        try {
            String screenshotPath = getScreenshot("LandingPage");
            test.addScreenCaptureFromPath(screenshotPath, "LandingPage Screenshot");
        } catch (IOException e) {
            test.warning("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void loginApplication() throws InterruptedException {
        test = extent.createTest("LoginApplication");

        className.click();
        test.info("Selected dropdown.");

        xpath.click();
        test.info("Clicked on Zong option.");

        cssSelector.click();
        test.info("Clicked on CSS selector button.");

        className1.sendKeys("3702241950");
        test.info("Entered phone number.");

        className2.click();
        test.info("Clicked on submit button.");

        Thread.sleep(31000); // Simulate OTP wait

        className3.click();
        test.info("Clicked on verification link.");

        pinInput1.sendKeys("0");
        pinInput2.sendKeys("0");
        pinInput3.sendKeys("0");
        pinInput4.sendKeys("0");
        test.info("Entered OTP.");

        Thread.sleep(3000);
        phoneNumberInput.sendKeys("3702241950");
        test.info("Re-entered phone number.");

        submitButton.click();
        test.info("Submitted the form.");
        try {
            String screenshotPath = getScreenshot("LoginApplication");
            test.addScreenCaptureFromPath(screenshotPath, "LoginApplication Screenshot");
        } catch (IOException e) {
            test.warning("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @AfterMethod
    public void captureFailureScreenshot(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            try {
                String screenshotPath = getScreenshot(result.getName());
                test.fail("Test Case Failed: " + result.getThrowable());
                test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
            } catch (IOException e) {
                test.fail("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        extent.flush();
    }

    public String getScreenshot(String testCaseName) throws IOException {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String destinationPath = System.getProperty("user.dir") + "/Report/" + testCaseName + ".png";
        File destination = new File(destinationPath);

        // Ensure directory exists
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }

        FileUtils.copyFile(source, destination);
        return destinationPath;
    }
}
