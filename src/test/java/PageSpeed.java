import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class PageSpeed
{
	@Test
	public void PageSpeedTest()
	{
		PageSpeedCalculater();
	}
	
	protected void PageSpeedCalculater()
	{
		String pageLink = "";
		String time = "";
		long start = 00;
		long finish = 00;
		long totalTime = 00;

		SetChromeDriver setChromeDriver = new SetChromeDriver();
		WebDriver driver = setChromeDriver.setDriver();

		try {
			// Calculate & display the page speed of the baseurl
			System.out.println("\n\nThe page  " + SetChromeDriver.BASEURL);
			start = System.currentTimeMillis();
			driver.get(SetChromeDriver.BASEURL);
			setChromeDriver.waitForPageLoaded(driver);
			finish = System.currentTimeMillis();
			totalTime = (finish - start)/1000;
			time = Long.toString(totalTime);
			System.out.println("0  The page speed is " + time + "s");

			// Get the number of pages from the navigation the navigation tab
			WebElement element = driver.findElement(By.id("menu-main-navigation"));
			List<WebElement> navLinks = element.findElements(By.tagName("a"));
			System.out.println("\nTotal links in navigation are " + navLinks.size());

			// Loop to check the page speed of the each page from the navigation tab.
			for ( int j = 0; j < navLinks.size(); j++ ) {
				element = driver.findElement(By.id("menu-main-navigation"));
				navLinks = element.findElements(By.tagName("a"));

				// Replace tackecare from url with "takecare:takecare@takecare" to authenticate the url in the browser.
				pageLink = navLinks.get(j).getAttribute("href").replace("takecare", "takecare:takecare@takecare");

				if (!pageLink.endsWith("#")) {
					System.out.println("\n" + (j+1) + "  The page  " + pageLink);
					start = System.currentTimeMillis();
					// Load the URL if URL not loaded the refresh the browser.
					try {
						driver.get(pageLink);
					} catch (TimeoutException e2) {
						System.out.println("Catching timeout exception");
						driver.navigate().refresh();
					}

					setChromeDriver.waitForPageLoaded(driver);
					finish = System.currentTimeMillis();
					totalTime = (finish - start)/1000;
					time = Long.toString(totalTime);
					System.out.println("\tThe page speed is " + time + "s");
				} 
			}		
		} finally {
			driver.quit();
		}
	}
}
