package reusableComponents;

import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.AssertJUnit;

public class SetChromeDriver 
{ 
	public static final String BASEURL = "http://prdxnstaging.com/events/";
	public static final String BASETITLE = "The Economist Events";
	
	// Set the required properties of chromedriver & create object of it.	
	public WebDriver setDriver() 
	{
		// Set the chrome driver path.
		System.setProperty("webdriver.chrome.driver","lib/driver/Chromedriver72.0.3626.69/chromedriver");
    	String downloadFilepath = "downloads";
    	
    	HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
    	chromePrefs.put("profile.default_content_settings.popups", 0);
    	chromePrefs.put("download.default_directory", downloadFilepath);
    	
    	ChromeOptions options = new ChromeOptions();
    	options.setExperimentalOption("prefs", chromePrefs);
    	options.addArguments("--test-type");
    	options.addArguments("--no-sandbox");
    	options.addArguments("--disable-dev-shm-usage");
	    options.addArguments("--headless");

	    HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
    	DesiredCapabilities cap = DesiredCapabilities.chrome();
    	cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
    	cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    	cap.setCapability(ChromeOptions.CAPABILITY, options);

    	WebDriver driver = new ChromeDriver(cap);
	    
        // maximize the browser window
	    driver.manage().window().maximize();
	    
	    openUrl(driver, BASEURL, BASETITLE);
		return driver;
	}
	
	/* 
	 * This method will open url in chrome and check the titles for verification.
	 * openUrl() will have the 3 paratmeter.
	 * 1. WebDriver driver -  This is the reference of the current browser
	 * 2. String url - Pass the page URL which you want to open on the browser.
	 * 3. String Title - The tilte of the page which you want to open. By using this it will cross check that the currrect page opened.
    */
	public WebDriver openUrl(WebDriver driver, String url, String Title) 
	{
		String expectedTitle = Title;	
		driver.get(url);
	    String actualTitle = driver.getTitle(); 
	    
	    try {
	    	AssertJUnit.assertEquals(expectedTitle,actualTitle);
		} catch (AssertionError e) {
			System.out.println("The title do not match for url: " + url);
		}	
		return driver;
	}
	
	// This method will give the status whether the page loaded or not.
	 public void waitForPageLoaded(WebDriver driver) 
	 {
		 // This will return whether the page ready or not.
		 ExpectedCondition<Boolean> expectation = new
			ExpectedCondition<Boolean>() {
	              public Boolean apply(WebDriver driver) {
	                     return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
	              }
	       };
	       
	       try {
	           WebDriverWait wait = new WebDriverWait(driver, 1000);
	           // To until() we pass the expectation object which is created above to check the page is ready or not. It will wait until page get ready.
	           wait.until(expectation);
	       } catch (Throwable error) {
	           Assert.fail("Timeout waiting for Page Load Request to complete.");
	       }
	 }
}
