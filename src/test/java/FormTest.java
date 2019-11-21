import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class FormTest 
{
	
	private static final String URL = "http://takecare:takecare@staging.takecare.mysites.io/say-hello-to-you/";
	
	// creating instance of SetChromeDriver class
	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();
	WebDriverWait wait = new WebDriverWait(driver, 30);
	Actions action = new Actions(driver);

	int progress = 0;
	boolean flag = false;
	
	@Test
	public void init()
	{
		this.pageVisitor();
	}
	
	// pageVisitor method used to open url and find get started button using cssSelector
	protected void pageVisitor()
	{
		Dimension d = new Dimension(1200, 800);
		try {
			driver.get(URL);
			System.out.println("On Page : " + URL + "\n+++++++++++++++++++++++++++++++++++++++++++++++++++"
					+ "++++++++++++++++++++++++++++++++++++++");
			driver.manage().window().setSize(d);
			setChromeDriver.waitForPageLoaded(driver);
			
			// finding the button using cssSelector and performing click operation
			action.moveToElement(driver.findElement(By.cssSelector("a.cta"))).click().perform();
			setChromeDriver.waitForPageLoaded(driver);
			
			// calling checkQuestionsOnPage method
			checkQuestionsOnPage();
		} finally {
			driver.quit();
		}
	}

	// checkQuestionsOnPage method used to check whether the slide contains questions or information
	protected void checkQuestionsOnPage() 
	{
		try {
			WebElement slickTrack = driver.findElement(By.cssSelector("div.slick-track"));
			List<WebElement> slickSlideLi = slickTrack.findElements(By.tagName("li"));
			
			for (int i = 0; i < slickSlideLi.size(); i++) {
				try {
					if (Integer.parseInt(driver.findElement(By.className("inprogress")).getText()) == progress) {
						System.out.println("Total Number Of Slides : " + driver.findElement(By.className("total")).getText()
								+ "\n============================================================================");
						System.out.println("In Progress Slide Number : " + driver.findElement(By.className("inprogress")).getText()
								+ "\n============================================================================");
						progress++;
					}
					
					if (slickSlideLi.get(i).getAttribute("class").contains("progress-required")) {
						Thread.sleep(500);
						isPresentHint(slickSlideLi.get(i), driver.findElement(By.className("inprogress")).getText());
						Thread.sleep(500);
						checkErrorMessage(slickSlideLi.get(i));
					} else if (slickSlideLi.get(i).getAttribute("class").contains("progress-not-required")) {
						Thread.sleep(1000);
						action.moveToElement(driver.findElement(By.cssSelector("a.next"))).click().perform();
					}
				} catch(NoSuchElementException | InterruptedException e) {
					System.out.println("Unable To Find Element : " + e.getMessage());
				}
			} 
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		}
	}

	// isPresentHint method used to check whether hint is present on the slide or not
	protected boolean isPresentHint(WebElement slickSlideElement, String slideNumber)
	{
	    try {
	    	if (slickSlideElement.findElement(By.cssSelector("span.hint")).isDisplayed()) {
	    		System.out.println("Hint Is Present On Slide Number : " + slideNumber + "\n----------------------"
	    				+ "-------------------------------------------------------------");
	    	}
	    	// calling openingHint method
	    	openingHint(slickSlideElement, By.cssSelector("span.hint"), slideNumber);
	    } catch(NoSuchElementException e) {
	        System.out.println("Hint Is Not Present On Slide Number : " + slideNumber + "\n----------------------------------------------------"
	        		+ "-------------------------------------------------------------");
	    }
		return false;
	}
	
	// openingHint method is used to open the hint functionality by clicking on it(i symbol)
	protected void openingHint(WebElement slickSlideElement, By spanOpenCloseElement, String slideNumber)
	{
		try {
			action.moveToElement(slickSlideElement.findElement(spanOpenCloseElement)).click().perform();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.hintdisplay"))); 
			// checking whether the div with class hintdisplay is visible/enabled or not
			if (slickSlideElement.findElement(By.cssSelector("div.hintdisplay")).isEnabled()
				&& slickSlideElement.findElement(spanOpenCloseElement).getAttribute("class").contains("iconshow")) {
				flag = true;
			} else {
				flag = false;
			}
			// calling closingHint method
			closingHint(slickSlideElement, slideNumber);
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		}
	}
	
	// closingHint method is used to close the hint by clicking on cross button and on i icon image
	protected void closingHint(WebElement slickSlideElement, String slideNumber)
	{
		try {
			action.moveToElement(slickSlideElement.findElement(By.cssSelector("span.close"))).click().perform();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.hintdisplay")));
			// checking whether the div with class hintdisplay is visible/enabled or not
			if (slickSlideElement.findElement(By.cssSelector("div.hintdisplay")).isEnabled()) {
				flag = true;
			} else {
				flag = false;
			}
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		} 
		
		if (flag) {
			System.out.println("Hint Is Working Properly On Slide Number : " + slideNumber + "\n-----------------------------------------------------"
					+ "---------------------------------------------");
		} else {
			System.out.println("Hint Is Not Working Properly On Slide Number : " + slideNumber + "\n-------------------------------------------------"
					+ "---------------------------------------------");
		}
	}
	
	// checkErrorMessage method is used to check errors on slide before clicking answer of the questions
	protected void checkErrorMessage(WebElement listElement)
	{
		try {	
			// Directly Pressing Enter Key Without Selecting Any Option
			action.sendKeys(Keys.ENTER).build().perform();
			setChromeDriver.waitForPageLoaded(driver);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.error_message"))); 
			
			// calling printErrorMessage method
			printErrorMessage(listElement, "Enter");
			
			// Directly Pressing Next Arrow Key Without Selecting Any Option
			action.moveToElement(driver.findElement(By.cssSelector("a.next"))).click().perform();
			setChromeDriver.waitForPageLoaded(driver);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.error_message"))); 
			
			// calling printErrorMessage method
			printErrorMessage(listElement, "Next Arrow");
			
			// calling testQuestionAnswers method
			testQuestionAnswers(listElement);
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		}
	}
	
	/* printErrorMessage method is used to print the exact error message of wrong operation performed by 
	 * the user or accordingly
	 */
	protected void printErrorMessage(WebElement listElement, String key)
	{
		try {
			setChromeDriver.waitForPageLoaded(driver);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("error_message"))); 
			// collecting the element by using cssSelector
			WebElement errorElement = listElement.findElement(By.cssSelector("span.error_message"));
			
			if (errorElement.getText().equalsIgnoreCase("Field must be selected.")) {
				System.out.println("Pressed " + key + " Key Without Selecting Any Answer\n+++++++++++++++++++++++"
						+ "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			} else if (errorElement.getText().equalsIgnoreCase("Please enter valid input.")) {
				System.out.println("Entered " + key + " keys\n+++++++++++++++++++++++"
						+ "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			}
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		} 
	}

	// testQuestionAnswers method to test question answers works properly after clicking any one of the answer and procedding
	protected void testQuestionAnswers(WebElement listElement) 
	{
		try {
			List<WebElement> divElements = listElement.findElements(By.tagName("div"));
			for (int i = 0; i < divElements.size(); i++) {
				try {
					if (divElements.get(i).getAttribute("class").contains("radio-button")) {
						List<WebElement> labelElements = divElements.get(i).findElements(By.tagName("label"));
						action.moveToElement(labelElements.get(0)).click().build().perform();
						System.out.println("Clicked On One Of The Option/Answer. Now Moving To Next Slide\n+"
								+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
						break;
					} else if (divElements.get(i).getAttribute("class").contains("wrap-textarea")) {
						testTextArea(listElement);
						break;
					}
				} catch(NoSuchElementException e) {
					System.out.println("Unable To Find Element : " + e.getMessage());
				}
			}
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		}
	}

	// testTextArea method is used to test text area fields in the slide
	protected void testTextArea(WebElement listElement) 
	{
		try {
			listElement.findElement(By.tagName("textarea")).sendKeys("Testing");
			System.out.println("Entered Dummy Data And Now Moving To Next Slide\n+++++++++++++++++++++++++++++"
					+ "++++++++++++++++++++++++++++++++++++++++++++++++++++");
			action.sendKeys(Keys.ENTER).build().perform();
			if (progress == Integer.parseInt(driver.findElement(By.className("total")).getText())) {
				System.out.println("Total Number Of Slides : " + driver.findElement(By.className("total")).getText()
					+ "\n============================================================================");
				System.out.println("Completing Slide Number : " + driver.findElement(By.className("inprogress")).getText()
					+ "\n============================================================================");
				checkThankyouPage();
			}
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		} 
	}

	// checkThankYouPage method used to call the continue and download functionality method
	protected void checkThankyouPage() 
	{
		exploreMore();
		emailFunctionality();
	}

	// exploreMore method for checking continue button whether it is redirecting to results page or not
	protected void exploreMore() 
	{
		try {
			setChromeDriver.waitForPageLoaded(driver);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.next_cta")));
			action.moveToElement(driver.findElement(By.cssSelector("a.next_cta"))).click().build().perform();
			setChromeDriver.waitForPageLoaded(driver);
			
			if (driver.getTitle().equalsIgnoreCase("Result – TakeCare")) {
				System.out.println("Explore More Button Is Working Properly\n--------------------------------------------");
			} else {
				System.out.println("Explore More Button Is Not Working Properly\n--------------------------------------------");
			}
			driver.navigate().back();
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		} 
	}
	
	/* emailFunctionality method for checking email button whether it send's the email to the appropriate 
	 * id or not and checking the response code of the page from which the email has been send 
	 */
	protected void emailFunctionality() 
	{
		String emailId = "testingprdxn@gmail.com", userCredentials = "takecare:takecare", basicAuth = "";
		int respCode = 0;
		try {
			setChromeDriver.waitForPageLoaded(driver);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.e-mail_send")));
			action.moveToElement(driver.findElement(By.cssSelector("a.e-mail_send"))).click().perform();
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
			driver.findElement(By.id("email")).sendKeys(emailId);
			
			action.moveToElement(driver.findElement(By.cssSelector("input[value='Send']"))).click().perform();
			System.out.println("Send PDF via Email Button Clicked\n+++++++++++++++++++++++++++++++++++++++++++++");
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			action.moveToElement(driver.findElement(By.cssSelector("span.close"))).click().perform();
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			
			HttpURLConnection huc = null;
			
			try {
		        huc = (HttpURLConnection)(new URL(driver.getCurrentUrl()).openConnection());
		        basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
				huc.setRequestProperty ("Authorization", basicAuth);
				huc.setRequestMethod("GET");
		        huc.connect();
		        respCode = huc.getResponseCode();
		        System.out.println("URL : " + driver.getCurrentUrl());
		        System.out.println("Response Message : " + huc.getResponseMessage());
		        
				if (respCode == 200) {
					System.out.println("Link Is Working Properly\n-------------------------------"
							+ "------------------------------------------------------------");
				} else if (respCode == 404) {
					System.out.println("Link Is Broken\n--------------------------------------------------"
							+ "-------------------------------------------------------------");
				} else if (respCode == 401) {
					System.out.println("Authentication Error\n---------------------------------------------"
							+ "-------------------------------------------------------------");
				}
			} catch(Exception e) {
				System.out.println("Connection Error :- " + driver.getCurrentUrl() + "\n---------------------------------------"
						+ "-----------------------------------------------------------------");
			}
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		}
	}
	
}
