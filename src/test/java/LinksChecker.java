import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class LinksChecker
{
	// declared maps for storing links and error messages
	Map<Integer, String[]> headerLinks = new HashMap<Integer, String[]>();
	Map<Integer, String[]> headerLinksToBeTraversed = new HashMap<Integer, String[]>();
	Map<Integer, String[]> footerLinks = new HashMap<Integer, String[]>();
	Map<Integer, String[]> hashMap = new HashMap<Integer, String[]>();
	Map<Integer, String[]> errorPages = new HashMap<Integer, String[]>();
	Map<Integer, String[]> errorImages = new HashMap<Integer, String[]>();
	Map<Integer, String[]> errorVideo = new HashMap<Integer, String[]>();
	Map<Integer, String[]> errorHeaderLink = new HashMap<Integer, String[]>();
	Map<Integer, String[]> errorFooterLink = new HashMap<Integer, String[]>();
	
	int index = 0, respCode = 0;
	// initialized strings
	String page = "",
		target = "",
		text = "",
		href = "",
		src = "",
		title = "",
		altCheck = "",
		pageName = "",
		pageLink = "";

	// creating instance of SetChromeDriver class
	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();
	
	@Test
	public void init() 
	{
		// calling linkcheckerstest method
		this.LinksCheckerTest();
	}
	
	protected void LinksCheckerTest()
	{
		try {
			// opening/starting base url
			setChromeDriver.openUrl(driver, SetChromeDriver.BASEURL, SetChromeDriver.BASETITLE);
			// waiting for page to load
	    	setChromeDriver.waitForPageLoaded(driver);
	    	
	    	// call for putting all links of header and footer in respective dataSets
	    	putElements(By.tagName("header"), driver, headerLinks);
	    	putElements(By.tagName("footer"), driver, footerLinks);
	    	putElements(By.id("menu-main-navigation"), driver, headerLinksToBeTraversed);
	    	
	    	// call for checking error prone links , images and iframe
	    	LinkTraverser(headerLinksToBeTraversed, "a", errorPages);
	    	LinkTraverser(headerLinksToBeTraversed, "img", errorImages);
			LinkTraverser(headerLinksToBeTraversed, "iframe", errorVideo);
			
			//call for checking header and footer links
			checkHeaderFooterLinks( headerLinks, errorHeaderLink);
			checkHeaderFooterLinks( footerLinks, errorFooterLink);
			
			// call for printing error dataSets
			printList("Links", errorPages);
			printList("Images", errorImages);
			printList("Video", errorVideo);
			printList("Header Links", errorHeaderLink);
			printList("Footer Links", errorFooterLink);
		} finally {
			driver.quit();
		}
	}

	// header and footer links creator
	protected void putElements(org.openqa.selenium.By selector, WebDriver driver, Map<Integer, String[]> dataSet) 
	{
		try {
			// opening/starting the base url 
			driver.get(SetChromeDriver.BASEURL);
		} catch (TimeoutException e2) {
			System.out.println("Catching timeout exception");
			// refreshing page
			driver.navigate().refresh();
		}
		setChromeDriver.waitForPageLoaded(driver);
		
		try {
			// collecting element by tagname
			WebElement element = driver.findElement(selector);
			// collecting elements by tagname
			List<WebElement> navLinks = element.findElements(By.tagName("a"));

			for (int i = 0; i < navLinks.size(); i++) {
				try {
					href = navLinks.get(i).getAttribute("href");
					if (href.startsWith("mailto") || href.startsWith("tel")) {
						System.out.println("Mail To / Tel To Found\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					} else {
						try {
							text = navLinks.get(i).getText();
							target = navLinks.get(i).getAttribute("target");
							if (target.isEmpty()) {
								target = "NULL";
							}
							// putting all data into maps
							dataSet.put(i ,new String[] { href, text, target});
						} catch (Exception e) {
							System.out.println("Element Not Found");
						}
					}
				} catch (Exception e) {
					System.out.println("No Element Found");
				}
			}
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}
	
	// Traversing Link
	protected void LinkTraverser(Map<Integer, String[]> headerLinks, String pointerElement, Map<Integer, String[]> errorDataSet)
	{
		for (Map.Entry<Integer,String[] > entry:headerLinks.entrySet()) {
			pageLink = entry.getValue()[0];
			pageName = entry.getValue()[1];
			if (pageLink.endsWith("#")) {
				System.out.println("The Page :- " + pageLink + " Ends With #");
			} else {
				// calling contentLinkAdder method
				contentLinkAdder(pageLink,pointerElement,errorDataSet);
			}
		}
	}

	// checking for content link
	protected void contentLinkAdder(String pageLink, String pointerElement, Map<Integer, String[]> errorDataSet)
	{
		try {
			// opening/starting page
			driver.get(pageLink);
		} catch (TimeoutException e2) {
			System.out.println("Catching timeout exception");
			// refreshing page
			driver.navigate().refresh();
		}
		setChromeDriver.waitForPageLoaded(driver);
		
		try {
			// collecting element by id
			WebElement element = driver.findElement(By.id("content"));
			// collecting elements by tagname
			List<WebElement> contentNavLinks = element.findElements(By.tagName (pointerElement));
			// calling checkElement method
			checkElement(contentNavLinks, driver, errorDataSet, pointerElement);
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}

	// checking for element
	protected void checkElement(List<WebElement> contentNavLinks, WebDriver driver, Map<Integer, String[]> errorDataSet, String pointerElement)
	{
		if (pointerElement.equals("a")) {
			for (int i = 0; i < contentNavLinks.size(); i++) {
				try {
					href = contentNavLinks.get(i).getAttribute("href");
					text = contentNavLinks.get(i).getText();
					target = contentNavLinks.get(i).getAttribute("target");
					if (target.isEmpty()) {
						target = "NULL";
					}
				} catch (NoSuchElementException e) {
					System.out.println("No Element Found");
				}
				// calling checForFourZeroFour method
				checkForFourZeroFour(href, errorDataSet ,text);
				// calling checkForErrors method
				checkForErrors(href, target, driver, text, errorDataSet);
			}
		} else if (pointerElement.equals("img")) {
			page = driver.getTitle();
			for (int i = 0; i < contentNavLinks.size(); i++) {
				try {
					text = contentNavLinks.get(i).getText();
					src = contentNavLinks.get(i).getAttribute("src");
					title = contentNavLinks.get(i).getAttribute("title");
				} catch (NoSuchElementException e) {
					System.out.println("No Element Found");
				}
				altCheck = null;
				try {
					altCheck = contentNavLinks.get(i).getAttribute("alt");
				} catch (Exception e) {
					index = errorDataSet.size();
					page = driver.getTitle();
					// putting errors in map
					errorDataSet.put(index, new String[] {src, page, "Alt Attribute Does Not Exists"});
				}
				// calling checkForFourZeroFour method
				checkForFourZeroFour(src, errorDataSet, text);
			}
		} else if (pointerElement.equals("iframe")) {
			for (int i = 0; i < contentNavLinks.size(); i++) {
				try {
					text = contentNavLinks.get(i).getText();
					src = contentNavLinks.get(i).getAttribute("src");
				} catch (NoSuchElementException e) {
					System.out.println("No Element Found");
				}
				// calling checkForFourZeroFour method
				checkForFourZeroFour(src, errorDataSet, text);
			}
		}
	}

	// checking for 404 error
	protected void checkForFourZeroFour(String href, Map<Integer, String[]> errorDataSet, String text)
	{
		HttpURLConnection huc = null;
		
		if (!href.startsWith("mailto") || href.startsWith("tel")) {
			if (href.startsWith("https://takecare.mysites.")) {
				href = href.replaceAll("https://takecare.mysites.io/", SetChromeDriver.BASEURL);
			}
			
			try {
		        huc = (HttpURLConnection)(new URL(href).openConnection());
		        huc.setRequestMethod("HEAD");
		        huc.connect();
		        respCode = huc.getResponseCode();
		        page = driver.getTitle();
				if (respCode == 404){
					index = errorDataSet.size();
					// putting errors in map
					errorDataSet.put(index,new String[] {href, page, "Goes to 404", text});
				}
			} catch (Exception e) {
				System.out.println("Connection Error :- " + href);
			}
		}
	}
	
	// checking for errors
	protected void checkForErrors(String href, String target, WebDriver driver, String text, Map<Integer, String[]> errorDataSet)
	{
		if (href.startsWith("https://takecare.mysites.")) {
			href = href.replaceAll("https://takecare.mysites.io/", SetChromeDriver.BASEURL);
		}
		
		if (href.startsWith(SetChromeDriver.BASEURL)) {
			if (target.equals("NULL")) {
				System.out.println("The Link " + href + " Is VALID\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			} else {
				if (href.endsWith("#") || href.endsWith("#twitter") || href.endsWith("#facebook") || href.endsWith(".jpeg")) {
					System.out.println("The Link " + href + " Is VALID\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				} else {
					index = errorDataSet.size();
					// putting errors in map
					errorDataSet.put(index, new String[] {href, page, " internal link but has _blank as target", text});
				}
			}
		} else if (!href.startsWith(SetChromeDriver.BASEURL)) {
			if (target.equals("_blank")) {
				System.out.println("The Link " + href + " Is VALID\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			} else {
				index = errorDataSet.size();
				// putting errors in map
				errorDataSet.put(index, new String[] {href, page, " external link but has null as target", text});
			}
		}
	}
	
	// checking for header and footer links
	protected void checkHeaderFooterLinks(Map<Integer, String[]> dataSet, Map<Integer, String[]> errorDataSet) 
	{
		for (Map.Entry<Integer,String[] > entry:dataSet.entrySet()) {
			href = entry.getValue()[0];
			text = entry.getValue()[1];
			target = entry.getValue()[2];
			// calling checkForFourZeroFour method
			checkForFourZeroFour(href, errorDataSet, text);
			// calling checkForErrors method
			checkForErrors(href, target, driver, text, errorDataSet);
		}
	}
	
	// printing error dataSet
	protected void printList(String string, Map<Integer, String[]> errorPages)
	{
		System.out.println("ERROR PRONE " + string + " IN : " + errorPages.size() + "===================================");
		for (Entry<Integer, String[]> entry : errorPages.entrySet()) {
			System.out.println("{ Page Link : " + entry.getValue()[0] + " } , { in page : " + entry.getValue()[1] + " } , { has error : " + entry.getValue()[2] + " } , { at : " + entry.getValue()[3] + " }");
		}
	}
}
