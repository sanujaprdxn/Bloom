import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class ReadMoreLess 
{
	private final String PAGEURL = "https://takecare:takecare@takecare.mysites.io/film/";
	private final String PAGETITLE = "Films – TakeCare";

	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();

//	This test will check the read more & read less fucation for films page only.
	@Test
	public void init_Test() {
		viewDetailClick();
	}
	
	protected void viewDetailClick()
	{	
		try {
			setChromeDriver.openUrl(driver, PAGEURL, PAGETITLE);
			setChromeDriver.waitForPageLoaded(driver);
			String pageLink = ""; 
			
			// Get the number of the View Details buttouns from Films page.
			WebElement element = driver.findElement(By.className("film_collection"));
			List<WebElement> navLinks = element.findElements(By.cssSelector("article a[title=\"VIEW DETAILS\"]"));
			System.out.println("Total links in navigation are " + navLinks.size());

			// Naviget all the View details page one by one.
			for (int j = 0; j < navLinks.size(); j++) {
				driver.get(PAGEURL);
				setChromeDriver.waitForPageLoaded(driver);
				element = driver.findElement(By.className("film_collection"));
				pageLink = element.findElements(By.cssSelector("article a[title=\"VIEW DETAILS\"]")).get(j).getAttribute("href");
				
				if (!pageLink.endsWith("#")) {
					System.out.println("\n" + j + "  The page  " + pageLink);
					try {
						driver.get(pageLink);
						setChromeDriver.waitForPageLoaded(driver);
						readMore_Less_Test();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (TimeoutException e2) {
						System.out.println("Catching timeout exception.\nPage refersh done");
						driver.navigate().refresh();
					} 
				} 
			}
		} finally {
			driver.quit();
		}
	}

	// Actually check the read more & read less button funcation
	protected void readMore_Less_Test() throws InterruptedException 
	{
		boolean flag = false;
		WebElement readmore = null;
		
		try {
			// Find out the readmore_cta button from the page. If found set flag to true.
			readmore = this.driver.findElement(By.className("readmore_cta"));
			flag = true;
		} catch (NoSuchElementException e) {
			flag = false;
		}
		
		// If flag is true then click on the readmore button. Then check the Readless button too.
		if (flag) {
			readmore.click();
			Thread.sleep(1000);
		
			// After clicking on the readmore button the text of the button will change to Read Less.
			if (readmore.getText().equals("READ LESS")) {
				System.out.println("\tThe Read More working.");
				
				Actions actions = new Actions(driver);
				actions.moveToElement(readmore).click().perform();
				Thread.sleep(1000);
				
				// After clicking on the readless button the text of the button will change to Read More.
				if (readmore.getText().equals("READ MORE"))
					System.out.println("\tThe Read Less working.\n");						
			}
			
		} else {
			System.out.println("\n\tThe Read More button not present for this page.\n");
		}
	}
}
