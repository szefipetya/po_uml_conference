/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selenium.clienttest;

/**
 *
 * @author h9pbcl
 */


import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import selenium.clienttest.config.SeleniumConfig;
import selenium.clienttest.config.testcategories.FullIntegrationTest;
import selenium.clienttest.config.testcategories.MockedUnitTest;
import selenium.clienttest.utils.TestUtils;

public class T2_LoginTest {
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
    @Category({FullIntegrationTest.class})

  public void test1() throws Exception {
    driver.get("http://localhost:8099");
    driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();
    driver.findElement(By.id("loginUsername")).clear();
    driver.findElement(By.id("loginUsername")).sendKeys("user");
    driver.findElement(By.id("loginPassword")).clear();
    driver.findElement(By.id("loginPassword")).sendKeys("pass1");
    driver.findElement(By.id("loginUsername")).click();
        String startVal= driver.findElement(By.cssSelector(".errors")).getText();

    driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/div/app-login/div/div/div/div/form/button/div")).click();
         TestUtils.waitUntilValueChanges(By.cssSelector(".errors"),startVal);
    Assertions.assertEquals("Login Succesful",driver.findElement(By.cssSelector(".errors")).getText());

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
