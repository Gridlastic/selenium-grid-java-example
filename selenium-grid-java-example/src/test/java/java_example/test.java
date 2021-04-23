// NOTE: RUN THIS EXAMPLE AS A MAVEN PROJECT WITH THE HUB AND VIDEO URL PARAMETERS LIKE
// GOAL: test -Dhub=https://USERNAME:ACCESS_KEY@HUB_SUBDOMAIN.gridlastic.com/wd/hub -DvideoUrl=VIDEO_URL
// USERNAME:ACCESS_KEY@HUB_SUBDOMAIN and VIDEO_URL is found in the Gridlastic dashboard after you start your selenium grid.


package java_example;

import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class test {

	private RemoteWebDriver driver;
	ITestContext myTestContext;

	@Parameters({ "browser-name", "platform-name", "browser-version", "hub","videoUrl", "record-video" })
	@BeforeMethod(alwaysRun = true)
	public void beforeMethod(String browser_name, String platform_name, String browser_version, String hub, String videoUrl, String record_video,ITestContext myTestContext) throws Exception {	


		//CHROME specifics
		if (browser_name.equalsIgnoreCase("chrome")){	
			ChromeOptions options = new ChromeOptions();
			options.setCapability("version", browser_version); 
			options.setCapability("platform", platform_name);
			if (!platform_name.equalsIgnoreCase("linux")){
			options.setCapability("platformName", "windows"); //required from selenium version 3.9.1 when testing with firefox or IE. Required when testing with Chrome 77+.
			}
			options.setCapability("video", record_video); // NOTE: case sensitive string, not boolean.


			// On Linux start-maximized does not expand browser window to max screen size. Always set a window size.
			if (platform_name.equalsIgnoreCase("linux")) {
				options.addArguments(Arrays.asList("--window-position=0,0"));
				options.addArguments(Arrays.asList("--window-size=1840,1080"));	// starting with Chrome version 83, use width of 1840 instead of 1920 to capture the entire webpage on video recording.
			} else {
				options.addArguments(Arrays.asList("--start-maximized"));
			}
			driver = new RemoteWebDriver(new URL(hub),options);
		} 



		//FIREFOX version 55+ specifics
		if (browser_name.equalsIgnoreCase("firefox")){
			FirefoxOptions options = new FirefoxOptions();
			options.setCapability("version", browser_version); 
			options.setCapability("platform", platform_name);
			if (!platform_name.equalsIgnoreCase("linux")){
			options.setCapability("platformName", "windows");
			}
			options.setCapability("video", record_video); 

			// Required to specify firefox binary location on Gridlastic grid nodes starting from selenium version 3.5.3+, see firefox documentation https://www.gridlastic.com/test-environments.html#firefox_testing				
			if (!browser_version.equalsIgnoreCase("latest")) {
			if (platform_name.equalsIgnoreCase("linux")){
				options.setBinary("/home/ubuntu/Downloads/firefox"+browser_version+"/firefox");
			} else {
				options.setBinary("C:\\Program Files (x86)\\Mozilla Firefox\\firefox"+browser_version+"\\firefox.exe");
			}	
			}
			driver = new RemoteWebDriver(new URL(hub),options);		
		}
		
		// INTERNER EXPLORER specifics
		if (browser_name.equalsIgnoreCase("internet explorer")) {
			InternetExplorerOptions options = new InternetExplorerOptions();
			options.setCapability("version", browser_version);
			options.setCapability("platform", platform_name);
			options.setCapability("platformName", "windows"); // required from selenium version 3.9.1 when testing with
																// firefox or IE. Required when testing with Chrome 77+.
			options.setCapability("video", record_video); // NOTE: case sensitive string, not boolean.

			driver = new RemoteWebDriver(new URL(hub), options);
		}
	
		
		// MICROSOFT EDGE specifics
		if (browser_name.equalsIgnoreCase("MicrosoftEdge")) {
			EdgeOptions options = new EdgeOptions();
			options.setCapability("version", browser_version);
			options.setCapability("platform", platform_name);
			options.setCapability("platformName", "windows"); // required from selenium version 3.9.1 when testing with
																// firefox or IE. Required when testing with Chrome 77+.
			options.setCapability("video", record_video); // NOTE: case sensitive string, not boolean.

			driver = new RemoteWebDriver(new URL(hub), options);
			driver.manage().window().maximize();
		}
		
		
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

		// On LINUX/FIREFOX the "driver.manage().window().maximize()" option does not expand browser window to max screen size. Always set a window size.
		if (platform_name.equalsIgnoreCase("linux") && browser_name.equalsIgnoreCase("firefox")) {
			driver.manage().window().setSize(new Dimension(1920, 1080));	
		}

  		// VIDEO URL
        if(record_video.equals("True")){
        myTestContext.setAttribute("video_url", videoUrl+"/play.html?" + ((RemoteWebDriver) driver).getSessionId()); 
        } else {
        myTestContext.removeAttribute("video_url");	
        }

	}

	@Parameters({"test-title"})  
	@Test
	   public void test_site(String test_title, ITestContext myTestContext) throws Exception  { 	
		driver.get("https://www.google.com/ncr");
		Thread.sleep(10000); //slow down for demo purposes
		WebElement element = driver.findElement(By.name("q"));
		element.sendKeys("webdriver");
		element.submit();
		Thread.sleep(5000); //slow down for demo purposes
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() throws Exception {
		driver.quit();
	}

}

