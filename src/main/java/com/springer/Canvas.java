package com.springer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

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
  public Canvas drawLine(final int x1, final int y1, final int x2, final int y2, final char c) {
    // check if any point of the line should be on the canvas or not
    if (Math.min(x1, x2) > width || Math.max(x1, x2) < 1
            || Math.min(y1, y2) > height || Math.max(y1, y2) < 1) {
      return this;
    }
    int end, start;
    Function<Integer, Integer> function;
    if (x1 == x2) {
      start = Math.max(0, Math.min(y1, y2) - 1);
      end = Math.min(height, Math.max(y1, y2));
      function = i -> width * i + x1 - 1;
    } else if (y1 == y2) {
      start = Math.max(0, Math.min(x1, x2) - 1);
      end = Math.min(width, Math.max(x1, x2));
      function = i -> (y1 - 1) * width + i;
    } else {
      throw new UnsupportedOperationException(
          "Currently only horizontal and vertical lines are supported");
    }
    for (int i = start; i < end; i++) {
      canvasBoard[function.apply(i)] = c;
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
  public Canvas drawLine(final int x1, final int y1, final int x2, final int y2) {
    return drawLine(x1, y1, x2, y2, DEFAULT_LINE_CHAR);
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
  public Canvas drawRectangle(final int x1,
                              final int y1,
                              final int x2,
                              final int y2) {
    return drawLine(x1, y1, x2, y1)
      .drawLine(x1, y2, x2, y2)
      .drawLine(x1, y1, x1, y2)
      .drawLine(x2, y1, x2, y2);
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
   * and so forth while neighbours have the same
   * initial color (different from the target one).
   * @param pointsToUpdate  initial collection of points to update
   * @param initialColor    color that should be changed
   * @param targetColor        new color
   */
  private void updateColorNeighbours(final Collection<Integer> pointsToUpdate,
                                     final char initialColor,
                                     final char targetColor) {
    if (pointsToUpdate.isEmpty() || initialColor == targetColor) {
      return;
    }
    // each time the max number of candidates will increase 4 times
    // (3 potential candidates to change their color: 1 dedicated
    // and two shared with other points)
    Set<Integer> candidates = new HashSet<>(pointsToUpdate.size() * 4);
    for (int i : pointsToUpdate) {
      canvasBoard[i] = targetColor;
      // check lower point
      if (i + width < canvasBoard.length
          && canvasBoard[i + width] == initialColor) {
        candidates.add(i + width);
      }
      // check upper point
      if (i - width >= 0 && canvasBoard[i - width] == initialColor) {
        candidates.add(i - width);
      }
      // check left point
      if (i > 0 && i / width == (i - 1) / width
          && canvasBoard[i - 1] == initialColor) {
        candidates.add(i - 1);
      }
      // check right point
      if (i / width == (i + 1) / width && canvasBoard[i + 1] == initialColor) {
        candidates.add(i + 1);
      }
    }
    updateColorNeighbours(candidates, initialColor, targetColor);
  }

  /**
   * String representation of the current canvas state.
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
