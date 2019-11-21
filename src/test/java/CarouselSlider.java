import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class CarouselSlider 
{
	public static final String URL = "http://takecare:takecare@staging.takecare.mysites.io/upcoming-events/";
	
	SetChromeDriver setChromeDriver = new SetChromeDriver();
	WebDriver driver = setChromeDriver.setDriver();

	String link = "";
	
	@Test
	public void init() 
	{
		pageVisitor();
	}

	// pageVisitor method is used to open the page which contains carousel slider
	protected void pageVisitor() 
	{
		Dimension dimension = new Dimension(1400, 600);
		try {
			// opening the url
			driver.get(URL);
			driver.manage().window().setSize(dimension);
			// collecting all the anchor tags from the element using id
			WebElement element = driver.findElement(By.id("menu-event"));
			List<WebElement> subElement = driver.findElement(By.id("menu-event")).findElements(By.tagName("a"));
			
			// traversing each anchor links 
			for (int i = 0; i < subElement.size(); i++) {
				try {
					// reinitializing the elements to avoid stale element reference exception
					element = driver.findElement(By.className("sub_menu"));
					subElement = element.findElements(By.tagName("a"));
					// storing the href attribute of anchor tag in link variable
					link = subElement.get(i).getAttribute("href");
	
					try {
						// opening the href of the anchor tag
						driver.get(link);
						setChromeDriver.waitForPageLoaded(driver);
						// calling carouselSliderTesting method
						carouselSliderTesting();
					} catch (TimeoutException e) {
						System.out.println("Catching timeout exception");
						driver.navigate().refresh();
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

	// carouselSlidertesting method is used to collect the list, arrows and click on that arrow
	protected void carouselSliderTesting() 
	{
		int endNumber = 0;
		try {
			// calling clickElement method
			clickElement(driver.findElement(By.cssSelector("button.slick-next")), "Next Button", driver.findElement(By.cssSelector("li#slick-slide0" + (endNumber + 1))));
			// calling clickElement method
			clickElement(driver.findElement(By.cssSelector("button.slick-prev")), "Previous Button", driver.findElement(By.cssSelector("li#slick-slide0" + endNumber)));
			endNumber++;
		} catch(NoSuchElementException e) {
			System.out.println("Slider Is Not Present On Link : " + link + "\n-----------------------------------"
					+ "--------------------------------------------------------------------------");
		}
	}

	/* clickElement method is used to click on the next and previous arrow button and check for the 
	 * slick-current class in the slick active 
	 */
	protected void clickElement(WebElement clickElement, String button, WebElement slickTrackLi)
	{
		Actions action = new Actions(driver);
		boolean flag = false;
		
		for (int i = 0; i < 1; i++) {
			try {
				action.moveToElement(clickElement).click().build().perform();
				Thread.sleep(500);
				// checking for the slick current class in the appropriate li
				if (slickTrackLi.getAttribute("class").contains("slick-current")) {
					flag = true;
				}
			} catch(NoSuchElementException | InterruptedException  e) {
				System.out.println("Unable To Find Element : " + e.getMessage());
			}
		} 
		
		if (flag) {
			System.out.println("Carousel Slider Is Working Properly On Link : " + link + " With " + button + "\n+++++++" 
					+ "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} else {
			System.out.println("Carousel Slider Is Not Working Properly On Link : " + link 
					+ "\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		}
	}
	
}
