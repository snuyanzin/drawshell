package ru.nuyanzin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ru.nuyanzin.canvas.Canvas;
import ru.nuyanzin.commands.CommandHandler;
import ru.nuyanzin.commands.GeneralCommands;
import ru.nuyanzin.commands.ReflectiveCommandHandler;

/**
 * A shell for drawing on text canvas.
 */
public final class DrawingShell {

  /**
   * Maximum number of symbols of unknown command
   * to print in error handling message.
   */
  private static final int MAX_NUMBER_SYMBOLS_FOR_UNKNOWN_COMMAND = 100;

  /**
   * Flag to show should leave while cycle.
   */
  private boolean isExitRequired = false;

  /**
   * Output stream.
   */
  private final PrintStream outputStream;

  /**
   * Defined map of existing commands.
   */
  private final Map<String, CommandHandler> commandHandlerMap;

  /**
   * Current canvas instance.
   */
  private Canvas canvas;

  private final DrawingShellOpts opts;

  /**
   * DrawingShell constructor could be called only from this class.
   *
   * @throws UnsupportedEncodingException if any of the specified charsets
   *                                      for print stream does not exist
   */
  private DrawingShell() throws UnsupportedEncodingException {
    outputStream = new PrintStream(
        System.out, true, StandardCharsets.UTF_8.name());

    final GeneralCommands commands = new GeneralCommands(this);

    commandHandlerMap =
        Collections.unmodifiableMap(new HashMap<String, CommandHandler>() {{
            put("B", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "B"));
            put("B4", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "B4"));
            put("B8", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "B8"));
            put("C", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "C"));
            put("H", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "H"));
            put("L", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "L"));
            put("P", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "P"));
            put("R", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "R"));
            put("SET", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "SET"));
            put("Q", new ReflectiveCommandHandler<>(
                DrawingShell.this, commands, "Q"));
          }});
    opts = new DrawingShellOpts(this);
  }

  /**
   * @param args startup args
   * @throws UnsupportedEncodingException If the named encoding is not supported
   */
  public static void main(final String[] args)
      throws UnsupportedEncodingException {
    DrawingShell shell = new DrawingShell();
    if (args == null || args.length == 0) {
      shell.start(System.in);
    } else if (args.length == 1) {
      startShellWithFileStream(args[0], shell);
    } else {
      shell.output(Loc.getLocMessage("usage-start"));
    }
  }

  private static void startShellWithFileStream(String arg, DrawingShell shell) {
    Path absolutePathToFile = Paths.get(arg).toAbsolutePath();
    if (Files.exists(absolutePathToFile)) {
      try (FileInputStream fis =
               new FileInputStream(absolutePathToFile.toFile())) {
        shell.start(fis);
      } catch (IOException e) {
        shell.handleException(e);
      }
    } else {
      shell.output(Loc.getLocMessage("file-not-exist",
          absolutePathToFile.toString()));
    }
  }

  /**
   * Shell start.
   *
   * @param inputStream input stream to work with
   */
  private void start(final InputStream inputStream) {
    try (BufferedReader scanner = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()))) {
      String fullCommandLine = "";
      while (!isExitRequired && fullCommandLine != null) {
        try {
          output(getPrompt(), false);
          fullCommandLine = scanner.readLine();

          if (fullCommandLine == null) {
            output("\n" + Loc.getLocMessage("eof-detected"));
            isExitRequired = true;
            continue;
          }
          // if the input stream is not System.in then repeat the original
          // command (or cut version in case of very long line) in output.
          // Could be helpful while analysis
          // of output after working with file commands.
          if (!System.in.equals(inputStream)) {
            output(getCutString(fullCommandLine));
          }
          String trimmedLine = fullCommandLine.trim();
          if (trimmedLine.isEmpty()) {
            continue;
          }
          executeCommand(
              fullCommandLine, trimmedLine, getCommandName(trimmedLine));
        } catch (Throwable t) {
          handleException(t);
          output(getPrompt(), false);
        }
      }
    } catch (IOException e) {
      handleException(e);
    }
  }

  private String getCommandName(String trimmedLine) {
    // currently commands are simple and do not contain spaces
    // in case the requirement change the logic should be adapted
    int firstSpaceIndex = trimmedLine.indexOf(" ");
    String commandName = trimmedLine;
    if (firstSpaceIndex != -1) {
      commandName = trimmedLine.substring(0, firstSpaceIndex);
    }
    return commandName;
  }

  /**
   * Determine the right command handler and execute the command.
   *
   * @param fullCommandLine full command line
   * @param trimmedLine     trimmed command line
   * @param commandName     parsed command name
   */
  private void executeCommand(final String fullCommandLine,
                              final String trimmedLine,
                              final String commandName) {
    CommandHandler commandHandler = commandHandlerMap.get(commandName);
    if (commandHandler != null) {
      if (Objects.equals(commandName, trimmedLine)) {
        commandHandler.execute("");
      } else {
        commandHandler.execute(
            fullCommandLine.substring(
                fullCommandLine.indexOf(commandName)
                    + commandName.length() + 1));
      }
    } else {
      output(Loc.getLocMessage("unknown-command", getCutString(commandName)));
    }
  }

  /**
   * Cut the line if its length
   * is longer then {@link #MAX_NUMBER_SYMBOLS_FOR_UNKNOWN_COMMAND}.
   *
   * @param line line to cut
   * @return the cut or original line depending on its length
   */
  public static String getCutString(String line) {
    return line.length() > MAX_NUMBER_SYMBOLS_FOR_UNKNOWN_COMMAND
        ? line.substring(0, MAX_NUMBER_SYMBOLS_FOR_UNKNOWN_COMMAND)
        + Loc.getLocMessage("rest-is-cut")
        : line;
  }

  /**
   * Prompt.
   *
   * @return prompt string.
   */
  private String getPrompt() {
    return Loc.getLocMessage(
        canvas == null ? "create-canvas-prompt" : "prompt");
  }

  /**
   * Exit.
   */
  public void exit() {
    isExitRequired = true;
  }

  /**
   * Create new canvas for the shell with the specified width and height.
   *
   * @param w width of the canvas
   * @param h height of the canvas
   */
  public void createBoard(final int w, final int h) {
    // first set to null to allow gc to take the object
    // it will allow to recreate large canvas (with default jvm settings)
    // e.g. C 25000 25000 and then again C 25000 25000
    this.canvas = null;
    this.canvas = new Canvas(w, h, opts);
  }

  public Canvas getCanvas() {
    return canvas;
  }

  /**
   * Print the specified message to the console and add a new line in the end.
   *
   * @param msg the message to print
   */
  public void output(final String msg) {
    output(msg, true);
  }

  /**
   * Print the specified message to the console.
   *
   * @param msg     the message to print
   * @param newline if false, do not append a newline
   */
  public void output(final String msg, final boolean newline) {
    if (newline) {
      outputStream.println(msg);
    } else {
      outputStream.print(msg);
    }
  }

  /**
   * Get print stream output.
   *
   * @return the output stream
   */
  public PrintStream getOutput() {
    return outputStream;
  }

  /**
   * Exception handling.
   *
   * @param e exception/error/throwable to handle
   */
  public void handleException(Throwable e) {
    while (e instanceof InvocationTargetException) {
      e = ((InvocationTargetException) e).getTargetException();
    }
    e.printStackTrace(outputStream);
  }

  public DrawingShellOpts getOpts() {
    return opts;
  }
}
