package ru.nuyanzin.canvas;

import java.util.BitSet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link Layer} methods.
 * No validation is checked here.
 */
public class LayerTest {

  /**
   * Check if search for firstSetIndexInARow works correct
   * e.g. for BitSet
   * {0, 1, 2, 3, 4, 6, 8, 9, 10}
   * firstSetIndexInARow starting with 1 is 0
   * firstSetIndexInARow starting with 4 is 0
   * firstSetIndexInARow starting with 6 is 6
   * firstSetIndexInARow starting with 10 is 8
   */
  @Test
  public void testGetFirstSetIndexInARow() {
    final int width = 30;
    final int height = 30;
    BitSet bitSet = new BitSet(width);
    bitSet.set(0);
    bitSet.set(1);
    bitSet.set(2);
    bitSet.set(3);
    bitSet.set(4);
    bitSet.set(6);
    bitSet.set(8);
    bitSet.set(9);
    bitSet.set(10);
    Layer layer = new Layer(width, height);
    assertEquals(0, layer.getFirstSetIndexInARow(bitSet, 1));
    assertEquals(0, layer.getFirstSetIndexInARow(bitSet, 4));
    assertEquals(6, layer.getFirstSetIndexInARow(bitSet, 6));
    assertEquals(8, layer.getFirstSetIndexInARow(bitSet, 10));

    bitSet = new BitSet(width);
    assertEquals(-1, layer.getFirstSetIndexInARow(bitSet, 1));
    assertEquals(-1, layer.getFirstSetIndexInARow(bitSet, 2));

    bitSet = new BitSet(width);
    bitSet.set(1);
    bitSet.set(2);
    bitSet.set(3);
    bitSet.set(4);
    assertEquals(1, layer.getFirstSetIndexInARow(bitSet, 1));
    assertEquals(1, layer.getFirstSetIndexInARow(bitSet, 2));
    assertEquals(1, layer.getFirstSetIndexInARow(bitSet, 3));
    assertEquals(1, layer.getFirstSetIndexInARow(bitSet, 4));

    bitSet = new BitSet(width);
    bitSet.set(1);
    bitSet.set(3);
    bitSet.set(5);
    bitSet.set(7);
    assertEquals(-1, layer.getFirstSetIndexInARow(bitSet, 0));
    assertEquals(1, layer.getFirstSetIndexInARow(bitSet, 1));
    assertEquals(-1, layer.getFirstSetIndexInARow(bitSet, 2));
    assertEquals(3, layer.getFirstSetIndexInARow(bitSet, 3));
    assertEquals(-1, layer.getFirstSetIndexInARow(bitSet, 4));
    assertEquals(5, layer.getFirstSetIndexInARow(bitSet, 5));
    assertEquals(-1, layer.getFirstSetIndexInARow(bitSet, 6));
    assertEquals(7, layer.getFirstSetIndexInARow(bitSet, 7));
    assertEquals(-1, layer.getFirstSetIndexInARow(bitSet, 8));
    assertEquals(-1, layer.getFirstSetIndexInARow(bitSet, 9));
  }

  /**
   * Check if search for lastSetIndexInARow works correct
   * e.g. for BitSet
   * {0, 1, 3, 4, 5, 7}
   * lastSetIndexInARow starting with 1 is 0
   * lastSetIndexInARow starting with 1 is 1
   * lastSetIndexInARow starting with 3 is 5
   * lastSetIndexInARow starting with 5 is 5
   * lastSetIndexInARow starting with 7 is 7
   */
  @Test
  public void testGetLastSetIndexInARow() {
    final int width = 30;
    final int height = 30;
    BitSet bitSet = new BitSet(width);
    bitSet.set(0);
    bitSet.set(1);
    bitSet.set(3);
    bitSet.set(4);
    bitSet.set(5);
    bitSet.set(7);
    Layer layer = new Layer(width, height);
    assertEquals(1, layer.getLastSetIndexInARow(bitSet, 0));
    assertEquals(1, layer.getLastSetIndexInARow(bitSet, 1));
    assertEquals(5, layer.getLastSetIndexInARow(bitSet, 3));
    assertEquals(5, layer.getLastSetIndexInARow(bitSet, 5));
    assertEquals(7, layer.getLastSetIndexInARow(bitSet, 7));

    bitSet = new BitSet(width);
    bitSet.set(1);
    bitSet.set(2);
    bitSet.set(3);
    bitSet.set(4);
    assertEquals(4, layer.getLastSetIndexInARow(bitSet, 1));
    assertEquals(4, layer.getLastSetIndexInARow(bitSet, 2));
    assertEquals(4, layer.getLastSetIndexInARow(bitSet, 3));
    assertEquals(4, layer.getLastSetIndexInARow(bitSet, 4));

    bitSet = new BitSet(width);
    bitSet.set(4);
    bitSet.set(5);
    bitSet.set(6);
    assertEquals(-1, layer.getLastSetIndexInARow(bitSet, 3));
    assertEquals(6, layer.getLastSetIndexInARow(bitSet, 4));
    assertEquals(6, layer.getLastSetIndexInARow(bitSet, 5));
    assertEquals(6, layer.getLastSetIndexInARow(bitSet, 6));

    bitSet = new BitSet(width);
    bitSet.set(1);
    bitSet.set(3);
    bitSet.set(4);
    bitSet.set(5);
    bitSet.set(6);
    bitSet.set(7);
    bitSet.set(8);
    assertEquals(8, layer.getLastSetIndexInARow(bitSet, 8));
    assertEquals(-1, layer.getLastSetIndexInARow(bitSet, 9));
  }

