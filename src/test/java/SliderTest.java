import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import reusableComponents.SetChromeDriver;

public class SliderTest 
{
	@Test
	public void init()
	{
		// Calling VisitPageAndTestSlider Method 
		this.visitPageAndTestSlider();
	}
	
	private void visitPageAndTestSlider() 
	{
		String slideClass = "";
		boolean flag = false;
		
		// Creating Object of SetChromeDriver class
		SetChromeDriver setChromeDriver = new SetChromeDriver();
		WebDriver driver = setChromeDriver.setDriver();
		
		try {
			// Starting/Opening Base url
			setChromeDriver.openUrl(driver, SetChromeDriver.BASEURL, SetChromeDriver.BASETITLE);
			// Waiting for page to load
			setChromeDriver.waitForPageLoaded(driver);
			
			// Collecting elements by class name
			WebElement element = driver.findElement(By.className("banner-slider"));
			// Collecting elements by css selector
			List<WebElement> slickSlides = element.findElements(By.cssSelector("div.slick-track > li"));
			List<WebElement> slickDots = element.findElements(By.cssSelector("ul.slick-dots > li"));
			List<Integer> notWorkingButtons = new ArrayList<Integer>(); 
			
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n" 
			+ SetChromeDriver.BASEURL + "\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			
			// Traversing through all slides
			for (int i = 0; i < slickDots.size(); i++) {
				setChromeDriver.waitForPageLoaded(driver);
				if (!slickSlides.get(i).getAttribute("id").isEmpty()) {
					slideClass = slickDots.get(i).getAttribute("class");
				}
				if (slickDots.get(i).getAttribute("class").equalsIgnoreCase(slideClass)) {
					flag = true;
				}
			}
			
			// Checking whether slider is working properly or not using flag
			if (flag) {
				System.out.println("Banner Slider Works Properly\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			} else {
				System.out.println("Banner Slider Is Not Working Properly\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				for (int j = 0; j < notWorkingButtons.size(); j++) {
					System.out.println("\nButton " + notWorkingButtons.get(j) + " Is Not Working Properly\n");
				}
			}
		} catch (Exception e) {
			System.out.println("Unable To Traverse The Url : " + SetChromeDriver.BASEURL);
		} finally {
			driver.quit();
		}
	}
}
