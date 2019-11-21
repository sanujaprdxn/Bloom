import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class GA_Tracking 
{
	// Initializing Strings
	String page = "" ,
			target = "" ,
			text = "",
			href = "" ,
			pageLink = "",
			pageName = "",
			keyword = "gtm",
			data = null;

	// Declaring maps for storing links and errors
	Map<Integer, String[]> headerLinks = new HashMap<Integer, String[]>();
	Map<Integer, String[]> footerLinks = new HashMap<Integer, String[]>();
	Map<Integer, String[]> errorSet = new HashMap<Integer, String[]>();
	Map<Integer, String[]> headerLinksToBeTraversed = new HashMap<Integer,String[]>();
	
	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();

	Dimension d = new Dimension(1200,600);
	
	@Test
	public void init()
	{
		// Calling Tracker Method
		this.tracker();
	}
	
	private void tracker() 
	{
		try {	
			// Starting/Opening base url
		    setChromeDriver.openUrl(driver, SetChromeDriver.BASEURL, SetChromeDriver.BASETITLE);
		    // Waiting for page to load
		    setChromeDriver.waitForPageLoaded(driver);
		    // Maximizing window size 
		    driver.manage().window().setSize(d);
		    
		    // GA calls for header links
		    getPageLinks(By.tagName("header"), driver, headerLinks);
		    gotoLink(headerLinks, "a");
		    
		    // GA calls for footer links
		    getPageLinks(By.tagName("footer"), driver, footerLinks); 
		    gotoLink(footerLinks, "a");
		    
		    // GA calls for vimeo
		    gotoLink(headerLinks, "video");
		    
		    // GA calls for page content
		    getPageLinks(By.id("menu-main-navigation"), driver, headerLinksToBeTraversed);
		    gotoLink(headerLinksToBeTraversed, "content");
		    
		    // GA calls for carousel
		    carousel("https://takecare:takecare@takecare.mysites.io/film");
		    
		    // GA calls for ReadMore ReadLess
		    readMoreLess("https://takecare:takecare@takecare.mysites.io/film");
		    
		    // printing errorDataSet
		    print("GA errors are ");
		} catch (Exception e) {
			System.out.println("Unable To Call The Methods");
		} finally {
			driver.quit();
		}
	}

	private void getPageLinks(By selector, WebDriver driver, Map<Integer, String[]> dataSet) 
	{
		setChromeDriver.openUrl(driver, SetChromeDriver.BASEURL, SetChromeDriver.BASETITLE);
		setChromeDriver.waitForPageLoaded(driver);
		
		try {
			WebElement element = driver.findElement(selector);
			List<WebElement> navLinks = element.findElements(By.tagName("a"));

			for (int i = 0; i < navLinks.size(); i++) {
				try {
					href = navLinks.get(i).getAttribute("href");
					text = navLinks.get(i).getText();
					target = navLinks.get(i).getAttribute("target");
					
					if (href.endsWith("#")) {
						System.out.println("Found #");
					} else {
						if (text.isEmpty()) {
							text = "NO TEXT TO DISPLAY";
						}
						if (target.isEmpty()) {
							target = "NULL";
						}
						// putting all data into the map
						dataSet.put(i, new String[] { href, text });
					}
					
				} catch (NoSuchElementException e) {
					System.out.println("No Element Found");
				}
			}
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}
	
	private void gotoLink(Map<Integer, String[]> dataSet, String string) 
	{
		for (Map.Entry<Integer, String[] > entry : dataSet.entrySet()){
			pageLink = entry.getValue()[0];
			pageName = entry.getValue()[1];
			// calling openLink method
			openLink(pageLink, pageName, string);
		}
	}

	private void openLink(String pageLink, String pageName, String string) 
	{
		if (string.equals("a")) {
			// opening page
			driver.get(pageLink);
			// waiting for page to load
			setChromeDriver.waitForPageLoaded(driver);
			// calling checkGA method
			data = checkGA(pageLink, "Anchor");
			
			if (!data.contains(keyword)) {
				errorSet.put(errorSet.size(), new String[] { pageLink, pageName, "GA event not fired" });
			}
		} else if (string.equals("video")) {
			// opening page
			driver.get(pageLink);
			// waiting for page to load
			setChromeDriver.waitForPageLoaded(driver);
			// calling getModal method
			getModal(pageLink, pageName);
		} else if (string.equals("content")) {
			// opening page
			driver.get(pageLink);
			// waiting for page to load
			setChromeDriver.waitForPageLoaded(driver);
			try {
				WebElement element = driver.findElement(By.id("content"));
				List<WebElement> contentNavLinks = element.findElements(By.tagName ("a"));
				// calling checkElement method
				checkElement(contentNavLinks, driver, "a", pageName, pageLink);
			} catch (NoSuchElementException e) {
				System.out.println("No Element Found");
			}
		}
	}

	private String checkGA(String pageLink, String GATrackingOnElement) 
	{
		System.out.println("Checking GA Tracking On Page : " + pageLink + " On Element " + GATrackingOnElement 
				+ "\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
							+ "++++++++++++++++++++++++++++++++++++");
		// fetching GA
		String scriptToExecute = "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";
		String netData = ((JavascriptExecutor)driver).executeScript(scriptToExecute).toString();
		return netData;
	}
	
	private void getModal(String pageLink, String pageName) 
	{
		driver.get(pageLink);
		setChromeDriver.waitForPageLoaded(driver);
		if (pageLink.endsWith("film/")) {
			try {
				// collecting elements using css selector
				List<WebElement> modalFilmLinks = driver.findElements(By.cssSelector("#mCSB_1_container > figure"));
				for (int i = 0; i < modalFilmLinks.size(); i++) {
					// calling method clickmodal
					clickModal(modalFilmLinks.get(i), pageLink, pageName);
				}
				// collecting elements using css selector
				List<WebElement> modalFilmVideoLinks = driver.findElements(By.cssSelector("#mCSB_1_container > .video"));
				for (int i = 0; i < modalFilmVideoLinks.size(); i++) {
					// calling method clickmodal
					clickModal(modalFilmVideoLinks.get(i), pageLink, pageName);
				}
			} catch (NoSuchElementException e) {
				System.out.println("No Element Found");
			}
		}
		try {
			// collecting elements using css selector
			List<WebElement> modalLinks = driver.findElements(By.cssSelector(".vimeo-autoplay"));
			
			for (int i = 0; i < modalLinks.size(); i++) {
				// calling method clickmodal
				clickModal(modalLinks.get(i), pageLink, pageName);
			}
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}

	private void clickModal(WebElement webElement, String pageLink,
			String pageName) 
	{
		// Creating instance of Actions class
		Actions ob = new Actions(driver);
		// calling method checkModalOpen
		checkModalOpen(ob, webElement, pageLink, pageName);
		// calling method checkModalClose
		checkModalClose(ob, webElement, pageLink, pageName);
		// finding element by class name
		WebElement elementClose = driver.findElement(By.className("close-modal"));
		ob.moveToElement(elementClose).click().build().perform();
	}
	
	private void checkModalOpen(Actions ob, WebElement webElement, String pageLink,
			String pageName) 
	{
		ob.moveToElement(webElement).click().build().perform();
		// waiting for page to load
		setChromeDriver.waitForPageLoaded(driver);
		// calling checkGA method
		data = checkGA(pageLink, "Modal Open");
		if (!data.contains(keyword)) {
			// putting all errors in error dataset
			errorSet.put(errorSet.size(), new String[] { pageLink, pageName, "GA event not fired" });
		}
	}
	
	private void checkModalClose(Actions ob, WebElement webElement, String pageLink, String pageName) 
	{
		try {
			WebElement closeElement = driver.findElement(By.className("close-modal"));
			ob.moveToElement(closeElement).click().build().perform();
			setChromeDriver.waitForPageLoaded(driver);
			// calling checkGA method
			data = checkGA(pageLink, "Modal Close");
			if (!data.contains(keyword)) {
				// putting all errors in error dataset
				errorSet.put(errorSet.size(), new String[] { pageLink, pageName, "GA event not fired" });
			}
		} catch (NoSuchElementException e) {
			System.out.println("Modal Closed On 2nd Click");
		}
	}
	
	private void checkElement(List<WebElement> contentNavLinks, WebDriver driver, String pointerElement, String pageName, String pageLink) 
	{
		if (pointerElement.equals("a")) {
			for (int i = 0; i < contentNavLinks.size() ; i++) {
				try {
					href = contentNavLinks.get(i).getAttribute("href");
					// calling checkGA method
					data = checkGA(pageLink, "Anchor");
					if (!data.contains(keyword)) {
						// putting all errors in error dataset
						errorSet.put(errorSet.size(), new String[] { href, pageName, "GA event not fired" });
					}
				} catch (NoSuchElementException e) {
					System.out.println("No Element Found");
				}
			}
		}
	}
	
	private void carousel(String carouselUrl) 
	{
		driver.get(carouselUrl);
		setChromeDriver.waitForPageLoaded(driver);
		
		try {
			WebElement modalFilmLinks = driver.findElement(By.cssSelector("#mCSB_1_container > figure"));
			// calling method clickCarousel method
			clickCarousel(modalFilmLinks,carouselUrl, pageName);
		} catch (NoSuchElementException e) {
			System.out.println("No Element Found");
		}
	}
	
	private void clickCarousel(WebElement webElement, String pageLink, String pageName) 
	{
		Actions ob = new Actions(driver);
		ob.moveToElement(webElement).click().build().perform();
		setChromeDriver.waitForPageLoaded(driver);
		
		WebElement nextElement = driver.findElement(By.className("slick-next"));
		// calling traverseCarousel method
		traverseCarousel(nextElement, ob, pageLink);
		
		WebElement prevElement = driver.findElement(By.className("slick-prev"));
		// calling traverseCarousel method
		traverseCarousel(prevElement, ob, pageLink);
	}

	private void traverseCarousel(WebElement clickElement, Actions ob, String pageLink) 
	{
		for (int i = 0; i < 3; i++) {
			try {
				ob.moveToElement(clickElement).click().build().perform();
				setChromeDriver.waitForPageLoaded(driver);
				// calling checkGA method
				data = checkGA(pageLink, "Carousel Slider");
				if (!data.contains(keyword)) {
					// putting all errors in error dataset
					errorSet.put(errorSet.size(), new String[] {href, pageName, "GA event not fired"});
				}
			} catch (NoSuchElementException e) {
				System.out.println("Element Not Found");
			}
		}
	}
	
	private void readMoreLess(String readMoreLessUrl)
	{
		try {
			driver.get(readMoreLessUrl);
			setChromeDriver.waitForPageLoaded(driver);
			
			WebElement element = driver.findElement(By.className("film_collection"));
			List<WebElement> navLinks = element.findElements(By.cssSelector("article a[title=\"VIEW DETAILS\"]"));

			for (int j = 0; j < navLinks.size(); j++) {
				driver.get(readMoreLessUrl);
				setChromeDriver.waitForPageLoaded(driver);
				element = driver.findElement(By.className("film_collection"));
				navLinks = element.findElements(By.cssSelector("article a[title=\"VIEW DETAILS\"]"));
				pageLink = navLinks.get(j).getAttribute("href");
				
				if (!pageLink.endsWith("#")) {
					try {
						driver.get(pageLink);
						setChromeDriver.waitForPageLoaded(driver);
						// calling method readMore_Less_Test
						readMore_Less_Test();
					} catch (TimeoutException e2) {
						System.out.println("Catching timeout exception.\nPage refersh done");
						driver.navigate().refresh();
					}
				} 
			}
		} catch (Exception e) {
			System.out.println("Unable To Traverse On The Link : " + readMoreLessUrl);
		} 
	}

	private void readMore_Less_Test() 
	{
		boolean flag = false;
		WebElement readmore = null;
		
		try {
			// finding element by class name
			readmore = this.driver.findElement(By.className("readmore_cta"));
			flag = true;
		} catch (NoSuchElementException e) {
			flag = false;
		}
		
		try {
			if (flag) {
				readmore.click();
				Thread.sleep(1000);
				// calling checkGA method
				data = checkGA(pageLink, "Read More Read Less");
				if (!data.contains(keyword)) {
					// putting all errors in error dataset
					errorSet.put(errorSet.size(), new String[] { href, pageName, "GA event not fired" });
				}
			}
		} catch (Exception e) {
			System.out.println("No Class Found For Read More Read Less");
		} 
	}

	private void print(String string) 
	{
		System.out.println("===================================\n" + string + " " + errorSet.size() 
		+ "\n===================================");
		for (Entry<Integer, String[]> entry : errorSet.entrySet()){
			System.out.println("{ Page Link : " + entry.getValue()[0] + " } , "
							+ "{ in page : " + entry.getValue()[1] + " } , "
							+ "{ has error : " + entry.getValue()[2] + " }");
		}
	}
}
