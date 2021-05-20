/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selenium.clienttest;

import java.util.concurrent.TimeUnit;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import selenium.clienttest.config.SeleniumConfig;
import selenium.clienttest.utils.TestUtils;

/**
 *
 * @author h9pbcl
 */

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import selenium.clienttest.config.SeleniumConfig;
import selenium.clienttest.utils.TestUtils;


public class T4ProjectManagerTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
     driver = SeleniumConfig.getChromeDriver();
    baseUrl = "https://www.google.com/";
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
     actions= new Actions(driver);
  }
  Actions actions;
  
  public void createProject(String name) throws Exception {
       TestUtils.login("user", "pass1");    
    driver.findElement(By.xpath("(//img[@alt='folder_icon'])[2]")).click();
    Thread.sleep(1000);
    driver.findElement(By.xpath("(//img[@alt='folder_icon'])[7]")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(name);
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(Keys.ENTER);
        Thread.sleep(1000);
    actions.doubleClick(driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]"))).perform();
       Thread.sleep(1000);
  }
  public void createProjectFolder(String name) throws InterruptedException{
      
    driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/app-top-menu/p/mat-toolbar/div/button")).click();
    driver.findElement(By.xpath("(//img[@alt='folder_icon'])[6]")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(name);
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(Keys.ENTER);
       Thread.sleep(1000);
  }
    @Test//case 1
  public void testCreateProject() throws Exception {
        createProject("case1project");
            Thread.sleep(1000);
            assertNotNull(driver.findElement(By.linkText("case1project"+"/")));
                Thread.sleep(1000);
        
  }
  public void dblClickByText(String name){
          actions.doubleClick(driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]"))).perform();
  }
   public void clickByText(String name){
          driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]")).click();
  }
public WebElement nthElementInFileManager(int n){
  return  driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer/div/div/app-left-panel-component/div/div["+n+3+"]/div"));
}
  @Test//case 2
  public void testCreateProjectFolder() throws Exception {
      createProject("case2project");
       Thread.sleep(1000);
   createProjectFolder("testprojectfolder");
     Thread.sleep(1000);
            assertNotNull( driver.findElement(By.xpath("//*[contains(text(), 'testprojectfolder')]")));
            dblClickByText("testprojectfolder");
            assertNotNull(driver.findElement(By.linkText("testprojectfolder"+"/")));
      if(nthElementInFileManager(0).getText().equals("back")){
          nthElementInFileManager(0).click();
      }
         assertTrue("testprojectfolder".contains(driver.findElement(By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/app-attribute/div/div/div/span")).getText()));

            
  }

   //case3
   @Test (expected=NoSuchElementException.class)
  public void deleteProjectFolderTest() throws Exception {
     
         
         createProject("case3project");
createProjectFolder("projectfolder_to_delete");

     Thread.sleep(1000); 
     assertNotNull(driver.findElement(By.linkText("case3project/")));
                     driver.findElement(By.xpath("//*[contains(text(), 'projectfolder_to_delete')]")).click();
                         driver.findElement(By.xpath("(//img[@alt='folder_icon'])[7]")).click();//delete button
     Thread.sleep(1000);
       assertNull(driver.findElement(By.xpath("//*[contains(text(), 'projectfolder_to_delete')]")));
                                driver.findElement(By.xpath("(//img[@alt='folder_icon'])[8]")).click();//refresh button
 
         driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS); 
         try{
      Exception ex=Assertions.assertThrows(NoSuchElementException.class, () -> {
     driver.findElements(By.xpath("//*[contains(text(), 'projectfolder_to_delete')]")).isEmpty();
          assertTrue(false);
    });
         }catch(NoSuchElementException ex){
             assertNotNull(ex);
         }
           // assertNull(driver.findElement(By.linkText("testproject/")));
    
  }
  
    @Test//case 4
  public void testCreateNestedFolder() throws Exception {
        createProject("case4project");
        createProjectFolder("basefolder");
     
        dblClickByText("basefolder");
        createProjectFolder("nestedFolder");
            dblClickByText("nestedFolder");
                        assertNotNull(driver.findElement(By.linkText("nestedFolder"+"/")));
                        


  }
  /* @Test//case 5
  public void testCreate() throws Exception {
        createProject("case4project");
        createProjectFolder("basefolder");
        dblClickByText("basefolder");
        createProjectFolder("nestedFolder");
            dblClickByText("nestedFolder");
                        assertNotNull(driver.findElement(By.linkText("nestedFolder"+"/")));


  }*/

  @After
  public void tearDown() throws Exception {
          driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

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

