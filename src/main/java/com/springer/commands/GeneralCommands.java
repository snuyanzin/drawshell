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
   * @param line full command line.
   */
  public void q(final String line) {
    shell.exit();
  }
}
