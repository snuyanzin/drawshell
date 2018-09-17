package com.springer.commands;

import com.springer.DrawingShell;

import java.util.Arrays;

/**
 * Class for general commands.
 */
public final class GeneralCommands implements Commands {
  public static final String COMMAND_OPTIONS_REGEX = "\\s+";
  /**
   * Instance of the shell.
   */
  private final DrawingShell shell;

  /**
   * Constructor.
   * @param springerShell instance of the shell.
   */
  public GeneralCommands(final DrawingShell springerShell) {
    this.shell = springerShell;
  }

  /**
   * Exit command Q.
   * @param line    full command line.
   */
  public void q(final String line) {
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    if (parts.length > 0 && !parts[0].isEmpty() ) {
      shell.output(shell.getLocMessage("usage-q"));
      return;
    }
    shell.exit();
  }

  /**
   * Command H.
   * @param line    full command line.
   */
  public void h(final String line) {    
    shell.output(shell.getLocMessage("help"));
  }

  /**
   * Command C for new canvas creation.
   * @param line    full command line
   */
  public void c(final String line) {
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    if (parts.length != 2) {
      shell.output(shell.getLocMessage("usage-c"));
      return;
    }
    int w, h;
    try {
      w = Integer.parseInt(parts[0]);
      h = Integer.parseInt(parts[1]);
    } catch (NumberFormatException nfe) {
      shell.output(shell.getLocMessage("usage-c"));
      return;
    }
    if (w < 0 || h < 0) {
      shell.output(shell.getLocMessage("usage-c"));
      return;
    }
    shell.createBoard(w, h);
    shell.printCanvas();
  }

  /**
   * Command L for drawing horizontal and vertical lines on canvas.
   * @param line    full command line
   */
  public void l(final String line) {
    if (shell.getCanvas() == null) {
      shell.output(shell.getLocMessage("canvas-required"));
      return;
    }
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    if (parts.length != 4) {
      shell.output(shell.getLocMessage("usage-l"));
      return;
    }
    int[] args;
    try {
      args = validateIfNumbers(parts);
    } catch (NumberFormatException nfe) {
      shell.output(shell.getLocMessage("usage-l"));
      return;
    }
    shell.output(shell.getCanvas().drawLine(args[0], args[1], args[2], args[3]).toString());
  }

  /**
   * Command L for drawing horizontal and vertical lines on canvas.
   * @param line    full command line
   */
  public void r(final String line) {
    if (shell.getCanvas() == null) {
      shell.output(shell.getLocMessage("canvas-required"));
      return;
    }
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    if (parts.length != 4) {
      shell.output(shell.getLocMessage("usage-r"));
      return;
    }
    int[] args;
    try {
      args = validateIfNumbers(parts);
    } catch (NumberFormatException nfe) {
      shell.output(shell.getLocMessage("usage-r"));
      return;
    }
    shell.output(shell.getCanvas().drawRectangle(args[0], args[1], args[2], args[3]).toString());
  }

  /**
   * Command L for drawing horizontal and vertical lines on canvas.
   * @param line    full command line
   */
  public void b(final String line) {
    if (shell.getCanvas() == null) {
      shell.output(shell.getLocMessage("canvas-required"));
      return;
    }
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    if (parts.length != 3) {
      shell.output("Usage: B x y c");
      return;
    }
    int[] args;
    try {
      args = validateIfNumbers(Arrays.copyOfRange(parts, 0, parts.length - 1));
    } catch (NumberFormatException nfe) {
      shell.output(
          "Usage: B <x> <y> c. Where x, y must be integer numbers");
      return;
    }
    shell.output(shell.getCanvas().fill(args[0], args[1], parts[parts.length - 1].charAt(0)).toString());
  }

  private int[] validateIfNumbers(String ... args) {
    if (args == null || args.length == 0) {
      return null;
    }
    int[] result = new int[args.length];
    for (int i = 0; i < args.length; i++) {
      result[i] = Integer.parseInt(args[i]);
    }
    return result;
  }
}
