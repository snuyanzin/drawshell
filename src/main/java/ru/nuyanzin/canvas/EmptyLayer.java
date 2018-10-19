package ru.nuyanzin.canvas;

import java.util.BitSet;

/**
 * Empty layer which is used as initial background
 * and as a result it should be filled completely.
 */
public class EmptyLayer extends Layer {
  public EmptyLayer(int width, int height) {
    super(width, height);

    for (int i = 0; i < height; i++) {
      lazyInitBitSet(i);
      BitSet bitSet = getLine(i);
      bitSet.set(0, width);
    }
  }
}
