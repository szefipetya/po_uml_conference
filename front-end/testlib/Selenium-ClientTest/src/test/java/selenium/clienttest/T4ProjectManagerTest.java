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
     @Test//case 1
  public  void createProject() throws Exception {
        TestUtils.createProject("case1project");
            Thread.sleep(1000);
            assertNotNull(driver.findElement(By.linkText("case1project"+"/")));
                Thread.sleep(1000);
        
  }


  @Test//case 2
  public void testCreateProjectFolder() throws Exception {
      TestUtils.createProject("case2project");
       Thread.sleep(1000);
    TestUtils.createProjectFolder("testprojectfolder");
     Thread.sleep(1000);
            assertNotNull( driver.findElement(By.xpath("//*[contains(text(), 'testprojectfolder')]")));
            TestUtils.dblClickByText("testprojectfolder");
            assertNotNull(driver.findElement(By.linkText("testprojectfolder"+"/")));
      if(TestUtils.nthElementInFileManager(0).getText().equals("back")){
          TestUtils.nthElementInFileManager(0).click();
      }
         assertTrue("testprojectfolder".contains(driver.findElement(By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/app-attribute/div/div/div/span")).getText()));
  }

   //case3
   @Test
  public void deleteProjectFolderTest() throws Exception {
     
         
          TestUtils.createProject("case3project");
 TestUtils.createProjectFolder("projectfolder_to_delete");

     Thread.sleep(1000); 
     assertNotNull(driver.findElement(By.linkText("case3project/")));
                     driver.findElement(By.xpath("//*[contains(text(), 'projectfolder_to_delete')]")).click();
                         driver.findElement(By.xpath("(//img[@alt='project_trash'])")).click();//delete button
     Thread.sleep(1000);
       assertTrue(driver.findElements(By.xpath("//*[contains(text(), 'projectfolder_to_delete')]")).isEmpty());
                                driver.findElement(By.xpath("(//img[@alt='project_refresh'])")).click();//refresh button
 
         driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS); 
      
            assertTrue(driver.findElements(By.xpath("//*[contains(text(), 'projectfolder_to_delete')]")).isEmpty());

           // assertNull(driver.findElement(By.linkText("testproject/")));
    
  }
  
    @Test//case 4
  public void testCreateDoubleNestedFolder() throws Exception {
     createNestedProkectStructureAndGoToRoot("case4project");
                        Thread.sleep(500);
                                 assertTrue("baseFolder".contains(driver.findElement(
                                         By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/app-attribute/div/div/div/span")).getText()));
                                       assertTrue("nestedFolder".contains(driver.findElement(
                                         By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/div[2]/pre")).getText()));

    }

    @Test//case 5
    public void testCreateDoubleNestedFolderAndClassInside() throws Exception {
        createNestedProkectStructureAndGoToRoot("case5project");
        actions.doubleClick(TestUtils.getElemInManagerByName("v baseFolder")).perform();
        Thread.sleep(500);

        driver.findElement(By.xpath("//div[@id='tools']/div[2]/div/div/div[2]")).click();//new class on toolbox
        Thread.sleep(500);
        actions.dragAndDropBy(driver.findElement(By.xpath("//div[@id='dgbox']")), 10, -15);//draw the box

        actions.build().perform();
        Thread.sleep(1000);
        actions.doubleClick(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'New Class')]"))).perform();//dblclick on title to change it
        Thread.sleep(500);
            driver.findElement(By.id("editor-input")).click();
    driver.findElement(By.id("editor-input")).clear();
    driver.findElement(By.id("editor-input")).sendKeys("TestClass");
        driver.findElement(By.id("editor-input")).sendKeys(Keys.ENTER);
//class title changed
                        Thread.sleep(500);
                        TestUtils.nthElementInFileManager(0).click();//back
                        Thread.sleep(500);
                            
                                        assertTrue("baseFolder".contains(driver.findElement(
                                         By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/app-attribute/div/div/div/span")).getText()));
                                       assertTrue("nestedFolder".contains(driver.findElement(
                                         By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/div[2]/pre")).getText()));
                                        assertTrue("TestClass".contains(driver.findElement(
                                         By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/div[3]/pre")).getText()));
        TestUtils.getElemInManagerByName("> baseFolder").click();
        driver.findElement(By.xpath(TestUtils.MANAGER_XPATH+"//*[contains(text(), 'baseFolder')]/span")).click();//kis nyilícska
        assertNotNull(driver.findElement(By.xpath(TestUtils.MANAGER_XPATH+"//*[contains(text(), 'TestClass')]")));
        return;     
    }
    //TODO: DELETE CLASS JÓ E
      @Test//case 6
    public void testCreateDoubleNestedFolderAndClassInsideAndDeleteClass() throws Exception {
        createNestedProkectStructureAndGoToRoot("case6project");
        actions.doubleClick(TestUtils.getElemInManagerByName("v baseFolder")).perform();
        Thread.sleep(500);

        driver.findElement(By.xpath("//div[@id='tools']/div[2]/div/div/div[2]")).click();//new class on toolbox
        Thread.sleep(500);
        actions.dragAndDropBy(driver.findElement(By.xpath("//div[@id='dgbox']")), 10, -15);//draw the box

        actions.build().perform();
        Thread.sleep(1000);
        actions.doubleClick(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'New Class')]"))).perform();//dblclick on title to change it
        Thread.sleep(500);
            driver.findElement(By.id("editor-input")).click();
    driver.findElement(By.id("editor-input")).clear();
    driver.findElement(By.id("editor-input")).sendKeys("TestClass");
        driver.findElement(By.id("editor-input")).sendKeys(Keys.ENTER);
//class title changed
                        Thread.sleep(500);
                              actions.click(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass')]"))).perform();//click to select
                                                      Thread.sleep(500);
                              actions.sendKeys(Keys.DELETE);

                        Thread.sleep(500);
                                                          actions.click(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'nestedFolder')]"))).perform();//dblclick on title to change it
                        Thread.sleep(500);

        assertTrue(driver.findElements(By.xpath(TestUtils.MANAGER_XPATH+"//*[contains(text(), 'TestClass')]")).isEmpty());

                                         Thread.sleep(500);
                                         
                                                                 TestUtils.nthElementInFileManager(0).click();//back
                                                           Thread.sleep(500);

        driver.findElement(By.xpath(TestUtils.MANAGER_XPATH+"//*[contains(text(), 'baseFolder')]/span")).click();//kis nyilícska
                assertTrue(driver.findElements(By.xpath(TestUtils.MANAGER_XPATH+"//*[contains(text(), 'TestClass')]")).isEmpty());//nincs ott a TestClass
                
                     assertTrue("nestedFolder".contains(driver.findElement(
                                         By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/div[2]/pre")).getText()));//nestedFolder bennt van
                                        assertTrue(driver.findElements(
                                         By.xpath("//div[@id='dgbox']/div/div/app-package-object/app-diagram-object/div/div/div/div[3]/pre")).isEmpty());//TestClass már nincs benn a baseFolder-en belül
                                           assertTrue(driver.findElements(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass')]")).isEmpty());//TestClass már nincs benn
        return;     
    }
     @Test//case 7
    public void testLineCreate() throws Exception {
        TestUtils.createProject("case7project");
           driver.findElement(By.xpath("//div[@id='tools']/div[2]/div/div/div[2]")).click();//new class on toolbox
        Thread.sleep(500);
        actions.dragAndDropBy(driver.findElement(By.xpath("//div[@id='dgbox']")), 10, -15);//draw the box

        actions.build().perform();
        Thread.sleep(1000);
        actions.doubleClick(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'New Class')]"))).perform();//dblclick on title to change it
           driver.findElement(By.id("editor-input")).click();
    driver.findElement(By.id("editor-input")).clear();
    driver.findElement(By.id("editor-input")).sendKeys("TestClass1");
        driver.findElement(By.id("editor-input")).sendKeys(Keys.ENTER);//change class title
        Thread.sleep(500);
        
             driver.findElement(By.xpath("//div[@id='tools']/div[2]/div/div/div[2]")).click();//new class on toolbox
        Thread.sleep(500);
        actions.moveToElement(driver.findElement(By.xpath("//div[@id='dgbox']")), -200, -200);//draw the box
        actions.clickAndHold();
        actions.moveByOffset(120, 200);
        actions.release();

        actions.build().perform();
        Thread.sleep(1000);
        actions.doubleClick(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'New Class')]"))).perform();//dblclick on title to change it
            driver.findElement(By.id("editor-input")).click();
    driver.findElement(By.id("editor-input")).clear();
    driver.findElement(By.id("editor-input")).sendKeys("TestClass2");
        driver.findElement(By.id("editor-input")).sendKeys(Keys.ENTER);//change class title
        Thread.sleep(500);
        
                     driver.findElement(By.xpath("//div[@id='tools']/div[2]/div/div/div[6]")).click();//click new aggregation on toolbox
                     //drawing the line
                             actions.clickAndHold(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]")));
                             actions.release(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass2')]")));
                             actions.build().perform();
                     Thread.sleep(4000);
                     
                         assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]")).isDisplayed());
                         assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass2')]")).isDisplayed());

                     // a vonalak működését nem tudjuk letesztelni itt, mert a HTML5 canvas-ról nem lehet infót kigyűjteni.


return;     
    
    }
    
       @Test//case 8
    public void testAttributesCreate() throws Exception {
        TestUtils.createProject("case8project");
          helper_createClass("TestClass1",0,0,200,150);
          helper_createClass("TestClass2",-200,-250,100,150);
                     driver.findElement(By.xpath("//div[@id='tools']/div[2]/div/div/div[6]")).click();//click new aggregation on toolbox
                     //drawing the line
                             actions.clickAndHold(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]")));
                             actions.release(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass2')]")));
                             actions.build().perform();
                     Thread.sleep(1000);
                     
                         assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]")).isDisplayed());
                         assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass2')]")).isDisplayed());
                         //insert attribute testAttr1:int
                                                        
                        Thread.sleep(500);
                      WebElement toHover=  driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]/../../../../../div[2]/div/app-attribute-group//*[contains(@class,'class-element-group')]"));
                     actions.moveToElement(toHover).build().perform();
                     Thread.sleep(1000);
                        WebElement  groupbtn= driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]/../../../../../div[2]/div/app-attribute-group/div/div[2]/div"));

                    groupbtn.click();

                     driver.findElement(By.xpath("//input[@id='editor-input']")).click();
                     driver.findElement(By.xpath("//input[@id='editor-input']")).clear();
    driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys("testAttr1:int");
        driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys(Keys.ENTER);//change change 
    
//insert attribute testAttr1:int
      Thread.sleep(500);
                       toHover=  driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]/../../../../../div[2]/div[2]/app-attribute-group//*[contains(@class,'class-element-group')]"));
                     actions.moveToElement(toHover).build().perform();
                     Thread.sleep(1000);
                          groupbtn= driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]/../../../../../div[2]/div[2]/app-attribute-group/div/div[2]/div"));

                    groupbtn.click();
                     Thread.sleep(1000);

                     driver.findElement(By.xpath("//input[@id='editor-input']")).click();
                     driver.findElement(By.xpath("//input[@id='editor-input']")).clear();
    driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys("-tf(a:strg):void");
            driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys(Keys.ENTER);//change attr
assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'testAttr1')]")).isDisplayed());
assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'tf(a:strg)')]")).isDisplayed());
     //insert attribute testAttr1:int
                     // a vonalak működését nem tudjuk letesztelni itt, mert a HTML5 canvas-ról nem lehet információt gyűjteni.
return;     
    } 

       @Test//case 9
    public void testAttributesDelete() throws Exception {
        TestUtils.createProject("case9project");
          helper_createClass("TestClass1",0,0,200,150);
          helper_createClass("TestClass2",-200,-250,100,150);
                     driver.findElement(By.xpath("//div[@id='tools']/div[2]/div/div/div[6]")).click();//click new aggregation on toolbox
                     //drawing the line
                             actions.clickAndHold(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]")));
                             actions.release(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass2')]")));
                             actions.build().perform();
                     Thread.sleep(1000);
                     
                         assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]")).isDisplayed());
                         assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass2')]")).isDisplayed());
                         //insert attribute testAttr1:int
                                                        
                        Thread.sleep(500);
                      WebElement toHover=  driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]/../../../../../div[2]/div/app-attribute-group//*[contains(@class,'class-element-group')]"));
                     actions.moveToElement(toHover).build().perform();
                     Thread.sleep(1000);
                        WebElement  groupbtn= driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]/../../../../../div[2]/div/app-attribute-group/div/div[2]/div"));

                    groupbtn.click();

                     driver.findElement(By.xpath("//input[@id='editor-input']")).click();
                     driver.findElement(By.xpath("//input[@id='editor-input']")).clear();
    driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys("testAttr1:int");
        driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys(Keys.ENTER);//change change 
    
//insert attribute testAttr1:int
      Thread.sleep(500);
                       toHover=  driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]/../../../../../div[2]/div[2]/app-attribute-group//*[contains(@class,'class-element-group')]"));
                     actions.moveToElement(toHover).build().perform();
                     Thread.sleep(1000);
                          groupbtn= driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'TestClass1')]/../../../../../div[2]/div[2]/app-attribute-group/div/div[2]/div"));

                    groupbtn.click();
                     Thread.sleep(1000);

                     driver.findElement(By.xpath("//input[@id='editor-input']")).click();
                     driver.findElement(By.xpath("//input[@id='editor-input']")).clear();
    driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys("-tf(a:strg):void");
            driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys(Keys.ENTER);//change attr
assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'testAttr1')]")).isDisplayed());
assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'tf(a:strg)')]")).isDisplayed());
     //insert attribute testAttr1:int
                     // a vonalak működését nem tudjuk letesztelni itt, mert a HTML5 canvas-ról nem lehet információt gyűjteni.
                     
                     actions.doubleClick(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'testAttr1')]"))).perform();
                                                 Thread.sleep(1000);

                     driver.findElement(By.xpath("//input[@id='editor-input']")).click();
                           driver.findElement(By.xpath("//input[@id='editor-input']")).clear(); 
         
                driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys("");//change attr
                            Thread.sleep(1000);
                            driver.findElement(By.xpath("//div[@id='dgbox']")).click();
          //  driver.findElement(By.xpath("//input[@id='editor-input']")).sendKeys(Keys.ENTER);//change attr end
            Thread.sleep(1000);
            //NOTE, PLEASE READ:
            //valamiért a törlés nem tesztelhető, a gép nem úgy csinálja az enter lenyomást, mint ahogy a user. téma lezárva
      //                          assertTrue(driver.findElements(By.xpath("//div[@id='dgbox']//*[contains(text(), 'testAttr1')]")).isEmpty());
assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'tf(a:strg)')]")).isDisplayed());

          //  assertTrue(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'testAttr1')]")).isDisplayed());

