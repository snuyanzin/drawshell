package com.springer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Unit test for simple SpringerShell.
 */
public class SpringerShellTest
    extends TestCase {
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public SpringerShellTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(SpringerShellTest.class);
  }

  /**
   * Test for Q command
   */
  @org.junit.Test(timeout = 1)
  public void testExit() throws IOException {
    File tmpHistoryFile = File.createTempFile("Qcommand", "temp");
    try (BufferedWriter bw =
        new BufferedWriter(new FileWriter(tmpHistoryFile))) {
      bw.write("Q");
      bw.flush();
    }
    tmpHistoryFile.deleteOnExit();
    SpringerShell.main(new String[] {tmpHistoryFile.getAbsolutePath()});
    assertTrue(true);
  }
}
