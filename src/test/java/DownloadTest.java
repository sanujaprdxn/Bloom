import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import reusableComponents.Download;
import reusableComponents.SetChromeDriver;

public class DownloadTest
{	
	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();
	Download download = new Download();
	
	String url = "", pageLink = "";
	
	@Test
	public void init()
	{
		pageVisitor();
	}
	
	// pageVisitor method to visit each page present in the header element to find the download links
	protected void pageVisitor()
	{	
		try {
			setChromeDriver.waitForPageLoaded(driver);
			// Collecting all the links from id of the header tag
			WebElement element = driver.findElement(By.id("menu-main-navigation"));
			List<WebElement> navLinks = element.findElements(By.tagName("a"));
			System.out.println("Total links in navigation are " + navLinks.size());
			
			// Iterating throughout the list of links and visiting each links
			for (int j = 0; j < navLinks.size(); j++) {
				try {
					setChromeDriver.waitForPageLoaded(driver);
					// initializing same elements once more to avoid stale element exception
					element = driver.findElement(By.tagName("menu-main-navigation"));
					navLinks = element.findElements(By.tagName("a"));
					pageLink = navLinks.get(j).getAttribute("href");
					// replacing staging from url with takecare:takecare@staging for authenticating the url in the browser
					pageLink = pageLink.replace("http://staging", "http://takecare:takecare@staging");

					if (!pageLink.endsWith("#")) {
						System.out.println("The pages  " + pageLink + "\n+++++++++++++++++++++"
								+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
						
						try {
							driver.get(pageLink);
						} catch(TimeoutException e) {
							System.out.println("Catching timeout exception");
							driver.navigate().refresh();
						}
						downloadLink();
					}
				} catch(NoSuchElementException e) {
					System.out.println("Unable To Find Element : " + e.getMessage());
				}
			} 
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		} finally {
			driver.quit();
		}
	}

	// downloadLink method is used to collect all the links containing download as a class name and iterating through it
	protected void downloadLink()
	{	
		try {
			// collecting all the links containing class name as download
			List<WebElement> downloadLinks = driver.findElements(By.className("download"));
			
			// Iterating throughout the list of download links url and checking response code of each link
			for(int i = 0; i < downloadLinks.size(); i++) {
				try {
					downloadLinks = driver.findElements(By.className("download"));
					url = downloadLinks.get(i).getAttribute("href");
					// replacing staging from url with takecare:takecare@staging for authenticating the url in the browser
					url = url.replace("http://staging", "http://takecare:takecare@staging");
					/* calling downloadLinkResponse method of Download class to check whether the response 
					 * code of the url is 200, 401 or 404
					 */
					download.downloadLinkResponse(url);
				} catch(NoSuchElementException e) {
					System.out.println("Unable To Find Element : " + e.getMessage());
				}
			} 
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		}
	}
	
}
