package ru.nuyanzin.commands;

import java.util.Locale;

import ru.nuyanzin.DrawingShell;

/**
 * A {@link CommandHandler} implementation that
 * uses reflection to determine the method to dispatch the command.
 * As one of the advantages is possibility to plug in new commands
 * from customer code.
 *
 * @param <T> Instance of a class where specific range of commands is defined.
 */
public class ReflectiveCommandHandler<T extends Commands>
    implements CommandHandler {
  /**
   * Instance of {@link DrawingShell} where the command is defined.
   */
  private final DrawingShell shell;
  /**
   * Instance of a class where the command is defined.
   */
  private final T commands;

  /**
   * Name of the command.
   */
  private final String loweredCommandName;

  /**
   * ReflectiveCommandHandler constructor.
   *
   * @param drawingShell     instance of {@link DrawingShell}
   *                         where the command is defined
   * @param commandsInstance commands instance
   * @param name             name of the command
   */
  public ReflectiveCommandHandler(final DrawingShell drawingShell,
                                  final T commandsInstance,
                                  final String name) {
    this.loweredCommandName = name.toLowerCase(Locale.ROOT);
    this.commands = commandsInstance;
    this.shell = drawingShell;
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
   *
   * @param line The full command line to execute
   */
  @Override
  public void execute(final String line) {
    try {
      commands.getClass().getMethod(getName(), String.class)
          .invoke(commands, line);
    } catch (Throwable t) {
      shell.handleException(t);
    }
  }
}
