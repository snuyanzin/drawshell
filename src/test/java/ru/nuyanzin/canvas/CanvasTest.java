package ru.nuyanzin.canvas;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ru.nuyanzin.DrawingShellOpts;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for direct drawing on {@link Canvas}.
 * No validation is checked here, only drawing.
 */
public class CanvasTest {

  private static final DrawingShellOpts OPTS = new DrawingShellOpts(null);

  @Test
  public void testToString() {
    // 0 is not allowed from the shell but here there is no validation
    assertEquals("--\n--\n", new Canvas(0, 0, OPTS).toString());
    assertEquals("---\n---\n", new Canvas(1, 0, OPTS).toString());
    assertEquals("--\n||\n--\n", new Canvas(0, 1, OPTS).toString());
    assertEquals("---\n| |\n---\n", new Canvas(1, 1, OPTS).toString());
    assertEquals("----\n|  |\n|  |\n----\n",
        new Canvas(2, 2, OPTS).toString());
  }

  @Test
  public void testDrawLineOutCanvas() {
    Canvas cnvs = new Canvas(3, 3, OPTS);
    // Before drawing
    assertEquals("-----\n"
        + "|   |\n"
        + "|   |\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
    cnvs.drawLine(Integer.MIN_VALUE,
        Integer.MIN_VALUE,
        Integer.MIN_VALUE,
        Integer.MAX_VALUE)
        .drawLine(0, 0, 0, 0)
        .drawLine(0, 0, 4, 0)
        .drawLine(-15, -5, 5, -5);
    // After drawing should be the same as everything drawn is out of the canvas
    assertEquals("-----\n"
        + "|   |\n"
        + "|   |\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
  }

  @Test
  public void testEquivalentsLines() {
    // It does not matter from which to
    // which point the line is going to be drawn

    // Out of canvas
    assertEquals(
        new Canvas(2, 2, OPTS).drawLine(20, 30, -100, 30).toString(),
        new Canvas(2, 2, OPTS).drawLine(-100, 30, 20, 30).toString());

    // On canvas
    assertEquals(
        new Canvas(5, 5, OPTS).drawRectangle(1, 2, 1, 5).toString(),
        new Canvas(5, 5, OPTS).drawRectangle(1, 5, 1, 2).toString());
    assertEquals(
        new Canvas(5, 5, OPTS)
            .drawRectangle(Integer.MIN_VALUE, 4, 5, 4).toString(),
        new Canvas(5, 5, OPTS)
            .drawRectangle(5, 4, Integer.MIN_VALUE, 4).toString());
  }

  @Test
  public void testLineAsAPoint() {
    // A line with the same two points should be a point
    Canvas cnvs = new Canvas(3, 3, OPTS);
    cnvs.drawLine(2, 2, 2, 2);
    assertEquals("-----\n"
        + "|   |\n"
        + "| x |\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
    cnvs.drawLine(1, 1, 1, 1);
    assertEquals("-----\n"
        + "|x  |\n"
        + "| x |\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
    cnvs.drawLine(1, 3, 1, 3);
    assertEquals("-----\n"
        + "|x  |\n"
        + "| x |\n"
        + "|x  |\n"
        + "-----\n", cnvs.toString());
    cnvs.drawLine(3, 3, 3, 3);
    assertEquals("-----\n"
        + "|x  |\n"
        + "| x |\n"
        + "|x x|\n"
        + "-----\n", cnvs.toString());
    cnvs.drawLine(3, 1, 3, 1);
    assertEquals("-----\n"
        + "|x x|\n"
        + "| x |\n"
        + "|x x|\n"
        + "-----\n", cnvs.toString());
  }

  @Test
  public void testDrawLine() {
    Canvas cnvs = new Canvas(4, 4, OPTS);
    cnvs.drawLine(1, 1, 2, 1);
    assertEquals("------\n"
        + "|xx  |\n"
        + "|    |\n"
        + "|    |\n"
        + "|    |\n"
        + "------\n", cnvs.toString());
    cnvs.drawLine(3, 2, 3, 4);
    assertEquals("------\n"
        + "|xx  |\n"
        + "|  x |\n"
        + "|  x |\n"
        + "|  x |\n"
        + "------\n", cnvs.toString());
    cnvs.drawLine(1, 3, 4, 3, '&');
    assertEquals("------\n"
        + "|xx  |\n"
        + "|  x |\n"
        + "|&&&&|\n"
        + "|  x |\n"
        + "------\n", cnvs.toString());
    // nothing should be changed after the same line
    cnvs.drawLine(1, 3, 4, 3, '&');
    assertEquals("------\n"
        + "|xx  |\n"
        + "|  x |\n"
        + "|&&&&|\n"
        + "|  x |\n"
        + "------\n", cnvs.toString());
  }

  @Test
  public void testDrawRectangleOutCanvas() {
    Canvas cnvs = new Canvas(3, 3, OPTS);
    // Before drawing
    assertEquals("-----\n"
        + "|   |\n"
        + "|   |\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
    cnvs.drawRectangle(
        Integer.MIN_VALUE,
        Integer.MIN_VALUE,
        Integer.MAX_VALUE,
        Integer.MAX_VALUE)
        .drawRectangle(0, 0, 0, 0)
        .drawRectangle(0, 0, 4, 4)
        .drawRectangle(-5, -5, 5, 5);
    // After drawing
    assertEquals("-----\n"
        + "|   |\n"
        + "|   |\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
  }

  @Test
  public void testEquivalentsRectangles() {
    // It does not matter from which
    // to which point the rectangle is going to be drawn

    // Out of canvas
    assertEquals(
        new Canvas(2, 2, OPTS).drawRectangle(2, 23, -12, -90).toString(),
        new Canvas(2, 2, OPTS).drawRectangle(-12, -90, 2, 23).toString());
    assertEquals(
        new Canvas(2, 2, OPTS).drawRectangle(2, -90, -12, 23).toString(),
        new Canvas(2, 2, OPTS).drawRectangle(-12, -90, 2, 23).toString());
    assertEquals(
        new Canvas(2, 2, OPTS).drawRectangle(-12, 23, 2, -90).toString(),
        new Canvas(2, 2, OPTS).drawRectangle(-12, -90, 2, 23).toString());

    // On canvas
    assertEquals(
        new Canvas(5, 5, OPTS).drawRectangle(1, 2, 4, 5).toString(),
        new Canvas(5, 5, OPTS).drawRectangle(4, 5, 1, 2).toString());
    assertEquals(
        new Canvas(5, 5, OPTS).drawRectangle(4, 2, 1, 5).toString(),
        new Canvas(5, 5, OPTS).drawRectangle(4, 5, 1, 2).toString());
    assertEquals(
        new Canvas(5, 5, OPTS).drawRectangle(1, 5, 4, 2).toString(),
        new Canvas(5, 5, OPTS).drawRectangle(4, 5, 1, 2).toString());
  }

  @Test
  public void testRectangleAsAPoint() {
    // rectangle with the same two opposite corners should be a point
    Canvas cnvs = new Canvas(3, 3, OPTS);
    cnvs.drawRectangle(2, 2, 2, 2);
    assertEquals("-----\n"
        + "|   |\n"
        + "| x |\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
    cnvs.drawRectangle(1, 1, 1, 1);
    assertEquals("-----\n"
        + "|x  |\n"
        + "| x |\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
    cnvs.drawRectangle(1, 3, 1, 3);
    assertEquals("-----\n"
        + "|x  |\n"
        + "| x |\n"
        + "|x  |\n"
        + "-----\n", cnvs.toString());
    cnvs.drawRectangle(3, 3, 3, 3);
    assertEquals("-----\n"
        + "|x  |\n"
        + "| x |\n"
        + "|x x|\n"
        + "-----\n", cnvs.toString());
    cnvs.drawRectangle(3, 1, 3, 1);
    assertEquals("-----\n"
        + "|x x|\n"
        + "| x |\n"
        + "|x x|\n"
        + "-----\n", cnvs.toString());
  }

  @Test
  public void testDrawRectangleAsALine() {
    Canvas cnvs = new Canvas(5, 5, OPTS);
    cnvs.drawRectangle(1, 1, 2, 1);
    assertEquals("-------\n"
        + "|xx   |\n"
        + "|     |\n"
        + "|     |\n"
        + "|     |\n"
        + "|     |\n"
        + "-------\n", cnvs.toString());
    cnvs.drawRectangle(3, 2, 5, 2);
    assertEquals("-------\n"
        + "|xx   |\n"
        + "|  xxx|\n"
        + "|     |\n"
        + "|     |\n"
        + "|     |\n"
        + "-------\n", cnvs.toString());
    cnvs.drawRectangle(2, 3, 2, 5);
    assertEquals("-------\n"
        + "|xx   |\n"
        + "|  xxx|\n"
        + "| x   |\n"
        + "| x   |\n"
        + "| x   |\n"
        + "-------\n", cnvs.toString());
  }

  @Test
  public void testDrawRectangles() {
    Canvas cnvs = new Canvas(5, 5, OPTS);
    cnvs.drawRectangle(Integer.MIN_VALUE, Integer.MIN_VALUE, 2, 2);
    assertEquals("-------\n"
        + "| x   |\n"
        + "|xx   |\n"
        + "|     |\n"
        + "|     |\n"
        + "|     |\n"
        + "-------\n", cnvs.toString());
    // If the points where rectangles should be drawn with color 'x'
    // are already have this color then nothing should be changed
    cnvs.drawRectangle(1, 3, 3, 5);
    assertEquals("-------\n"
        + "| x   |\n"
        + "|xx   |\n"
        + "|xxx  |\n"
        + "|x x  |\n"
        + "|xxx  |\n"
        + "-------\n", cnvs.toString());
    cnvs.drawRectangle(3, 0, 5, 2);
    assertEquals("-------\n"
        + "| xx x|\n"
        + "|xxxxx|\n"
        + "|xxx  |\n"
        + "|x x  |\n"
        + "|xxx  |\n"
        + "-------\n", cnvs.toString());
    // nothing should be changed after the same rectangle drawn
    cnvs.drawRectangle(3, 0, 5, 2);
    assertEquals("-------\n"
        + "| xx x|\n"
        + "|xxxxx|\n"
        + "|xxx  |\n"
        + "|x x  |\n"
        + "|xxx  |\n"
        + "-------\n", cnvs.toString());
  }

  @Test
  public void testFill() {
    Canvas cnvs = new Canvas(1, 1, OPTS);
    // Fill one element canvas
    cnvs.fill(1, 1, '>');
    assertEquals("---\n|>|\n---\n", cnvs.toString());

    cnvs = new Canvas(3, 3, OPTS);
    // non-keyboard symbol check
    cnvs.fill(1, 2, 'π');
    assertEquals("-----\n"
        + "|πππ|\n"
        + "|πππ|\n"
        + "|πππ|\n"
        + "-----\n", cnvs.toString());

    cnvs = new Canvas(3, 3, OPTS);
    // fill a hole of one element
    cnvs.drawRectangle(1, -6, 3, 2);
    assertEquals("-----\n"
        + "|x x|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());
    cnvs.fill(2, 1, '*');
    assertEquals("-----\n"
        + "|x*x|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----\n", cnvs.toString());

    // fill with a boundary symbols
    cnvs.fill(1, 3, '-');
    assertEquals("-----\n"
        + "|x*x|\n"
        + "|xxx|\n"
        + "|---|\n"
        + "-----\n", cnvs.toString());
    cnvs.fill(3, 1, '|');
    assertEquals("-----\n"
        + "||*||\n"
        + "|||||\n"
        + "|---|\n"
        + "-----\n", cnvs.toString());

    // fill boundary symbols with non-boundary
    cnvs.fill(3, 1, '#');
    assertEquals("-----\n"
        + "|#*#|\n"
        + "|###|\n"
        + "|---|\n"
        + "-----\n", cnvs.toString());
    cnvs.fill(3, 3, 'ß');
    assertEquals("-----\n"
        + "|#*#|\n"
        + "|###|\n"
        + "|ßßß|\n"
        + "-----\n", cnvs.toString());
  }

  @Test
  public void testFillWithExistingColor() {
    Canvas cnvs = new Canvas(3, 3, OPTS);
    cnvs.drawLine(2, 1, 2, 3);
    assertEquals("-----\n"
        + "| x |\n"
        + "| x |\n"
        + "| x |\n"
        + "-----\n", cnvs.toString());
    cnvs.fill(1, 1, '!');
    assertEquals("-----\n"
        + "|!x |\n"
        + "|!x |\n"
        + "|!x |\n"
        + "-----\n", cnvs.toString());
    cnvs.fill(3, 1, '!');
    assertEquals("-----\n"
        + "|!x!|\n"
        + "|!x!|\n"
        + "|!x!|\n"
        + "-----\n", cnvs.toString());
  }

  /**
   * Test creates simple structure with diagonal connections and then
   * it applies B8 (8-dots way) filling to verify
   * if the canvas filled correctly.
   */
  @Test
  public void testB8Fill() {
    Canvas cnvs = new Canvas(6, 7, OPTS);
    cnvs.drawLine(1, 2, 1, 3);
    cnvs.drawLine(1, 5, 1, 6);
    cnvs.drawLine(6, 2, 6, 3);
    cnvs.drawLine(6, 5, 6, 6);
    cnvs.drawLine(2, 1, 5, 1);
    cnvs.drawLine(2, 4, 5, 4);
    cnvs.drawLine(2, 7, 5, 7);
    cnvs.drawLine(2, 2, 2, 2, '*');
    cnvs.drawLine(5, 6, 5, 6, '.');
    assertEquals("--------\n"
        + "| xxxx |\n"
        + "|x*   x|\n"
        + "|x    x|\n"
        + "| xxxx |\n"
        + "|x    x|\n"
        + "|x   .x|\n"
        + "| xxxx |\n"
        + "--------\n", cnvs.toString());
    cnvs.fill(1, 1, '.', false);
    assertEquals("--------\n"
        + "|.xxxx |\n"
        + "|x*   x|\n"
        + "|x    x|\n"
        + "| xxxx |\n"
        + "|x    x|\n"
        + "|x   .x|\n"
        + "| xxxx |\n"
        + "--------\n", cnvs.toString());
    cnvs.fill(3, 2, ';', false);
    assertEquals("--------\n"
        + "|.xxxx;|\n"
        + "|x*;;;x|\n"
        + "|x;;;;x|\n"
        + "|;xxxx;|\n"
        + "|x;;;;x|\n"
        + "|x;;;.x|\n"
        + "|;xxxx |\n"
        + "--------\n", cnvs.toString());
    cnvs.fill(2, 1, '@', false);
    assertEquals("--------\n"
        + "|.@@@@;|\n"
        + "|@*;;;@|\n"
        + "|@;;;;@|\n"
        + "|;@@@@;|\n"
        + "|@;;;;@|\n"
        + "|@;;;.@|\n"
        + "|;@@@@ |\n"
        + "--------\n", cnvs.toString());
  }

  /**
   * The tests creates a maze and check if fill works correctly
   * while calling fill (4-dots approach) at each point.
   * (except 3 points with color '!', '#', '&amp;' which are required
   * to be sure that the test work ok in case of more than 2 colors)
   */
  @Test
  public void testFillB4Maze() {
    int mazeWidth = 20;
    int mazeHeight = 20;
    Canvas cnvs = new Canvas(mazeWidth, mazeHeight, OPTS);
    cnvs.drawLine(2, 2, 2, 10);
    cnvs.drawLine(2, 2, 9, 2);
    cnvs.drawLine(9, 2, 9, 8);
    cnvs.drawLine(9, 8, 4, 8);
    cnvs.drawLine(4, 8, 4, 4);
    cnvs.drawLine(7, 4, 4, 4);
    cnvs.drawLine(7, 4, 7, 6);
    cnvs.drawLine(6, 6, 7, 6);

    cnvs.drawLine(2, 10, 19, 10);
    cnvs.drawLine(12, 2, 19, 2);
    cnvs.drawLine(19, 2, 19, 10);
    cnvs.drawLine(17, 8, 12, 8);
    cnvs.drawLine(12, 2, 12, 8);
    cnvs.drawLine(17, 8, 17, 4);
    cnvs.drawLine(17, 4, 14, 4);
    cnvs.drawLine(14, 4, 14, 6);
    cnvs.drawLine(14, 6, 15, 6);
    cnvs.drawLine(15, 6, 15, 6, '#');

    cnvs.drawLine(2, 19, 2, 10);
    cnvs.drawLine(2, 19, 9, 19);
    cnvs.drawLine(9, 19, 9, 12);
    cnvs.drawLine(9, 12, 4, 12);
    cnvs.drawLine(4, 12, 4, 17);
    cnvs.drawLine(7, 17, 4, 17);
    cnvs.drawLine(7, 14, 7, 17);
    cnvs.drawLine(6, 14, 7, 14);
    cnvs.drawLine(6, 15, 7, 15);
    cnvs.drawLine(6, 14, 6, 14, '&');

    cnvs.drawLine(12, 19, 19, 19);
    cnvs.drawLine(19, 19, 19, 10);
    cnvs.drawLine(17, 12, 12, 12);
    cnvs.drawLine(12, 19, 12, 12);
    cnvs.drawLine(17, 12, 17, 17);
    cnvs.drawLine(17, 17, 14, 17);
    cnvs.drawLine(14, 17, 14, 14);
    cnvs.drawLine(14, 14, 15, 14);
    cnvs.drawLine(14, 15, 15, 15);
    cnvs.drawLine(15, 14, 15, 14, '!');

    final String initialMaze = "----------------------\n"
        + "|                    |\n"
        + "| xxxxxxxx  xxxxxxxx |\n"
        + "| x      x  x      x |\n"
        + "| x xxxx x  x xxxx x |\n"
        + "| x x  x x  x x  x x |\n"
        + "| x x xx x  x x# x x |\n"
        + "| x x    x  x    x x |\n"
        + "| x xxxxxx  xxxxxx x |\n"
        + "| x                x |\n"
        + "| xxxxxxxxxxxxxxxxxx |\n"
        + "| x                x |\n"
        + "| x xxxxxx  xxxxxx x |\n"
        + "| x x    x  x    x x |\n"
        + "| x x &x x  x x! x x |\n"
        + "| x x xx x  x xx x x |\n"
        + "| x x  x x  x x  x x |\n"
        + "| x xxxx x  x xxxx x |\n"
        + "| x      x  x      x |\n"
        + "| xxxxxxxx  xxxxxxxx |\n"
        + "|                    |\n"
        + "----------------------\n";
    final String x2PiExpectedResult = "----------------------\n"
        + "|                    |\n"
        + "| ππππππππ  ππππππππ |\n"
        + "| π      π  π      π |\n"
        + "| π ππππ π  π ππππ π |\n"
        + "| π π  π π  π π  π π |\n"
        + "| π π ππ π  π π# π π |\n"
        + "| π π    π  π    π π |\n"
        + "| π ππππππ  ππππππ π |\n"
        + "| π                π |\n"
        + "| ππππππππππππππππππ |\n"
        + "| π                π |\n"
        + "| π ππππππ  ππππππ π |\n"
        + "| π π    π  π    π π |\n"
        + "| π π &π π  π π! π π |\n"
        + "| π π ππ π  π ππ π π |\n"
        + "| π π  π π  π π  π π |\n"
        + "| π ππππ π  π ππππ π |\n"
        + "| π      π  π      π |\n"
        + "| ππππππππ  ππππππππ |\n"
        + "|                    |\n"
        + "----------------------\n";
    final String space2QuoteExpectedResult = "----------------------\n"
        + "|''''''''''''''''''''|\n"
        + "|'xxxxxxxx''xxxxxxxx'|\n"
        + "|'x''''''x''x''''''x'|\n"
        + "|'x'xxxx'x''x'xxxx'x'|\n"
        + "|'x'x''x'x''x'x''x'x'|\n"
        + "|'x'x'xx'x''x'x#'x'x'|\n"
        + "|'x'x''''x''x''''x'x'|\n"
        + "|'x'xxxxxx''xxxxxx'x'|\n"
        + "|'x''''''''''''''''x'|\n"
        + "|'xxxxxxxxxxxxxxxxxx'|\n"
        + "|'x''''''''''''''''x'|\n"
        + "|'x'xxxxxx''xxxxxx'x'|\n"
        + "|'x'x''''x''x''''x'x'|\n"
        + "|'x'x'&x'x''x'x!'x'x'|\n"
        + "|'x'x'xx'x''x'xx'x'x'|\n"
        + "|'x'x''x'x''x'x''x'x'|\n"
        + "|'x'xxxx'x''x'xxxx'x'|\n"
        + "|'x''''''x''x''''''x'|\n"
        + "|'xxxxxxxx''xxxxxxxx'|\n"
        + "|''''''''''''''''''''|\n"
        + "----------------------\n";
    Map<Character, BucketFill> colorFillingMap = new HashMap<>();
    colorFillingMap.put('x',
        new BucketFill('π', x2PiExpectedResult));
    colorFillingMap.put(' ',
        new BucketFill('\'', space2QuoteExpectedResult));
    iterativeCheckFill(cnvs, initialMaze, colorFillingMap, true);
  }

  /**
   * The tests creates a maze and check if fill works correctly
   * while calling fill (8-dots approach) at each point.
   * (except 3 points with color '!', '#', '&amp;' which are required
   * to be sure that the test work ok in case of more than 2 colors)
   */
  @Test
  public void testB8FillMaze() {
    int mazeWidth = 20;
    int mazeHeight = 20;
    Canvas cnvs = new Canvas(mazeWidth, mazeHeight, OPTS);
    cnvs.drawLine(1, 1, 1, 1);
    cnvs.drawLine(20, 1, 20, 1);
    cnvs.drawLine(1, 20, 1, 20);
    cnvs.drawLine(20, 20, 20, 20);
    cnvs.drawLine(10, 1, 11, 1);
    cnvs.drawLine(10, 9, 11, 9);
    cnvs.drawLine(10, 11, 11, 11);
    cnvs.drawLine(10, 20, 11, 20);

    cnvs.drawLine(2, 2, 2, 10);
    cnvs.drawLine(2, 2, 9, 2);
    cnvs.drawLine(9, 2, 9, 8);
    cnvs.drawLine(9, 8, 4, 8);
    cnvs.drawLine(4, 8, 4, 4);
    cnvs.drawLine(7, 4, 4, 4);
    cnvs.drawLine(7, 4, 7, 6);
    cnvs.drawLine(6, 6, 7, 6);
    cnvs.drawLine(5, 5, 5, 5);

    cnvs.drawLine(2, 10, 19, 10);
    cnvs.drawLine(12, 2, 19, 2);
    cnvs.drawLine(19, 2, 19, 10);
    cnvs.drawLine(17, 8, 12, 8);
    cnvs.drawLine(12, 2, 12, 8);
    cnvs.drawLine(17, 8, 17, 4);
    cnvs.drawLine(17, 4, 14, 4);
    cnvs.drawLine(14, 4, 14, 6);
    cnvs.drawLine(14, 6, 15, 6);
    cnvs.drawLine(16, 5, 16, 5);
    cnvs.drawLine(15, 6, 15, 6, '#');

    cnvs.drawLine(2, 19, 2, 10);
    cnvs.drawLine(2, 19, 9, 19);
    cnvs.drawLine(9, 19, 9, 12);
    cnvs.drawLine(9, 12, 4, 12);
    cnvs.drawLine(4, 12, 4, 17);
    cnvs.drawLine(7, 17, 4, 17);
    cnvs.drawLine(7, 14, 7, 17);
    cnvs.drawLine(6, 14, 7, 14);
    cnvs.drawLine(6, 15, 7, 15);
    cnvs.drawLine(5, 16, 5, 16);
    cnvs.drawLine(6, 14, 6, 14, '&');

    cnvs.drawLine(12, 19, 19, 19);
    cnvs.drawLine(19, 19, 19, 10);
    cnvs.drawLine(17, 12, 12, 12);
    cnvs.drawLine(12, 19, 12, 12);
    cnvs.drawLine(17, 12, 17, 17);
    cnvs.drawLine(17, 17, 14, 17);
    cnvs.drawLine(14, 17, 14, 14);
    cnvs.drawLine(14, 14, 15, 14);
    cnvs.drawLine(14, 15, 15, 15);
    cnvs.drawLine(16, 16, 16, 16);
    cnvs.drawLine(15, 14, 15, 14, '!');

    final String initialMaze = "----------------------\n"
        + "|x        xx        x|\n"
        + "| xxxxxxxx  xxxxxxxx |\n"
        + "| x      x  x      x |\n"
        + "| x xxxx x  x xxxx x |\n"
        + "| x xx x x  x x xx x |\n"
        + "| x x xx x  x x# x x |\n"
        + "| x x    x  x    x x |\n"
        + "| x xxxxxx  xxxxxx x |\n"
        + "| x       xx       x |\n"
        + "| xxxxxxxxxxxxxxxxxx |\n"
        + "| x       xx       x |\n"
        + "| x xxxxxx  xxxxxx x |\n"
        + "| x x    x  x    x x |\n"
        + "| x x &x x  x x! x x |\n"
        + "| x x xx x  x xx x x |\n"
        + "| x xx x x  x x xx x |\n"
        + "| x xxxx x  x xxxx x |\n"
        + "| x      x  x      x |\n"
        + "| xxxxxxxx  xxxxxxxx |\n"
        + "|x        xx        x|\n"
        + "----------------------\n";

    final String x2PiExpectedResult = "----------------------\n"
        + "|π        ππ        π|\n"
        + "| ππππππππ  ππππππππ |\n"
        + "| π      π  π      π |\n"
        + "| π ππππ π  π ππππ π |\n"
        + "| π ππ π π  π π ππ π |\n"
        + "| π π ππ π  π π# π π |\n"
        + "| π π    π  π    π π |\n"
        + "| π ππππππ  ππππππ π |\n"
        + "| π       ππ       π |\n"
        + "| ππππππππππππππππππ |\n"
        + "| π       ππ       π |\n"
        + "| π ππππππ  ππππππ π |\n"
        + "| π π    π  π    π π |\n"
        + "| π π &π π  π π! π π |\n"
        + "| π π ππ π  π ππ π π |\n"
        + "| π ππ π π  π π ππ π |\n"
        + "| π ππππ π  π ππππ π |\n"
        + "| π      π  π      π |\n"
        + "| ππππππππ  ππππππππ |\n"
        + "|π        ππ        π|\n"
        + "----------------------\n";

    final String space2QuoteExpectedResult = "----------------------\n"
        + "|x''''''''xx''''''''x|\n"
        + "|'xxxxxxxx''xxxxxxxx'|\n"
        + "|'x''''''x''x''''''x'|\n"
        + "|'x'xxxx'x''x'xxxx'x'|\n"
        + "|'x'xx'x'x''x'x'xx'x'|\n"
        + "|'x'x'xx'x''x'x#'x'x'|\n"
        + "|'x'x''''x''x''''x'x'|\n"
        + "|'x'xxxxxx''xxxxxx'x'|\n"
        + "|'x'''''''xx'''''''x'|\n"
        + "|'xxxxxxxxxxxxxxxxxx'|\n"
        + "|'x'''''''xx'''''''x'|\n"
        + "|'x'xxxxxx''xxxxxx'x'|\n"
        + "|'x'x''''x''x''''x'x'|\n"
        + "|'x'x'&x'x''x'x!'x'x'|\n"
        + "|'x'x'xx'x''x'xx'x'x'|\n"
        + "|'x'xx'x'x''x'x'xx'x'|\n"
        + "|'x'xxxx'x''x'xxxx'x'|\n"
        + "|'x''''''x''x''''''x'|\n"
        + "|'xxxxxxxx''xxxxxxxx'|\n"
        + "|x''''''''xx''''''''x|\n"
        + "----------------------\n";

    Map<Character, BucketFill> colorFillingMap = new HashMap<>();
    colorFillingMap.put('x',
        new BucketFill('π', x2PiExpectedResult));
    colorFillingMap.put(' ',
        new BucketFill('\'', space2QuoteExpectedResult));
    iterativeCheckFill(cnvs, initialMaze, colorFillingMap, false);
  }

  /**
   * The tests creates a chessboard and check if fill works correctly
   * while calling fill (8-dots approach) at each point.
   */
  @Test
  public void testFillB8ChessBoard() {
    int chessBoardWidth = 8;
    int chessBoardHeight = 8;
    Canvas cnvs = new Canvas(chessBoardWidth, chessBoardHeight, OPTS);
    cnvs.drawLine(1, 1, 1, 1);
    cnvs.drawLine(3, 1, 3, 1);
    cnvs.drawLine(5, 1, 5, 1);
    cnvs.drawLine(7, 1, 7, 1);

    cnvs.drawLine(2, 2, 2, 2);
    cnvs.drawLine(4, 2, 4, 2);
    cnvs.drawLine(6, 2, 6, 2);
    cnvs.drawLine(8, 2, 8, 2);

    cnvs.drawLine(1, 3, 1, 3);
    cnvs.drawLine(3, 3, 3, 3);
    cnvs.drawLine(5, 3, 5, 3);
    cnvs.drawLine(7, 3, 7, 3);

    cnvs.drawLine(2, 4, 2, 4);
    cnvs.drawLine(4, 4, 4, 4);
    cnvs.drawLine(6, 4, 6, 4);
    cnvs.drawLine(8, 4, 8, 4);

    cnvs.drawLine(1, 5, 1, 5);
    cnvs.drawLine(3, 5, 3, 5);
    cnvs.drawLine(5, 5, 5, 5);
    cnvs.drawLine(7, 5, 7, 5);

    cnvs.drawLine(2, 6, 2, 6);
    cnvs.drawLine(4, 6, 4, 6);
    cnvs.drawLine(6, 6, 6, 6);
    cnvs.drawLine(8, 6, 8, 6);

    cnvs.drawLine(1, 7, 1, 7);
    cnvs.drawLine(3, 7, 3, 7);
    cnvs.drawLine(5, 7, 5, 7);
    cnvs.drawLine(7, 7, 7, 7);

    cnvs.drawLine(2, 8, 2, 8);
    cnvs.drawLine(4, 8, 4, 8);
    cnvs.drawLine(6, 8, 6, 8);
    cnvs.drawLine(8, 8, 8, 8);

    final String initialChessBoard = "----------\n"
        + "|x x x x |\n"
        + "| x x x x|\n"
        + "|x x x x |\n"
        + "| x x x x|\n"
        + "|x x x x |\n"
        + "| x x x x|\n"
        + "|x x x x |\n"
        + "| x x x x|\n"
        + "----------\n";
    assertEquals(initialChessBoard, cnvs.toString());

    for (int i = 1; i <= chessBoardHeight; i++) {
      for (int j = 1; j <= chessBoardWidth; j++) {
        if ((i + j) % 2 == 0) {
          cnvs.fill(i, j, '*', false);
          assertEquals("----------\n"
              + "|* * * * |\n"
              + "| * * * *|\n"
              + "|* * * * |\n"
              + "| * * * *|\n"
              + "|* * * * |\n"
              + "| * * * *|\n"
              + "|* * * * |\n"
              + "| * * * *|\n"
              + "----------\n", cnvs.toString());
          cnvs.fill(i, j, 'x', false);
        } else {
          cnvs.fill(i, j, '.', false);
          assertEquals("----------\n"
              + "|x.x.x.x.|\n"
              + "|.x.x.x.x|\n"
              + "|x.x.x.x.|\n"
              + "|.x.x.x.x|\n"
              + "|x.x.x.x.|\n"
              + "|.x.x.x.x|\n"
              + "|x.x.x.x.|\n"
              + "|.x.x.x.x|\n"
              + "----------\n", cnvs.toString());
          cnvs.fill(i, j, ' ', false);
        }
        assertEquals(initialChessBoard, cnvs.toString());
      }
    }
  }

  /**
   * The tests creates a board and check if fill works correctly
   * while calling fill (4-dots approach) at each non-blank point.
   */
  @Test
  public void testFillB4Board() {
    Canvas cnvs = new Canvas(8, 8, OPTS);
    cnvs.drawLine(1, 1, 1, 1);
    cnvs.drawLine(3, 1, 3, 1);
    cnvs.drawLine(5, 1, 5, 1);
    cnvs.drawLine(7, 1, 7, 1);

    cnvs.drawLine(1, 2, 8, 2);

    cnvs.drawLine(1, 3, 1, 3);
    cnvs.drawLine(3, 3, 3, 3);
    cnvs.drawLine(5, 3, 5, 3);
    cnvs.drawLine(7, 3, 7, 3);

    cnvs.drawLine(1, 4, 8, 4);

    cnvs.drawLine(1, 5, 1, 5);
    cnvs.drawLine(3, 5, 3, 5);
    cnvs.drawLine(5, 5, 5, 5);
    cnvs.drawLine(7, 5, 7, 5);

    cnvs.drawLine(1, 6, 8, 6);

    cnvs.drawLine(1, 7, 1, 7);
    cnvs.drawLine(3, 7, 3, 7);
    cnvs.drawLine(5, 7, 5, 7);
    cnvs.drawLine(7, 7, 7, 7);

    cnvs.drawLine(1, 8, 8, 8);

    String initialBoard = "----------\n"
        + "|x x x x |\n"
        + "|xxxxxxxx|\n"
        + "|x x x x |\n"
        + "|xxxxxxxx|\n"
        + "|x x x x |\n"
        + "|xxxxxxxx|\n"
        + "|x x x x |\n"
        + "|xxxxxxxx|\n"
        + "----------\n";

    Map<Character, BucketFill> colorFillingMap = new HashMap<>();
    colorFillingMap.put('x', new BucketFill('*', "----------\n"
        + "|* * * * |\n"
        + "|********|\n"
        + "|* * * * |\n"
        + "|********|\n"
        + "|* * * * |\n"
        + "|********|\n"
        + "|* * * * |\n"
        + "|********|\n"
        + "----------\n"));
    iterativeCheckFill(cnvs, initialBoard, colorFillingMap, true);
  }

  private void iterativeCheckFill(Canvas canvas,
                                  String expectedInitialState,
                                  Map<Character, BucketFill> color2BFillMap,
                                  boolean isB4) {
    assertEquals(expectedInitialState, canvas.toString());
    for (int i = 1; i <= canvas.getHeight(); i++) {
      for (int j = 1; j <= canvas.getWidth(); j++) {
        Map.Entry<Character, Layer> color2Layer =
            canvas.getLayerForPoint(i - 1, j - 1);
        char color = color2Layer.getKey();
        BucketFill bucketFill = color2BFillMap.get(color);
        if (bucketFill == null) {
          // Still not sure if this message helpful or not
          // Currently could not find a case where it is helpful
          // but may be in future
          //System.err.println("Color '" + color + "' at position ("
          //    + i + ", " + j + ") is skipped");
          continue;
        }
        final char targetColor = bucketFill.getTargetColor();
        canvas.fill(i, j, targetColor, isB4);
        final String message = "Check filling point (" + i + ", " + j
            + ") with color \"";
        assertEquals(bucketFill.getExpectedResult(), canvas.toString(),
            message + targetColor + "\"");
        canvas.fill(i, j, color, isB4);
        assertEquals(expectedInitialState, canvas.toString(),
            message + color + "'");
      }
    }
  }

  /**
   * BucketFill class for iterative tests of filling areas.
   */
  class BucketFill {
    private final char targetColor;
    private final String expectedResult;

    BucketFill(char targetColor,
               String expectedResult) {
      this.targetColor = targetColor;
      this.expectedResult = expectedResult;
    }

    public char getTargetColor() {
      return targetColor;
    }

    public String getExpectedResult() {
      return expectedResult;
    }
  }
}
