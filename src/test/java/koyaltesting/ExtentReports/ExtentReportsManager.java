package koyaltesting.ExtentReports;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportsManager implements ITestListener {

    private ExtentSparkReporter sparkReporter;
    private ExtentReports extent;
    private ExtentTest test;

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Test Execution Started");

        // Set up the Extent Report
        String reportPath = System.getProperty("user.dir") + "/ExtentReport.html";
        sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Automation Test Report");
        sparkReporter.config().setReportName("Test Execution Results");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Tester", "Noreen");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Browser", "Chrome");
    }

    @Override
    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.log(Status.PASS, "Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.log(Status.FAIL, "Test Failed: " + result.getMethod().getMethodName());
        test.log(Status.FAIL, "Cause of Failure: " + result.getThrowable());

        WebDriver driver = null;

        try {
            // Retrieve the WebDriver instance from the test class
            driver = (WebDriver) result.getTestClass().getRealClass().getField("driver").get(result.getInstance());
        } catch (Exception e) {
            test.log(Status.FAIL, "Failed to retrieve WebDriver instance: " + e.getMessage());
            e.printStackTrace();
        }

        if (driver != null) {
            try {
                // Capture and add the screenshot to the Extent Report
                String screenshotPath = captureScreenshot(result.getMethod().getMethodName(), driver);
                test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
            } catch (IOException e) {
                test.fail("Failed to attach screenshot: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            test.log(Status.FAIL, "WebDriver instance is null. Screenshot not captured.");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.log(Status.SKIP, "Test Skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Test Execution Finished");
        extent.flush();
    }

    /**
     * Captures a screenshot and saves it to the specified location.
     *
     * @param testCaseName The name of the test case.
     * @param driver       The WebDriver instance.
     * @return The path to the saved screenshot.
     * @throws IOException If an error occurs while saving the screenshot.
     */
    
    
    public static String captureScreenshot(String testCaseName, WebDriver driver) throws IOException {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        // Define the destination path
        String destinationPath = System.getProperty("user.dir") + "./screenshots/" + testCaseName + "png";
        File destination = new File(destinationPath);

        // Ensure the screenshots directory exists
        if (destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }

        // Save the screenshot
        FileUtils.copyFile(source, destination);
      
        return destinationPath;

    
    
    }}

	
