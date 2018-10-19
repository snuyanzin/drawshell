package ru.nuyanzin.canvas;

import java.util.BitSet;

/**
 * Layer is associated only with one color.
 * Keep only colored by associated color points.
 * Association is done on {@link Canvas} level.
 */
public class Layer {
  private final BitSet[] points;
  private final int width;

  public Layer(int width, int height) {
    points = new BitSet[height];
    this.width = width;
  }

  public Layer(BitSet[] points, int width) {
    this.points = points;
    this.width = width;
  }

  /**
   * Draw vertical line on the current layer
   * with coordinate {@code x}
   * and between {@code y1} and {@code y2}
   *
   * @param x  coordinate x
   * @param y1 first coordinate y
   * @param y2 second coordinate y
   */
  protected void drawVerticalLine(final int x,
                                  final int y1,
                                  final int y2) {
    for (int i = y1; i <= y2; i++) {
      lazyInitBitSet(i);
      points[i].set(x);
    }
  }

  /**
   * Clear vertical line on the current layer
   * with coordinate {@code x}
   * and between {@code y1} and {@code y2}
   *
   * @param x  coordinate x
   * @param y1 first coordinate y
   * @param y2 second coordinate y
   */
  protected void clearVerticalLine(final int x,
                                   final int y1,
                                   final int y2) {
    for (int i = y1; i <= y2; i++) {
      if (points[i] == null) {
        continue;
      }
      points[i].set(x, false);
      if (points[i].isEmpty()) {
        points[i] = null;
      }
    }
  }

  /**
   * Draw horizontal line on the current layer
   * with coordinate {@code x}
   * and between {@code y1} and {@code y2}
   *
   * @param x1 first coordinate x
   * @param x2 second coordinate x
   * @param y  coordinate y
   */
  protected void drawHorizontalLine(final int x1,
                                    final int x2,
                                    final int y) {
    lazyInitBitSet(y);
    points[y].set(x1, x2 + 1);
  }

  /**
   * Clear horizontal line on the current layer
   * with coordinate {@code x}
   * and between {@code y1} and {@code y2}
   *
   * @param x1 first coordinate x
   * @param x2 second coordinate x
   * @param y  coordinate y
   */
  protected void clearHorizontalLine(final int x1,
                                     final int x2,
                                     final int y) {
    if (points[y] == null) {
      return;
    }
    if ((x1 == 0 || x2 == 0) && (x1 == width - 1 || x2 == width - 1)) {
      points[y] = null;
    } else {
      points[y].set(x1, x2 + 1, false);
      if (points[y].isEmpty()) {
        points[y] = null;
      }
    }
  }

  /**
   * Fill area on the current layer starting with point {@code startX, startY}.
   * Merge result to {@code destination}.
   *
   * @param startX      start x coordinate
   * @param startY      start y coordinate
   * @param destination layer to merge result
   * @param isB4        4-dots or 8-dots approach to use
   * @return destination with merged result.
   */
  public Layer fill(final int startX,
                    final int startY,
                    final Layer destination,
                    final boolean isB4) {
    if (points[startY] == null || !points[startY].get(startX)) {
      return this;
    }

    int downBoundary = getDownBoundary(startY);
    int upBoundary = getUpBoundary(startY);
    BitSet[] currentFill = new BitSet[points.length];
    for (int i = downBoundary; i <= upBoundary; i++) {
      if (currentFill[i] == null) {
        currentFill[i] = new BitSet(width);
      }
    }
    int y = startY;

    currentFill[startY].set(startX);
    do {
      for (int i = y; i >= downBoundary; i--) {
        goLeftOrRight(i, currentFill, isB4);
      }
      for (int i = y + 1; i <= upBoundary; i++) {
        goLeftOrRight(i, currentFill, isB4);
      }
      y = getLineToHandleAgain(currentFill, downBoundary, upBoundary, isB4);
    } while (y <= upBoundary && y >= downBoundary);

    for (int i = 0; i < points.length; i++) {
      if (currentFill[i] != null && !currentFill[i].isEmpty()) {
        if (destination.points[i] == null) {
          destination.points[i] = currentFill[i];
        } else {
          destination.points[i].or(currentFill[i]);
        }
      }
    }
    return destination;
  }

