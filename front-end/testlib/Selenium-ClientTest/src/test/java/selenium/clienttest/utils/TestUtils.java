/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selenium.clienttest.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 *
 * @author h9pbcl
 */
public class TestUtils {
    static private WebDriver driver;
    static private Actions actions;
    public static String MANAGER_XPATH="//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer/div/div/app-left-panel-component";

    public static WebDriver getDriver() {
        return driver;
    }
   public static void setDriver(WebDriver driver1){
        driver=driver1;
     actions=new Actions(driver);
              
    }
   public static void waitUntilValueChanges(By by,String startVal){
      
        int i=0;
          while(getDriver().findElement(by).getText().equals(startVal)&&i<=15){
              try {i++;
                  System.out.println("sleep");
                  Thread.sleep(500);
              } catch (InterruptedException ex) {
                  Logger.getLogger(TestUtils.class.getName()).log(Level.SEVERE, null, ex);
              }
    }
    }
   public static void login(String username,String password){
        driver.get("http://localhost:8099");
    driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();
    driver.findElement(By.id("loginUsername")).clear();
    driver.findElement(By.id("loginUsername")).sendKeys(username);
    driver.findElement(By.id("loginPassword")).clear();
    driver.findElement(By.id("loginPassword")).sendKeys(password);
    driver.findElement(By.id("loginUsername")).click();
        String startVal= driver.findElement(By.cssSelector(".errors")).getText();

    driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/div/app-login/div/div/div/div/form/button/div")).click();
         TestUtils.waitUntilValueChanges(By.cssSelector(".errors"),startVal);
   }
   
     public static void createProject(String name) throws Exception {
       TestUtils.login("user", "pass1");    
                  driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/app-top-menu/p/mat-toolbar/div/button")).click();//click editor

    driver.findElement(By.xpath("(//img[@alt='folder_open'])")).click();
    Thread.sleep(500);
    driver.findElement(By.xpath("(//img[@alt='project_create'])")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(name);
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(Keys.ENTER);
        Thread.sleep(500);
    actions.doubleClick(driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]"))).perform();
       Thread.sleep(500);
                                       driver.findElement(By.xpath("(//img[@alt='project_refresh'])")).click();//refresh button
       Thread.sleep(1000);

         //  driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/app-top-menu/p/mat-toolbar/div/button")).click();//click editor

  }
  public static void createProjectFolder(String name) throws InterruptedException{
      
    driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer-content/app-top-menu/p/mat-toolbar/div/button")).click();
    driver.findElement(By.xpath("(//img[@alt='pfolder_create'])")).click();//create project folder
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(name);
    driver.findElement(By.xpath("//input[@value='']")).sendKeys(Keys.ENTER);
       Thread.sleep(1000);
  }
 
  public static void dblClickByText(String name){
          actions.doubleClick(driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]"))).perform();
  }
   public static void clickByText(String name){
          driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]")).click();
  }
   public static WebElement nthElementInFileManager(int n){
  return  driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer/div/div/app-left-panel-component/div/div["+n+3+"]/div"));
}
    public static WebElement getElemInManagerByName(String name) throws NoSuchElementException{
        int i=0;
       while(i<100){
       WebElement   elem=driver.findElement(By.xpath("//div[@id='root']/app-root/div/app-main/div/mat-drawer-container/mat-drawer/div/div/app-left-panel-component/div/div[3]/div["+(i+1)+"]/app-file/div/div"));
            if(elem.getText().equalsIgnoreCase(name)){
           return elem;
            }  i++;
       }
return null;
}
    public static void clickBackInManager(String name){
        getElemInManagerByName("back").click();
    }
}
