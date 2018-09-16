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

  public void testPutHorizontalLine() {
    Canvas cnvs = new Canvas(0,  0);
    cnvs.putHorizontalLine(0, 0, 0);
    assertEquals("--\n--", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putHorizontalLine(0, 0, 0);
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putHorizontalLine(1, 1, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putHorizontalLine(-1, 10, 10);
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putHorizontalLine(10, -1, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    assertEquals(
        new Canvas(5, 5).putHorizontalLine(-30, 40, 4).toString(),
        new Canvas(5, 5).putHorizontalLine(40, -30, 4).toString());

    cnvs = new Canvas(3,  3);
    cnvs.putHorizontalLine(2, 10, 2);
    assertEquals("-----\n"
        + "|   |\n"
        + "| xx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.putHorizontalLine(2, -10, 1, '*');
    assertEquals("-----\n"
        + "|** |\n"
        + "| xx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.putHorizontalLine(1, 1, 2, '&');
    assertEquals("-----\n"
        + "|** |\n"
        + "|&xx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.putHorizontalLine(1, 3, 3);
    assertEquals("-----\n"
        + "|** |\n"
        + "|&xx|\n"
        + "|xxx|\n"
        + "-----", cnvs.toString());
  }

  public void testPutVerticalLine() {
    Canvas cnvs = new Canvas(0,  0);
    cnvs.putVerticalLine(0, 0, 0);
    assertEquals("--\n--", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putVerticalLine(0, 0, 0);
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putVerticalLine(1, 1, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putVerticalLine(-1, 10, 10);
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putVerticalLine(1, 10, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    assertEquals(
        new Canvas(5, 5).putVerticalLine(2, 23, -12).toString(),
        new Canvas(5, 5).putVerticalLine(2, -12, 23).toString());

    cnvs = new Canvas(3,  3);
    cnvs.putVerticalLine(2, 10, 2);
    assertEquals("-----\n"
        + "|   |\n"
        + "| x |\n"
        + "| x |\n"
        + "-----", cnvs.toString());
    cnvs.putVerticalLine(1, 1, 2, '*');
    assertEquals("-----\n"
        + "|*  |\n"
        + "|*x |\n"
        + "| x |\n"
        + "-----", cnvs.toString());
    cnvs.putVerticalLine(3, 1, 2, '&');
    assertEquals("-----\n"
        + "|* &|\n"
        + "|*x&|\n"
        + "| x |\n"
        + "-----", cnvs.toString());
    cnvs.putVerticalLine(1, 3, 3);
    assertEquals("-----\n"
        + "|* &|\n"
        + "|*x&|\n"
        + "|xx |\n"
        + "-----", cnvs.toString());
  }


  public void testPutRectangle() {
    Canvas cnvs = new Canvas(0,  0);
    cnvs.putRectangle(0, 0, 0, 0);
    assertEquals("--\n--", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putRectangle(0, 0, 0, 0);
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putRectangle(1, 1, 1, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putRectangle(-1, 10, 10, -1);
    assertEquals("---\n| |\n---", cnvs.toString());

    cnvs = new Canvas(1,  1);
    cnvs.putRectangle(1, 10, 10, 1);
    assertEquals("---\n|x|\n---", cnvs.toString());

    assertEquals(
        new Canvas(5, 5).putRectangle(2, 23, -12, -90).toString(),
        new Canvas(5, 5).putRectangle(-12, -90, 2, 23).toString());

    cnvs = new Canvas(3,  3);
    cnvs.putRectangle(1, -6, 3, 2);
    assertEquals("-----\n"
        + "|x x|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.putRectangle(2, 2, 2, 2);
    assertEquals("-----\n"
        + "|x x|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.putRectangle(2, 1, 2, 1);
    assertEquals("-----\n"
        + "|xxx|\n"
        + "|xxx|\n"
        + "|   |\n"
        + "-----", cnvs.toString());
    cnvs.putRectangle(1, 3, 2, 3);
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
    cnvs.putRectangle(1, -6, 3, 2);
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

    cnvs.fill(1, 3, '&');
    assertEquals("-----\n"
        + "|x*x|\n"
        + "|xxx|\n"
        + "|&&&|\n"
        + "-----", cnvs.toString());
    cnvs.fill(3, 1, '#');
    assertEquals("-----\n"
        + "|#*#|\n"
        + "|###|\n"
        + "|&&&|\n"
        + "-----", cnvs.toString());
  }

}
