package com.springer.commands;

import com.springer.CommandHandler;

/**
 * A {@link CommandHandler} implementation that
 * uses reflection to determine the method to dispatch the command.
 * @param <T> Instance of a class where specific range of commands is defined.
 */
public class ReflectiveCommandHandler<T extends Commands>
    implements CommandHandler {
  /**
   * Instance of a class where the command is defined.
   */
  private final T commands;

  /**
   * Name of the command.
   */
  private final String loweredCommandName;

  /**
   * Constructor.
   * @param commandsInstance commands instance
   * @param name name of the command
   */
  public ReflectiveCommandHandler(final T commandsInstance,
                                  final String name) {
    this.loweredCommandName = name.toLowerCase();
    this.commands = commandsInstance;
  }

  /**
   * @return the command's lowered name.
   */
  @Override
  public String getName() {
    return loweredCommandName;
  }

  /**
   * Looks up for a method to execute the command from the command line
   * and calls this method.
   * @param line The full command line to execute
   */
  @Override
  public void execute(final String line) {
    try {
      commands.getClass().getMethod(getName(), String.class)
          .invoke(commands, line);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

}
