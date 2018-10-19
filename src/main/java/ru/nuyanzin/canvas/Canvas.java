package ru.nuyanzin.canvas;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ru.nuyanzin.DrawingShellOpts;
import ru.nuyanzin.properties.DrawingShellPropertiesEnum;

/**
 * Canvas to draw with on it.
 */
public class Canvas {
  /**
   * Artificial limit for canvas size.
   */
  public static final int CANVAS_DIMENSION_LIMIT = Integer.MAX_VALUE;
  /**
   * The string length at which flushing to output stream should happen.
   * Current value is not a silver bullet
   * and could be tuned depending on cases
   */
  private static final int STRING_LENGTH_FOR_FLUSHING = 5000000;

  /**
   * Default symbol to draw lines and rectangles.
   */
  private static final char DEFAULT_LINE_CHAR = 'x';

  private final DrawingShellOpts drawingShellOpts;

  /**
   * Default symbol for initial background.
   */
  private static final char DEFAULT_EMPTY_CHAR = ' ';

  /**
   * Map of colors to layers. Each color exists on its own layer.
   */
  private Map<Character, Layer> colorToLayerMap;

  /**
   * Width of canvas.
   */
  private final int width;
  /**
   * Height of canvas.
   */
  private final int height;

  public Canvas(final int width, final int height, DrawingShellOpts opts) {
    this.height = height;
    this.width = width;
    colorToLayerMap = new HashMap<>();
    colorToLayerMap.put(DEFAULT_LINE_CHAR, new Layer(width, height));
    colorToLayerMap.put(DEFAULT_EMPTY_CHAR, new EmptyLayer(width, height));
    this.drawingShellOpts = opts;
  }

  /**
   * Draw horizontal or vertical (based on points coordinates analysis) line
   * on the canvas from the point (x1, y1) to (x2, y2) with symbol color.
   * If any point of the line is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   *
   * @param x1    first x coordinate of the vertical line
   * @param x2    second x coordinate of the vertical line
   * @param y1    first y coordinate of the first point
   * @param y2    second y coordinate  of the second point
   * @param color symbol which used as a color of the line to draw
   * @return canvas with a new line.
   */
  public Canvas drawLine(final int x1,
                         final int y1,
                         final int x2,
                         final int y2,
                         final char color) {
    Character characterColor = color;

    // check if any point of the line should be on the canvas or not
    if (Math.min(x1, x2) > width || Math.max(x1, x2) < 1
        || Math.min(y1, y2) > height || Math.max(y1, y2) < 1) {
      return this;
    }
    if (x1 != x2 && y1 != y2) {
      // Should not be reachable by calls via shell because of validation
      // at {@link GeneralCommands}.
      // As currently only horizontal or vertical lines
      // drawing is supported then do nothing.
      return this;
    }

    if (colorToLayerMap.get(characterColor) == null) {
      colorToLayerMap.put(characterColor, new Layer(width, height));
    }

    if (x1 == x2) {
      int start = getMinIntersectionCoordinate(y1, y2);
      int end = getMaxIntersectionCoordinate(y1, y2, height);
      for (Map.Entry<Character, Layer> lEntry : colorToLayerMap.entrySet()) {
        Layer layer = lEntry.getValue();
        if (Objects.equals(characterColor, lEntry.getKey())) {
          layer.drawVerticalLine(x1 - 1, start, end);
        } else {
          // clear the same coordinates on all other layers
          layer.clearVerticalLine(x1 - 1, start, end);
        }
      }
    } else {
      int start = getMinIntersectionCoordinate(x1, x2);
      int end = getMaxIntersectionCoordinate(x1, x2, width);
      for (Map.Entry<Character, Layer> lEntry : colorToLayerMap.entrySet()) {
        Layer layer = lEntry.getValue();
        if (Objects.equals(characterColor, lEntry.getKey())) {
          layer.drawHorizontalLine(start, end, y1 - 1);
        } else {
          // clear the same coordinates on all other layers
          layer.clearHorizontalLine(start, end, y1 - 1);
        }
      }
    }
    return this;
  }

  public int getMinIntersectionCoordinate(final int coord1, final int coord2) {
    return Math.min(coord1, coord2) == Integer.MIN_VALUE
        ? 0 : Math.max(0, Math.min(coord1, coord2) - 1);
  }

  public int getMaxIntersectionCoordinate(final int coord1,
                                          final int coord2,
                                          final int limit) {
    return Math.min(limit, Math.max(coord1, coord2)) - 1;
  }

