import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class MetaChecker 
{
	// declared maps for storing links and error messages
	Map<Integer, String[]> pageLinks = new HashMap<Integer, String[]>();
	Map<Integer, String[]> errorMetaLinks = new HashMap<Integer, String[]>();
	
	// creating instance of SetChromeDriver class
	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();
	
	// initialized strings
	String page = "" ,
			target = "" ,
			text = "",
			href = "" ,
			pageLink = "",
			pageName = "";
	
	@Test
	public void init() 
	{
		// calling metaTagTest method
		this.metaTagTest();
	}
	
	private void metaTagTest() 
	{
		try {
			// gather all pagelinks
			getPageLinks(By.tagName("header"), driver, pageLinks);
			
			// traverse through pages and open or close modal
			pagelinkTraverse(pageLinks, errorMetaLinks);
			
			// print errorDataSet for modal
			print(errorMetaLinks);
		} finally {
			driver.quit();
		}
		
	}

	private void getPageLinks(By tagName, WebDriver driver, Map<Integer, String[]> dataSet) 
	{
		try {
			// opening/starting base url
			driver.get(SetChromeDriver.BASEURL);
		} catch (TimeoutException e2) {
			System.out.println("Catching timeout exception");
			// refreshing page
			driver.navigate().refresh();
		}
		// waiting for page to load
		setChromeDriver.waitForPageLoaded(driver);
		
		try {
			// collecting element by tagname
			WebElement element = driver.findElement(tagName);
			List<WebElement> navLinks = element.findElements(By.tagName("a"));

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
						// putting data in map
						dataSet.put(i ,new String[] {href, text});
					}
				} catch (Exception e) {
					System.out.println("No Element Found");
				}
			}
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}
	
	private void pagelinkTraverse(Map<Integer, String[]> dataSet, Map<Integer, String[]> errorDataSet) 
	{
		for (Map.Entry<Integer, String[] > entry:dataSet.entrySet()) {
			pageLink = entry.getValue()[0];
			pageName = entry.getValue()[1];
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("Traversing To Page : " + pageLink);
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// calling getMeta method
			getMeta(pageLink, pageName, errorDataSet);
		}
	}

	private void getMeta(String pageLink, String pageName, Map<Integer, String[]> errorDataSet) 
	{
		try {
			driver.get(pageLink);
		} catch (TimeoutException e2) {
			System.out.println("Catching timeout exception");
			// refreshing page
			driver.navigate().refresh();
		}
		// waiting for page to load
		setChromeDriver.waitForPageLoaded(driver);
		
		try {
			// collecting elements by tagname
			List<WebElement> modalMeta = driver.findElements(By.tagName("meta"));
			for (int i = 0; i < modalMeta.size() ; i++) {
				// calling checkMeta method
				checkMeta(modalMeta.get(i), errorDataSet, pageLink, pageName);
			}
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}

	private void checkMeta(WebElement webElement, Map<Integer, String[]> errorDataSet, String pageLink,
			String pageName) 
	{
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		Object attributes = executor.executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;", webElement);
		if (attributes == null) {
			// putting errors in map
			errorDataSet.put(errorDataSet.size(), new String[] {pageLink, " has empty meta tag!"});
		}
	}
	
	private void print(Map<Integer, String[]> dataSet) 
	{
		System.out.println("----------------------------------------------------");
		System.out.println("Meta Tag With No Attributes Are " + dataSet.size());
		System.out.println("----------------------------------------------------");
		System.out.println("===================================");
		for (Entry<Integer, String[]> entry:dataSet.entrySet()) {
			System.out.println("{ Page Link : " + entry.getValue()[0] + " } , { " + entry.getValue()[1] + " }");
		}
	}
}
