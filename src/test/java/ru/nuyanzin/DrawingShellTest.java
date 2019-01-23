package ru.nuyanzin;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.NumberFormat;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.nuyanzin.canvas.Canvas;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for DrawingShell.
 * Tests against end-to-end cases including validation.
 * File input stream is used instead of user interactive mode.
 */
public class DrawingShellTest {

  /**
   * Backup field to original System.out PrintStream object
   */
  private PrintStream originalSystemIn;

  /**
   * Byte stream to get and read data from.
   */
  private ByteArrayOutputStream os;

  /**
   * PrintStream object to use as System.out in {@link DrawingShellTest}
   */
  private PrintStream testOutputStream;


  @BeforeEach
  public void reassignOutput() {
    try {
      os = new ByteArrayOutputStream();
      testOutputStream =
          new PrintStream(os, true, StandardCharsets.UTF_8.name());
      originalSystemIn = System.out;
      System.setOut(testOutputStream);
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  @AfterEach
  public void reassignOutputBack() {
    try {
      System.setOut(originalSystemIn);
      testOutputStream.close();
      os.close();
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for non existing file
   */
  @Test
  public void testWrongFileCommand() {
    String wrongFileAbsolutePath =
        Paths.get("WrongFile").toAbsolutePath().toString();
    try {
      DrawingShell.main(new String[]{wrongFileAbsolutePath});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              Loc.getLocMessage("file-not-exist", wrongFileAbsolutePath)));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for wrong start up arguments e.g. 2 files
   */
  @Test
  public void testWrongStartupArguments() {
    File tmpCommandFile = createTmpCommandFile("testWrongStartupArguments",
        "");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath(),
          tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("usage-start")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for long command
   */
  @Test
  public void testLongCommand() {
    String longCommand = "lslfjslfjhslfjshljhfslkjhslkjfhsljfslkjfhslkjfs"
        + "asdkfjasdfskdhfgksdtfgskhfsgkjahgfskjfhsgkjhskjdtskjhf"
        + "sdflsjhdflskjhflskjafhlskjafhslkjdfhslkjdfhlskdfhskhjskdf";
    File tmpCommandFile = createTmpCommandFile("testLongCommand", longCommand);
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(DrawingShell.getCutString(longCommand)));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for C command
   */
  @Test
  public void testCCommand() {
    File tmpCommandFile = createTmpCommandFile("testCCommand",
        "C 2 3");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString("----\n"
              + "|  |\n"
              + "|  |\n"
              + "|  |\n"
              + "----"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for C command with wrong arguments
   */
  @Test
  public void testCCommandWrongArguments() {
    File tmpCommandFile = createTmpCommandFile("testCCommandWrongArguments",
        "C -2 A ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              Loc.getLocMessage("usage-c", Canvas.CANVAS_DIMENSION_LIMIT)));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for C command with wrong number of arguments
   */
  @Test
  public void testCCommandWrongNumberOfArguments() {
    File tmpCommandFile = createTmpCommandFile(
        "testCCommandWrongNumberOfArguments",
        "C 1 1 12 20 ds2 A 213 213");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              Loc.getLocMessage("usage-c", Canvas.CANVAS_DIMENSION_LIMIT)));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for B command for the point out of the canvas
   */
  @Test
  public void testBCommandForPointOutOfCanvas() {
    File tmpCommandFile = createTmpCommandFile(
        "testBCommandForPointOutOfCanvas",
        "C 2 3",
        "B 12 23 q ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("usage-b", "B", 2, 3)));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for B command without specifying `c`
   */
  @Test
  public void testBCommandWithoutCharacter() {
    File tmpCommandFile = createTmpCommandFile("testBCommandWithoutCharacter",
        "C 2 3",
        "B 1 2");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("usage-b", "B", 2, 3)));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for B command with a wrong character
   */
  @Test
  public void testBCommandWithWrongCharacter() {
    File tmpCommandFile = createTmpCommandFile("testBCommandWithWrongCharacter",
        "C 2 3",
        "B4 1 2 qwe ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              Loc.getLocMessage("usage-b", "B4", 2, 3)));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for B command without having Canvas created
   */
  @Test
  public void testBCommandWithoutCanvas() {
    File tmpCommandFile = createTmpCommandFile("testBCommandWithoutCanvas",
        "B 2 3 @");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("canvas-required")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for B command called several times.
   */
  @Test
  public void testTwiceBCommand() {
    File tmpCommandFile = createTmpCommandFile("testTwiceBCommand",
        "C 50 2 ",
        "B 1 1 -",
        "B 2 2 @",
        "B 5 1 @");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              "----------------------------------------------------\n"
                  + "|@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@|\n"
                  + "|@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@|\n"
                  + "----------------------------------------------------"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for B command called several times.
   */
  @Test
  public void testFillOtherPartWithExistingSymbol() {
    File tmpCommandFile = createTmpCommandFile("testTwiceBCommand",
        "C 50 2 ",
        "L 20 1 20 2",
        "B 2 2 @",
        "B 25 1 @");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              "----------------------------------------------------\n"
                  + "|@@@@@@@@@@@@@@@@@@@x@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@|\n"
                  + "|@@@@@@@@@@@@@@@@@@@x@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@|\n"
                  + "----------------------------------------------------"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for B8 command.
   */
  @Test
  public void testB8Command() {
    File tmpCommandFile = createTmpCommandFile("testTwiceBCommand",
        "C 9 2 ",
        "L 1 1 1 1",
        "L 2 2 2 2",
        "L 3 1 5 1",
        "L 6 2 9 2",
        "B8 1 2 @");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              "-----------\n"
                  + "|x@xxx@@@@|\n"
                  + "|@x@@@xxxx|\n"
                  + "-----------"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for B command to check if for canvas 2000x2000
   * it does not crash with OutOfMemory or StackOverFlow errors.
   * As the numbers are more than 1000 there is also current format is used.
   */
  @Test
  public void testBCommandForMillionsPoints() {
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    String formatted2000 = numberFormat.format(2000);
    File tmpCommandFile = createTmpCommandFile("testBCommandForMillionsPoints",
        "C " + formatted2000 + " " + formatted2000,
        "L 2 2 2 2",
        "B 1 1 @");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.not(CoreMatchers.containsString("Error")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for commands around with spaces
   */
  @Test
  public void testCommandsAroundWithSpaces() {
    File tmpCommandFile = createTmpCommandFile("testCommandsAroundWithSpaces",
        "        C              4        3 ",
        "        L        1        2        3       2        ",
        "  L   2      2  4            2         ",
        "           R       1       1          21       12  ",
        "      B8        1          2     @     ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              "------\n"
                  + "|@@@@|\n"
                  + "|@@@@|\n"
                  + "|@   |\n"
                  + "------"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test that for really big sizes of canvas OOM happens.
   * As the numbers are more than 1000 there is also current format is used.
   */
  @Test
  public void testOOMHappen() {
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    String formattedMaxValue = numberFormat.format(Integer.MAX_VALUE);
    try {
      File tmpCommandFile = createTmpCommandFile("testOOMHappen",
          "C " + formattedMaxValue + " " + formattedMaxValue);
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString("OutOfMemoryError"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test that after OOM happened the application could continue working
   */
  @Test
  public void testPossibilityToWorkAfterOOM() {
    File tmpCommandFile = createTmpCommandFile("testPossibilityToWorkAfterOOM",
        "C " + Integer.MAX_VALUE + " " + Integer.MAX_VALUE,
        "C 3 3",
        "B 2 2 :");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString("-----\n"
              + "|:::|\n"
              + "|:::|\n"
              + "|:::|\n"
              + "-----"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for H command
   */
  @Test
  public void testHCommand() {
    File tmpCommandFile = createTmpCommandFile("testHCommand",
        "H ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("help")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for L command without having Canvas created
   */
  @Test
  public void testLCommandWithoutCanvas() {
    File tmpCommandFile = createTmpCommandFile("testLCommandWithoutCanvas",
        "L -2 3 1 -2 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("canvas-required")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for L command with wrong number of arguments
   */
  @Test
  public void testLCommandWithWrongNumberOfArguments() {
    File tmpCommandFile = createTmpCommandFile(
        "testLCommandWithWrongNumberOfArguments",
        "C 2 3",
        "L 1 2");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("usage-l")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for L command with wrong number of arguments
   */
  @Test
  public void testLCommandWrongTypeOfArguments() {
    File tmpCommandFile = createTmpCommandFile(
        "testLCommandWrongTypeOfArguments",
        "C 2 3",
        "L A 2 A 2 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("usage-l")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for L command for non horizontal and non vertical line
   */
  @Test
  public void testLCommandForNonHorizontalNonVertical() {
    File tmpCommandFile = createTmpCommandFile("testLCommand",
        "C 3 3",
        "L -1 223 3 231 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              Loc.getLocMessage("draw-line-not-supported")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for L command with wrong number of arguments
   */
  @Test
  public void testLCommand() {
    File tmpCommandFile = createTmpCommandFile("testLCommand",
        "C 3 3",
        "L -1 2 3 2 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              "-----\n"
                  + "|   |\n"
                  + "|xxx|\n"
                  + "|   |\n"
                  + "-----"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for R command without having Canvas created
   */
  @Test
  public void testRCommandWithoutCanvas() {
    File tmpCommandFile = createTmpCommandFile("testRCommandWithoutCanvas",
        "R -2 3 1 -2 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("canvas-required")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }


  /**
   * Test for R command with wrong number of arguments
   */
  @Test
  public void testRCommandWithWrongNumberOfArguments() {
    File tmpCommandFile = createTmpCommandFile(
        "testRCommandWithWrongNumberOfArguments",
        "C 2 3",
        "R 1 2");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("usage-r")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for R command with wrong number of arguments
   */
  @Test
  public void testRCommandWrongTypeOfArguments() {
    File tmpCommandFile = createTmpCommandFile(
        "testRCommandWrongTypeOfArguments",
        "C 2 3",
        "R A 2 A 2 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(Loc.getLocMessage("usage-r")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for R command with wrong number of arguments
   */
  @Test
  public void testRCommand() {
    File tmpCommandFile = createTmpCommandFile("testRCommand",
        "C 2 3",
        "R 1 1 1 1 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              "----\n"
                  + "|x |\n"
                  + "|  |\n"
                  + "|  |\n"
                  + "----"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for R command with partially out rectangle.
   */
  @Test
  public void testRCommandWithPartiallyOutRectangle() {
    File tmpCommandFile = createTmpCommandFile(
        "testRCommandWithPartiallyOutRectangle",
        "C 2 3",
        "R 2 2 3 3 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.containsString(
              "----\n"
                  + "|  |\n"
                  + "| x|\n"
                  + "| x|\n"
                  + "----"));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for unknown command
   */
  @Test
  public void testUnknownCommand() {
    File tmpCommandFile = createTmpCommandFile("testUnknownCommand",
        "        123456 ");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.allOf(CoreMatchers.containsString("Unknown command:"),
              CoreMatchers.containsString("Please use command H for help.")));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  /**
   * Test for Q command
   */
  @Test
  public void testExit() {
    File tmpCommandFile = createTmpCommandFile("testExit",
        "Q");
    try {
      DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
      assertThat(os.toString("UTF8"),
          CoreMatchers.allOf(CoreMatchers.containsString("Q"),
              CoreMatchers.not(
                  CoreMatchers.containsString(
                      Loc.getLocMessage("unknown-command")))));
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }

  private File createTmpCommandFile(final String prefix,
                                    final String... commands) {
    try {
      File tmpCommandFile = File.createTempFile(prefix, "temp");
      try (BufferedWriter bw =
               new BufferedWriter(new FileWriter(tmpCommandFile))) {
        for (String command : commands) {
          bw.write(command);
          bw.write("\n");
        }
        bw.flush();
      }
      tmpCommandFile.deleteOnExit();
      return tmpCommandFile;
    } catch (Exception e) {
      // fail
      throw new RuntimeException(e);
    }
  }
}
