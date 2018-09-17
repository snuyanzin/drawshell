package com.springer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Unit test for simple DrawingShell.
 */
public class DrawingShellTest
    extends TestCase {
  /**
   * 10 seconds max per method tested
   */
  @Rule
  public Timeout globalTimeout = Timeout.seconds(10);

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public DrawingShellTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(DrawingShellTest.class);
  }

  /**
   * Test for Q command
   */
  public void testExit() throws IOException {
    File tmpHistoryFile = File.createTempFile("Qcommand", "temp");
    try (BufferedWriter bw =
        new BufferedWriter(new FileWriter(tmpHistoryFile))) {
      bw.write("Q             ");
      bw.flush();
    }
    tmpHistoryFile.deleteOnExit();
    DrawingShell.main(new String[] {tmpHistoryFile.getAbsolutePath()});
    assertTrue(true);
  }
}
