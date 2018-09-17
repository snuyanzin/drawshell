package com.springer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Canvas to draw with on it.
 */
public class Canvas {
  /**
   * Default symbol to draw horizontal boundary.
   */
  private static final String DEFAULT_HORIZONTAL_BOUNDARY = "-";
  /**
   * Default symbol to draw vertical boundary.
   */
  private static final String DEFAULT_VERTICAL_BOUNDARY = "|";
  /**
   * Default symbol to draw lines and rectangles.
   */
  private static final char DEFAULT_LINE_CHAR = 'x';
  /**
   * Canvas content.
   */
  private final char[] canvasBoard;
  /**
   * Width of canvas.
   */
  private final int width;
  /**
   * Height of canvas.
   */
  private final int height;

  /**
   * Canvas constructor.
   * @param w       width of canvas.
   * @param h       height of canvas.
   */
  public Canvas(final int w, final int h) {
    this.width = w;
    this.height = h;
    this.canvasBoard = new char[w * h];
  }

  /**
   * Add horizontal boundary of the canvas.
   * @param result  horizontal boundary of the canvas
   */
  private void appendHorizontalBoundary(final StringBuilder result) {
    for (int i = 0; i < width + 2; i++) {
      result.append(DEFAULT_HORIZONTAL_BOUNDARY);
    }
  }


  /**
   * Draw horizontal or vertical (based on points coordinates analysis) line
   * on the canvas from the point (x, y1) to (x, y2) with symbol c.
   * If any point of the line is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   * @param x1      x coordinate of the vertical line
   * @param x2      x coordinate of the vertical line
   * @param y1      y coordinate of the first point
   * @param y2      y coordinate  of the second point
   * @return        canvas with a new line.
   */
  public Canvas putLine(final int x1, final int y1, final int x2, final int y2, final char c) {
    if (x1 == x2) {
      putVerticalLine(x1, y1, y2, c);
    } else if (y1 == y2) {
      putHorizontalLine(x1, x2, y1, c);
    } else {
      throw new UnsupportedOperationException(
          "Currently only horizontal and vertical are supported");
    }
    return this;
  }

  /**
   * Draw horizontal or vertical (based on points coordinates analysis) line
   * on the canvas from the point (x, y1) to (x, y2) with
   * symbol {@link Canvas#DEFAULT_LINE_CHAR}.
   * If any point of the line is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   * @param x1      x coordinate of the vertical line
   * @param x2      x coordinate of the vertical line
   * @param y1      y coordinate of the first point
   * @param y2      y coordinate  of the second point
   * @return        canvas with a new line.
   */
  public Canvas putLine(final int x1, final int y1, final int x2, final int y2) {
    return putLine(x1, y1, x2, y2, DEFAULT_LINE_CHAR);
  }

  /**
   * Draw vertical line on the canvas from
   * the point (x, y1) to (x, y2) with symbol {@link Canvas#DEFAULT_LINE_CHAR}.
   * If any point of the line is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   * @param x       x coordinate of the vertical line
   * @param y1      y coordinate of the first point
   * @param y2      y coordinate  of the second point
   * @return        canvas with a new line.
   */
  public Canvas putVerticalLine(final int x, final int y1, final int y2) {
    return putVerticalLine(x, y1, y2, DEFAULT_LINE_CHAR);
  }

  /**
   * Draw vertical line on the canvas
   * from the point (x, y1) to (x, y2) with symbol ch.
   * If any point of the line is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   * @param x       x coordinate of the vertical line
   * @param y1      y coordinate of the first point
   * @param y2      y coordinate  of the second point
   * @param ch      symbol to use while line drawing
   * @return        canvas with a new line.
   */
  public Canvas putVerticalLine(final int x,
                                final int y1,
                                final int y2,
                                final char ch) {
    if (x < 1 || x > width
        || Math.min(y1, y2) > height || Math.max(y1, y2) < 1) {
      return this;
    }
    for (int i = Math.max(0, Math.min(y1, y2) - 1);
        i < Math.min(height, Math.max(y1, y2));
        i++) {
      canvasBoard[width * i + x - 1] = ch;
    }
    return this;
  }

  /**
   * Draw horizontal line on the canvas from
   * the point (x1, y) to (x2, y) with symbol {@link Canvas#DEFAULT_LINE_CHAR}.
   * If any point of the line is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   * @param x1      x coordinate of the first point
   * @param x2      x coordinate of the second point
   * @param y       coordinate of the horizontal line
   * @return        canvas with a new line.
   */
  public Canvas putHorizontalLine(final int x1, final int x2, final int y) {
    return putHorizontalLine(x1, x2, y, DEFAULT_LINE_CHAR);
  }