  /**
   * Check if search for downBoundary works correct
   * e.g. for BitSet[] {
   * null
   * {1, 2, 3}
   * null
   * {2}
   * {1, 2, 3, 4}
   * }
   * getDownBoundary starting with 0 is 0
   * getDownBoundary starting with 1 is 1
   * getDownBoundary starting with 2 is 2
   * getDownBoundary starting with 3 is 3
   * getDownBoundary starting with 4 is 3
   */
  @Test
  public void testDownBoundary() {
    Layer layer = new Layer(3, 3);
    layer.drawHorizontalLine(1, 3, 1);
    layer.drawHorizontalLine(2, 2, 0);

    assertEquals(0, layer.getDownBoundary(1));
    assertEquals(2, layer.getDownBoundary(2));
    assertEquals(0, layer.getDownBoundary(0));

    layer = new Layer(5, 5);
    layer.drawHorizontalLine(1, 3, 1);
    layer.drawHorizontalLine(2, 2, 3);
    layer.drawHorizontalLine(1, 4, 4);

    assertEquals(1, layer.getDownBoundary(1));
    assertEquals(2, layer.getDownBoundary(2));
    assertEquals(3, layer.getDownBoundary(3));
    assertEquals(3, layer.getDownBoundary(4));
    assertEquals(0, layer.getDownBoundary(0));
  }

  /**
   * Check if search for upBoundary works correct
   * e.g. for BitSet[] {
   * null
   * {1, 2, 3}
   * null
   * {2}
   * {1, 2, 3, 4}
   * }
   * getUpBoundary starting with 0 is 0
   * getUpBoundary starting with 1 is 1
   * getUpBoundary starting with 2 is 2
   * getUpBoundary starting with 3 is 4
   * getUpBoundary starting with 4 is 4
   */
  @Test
  public void testUpBoundary() {
    Layer layer = new Layer(5, 5);
    layer.drawHorizontalLine(1, 3, 1);
    layer.drawHorizontalLine(2, 2, 3);
    layer.drawHorizontalLine(1, 4, 4);

    assertEquals(1, layer.getUpBoundary(1));
    assertEquals(2, layer.getUpBoundary(2));
    assertEquals(4, layer.getUpBoundary(3));
    assertEquals(4, layer.getUpBoundary(4));
    assertEquals(0, layer.getUpBoundary(0));
  }

  /**
   * Check if getLineToHandleAgain works correct.
   * If there are points in source which should be colored
   * and moved to destination then getLineToHandleAgain should return non -1.
   * E.g.
   * Source BitSet {
   * {0, 2}
   * {1, 3}
   * {}
   * {}
   * }
   * and
   * Destination BitSet {
   * {}
   * {}
   * {0, 2}
   * {1, 3}
   * }
   * <p>
   * With 4-dots approach there is nothing to be colored
   * and getLineToHandleAgain should return -1
   * With 8-dots approach {1, 3} from Source are diagonally connected
   * to {0, 2} from Destination and getLineToHandleAgain should return 1.
   */
  @Test
  public void testGetLineToHandleAgain() {
    int height = 4;
    int width = 4;
    BitSet[] source = new BitSet[height];
    BitSet[] destination = new BitSet[height];
    for (int i = 0; i < height; i++) {
      source[i] = new BitSet(width);
      destination[i] = new BitSet(width);
    }
    source[0].set(0);
    source[0].set(2);
    source[1].set(1);
    source[1].set(3);

    destination[2].set(0);
    destination[2].set(2);
    destination[3].set(1);
    destination[3].set(3);
    Layer sourceLayer = new Layer(source, width);

    assertEquals(1,
        sourceLayer.getLineToHandleAgain(destination, 0, 3, false));
    assertEquals(-1,
        sourceLayer.getLineToHandleAgain(destination, 0, 3, true));
  }

  @Test
  public void testGetLineToHandleAgainChessBoardIssue() {
    int height = 8;
    int width = 8;
    BitSet[] source = new BitSet[height];
    BitSet[] destination = new BitSet[height];
    for (int i = 0; i < height; i++) {
      destination[i] = new BitSet(width);
    }
    destination[0].set(1);
    destination[0].set(3);
    destination[0].set(5);
    destination[0].set(7);

    destination[1].set(0);
    destination[1].set(2);
    destination[1].set(4);
    destination[1].set(6);

    destination[2].set(1);
    destination[2].set(3);
    destination[2].set(5);
    destination[2].set(7);

    destination[3].set(0);
    destination[3].set(2);
    destination[3].set(4);
    destination[3].set(6);

    destination[4].set(1);
    destination[4].set(3);
    destination[4].set(5);
    destination[4].set(7);

    destination[5].set(0);
    destination[5].set(2);
    destination[5].set(4);
    destination[5].set(6);

    destination[6].set(1);
    destination[6].set(3);
    destination[6].set(5);
    destination[6].set(7);

    destination[7].set(0);
    destination[7].set(2);
    destination[7].set(4);
    destination[7].set(6);

    source[2] = new BitSet(width);
    source[2].set(7);

    Layer sourceLayer = new Layer(source, width);

    assertEquals(2,
        sourceLayer.getLineToHandleAgain(destination, 0, 7, false));
    assertEquals(2,
        sourceLayer.getLineToHandleAgain(destination, 0, 7, true));
  }


}
