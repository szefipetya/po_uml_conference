/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selenium.clienttest;


import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import selenium.clienttest.config.SeleniumConfig;
import selenium.clienttest.utils.TestUtils;

public class T3_CreateFolderTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
     driver = SeleniumConfig.getChromeDriver();
    baseUrl = "https://www.google.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testCreateFolder() throws Exception {
      TestUtils.login("user", "pass1");
    
      Thread.sleep(1000);
    driver.findElement(By.xpath("(//img[@alt='folder_icon'])[2]")).click();
     Thread.sleep(1000);
    driver.findElement(By.xpath("(//img[@alt='folder_icon'])[6]")).click();
     Thread.sleep(200);
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("testfolder");
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(Keys.ENTER);
      Thread.sleep(500);
     Assertions.assertEquals("testfolder",driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer/div/div/app-left-panel-component/div/div[3]/div[3]/app-file/div/div")).getText());
     Thread.sleep(500);
      
      
    // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | //div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer/div/div/app-left-panel-component/div/div[3]/div[2]/app-file/div/div | ]]
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
