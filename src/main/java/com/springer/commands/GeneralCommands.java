package com.springer.commands;

import com.springer.SpringerShell;

/**
 * Class for general commands.
 */
public final class GeneralCommands implements Commands {
  /**
   * Instance of the shell.
   */
  private final SpringerShell shell;

  /**
   * Constructor.
   * @param springerShell instance of the shell.
   */
  public GeneralCommands(final SpringerShell springerShell) {
    this.shell = springerShell;
  }

  /**
   * Exit command Q.
   * @param line    full command line.
   */
  public void q(final String line) {
    shell.exit();
  }

  /**
   * Command C for new canvas creation.
   * @param line    full command line
   */
  public void c(final String line) {
    String[] parts = line.trim().split("\\s+");
    if (parts.length != 3) {
      try {
        shell.output("Usage: C <w> <h>");
      } catch (Throwable t) {
        t.printStackTrace();
      }
      return;
    }
    int w, h;
    try {
      w = Integer.parseInt(parts[1]);
      h = Integer.parseInt(parts[2]);
    } catch (NumberFormatException nfe) {
      shell.output(
          "Usage: C <w> <h>. Where w and h must be non-negative numbers");
      return;
    }
    if (w < 0 || h < 0) {
      shell.output(
          "Usage: C <w> <h>. Where w and h must be non-negative numbers");
      return;
    }
    System.out.println("inside");
    shell.createBoard(w, h);
    shell.printCanvas();
  }

}
