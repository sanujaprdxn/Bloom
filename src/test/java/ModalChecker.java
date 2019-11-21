import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class ModalChecker 
{
	Map<Integer, String[]> pageLinks = new HashMap<Integer, String[]>();
	Map<Integer, String[]> errorModalLinks = new HashMap<Integer, String[]>();
	
	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();
	
	String page = "" ,
			target = "" ,
			text = "",
			href = "" ,
			pageLink = "",
			pageName = "";
	
	@Test
	public void modalCheckerTest()
	{
		try {
			// Creating the collection of all pagelinks from header
			getPageLinks( driver, By.tagName("header"),pageLinks);
			
			// Traverse through pages from pageLinks and open or close modal
			pagelinkTraverse(pageLinks, errorModalLinks);
			
			//print errorDataSet for modal
			print("Modal Errors are", errorModalLinks);
			
		} finally {
			driver.quit();
		}
	}

	// Get the list of all the header links.
	private void getPageLinks(WebDriver driver, By tagName, Map<Integer, String[]> dataSet) 
	{
		setChromeDriver.waitForPageLoaded(driver);
		
		try {
			WebElement element = driver.findElement(tagName);
			List<WebElement> navLinks = element.findElements(By.tagName("a"));

			// Get the attribute of links. Filtered the internal page url from all header links. 
			for (int i = 0; i < navLinks.size() ; i++) {
				try {
					href = navLinks.get(i).getAttribute("href");
					text = navLinks.get(i).getText();
					target = navLinks.get(i).getAttribute("target");
					
					if (href.startsWith("mailto") || href.startsWith("tel") || target.equals("_blank") || href.endsWith("#") || href.equals("https://google.com/")) {
						break;
					} else {
						if (text.isEmpty()) {
							text = "NO TEXT TO DISPLAY";
						}
						if (target.isEmpty()) {
							target = "NULL";
						}
						dataSet.put(i ,new String[] {href, text});
					}
				} catch (NoSuchElementException e) {
					System.out.println("No Element Found");
				}
			}
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}
	
	// The load the pages one by one from the geted pageLinks set.
	private void pagelinkTraverse(Map<Integer, String[]> dataSet, Map<Integer, String[]> errorDataSet) 
	{
		for (Map.Entry<Integer, String[] > entry:dataSet.entrySet()){
			pageLink = entry.getValue()[0];
			pageName = entry.getValue()[1];
			
			getModal(pageLink, pageName, errorDataSet);
		}
	}

	// This will get the modal elements from the fils page. 
	private void getModal(String pageLink, String pageName, Map<Integer, String[]> errorDataSet) 
	{
		try {
			System.out.println("Traversing To Page : " + pageLink + " And Checking Modal\n++++++++++++"
					+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			driver.get(pageLink);
		} catch (TimeoutException e2) {
			System.out.println("Catching timeout exception");
			driver.navigate().refresh();
		}
		
		setChromeDriver.waitForPageLoaded(driver);
		
		if (pageLink.endsWith("film/")) {
			try {
				System.out.println("Modal Found On Page : " + pageLink 
						+ "\n------------------------------------\nTesting Modal\n-------------------------");
				List<WebElement> modalFilmLinks = driver.findElements(By.cssSelector("#mCSB_1_container > figure"));
				for (int i = 0; i < modalFilmLinks.size(); i++) {
					clickModal(modalFilmLinks.get(i), errorDataSet, pageLink, pageName);
				}
				List<WebElement> modalFilmVideoLinks = driver.findElements(By.cssSelector("#mCSB_1_container > .video"));
				for (int i = 0; i < modalFilmVideoLinks.size(); i++) {
					clickModal(modalFilmVideoLinks.get(i), errorDataSet, pageLink, pageName);
				}
				System.out.println("Modal Is Working Properly\n-----------------------------------------");
			} catch (NoSuchElementException e) {
				System.out.println("No Element Found");
			}
		} else {
			System.out.println("No Modal Found On Page : " + pageLink + "\n++++++++++++++++++++++++++++++++++"
								+ "++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		}
		try {
			List<WebElement> modalLinks = driver.findElements(By.cssSelector(".vimeo-autoplay"));
			
			for (int i = 0; i < modalLinks.size(); i++)
				clickModal(modalLinks.get(i), errorDataSet, pageLink, pageName);
			
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}

	// This is use to click on modal elements
	private void clickModal(WebElement webElement, Map<Integer, String[]> errorDataSet, String pageLink, String pageName) 
	{
		Actions ob = new Actions(driver);
		checkModalOpen(ob, webElement, errorDataSet, pageLink, pageName);
		checkModalClose(ob, webElement, errorDataSet, pageLink, pageName);
		WebElement elementClose = driver.findElement(By.className("close-modal"));
		ob.moveToElement(elementClose).click().build().perform();
	}

	// This funaction will check the open fucation of the modal
	private void checkModalOpen(Actions ob, WebElement webElement, Map<Integer, String[]> errorDataSet, String pageLink,
			String pageName) 
	{
		ob.moveToElement(webElement).click().build().perform();
		setChromeDriver.waitForPageLoaded(driver);
		
		try {
			driver.findElement(By.className("show"));
			try {
				WebElement iframeElement = driver.findElement(By.tagName("iframe"));
				String src = iframeElement.getAttribute("src");
				if (src == "") {
					errorDataSet.put(errorDataSet.size(), new String[] {pageLink, pageName, "Iframe Has No Src"});
				}
			} catch (NoSuchElementException e) {
				System.out.println("Iframe Not Found");
			}
		} catch (NoSuchElementException e) {
			System.out.println("Modal Not Opened On Click");
			errorDataSet.put(errorDataSet.size(), new String[] {pageLink, pageName, "Modal Not Opened On Click"});
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	// This method will check close fucation of modal
	private void checkModalClose(Actions ob, WebElement webElement, Map<Integer, String[]> errorDataSet,
			String pageLink, String pageName) 
	{
		try {
			WebElement closeElement = driver.findElement(By.className("close-modal"));
			ob.moveToElement(closeElement).click().build().perform();
			setChromeDriver.waitForPageLoaded(driver);
		} catch (NoSuchElementException e) {
			System.out.println("Modal Closed On 2nd Click");
			errorDataSet.put(errorDataSet.size(), new String[] {pageLink, pageName, "Modal Not Closed On Click"});
		}
	}
	
	// This will print the all the links in which we get the error.
	private void print(String erroMsg, Map<Integer, String[]> dataSet) 
	{
		System.out.println("------------------------------------------------\n" + erroMsg + " " + dataSet.size()
							+ "\n------------------------------------------------");
		for (Entry<Integer, String[]> entry:dataSet.entrySet())
			System.out.println("Page Link : " + entry.getValue()[0] + " in page : " + entry.getValue()[1] + " " + entry.getValue()[2]);
	}
}