  /**
   * Returns up boundary for processing i.e. the nearest up index
   * of points where the whole line is null or empty.
   *
   * @param startY start y point
   * @return up boundary
   */
  protected int getUpBoundary(int startY) {
    int upBoundary = startY;
    if (points[upBoundary] == null) {
      return startY;
    }
    while (points[upBoundary] != null
        && (points[upBoundary].get(0)
        || points[upBoundary].nextSetBit(0) != -1)
        && upBoundary < points.length - 1) {
      upBoundary++;
    }
    return points[upBoundary] == null ? upBoundary - 1 : upBoundary;
  }

  /**
   * Returns down boundary for processing i.e. the nearest down index
   * of points where the whole line is null or empty.
   *
   * @param startY start y point
   * @return down boundary
   */
  protected int getDownBoundary(int startY) {
    int downBoundary = startY;
    if (points[downBoundary] == null) {
      return startY;
    }
    while (points[downBoundary] != null
        && (points[downBoundary].get(0)
        || points[downBoundary].nextSetBit(0) != -1)
        && downBoundary > 0) {
      downBoundary--;
    }
    return points[downBoundary] == null ? downBoundary + 1 : downBoundary;
  }

  /**
   * Process specified line of source and fill points of the line if possible.
   * Also mark points to fill at {@code destination}
   * in case of they are from other lines
   *
   * @param y           line to process
   * @param destination destination of filling
   * @param isB4        B4 or B8 method of filling to use
   */
  public void goLeftOrRight(final int y,
                            final BitSet[] destination,
                            final boolean isB4) {
    if (points[y] == null || points[y].isEmpty()) {
      return;
    }
    // k >= 0 required for the case of command B 2,147,483,647 1 .
    for (int k = 0; k < width && k >= 0; k++) {
      if (!destination[y].get(k) || points[y] == null) {
        continue;
      }

      int prevIndex = getPrevIndex(points[y], k);
      int nextIndex = getNextIndex(points[y], k);

      if (prevIndex == -1 || nextIndex == -1) {
        continue;
      }

      // Handling of upper neighbours
      if (y > 0 && points[y - 1] != null && !points[y - 1].isEmpty()) {
        if (destination[y - 1] == null) {
          destination[y - 1] = new BitSet(width);
        }
        if (!isB4 && k > 0) {
          handleNeighbours(
              destination,
              getPrevIndex(points[y - 1], prevIndex),
              getNextIndex(points[y - 1], prevIndex),
              y - 1);
        }
        handleNeighbours(destination, prevIndex, nextIndex, y - 1);
        if (!isB4 && k < width - 1) {
          handleNeighbours(
              destination,
              getPrevIndex(points[y - 1], nextIndex),
              getNextIndex(points[y - 1], nextIndex),
              y - 1);
        }
      }

      // Handling of down neighbours
      if (y < destination.length - 1
          && points[y + 1] != null
          && !points[y + 1].isEmpty()) {
        if (destination[y + 1] == null) {
          destination[y + 1] = new BitSet(width);
        }
        if (!isB4 && k > 0) {
          handleNeighbours(
              destination,
              getPrevIndex(points[y + 1], prevIndex),
              getNextIndex(points[y + 1], prevIndex),
              y + 1);
        }
        handleNeighbours(destination, prevIndex, nextIndex, y + 1);
        if (!isB4 && k < width - 1) {
          handleNeighbours(
              destination,
              getPrevIndex(points[y + 1], nextIndex),
              getNextIndex(points[y + 1], nextIndex),
              y + 1);
        }
      }
      destination[y].set(prevIndex, nextIndex + 1);
      if (prevIndex == 0 && nextIndex + 1 == width
          || prevIndex > 0 && points[y].previousSetBit(prevIndex - 1) == -1
          && points[y].nextSetBit(nextIndex + 1) == -1) {
        points[y] = null;
      } else {
        points[y].set(prevIndex, nextIndex + 1, false);
      }

      k = nextIndex + 1;
    }
  }

  private void handleNeighbours(BitSet[] destination,
                                int prevIndex,
                                int nextIndex,
                                int lineNumber) {
    if (prevIndex == -1) {
      return;
    }
    for (int i = points[lineNumber].nextSetBit(prevIndex);
         i <= nextIndex;
         i++) {
      if (i == -1) {
        break;
      }
      if (!points[lineNumber].get(i)) {
        continue;
      }
      int start = getFirstSetIndexInARow(points[lineNumber], i);
      int end = -1;
      if (start >= 0) {
        end = getLastSetIndexInARow(points[lineNumber], i);
        destination[lineNumber].set(start, end + 1);
      }
      i = end > i + 1 ? end : i;
    }
  }

