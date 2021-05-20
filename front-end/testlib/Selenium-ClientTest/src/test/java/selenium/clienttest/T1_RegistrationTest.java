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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import selenium.clienttest.config.SeleniumConfig;
import selenium.clienttest.config.testcategories.FullIntegrationTest;
import selenium.clienttest.config.testcategories.MockedUnitTest;
import selenium.clienttest.utils.TestUtils;

public class T1_RegistrationTest {
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
  @Category({MockedUnitTest.class,FullIntegrationTest.class})
  public void testRegistration() throws Exception {
    driver.get("http://localhost:8099");
    driver.findElement(By.linkText("Register")).click();
    driver.findElement(By.id("registerUsername")).clear();
    driver.findElement(By.id("registerUsername")).sendKeys("user");
    driver.findElement(By.id("registerPassword")).clear();
    driver.findElement(By.id("registerPassword")).sendKeys("pass1");
    driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/div/app-register/div/div/div")).click();
    driver.findElement(By.id("registerUsername")).clear();
    driver.findElement(By.id("registerUsername")).sendKeys("test1");
    driver.findElement(By.id("registerName")).click();
    driver.findElement(By.id("registerName")).clear();
    driver.findElement(By.id("registerName")).sendKeys("TestUser1");
    driver.findElement(By.id("registerEmail")).click();
    driver.findElement(By.id("registerEmail")).clear();
    driver.findElement(By.id("registerEmail")).sendKeys("test@test.com");
    driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/div/app-register/div/div/div")).click();
    driver.findElement(By.id("registerPassword")).clear();
    driver.findElement(By.id("registerPassword")).sendKeys("test1");
    driver.findElement(By.id("registerPassword2")).click();
    driver.findElement(By.id("registerPassword2")).clear();
    driver.findElement(By.id("registerPassword2")).sendKeys("test1pass");
    driver.findElement(By.id("registerPassword")).click();
    driver.findElement(By.id("registerPassword")).clear();
    driver.findElement(By.id("registerPassword")).sendKeys("test1pass");
    String startVal= driver.findElement(By.cssSelector("pre")).getText();
    driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/div/app-register/div/div/div/div/form/button/div")).click();
      TestUtils.waitUntilValueChanges(By.cssSelector("pre"),startVal);
    Assertions.assertEquals("Registration for test1 succesful",driver.findElement(By.cssSelector("pre")).getText());
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
