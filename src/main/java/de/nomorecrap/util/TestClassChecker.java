package de.nomorecrap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestClassChecker {

  public TestClassChecker() {
    super();
  }
  
  public boolean isTestClass(File classFile)  {
    try {
      CPStringReader reader = new CPStringReader(new FileInputStream(classFile));
      String superclassName = reader.getSuperClassName();
      if (isSuperClassTestClass(superclassName)) {
        return true;
      }
      if (isJUnit4TestClass(reader.getConstantPoolStrings())) {
        return true;
      }
    } catch (FileNotFoundException e) {     
      e.printStackTrace();
    } catch (IOException e) {     
      e.printStackTrace();
    }
    return false;
  }

  /** Not exactly the right name. Also checks for testsuites */
  private boolean isJUnit4TestClass(String[] constantPoolStrings) {
    for (int i = 0; i < constantPoolStrings.length; i++) {
      if (constantPoolStrings[i].equals("Lorg/junit/Test;") || constantPoolStrings[i].equals("Ljunit/framework/Test;")) {
          return true;
      }
    }    
    return false;
  }

  private boolean isSuperClassTestClass(String superclassName) {
    return superclassName != null && (extendsJunitTestCase(superclassName) || extendsAgitarTestCase(superclassName));
  }

  private boolean extendsAgitarTestCase(String superclassName) {
    return superclassName.equals("com.agitar.lib.AgitarTestCase") || superclassName.equals("com.agitar.lib.junit.AgitarTestCase");
  }

  private boolean extendsJunitTestCase(String superclassName) {
    return superclassName.equals("junit.framework.TestCase");
  }
  
}
