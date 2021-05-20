/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selenium.clienttest.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author h9pbcl
 */
public class TestUtils {
    static private WebDriver driver;

    public static WebDriver getDriver() {
        return driver;
    }
   public static void setDriver(WebDriver driver1){
        driver=driver1;
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
}
