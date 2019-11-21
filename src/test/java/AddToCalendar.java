import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class AddToCalendar 
{
	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();
	
	@Test
	public void init() 
	{
		pageVisitor();
	}
	
	// This method visit all the pages/links present in the header element
	protected void pageVisitor() 
	{		
		try {
			WebElement element = driver.findElement(By.id("menu-main-navigation"));
			List<WebElement> navLinks = element.findElements(By.tagName("a"));
			System.out.println("\nTotal Links In Navigation Are : " + navLinks.size()
							+ "\n=================================================");
			
			for (int i = 0; i < navLinks.size(); i++) {
				try { 
					element = driver.findElement(By.id("menu-main-navigation"));
					navLinks = element.findElements(By.tagName("a"));
					System.out.println("Navigating Link : " + navLinks.get(i).getAttribute("href") 
							+ "\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					
					addToCalendarLinkLocator(navLinks.get(i).getAttribute("href"));
				} catch(NoSuchElementException e) {
					System.out.println("Unable To Find Element : " + e.getMessage());
				}
			}
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		}
	}

	/* addToCalendarLinkLocator method is used to find the link containing the add to calendar functionality
	 *  by searching it's title
	 */
	protected void addToCalendarLinkLocator(String eventsLink) 
	{
		try {
			driver.get(eventsLink);
			setChromeDriver.waitForPageLoaded(driver);
			
			WebElement contentElement = driver.findElement(By.cssSelector("div#content"));
			List<WebElement> contentEventLinkElements = contentElement.findElements(By.cssSelector("div.content > a[title='Add To Calendar']"));
			
			if (contentEventLinkElements.size() == 0) {
				System.out.println("Add To Calendar Link Is Not Present On Page : " + eventsLink 
						+ "\n----------------------------------------------------------------------------------------");;
			} else {
				System.out.println("Add To Calendar Link Is Present On Page : " + eventsLink
						+ "\n----------------------------------------------------------------------------------------");
			}

			for (int i = 0; i < contentEventLinkElements.size(); i++) {
				try {
					contentElement = driver.findElement(By.cssSelector("div#content"));
					contentEventLinkElements = contentElement.findElements(By.cssSelector("div.content > a[title='Add To Calendar']"));

					checkingCalendarUrl(contentEventLinkElements.get(i).getAttribute("href"));
				} catch(NoSuchElementException e) {
					System.out.println("Unable To Find Element : " + e.getMessage());
				}
			}
		} catch(NoSuchElementException e) {
			System.out.println("Unable To Find Element : " + e.getMessage());
		}
	}

	/* checkingCalendarUrl method is used to check whether the url parameters like name, details, dates, etc
	 * are not empty after saving all the event details 
	 */
	protected void checkingCalendarUrl(String calendarHref)  
	{  
		String name = "", value = "";
	    String[] params = calendarHref.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    
	    for (String param : params) { 
	        if (name.contains("?")) {
	        	String[] textParam = name.split("\\?");
	        	map.put(textParam[1], value);
	        }
	        name = param.split("=")[0];  
	        value = param.split("=")[1];  
	        map.put(name, value); 
	    } 
	    // checking for the parameters along with the values in the url
	    if (map.get("details").isEmpty() && map.get("dates").isEmpty() && map.get("location").isEmpty() && map.get("text").isEmpty()) {
	    	System.out.println("Event Has Not Been Added To Calendar\n-----------------------------------");
	    } else {
	    	System.out.println("Event Has Been Added To Calendar\n-----------------------------------");
	    }
	}
	
}
