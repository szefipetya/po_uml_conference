/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selenium.clienttest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import selenium.clienttest.utils.TestUtils;

/**
 *
 * @author h9pbcl
 */
public class SeleniumConfig {
    public static WebDriver getChromeDriver(){
   
         System.setProperty("webdriver.chrome.driver",".//chromedriver90.exe");
              WebDriver driver=new ChromeDriver();
        TestUtils.setDriver(driver);
   return driver;
    }
}