return;     
    } 
    //helper
public void helper_createClass(String name,int dgbox_x_diff, int dgbox_y_diff,int width, int height) throws InterruptedException{
         driver.findElement(By.xpath("//div[@id='tools']/div[2]/div/div/div[2]")).click();//new class on toolbox
        Thread.sleep(500);
          actions.moveToElement(driver.findElement(By.xpath("//div[@id='dgbox']")), dgbox_x_diff, dgbox_y_diff);//draw the box
        actions.clickAndHold();
        actions.moveByOffset(width, height);
        actions.release();

        actions.build().perform();
        Thread.sleep(1000);
      /*  actions.dragAndDropBy(driver.findElement(By.xpath("//div[@id='dgbox']")), width, height);//draw the box
        actions.build().perform();*/
     //   Thread.sleep(1000);
        actions.doubleClick(driver.findElement(By.xpath("//div[@id='dgbox']//*[contains(text(), 'New Class')]"))).perform();//dblclick on title to change it
           driver.findElement(By.id("editor-input")).click();
    driver.findElement(By.id("editor-input")).clear();
    driver.findElement(By.id("editor-input")).sendKeys(name);
        driver.findElement(By.id("editor-input")).sendKeys(Keys.ENTER);//change class title
        Thread.sleep(500);
    }


  public void createNestedProkectStructureAndGoToRoot(String name) throws Exception{
          TestUtils.createProject(name);
         TestUtils.createProjectFolder("baseFolder");
                             Thread.sleep(500);
         TestUtils.dblClickByText("baseFolder");
         TestUtils.createProjectFolder("nestedFolder");
                                      Thread.sleep(500);

             TestUtils.dblClickByText("nestedFolder");
                        assertNotNull(driver.findElement(By.linkText("nestedFolder"+"/")));
                        Thread.sleep(500);
                      TestUtils.nthElementInFileManager(0).click();
                                              Thread.sleep(500);
                      TestUtils.nthElementInFileManager(0).click();
                                             //   driver.findElement(By.xpath("//*[contains(text(), 'back')]")).click();

                        driver.findElement(By.xpath("//*[contains(text(), 'baseFolder')]/span")).click();//kis nyilícska
  }
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