  /**
   * Return next index in a row from {@code fromPoint}.
   *
   * @param lineOfPoints line of points where to search for next index
   * @param fromPoint    starting point
   * @return next index in a row
   */
  protected int getNextIndex(BitSet lineOfPoints, int fromPoint) {
    int nextIndex;
    if (fromPoint < width - 1 && lineOfPoints.get(fromPoint + 1)) {
      nextIndex = getLastSetIndexInARow(lineOfPoints, fromPoint + 1);
      nextIndex = nextIndex == -1 ? fromPoint : nextIndex;
    } else if (fromPoint > 0 && lineOfPoints.get(fromPoint - 1)) {
      nextIndex = getLastSetIndexInARow(lineOfPoints, fromPoint - 1);
    } else {
      nextIndex = getLastSetIndexInARow(lineOfPoints, fromPoint);
    }
    return nextIndex;
  }

  /**
   * Return previous index in a row from {@code fromPoint}.
   *
   * @param lineOfPoints line of points where to search for prev index
   * @param fromPoint    starting point
   * @return previous index in a row
   */
  protected int getPrevIndex(BitSet lineOfPoints, int fromPoint) {
    int prevIndex;
    if (fromPoint > 0 && lineOfPoints.get(fromPoint - 1)) {
      prevIndex = getFirstSetIndexInARow(lineOfPoints, fromPoint - 1);
      prevIndex = prevIndex == -1 ? fromPoint : prevIndex;
    } else if (fromPoint < width - 1 && lineOfPoints.get(fromPoint + 1)) {
      prevIndex = getFirstSetIndexInARow(lineOfPoints, fromPoint + 1);
    } else {
      prevIndex = getFirstSetIndexInARow(lineOfPoints, fromPoint);
    }
    return prevIndex;
  }

  protected int getFirstSetIndexInARow(BitSet lineOfPoints, int index) {
    if (lineOfPoints == null) {
      return -1;
    }
    int prevSetBit = lineOfPoints.previousSetBit(index);
    if (prevSetBit == -1) {
      return -1;
    }
    int prevClearBit = lineOfPoints.previousClearBit(index);
    if (prevSetBit < prevClearBit) {
      return -1;
    }
    return prevClearBit + 1;
  }

  protected int getLastSetIndexInARow(BitSet lineOfPoints, int index) {
    if (lineOfPoints == null || lineOfPoints.isEmpty()) {
      return -1;
    }
    int nextSetBit = lineOfPoints.nextSetBit(index);
    if (nextSetBit == -1) {
      return -1;
    }
    int nextClearBit = lineOfPoints.nextClearBit(index);
    if (nextSetBit > nextClearBit) {
      return -1;
    }
    return nextClearBit - 1;
  }

  /**
   * Check if there is a line which contains not process points
   * and return its number otherwise return -1.
   *
   * @param destination  array of destination to check
   * @param downBoundary down boundary to check
   * @param upBoundary   up boundary to check
   * @return index of source line if there is a point
   * to process or -1 otherwise.
   */
  protected int getLineToHandleAgain(BitSet[] destination,
                                     int downBoundary,
                                     int upBoundary,
                                     boolean isB4) {
    for (int j = downBoundary; j <= upBoundary; j++) {
      if (destination[j] == null || destination[j].isEmpty()) {
        continue;
      }
      if (points[j] != null && destination[j].intersects(points[j])) {
        return j;
      }
      if (j < upBoundary) {
        if (points[j + 1] != null
            && destination[j].intersects(points[j + 1])) {
          return j + 1;
        } else if (destination[j + 1] != null
            && points[j] != null
            && destination[j + 1].intersects(points[j])) {
          return j;
        }
      }
      if (!isB4) {
        BitSet leftShiftedDestination =
            destination[j].get(1, destination[j].length());
        if (j < upBoundary && points[j + 1] != null
            && leftShiftedDestination.intersects(points[j + 1])) {
          return j + 1;
        }
        if (j > downBoundary && points[j - 1] != null
            && leftShiftedDestination.intersects(points[j - 1])) {
          return j - 1;
        }
      }
    }
    return -1;
  }

  /**
   * Initialization of points[index] on request.
   *
   * @param index index of {@code points} to init
   */
  protected void lazyInitBitSet(int index) {
    if (points[index] == null) {
      points[index] = new BitSet(width);
    }
  }

  protected BitSet getLine(int index) {
    return points[index];
  }
}