  /**
   * Draw horizontal or vertical (based on points coordinates analysis) line
   * on the canvas from the point (x, y1) to (x, y2) with
   * symbol {@link Canvas#DEFAULT_LINE_CHAR}.
   * If any point of the line is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   *
   * @param x1 x coordinate of the vertical line
   * @param x2 x coordinate of the vertical line
   * @param y1 y coordinate of the first point
   * @param y2 y coordinate  of the second point
   * @return canvas with a new line.
   */
  public Canvas drawLine(final int x1,
                         final int y1,
                         final int x2,
                         final int y2) {
    return drawLine(x1, y1, x2, y2, DEFAULT_LINE_CHAR);
  }

  /**
   * Draw rectangle on the canvas
   * with the top left point (x1, y1) and bottom right point (x2, y2)
   * with symbol {@link Canvas#DEFAULT_LINE_CHAR}.
   * If any point of the rectangle is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   *
   * @param x1 x coordinate of the first point
   * @param x2 x coordinate of the second point
   * @param y1 y coordinate of the first point
   * @param y2 y coordinate of the second point
   * @return canvas with a new rectangle.
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
   * Draw rectangle on the canvas
   * with the top left point (x1, y1) and bottom right point (x2, y2)
   * with symbol {@link Canvas#DEFAULT_LINE_CHAR}.
   * If any point of the rectangle is out of canvas this point will
   * not be drawn while all points presenting on the canvas will be drawn
   *
   * @param x1 x coordinate of the first point
   * @param x2 x coordinate of the second point
   * @param y1 y coordinate of the first point
   * @param y2 y coordinate of the second point
   * @param c  c color to draw rectangle
   * @return canvas with a new rectangle.
   */
  public Canvas drawRectangle(final int x1,
                              final int y1,
                              final int x2,
                              final int y2,
                              final char c) {
    return drawLine(x1, y1, x2, y1, c)
        .drawLine(x1, y2, x2, y2, c)
        .drawLine(x1, y1, x1, y2, c)
        .drawLine(x2, y1, x2, y2, c);
  }

  /**
   * Return color-layer map-entry for the specified coordinate.
   *
   * @param x x coordinate
   * @param y y coordinate
   * @return color-layer map-entry
   */
  protected Map.Entry<Character, Layer> getLayerForPoint(int x, int y) {
    for (Map.Entry<Character, Layer> color2LineEntry
        : colorToLayerMap.entrySet()) {
      Layer value = color2LineEntry.getValue();
      if (value.getLine(y) != null && value.getLine(y).get(x)) {
        return color2LineEntry;
      }
    }
    // Currently should never happen as each point has an association
    // with a layer and each layer has an association with a color.
    // However it could be an extension point to have null as code
    // for e.g. background color but some sophisticated logic
    // should be applied while doing fill.
    return null;
  }

  /**
   * Fill the point (x, y) and all its neighbours with
   * the same char content with a new char c.
   * The way of filling is 4-dots.
   * If point (x, y) is out of canvas this point will
   * not fill anything.
   *
   * @param startX      x coordinate of the start point to fill
   * @param startY      y coordinate of the start point to fill
   * @param targetColor a new char to fill
   * @return canvas with area filled with targetColor.
   */
  public Canvas fill(final int startX,
                     final int startY,
                     final char targetColor) {
    return fill(startX, startY, targetColor, true);
  }

  /**
   * Fill the point (x, y) and all its neighbours with
   * the same char content with a new char c.
   * The way of filling is defined by {@param isB4}.
   * If point (x, y) is out of canvas this point will
   * not fill anything.
   *
   * @param startX      x coordinate of the start point to fill
   * @param startY      y coordinate of the start point to fill
   * @param targetColor a new char to fill
   * @param isB4        specify the way of filling area
   *                    if true then 4-dots way will be used
   *                    if false then 8-dots way will be used
   * @return canvas with area filled with targetColor.
   */
  public Canvas fill(final int startX,
                     final int startY,
                     final char targetColor,
                     final boolean isB4) {
    final int x = startX - 1;
    final int y = startY - 1;
    Map.Entry<Character, Layer> color2Layer = getLayerForPoint(x, y);

    if (color2Layer != null && color2Layer.getKey() == targetColor) {
      return this;
    }
    // Currently color2Layer should never be null here as each point has
    // an association with a layer and each layer has an association
    // with a color. If it is null then then an issue is present for
    // some case which should be fixed
    assert color2Layer != null;

    Layer source = color2Layer.getValue();
    final Layer destination =
        colorToLayerMap.getOrDefault(targetColor, new Layer(width, height));

    colorToLayerMap.put(targetColor, source.fill(x, y, destination, isB4));
    cleanEmptyLayers();
    return this;
  }