  /**
   * Draw horizontal line on the canvas
   * from the point (x1, y) to (x2, y) with symbol ch.
   * If any point of the line is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   * @param x1      x coordinate of the first point
   * @param x2      x coordinate of the second point
   * @param y       coordinate of the horizontal line
   * @param ch      symbol to use while line drawing
   * @return        canvas with a new line.
   */
  public Canvas putHorizontalLine(final int x1,
                                  final int x2,
                                  final int y,
                                  final char ch) {
    if (Math.max(x1, x2) < 1
        || Math.min(x1, x2) > width || y > height || y < 1) {
      return this;
    }
    for (int i = Math.max(0, Math.min(x1, x2) - 1);
         i < Math.min(width, Math.max(x1, x2));
         i++) {
      canvasBoard[(y - 1) * width + i] = ch;
    }
    return this;
  }

  /**
   * Draw rectangle on the canvas
   * with the top left point (x1, y1) and bottom right point (x2, y2)
   * with symbol {@link Canvas#DEFAULT_LINE_CHAR}.
   * If any point of the rectangle is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   * @param x1      x coordinate of the first point
   * @param x2      x coordinate of the second point
   * @param y1      y coordinate of the first point
   * @param y2      y coordinate of the second point
   * @return        canvas with a new rectangle.
   */
  public Canvas putRectangle(final int x1,
                             final int y1,
                             final int x2,
                             final int y2) {
    return putHorizontalLine(x1, x2, y1)
      .putHorizontalLine(x1, x2, y2)
      .putVerticalLine(x1, y1, y2)
      .putVerticalLine(x2, y1, y2);
  }

  /**
   * Fill the point (x, y) and all its neighbours with
   * the same char content with a new char c.
   * If point (x, y) is out of canvas this point will
   * not fill anything.
   * @param x       x coordinate of the start point to fill
   * @param y       y coordinate of the start point to fill
   * @param c       a new char to fill
   * @return        canvas with a new rectangle.
   */
  public Canvas fill(final int x, final int y, final char c) {
    if (x < 1 || y < 1 || x > width || y > height) {
      System.out.println("out of the region");
      return this;
    }
    char currentChar = canvasBoard[(y - 1) * width + x - 1];
    if (currentChar == c) {
      return this;
    }
    updateColorNeighbours(
        Collections.singleton((y - 1) * width + x - 1), currentChar, c);
    return this;
  }

  /**
   * Update color (char content) of all specified points and its neighbours
   * and so forth while neighbours have the same initial color.
   * @param pointsToUpdate  initial collection of points to update
   * @param initialColor    color that should be changed
   * @param newColor        new color
   */
  private void updateColorNeighbours(final Collection<Integer> pointsToUpdate,
                                     final char initialColor,
                                     final char newColor) {
    if (pointsToUpdate.isEmpty() || initialColor == newColor) {
      return;
    }
    // each time the max number of candidates will increase 4 times
    Set<Integer> candidates = new HashSet<>(pointsToUpdate.size() * 4);
    for (int i : pointsToUpdate) {
      canvasBoard[i] = newColor;
      if (i + width < canvasBoard.length
          && canvasBoard[i + width] == initialColor) {
        candidates.add(i + width);
      }
      if (i - width >= 0 && canvasBoard[i - width] == initialColor) {
        candidates.add(i - width);
      }
      if (i > 0 && i / width == (i - 1) / width
          && canvasBoard[i - 1] == initialColor) {
        candidates.add(i - 1);
      }
      if (i / width == (i + 1) / width && canvasBoard[i + 1] == initialColor) {
        candidates.add(i + 1);
      }
    }
    updateColorNeighbours(candidates, initialColor, newColor);
  }

  /**
   * Draw current canvas state.
   *
   * @return canvas string representation
   */
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder((width + 2) * (height + 2));
    appendHorizontalBoundary(result);
    if (width + 2 > 0) {
      result.append("\n");
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width + 2; j++) {
        if (j == 0 || j == width + 1) {
          result.append(DEFAULT_VERTICAL_BOUNDARY);
        } else {
          int type = Character.getType(canvasBoard[width * i + j - 1]);
          if (type == Character.CONTROL) {
            result.append(' ');
          } else {
            result.append(canvasBoard[width * i + j - 1]);
          }
        }
      }
      result.append("\n");
    }
    appendHorizontalBoundary(result);
    return result.toString();
  }
}
