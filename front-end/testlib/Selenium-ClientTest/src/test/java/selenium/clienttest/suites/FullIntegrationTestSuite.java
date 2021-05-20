/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selenium.clienttest.suites;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import selenium.clienttest.T1_RegistrationTest;
import selenium.clienttest.T2_LoginTest;
import selenium.clienttest.config.testcategories.FullIntegrationTest;

/**
 *
 * @author h9pbcl
 */
@RunWith(Categories.class)
@Categories.IncludeCategory(FullIntegrationTest.class)
@Suite.SuiteClasses({T1_RegistrationTest.class, T2_LoginTest.class})
public class FullIntegrationTestSuite {
    
}