  /**
   * Remove layers which are not associated with any point.
   */
  public void cleanEmptyLayers() {
    Set<Character> colors = colorToLayerMap.keySet();
    Set<Character> candidatesToRemove = new HashSet<>(colors);
    for (Character color : colors) {
      Layer layer = colorToLayerMap.get(color);
      for (int i = 0; i < height; i++) {
        if (layer.getLine(i) != null && !layer.getLine(i).isEmpty()) {
          candidatesToRemove.remove(color);
          break;
        }
      }
    }
    for (Character color2Remove : candidatesToRemove) {
      colorToLayerMap.remove(color2Remove);
    }
  }

  /**
   * Print canvas representation to Appendable which could be a stream.
   * The method is used instead of toString()
   * as it could be used with streams and as a result
   * will not hold in memory big objects => OutOfMemoryError
   * (in case of big canvas like 20000x20000 and higher).
   *
   * @param appendable the Appendable to what print canvas
   */
  public void printTo(final Appendable appendable) throws IOException {
    StringBuilder sb = new StringBuilder(STRING_LENGTH_FOR_FLUSHING);
    for (int i = 0; i < width + 2; i++) {
      sb.append(
          drawingShellOpts.get(DrawingShellPropertiesEnum.HORIZONTAL_BORDER));
      if (sb.length() >= STRING_LENGTH_FOR_FLUSHING - 1) {
        appendable.append(sb.toString());
        sb = new StringBuilder(STRING_LENGTH_FOR_FLUSHING);
      }
    }
    sb.append("\n");
    //appendable.append(sb.toString());
    for (int i = 0; i < height; i++) {
      sb = lineToAppendable(appendable, sb, i);
    }
    for (int i = 0; i < width + 2; i++) {
      sb.append(
          drawingShellOpts.get(DrawingShellPropertiesEnum.HORIZONTAL_BORDER));
      if (sb.length() >= STRING_LENGTH_FOR_FLUSHING) {
        appendable.append(sb.toString());
        sb = new StringBuilder(STRING_LENGTH_FOR_FLUSHING);
      }
    }
    sb.append("\n");
    appendable.append(sb.toString());
  }

  /**
   * Print line content to appendable.
   *
   * @param appendable appendable to print line
   * @param lineNumber the number of a line which should be printed
   * @throws IOException
   */
  private StringBuilder lineToAppendable(Appendable appendable,
                                         StringBuilder sb,
                                         int lineNumber) throws IOException {
    sb.append(drawingShellOpts.get(DrawingShellPropertiesEnum.VERTICAL_BORDER));
    int cursor = 0;
    while (cursor < width) {
      Map.Entry<Character, Layer> color2LineEntry =
          getLayerForPoint(cursor, lineNumber);
      BitSet currentLine = color2LineEntry == null
          ? null : color2LineEntry.getValue().getLine(lineNumber);
      if (currentLine == null) {
        sb.append(DEFAULT_EMPTY_CHAR);
        cursor++;
      } else {
        int next = currentLine.cardinality() == width
            ? width - 1 : color2LineEntry.getValue()
            .getLastSetIndexInARow(currentLine, cursor);
        if (next >= cursor) {
          char key = color2LineEntry.getKey();
          for (int i = cursor; i <= next; i++) {
            sb.append(key);
            if (sb.length() >= STRING_LENGTH_FOR_FLUSHING - 1) {
              appendable.append(sb.toString());
              sb = new StringBuilder(STRING_LENGTH_FOR_FLUSHING);
            }
          }
          cursor += next - cursor + 1;
        } else {
          // should never happen
          throw new RuntimeException(
              "Could not handle line " + currentLine
                  + " since position " + cursor);
        }
      }
    }
    sb.append(drawingShellOpts.get(DrawingShellPropertiesEnum.VERTICAL_BORDER))
        .append("\n");
    return sb;
  }

  /**
   * WARNING: do not use it in case of huge canvas e.g. 1000x1000
   * and higher as it could lead to OutOfMemoryError.
   * Use {@link Canvas#printTo(Appendable)} instead.
   *
   * @return string representation of Canvas.
   */
  @Override
  public String toString() {
    try {
      StringBuilder stringBuilder = new StringBuilder();
      printTo(stringBuilder);
      return stringBuilder.toString();
    } catch (IOException e) {
      // should never happen as StringBuilder#append does not throw an exception
      throw new RuntimeException(e);
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
