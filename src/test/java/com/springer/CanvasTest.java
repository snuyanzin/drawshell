package com.springer;

import junit.framework.TestCase;

public class CanvasTest extends TestCase {

  public void testToString() {
    assertEquals("--\n--", new Canvas(0, 0).toString());
    assertEquals("---\n---", new Canvas(1, 0).toString());
    assertEquals("--\n||\n--", new Canvas(0, 1).toString());
    assertEquals("---\n| |\n---", new Canvas(1, 1).toString());
    assertEquals("----\n----", new Canvas(2, 0).toString());
    assertEquals("--\n||\n||\n--", new Canvas(0, 2).toString());
    assertEquals("--\n--", new Canvas(0, -1).toString());
    assertEquals("-\n-", new Canvas(-1, 0).toString());
    assertEquals("-\n-", new Canvas(-1, -1).toString());
    assertEquals("-\n-", new Canvas(-1, -2).toString());
    assertEquals("", new Canvas(-2, -2).toString());
    assertEquals("----\n|  |\n|  |\n----", new Canvas(2, 2).toString());
  }

  public void testDrawLine() {
    Canvas cnvs = new Canvas(0,  0);
    cnvs.drawLine(0, 0, 0, 0);
    assertEquals("--\n--", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.drawLine(0, 0, 0, 0);
    assertEquals("---\n| |\n---", cnvs.toString());

    // Line with the same end and start should be a point
    cnvs = new Canvas(1,  1);
    cnvs.drawLine(1, 1, 1, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    // The line outside of the canvas should not be drawn
    cnvs = new Canvas(1,  1);
    cnvs.drawLine(-1, 10, 10, 10);
    assertEquals("---\n| |\n---", cnvs.toString());

    // If a line starts or ends outside of the canvas but goes through
    // some points of it then these points should be drawn
    cnvs = new Canvas(1,  1);
    cnvs.drawLine(10, 1, -1, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    // It does not matter if the is going to be drawn
    // from the right to left or from the left to right
    // the same approach in case of from the up to down or from the down to up
    assertEquals(
        new Canvas(5, 5).drawLine(-30, 40, 4, 40).toString(),
        new Canvas(5, 5).drawLine(4, 40, -30, 40).toString());
    assertEquals(
        new Canvas(5, 5).drawLine(-5, -10, -5, 40).toString(),
        new Canvas(5, 5).drawLine(-5, 40, -5, -10).toString());

    cnvs = new Canvas(3,  3);
    cnvs.drawLine(2, 2, 10, 2);
    assertEquals("-----\n"
        + "|   |\n"
        + "| xx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.drawLine(2, 1, -10, 1, '*');
    assertEquals("-----\n"
        + "|** |\n"
        + "| xx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.drawLine(1, 2, 1, 3, '&');
    assertEquals("-----\n"
        + "|** |\n"
        + "|&xx|\n"
        + "|&  |\n"
        + "-----", cnvs.toString());
    cnvs.drawLine(1, 3, 3, 3);
    assertEquals("-----\n"
        + "|** |\n"
        + "|&xx|\n"
        + "|xxx|\n"
        + "-----", cnvs.toString());
    cnvs.drawLine(3, 3, 3, -5, '%');
    assertEquals("-----\n"
        + "|**%|\n"
        + "|&x%|\n"
        + "|xx%|\n"
        + "-----", cnvs.toString());
  }

  public void testDrawRectangle() {
    Canvas cnvs = new Canvas(0,  0);
    cnvs.drawRectangle(0, 0, 0, 0);
    assertEquals("--\n--", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.drawRectangle(0, 0, 0, 0);
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.drawRectangle(1, 1, 1, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.drawRectangle(-1, 10, 10, -1);
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.drawRectangle(1, 10, 10, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    assertEquals(
        new Canvas(5, 5).drawRectangle(2, 23, -12, -90).toString(),
        new Canvas(5, 5).drawRectangle(-12, -90, 2, 23).toString());

    cnvs = new Canvas(3,  3);
    cnvs.drawRectangle(1, -6, 3, 2);
    assertEquals("-----\n"
        + "|x x|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.drawRectangle(2, 2, 2, 2);
    assertEquals("-----\n"
        + "|x x|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.drawRectangle(2, 1, 2, 1);
    assertEquals("-----\n"
        + "|xxx|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.drawRectangle(1, 3, 2, 3);
    assertEquals("-----\n"
        + "|xxx|\n"
        + "|xxx|\n"
        + "|xx |\n"
        + "-----", cnvs.toString());
  }

  public void testFill() {
    Canvas cnvs = new Canvas(0,  0);
    cnvs.fill(0, 0, '@');
    assertEquals("--\n--", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.fill(0, 0, ';');
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.fill(1, 1, '>');
    assertEquals("---\n|>|\n---", cnvs.toString());

    cnvs = new Canvas(3,  3);
    cnvs.fill(1, 2, '!');
    assertEquals("-----\n"
        + "|!!!|\n"
        + "|!!!|\n"
        + "|!!!|\n"
        + "-----", cnvs.toString());

    cnvs = new Canvas(3,  3);
    cnvs.drawRectangle(1, -6, 3, 2);
    assertEquals("-----\n"
        + "|x x|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.fill(2, 1, '*');
    assertEquals("-----\n"
        + "|x*x|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());

    cnvs.fill(1, 3, '-');
    assertEquals("-----\n"
        + "|x*x|\n"
        + "|xxx|\n"
        + "|---|\n"
        + "-----", cnvs.toString());
    cnvs.fill(3, 1, '|');
    assertEquals("-----\n"
        + "||*||\n"
        + "|||||\n"
        + "|---|\n"
        + "-----", cnvs.toString());
    cnvs.fill(3, 1, '#');
    assertEquals("-----\n"
        + "|#*#|\n"
        + "|###|\n"
        + "|---|\n"
        + "-----", cnvs.toString());
    cnvs.fill(3, 3, '^');
    assertEquals("-----\n"
        + "|#*#|\n"
        + "|###|\n"
        + "|^^^|\n"
        + "-----", cnvs.toString());
  }

}
