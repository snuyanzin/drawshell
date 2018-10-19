package ru.nuyanzin;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.NumberFormat;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.nuyanzin.canvas.Canvas;

import static org.junit.Assert.assertThat;

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


  @Before
  public void reassignOutput() throws UnsupportedEncodingException {
    os = new ByteArrayOutputStream();
    testOutputStream = new PrintStream(os, true, StandardCharsets.UTF_8.name());
    originalSystemIn = System.out;
    System.setOut(testOutputStream);
  }

  @After
  public void reassignOutputBack() throws IOException {
    System.setOut(originalSystemIn);
    testOutputStream.close();
    os.close();
  }

  /**
   * Test for non existing file
   */
  @Test
  public void testWrongFileCommand() throws IOException {
    String wrongFileAbsolutePath =
        Paths.get("WrongFile").toAbsolutePath().toString();
    DrawingShell.main(new String[]{wrongFileAbsolutePath});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            Loc.getLocMessage("file-not-exist", wrongFileAbsolutePath)));
  }

  /**
   * Test for wrong start up arguments e.g. 2 files
   */
  @Test
  public void testWrongStartupArguments() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testWrongStartupArguments",
        "");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath(),
        tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("usage-start")));
  }

  /**
   * Test for long command
   */
  @Test
  public void testLongCommand() throws IOException {
    String longCommand = "lslfjslfjhslfjshljhfslkjhslkjfhsljfslkjfhslkjfs"
        + "asdkfjasdfskdhfgksdtfgskhfsgkjahgfskjfhsgkjhskjdtskjhf"
        + "sdflsjhdflskjhflskjafhlskjafhslkjdfhslkjdfhlskdfhskhjskdf";
    File tmpCommandFile = createTmpCommandFile("testLongCommand", longCommand);
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(DrawingShell.getCutString(longCommand)));
  }

  /**
   * Test for C command
   */
  @Test
  public void testCCommand() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testCCommand",
        "             C           2          3             ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString("----\n"
            + "|  |\n"
            + "|  |\n"
            + "|  |\n"
            + "----"));
  }

  /**
   * Test for C command with wrong arguments
   */
  @Test
  public void testCCommandWrongArguments() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testCCommandWrongArguments",
        "             C           -2          A             ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            Loc.getLocMessage("usage-c", Canvas.CANVAS_DIMENSION_LIMIT)));
  }

  /**
   * Test for C command with wrong number of arguments
   */
  @Test
  public void testCCommandWrongNumberOfArguments() throws IOException {
    File tmpCommandFile = createTmpCommandFile(
        "testCCommandWrongNumberOfArguments",
        "             C 1 1          12 20 ds2          A      213 213       ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            Loc.getLocMessage("usage-c", Canvas.CANVAS_DIMENSION_LIMIT)));
  }

  /**
   * Test for B command for the point out of the canvas
   */
  @Test
  public void testBCommandForPointOutOfCanvas() throws IOException {
    File tmpCommandFile = createTmpCommandFile(
        "testBCommandForPointOutOfCanvas",
        "             C           2          3       ",
        "    B           12          23       q ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("usage-b", "B", 2, 3)));
  }

  /**
   * Test for B command without specifying `c`
   */
  @Test
  public void testBCommandWithoutCharacter() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testBCommandWithoutCharacter",
        "             C           2          3       ",
        "    B           1          2      ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("usage-b", "B", 2, 3)));
  }

  /**
   * Test for B command with a wrong character
   */
  @Test
  public void testBCommandWithWrongCharacter() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testBCommandWithWrongCharacter",
        "             C           2          3       ",
        "    B4           1          2     qwe ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("usage-b", "B4", 2, 3)));
  }

  /**
   * Test for B command without having Canvas created
   */
  @Test
  public void testBCommandWithoutCanvas() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testBCommandWithoutCanvas",
        "             B           2          3      @     ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("canvas-required")));
  }

  /**
   * Test for B command called several times.
   */
  @Test
  public void testTwiceBCommand() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testTwiceBCommand",
        "C 50 2 ",
        "             B           1          1      -     ",
        "             B           2          2      @     ",
        "             B           5          1      @     ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            "----------------------------------------------------\n"
                + "|@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@|\n"
                + "|@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@|\n"
                + "----------------------------------------------------"));
  }

  /**
   * Test for B command called several times.
   */
  @Test
  public void testFillOtherPartWithExistingSymbol() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testTwiceBCommand",
        "C 50 2 ",
        "             L           20          1      20  2   ",
        "             B           2          2      @     ",
        "             B           25          1      @     ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            "----------------------------------------------------\n"
                + "|@@@@@@@@@@@@@@@@@@@x@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@|\n"
                + "|@@@@@@@@@@@@@@@@@@@x@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@|\n"
                + "----------------------------------------------------"));
  }

  /**
   * Test for B8 command.
   */
  @Test
  public void testB8Command() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testTwiceBCommand",
        "C 9 2 ",
        "             L           1          1      1   1  ",
        "             L           2          2      2   2  ",
        "             L           3          1      5   1  ",
        "             L           6          2      9   2  ",
        "             B8           1          2      @     ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            "-----------\n"
                + "|x@xxx@@@@|\n"
                + "|@x@@@xxxx|\n"
                + "-----------"));
  }

  /**
   * Test for B command to check if for canvas 2000x2000
   * it does not crash with OutOfMemory or StackOverFlow errors.
   * As the numbers are more than 1000 there is also current format is used.
   */
  @Test
  public void testBCommandForMillionsPoints() throws IOException {
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    String formatted2000 = numberFormat.format(2000);
    File tmpCommandFile = createTmpCommandFile("testBCommandForMillionsPoints",
        "C " + formatted2000 + " " + formatted2000,
        "             L           2     2 2    2          ",
        "             B           1          1      @     ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.not(CoreMatchers.containsString("Error")));
  }

  /**
   * Test that for really big sizes of canvas OOM happens.
   * As the numbers are more than 1000 there is also current format is used.
   */
  @Test
  public void testOOMHappen() throws IOException {
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    String formattedMaxValue = numberFormat.format(Integer.MAX_VALUE);
    File tmpCommandFile = createTmpCommandFile("testOOMHappen",
        "C " + formattedMaxValue + " " + formattedMaxValue);
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString("OutOfMemoryError"));
  }

  /**
   * Test that after OOM happened the application could continue working
   */
  @Test
  public void testPossibilityToWorkAfterOOM() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testPossibilityToWorkAfterOOM",
        "C " + Integer.MAX_VALUE + " " + Integer.MAX_VALUE,
        "             C           3          3      ",
        "            B           2          2   :   ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString("-----\n"
            + "|:::|\n"
            + "|:::|\n"
            + "|:::|\n"
            + "-----"));
  }

  /**
   * Test for H command
   */
  @Test
  public void testHCommand() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testHCommand",
        "             H ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("help")));
  }

  /**
   * Test for L command without having Canvas created
   */
  @Test
  public void testLCommandWithoutCanvas() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testLCommandWithoutCanvas",
        "             L           -2          3      1    -2 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("canvas-required")));
  }

  /**
   * Test for L command with wrong number of arguments
   */
  @Test
  public void testLCommandWithWrongNumberOfArguments() throws IOException {
    File tmpCommandFile = createTmpCommandFile(
        "testLCommandWithWrongNumberOfArguments",
        "             C           2          3       ",
        "    L           1          2      ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("usage-l")));
  }

  /**
   * Test for L command with wrong number of arguments
   */
  @Test
  public void testLCommandWrongTypeOfArguments() throws IOException {
    File tmpCommandFile = createTmpCommandFile(
        "testLCommandWrongTypeOfArguments",
        "             C           2          3       ",
        "    L           A          2      A  2 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("usage-l")));
  }

  /**
   * Test for L command for non horizontal and non vertical line
   */
  @Test
  public void testLCommandForNonHorizontalNonVertical() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testLCommand",
        "             C           3          3       ",
        "    L           -1          223      3  231 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            Loc.getLocMessage("draw-line-not-supported")));
  }

  /**
   * Test for L command with wrong number of arguments
   */
  @Test
  public void testLCommand() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testLCommand",
        "             C           3          3       ",
        "    L           -1          2      3  2 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            "-----\n"
                + "|   |\n"
                + "|xxx|\n"
                + "|   |\n"
                + "-----"));
  }

  /**
   * Test for R command without having Canvas created
   */
  @Test
  public void testRCommandWithoutCanvas() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testRCommandWithoutCanvas",
        "             R           -2          3      1    -2 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("canvas-required")));
  }


  /**
   * Test for R command with wrong number of arguments
   */
  @Test
  public void testRCommandWithWrongNumberOfArguments() throws IOException {
    File tmpCommandFile = createTmpCommandFile(
        "testRCommandWithWrongNumberOfArguments",
        "             C           2          3       ",
        "    R           1          2      ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("usage-r")));
  }

  /**
   * Test for R command with wrong number of arguments
   */
  @Test
  public void testRCommandWrongTypeOfArguments() throws IOException {
    File tmpCommandFile = createTmpCommandFile(
        "testRCommandWrongTypeOfArguments",
        "             C           2          3       ",
        "    R           A          2      A  2 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(Loc.getLocMessage("usage-r")));
  }

  /**
   * Test for R command with wrong number of arguments
   */
  @Test
  public void testRCommand() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testRCommand",
        "             C           2          3       ",
        "    R           1          1      1  1 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            "----\n"
                + "|x |\n"
                + "|  |\n"
                + "|  |\n"
                + "----"));
  }

  /**
   * Test for R command with partially out rectangle.
   */
  @Test
  public void testRCommandWithPartiallyOutRectangle() throws IOException {
    File tmpCommandFile = createTmpCommandFile(
        "testRCommandWithPartiallyOutRectangle",
        "             C           2          3       ",
        "    R           2          2      3  3 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.containsString(
            "----\n"
                + "|  |\n"
                + "| x|\n"
                + "| x|\n"
                + "----"));
  }

  /**
   * Test for unknown command
   */
  @Test
  public void testUnknownCommand() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testUnknownCommand",
        "        123456 ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.allOf(CoreMatchers.containsString("Unknown command:"),
            CoreMatchers.containsString("Please use command H for help.")));
  }

  /**
   * Test for Q command
   */
  @Test
  public void testExit() throws IOException {
    File tmpCommandFile = createTmpCommandFile("testExit",
        "             Q             ");
    DrawingShell.main(new String[]{tmpCommandFile.getAbsolutePath()});
    assertThat(os.toString("UTF8"),
        CoreMatchers.allOf(CoreMatchers.containsString("Q"),
            CoreMatchers.not(
                CoreMatchers.containsString(
                    Loc.getLocMessage("unknown-command")))));
  }

  private File createTmpCommandFile(final String prefix,
                                    final String... commands)
      throws IOException {
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
  }
}
