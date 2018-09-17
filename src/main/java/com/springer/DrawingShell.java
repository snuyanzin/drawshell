package com.springer;

import com.springer.commands.GeneralCommands;
import com.springer.commands.ReflectiveCommandHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * A shell for drawing on text canvas.
 */
public final class DrawingShell {
  private static final ResourceBundle RESOURCE_BUNDLE =
      ResourceBundle.getBundle("DrawingShell");
  /**
   * Flag to show should leave while cycle.
   */
  private boolean exit = false;
  /**
   * Output stream.
   */
  private PrintStream outputStream = new PrintStream(System.out, true);
  /**
   * Error stream.
   */
  private PrintStream errorStream = new PrintStream(System.err, true);
  /**
   * Defined map of existing commands.
   */
  private final Map<String, CommandHandler> commandHandlerMap;

  /**
   * Current canvas instance.
   */
  private Canvas canvas;

  /**
   * DrawingShell constructor could be called only from this class.
   */
  private DrawingShell() {
     final GeneralCommands commands = new GeneralCommands(this);

     commandHandlerMap =
         Collections.unmodifiableMap(new HashMap<String, CommandHandler>() {{
           put("B", new ReflectiveCommandHandler<>(DrawingShell.this, commands, "B"));
           put("C", new ReflectiveCommandHandler<>(DrawingShell.this, commands, "C"));
           put("H", new ReflectiveCommandHandler<>(DrawingShell.this, commands, "H"));
           put("L", new ReflectiveCommandHandler<>(DrawingShell.this, commands, "L"));
           put("R", new ReflectiveCommandHandler<>(DrawingShell.this, commands, "R"));
           put("Q", new ReflectiveCommandHandler<>(DrawingShell.this, commands, "Q"));
         }});
  }

  /**
   * @param args startup args
   */
  public static void main(final String[] args) {
    DrawingShell shell = new DrawingShell();
    if (args == null || args.length == 0) {
      shell.start(System.in);
    } else if (args.length == 1) {
      if (Files.exists(Paths.get(args[0]))) {
        try (FileInputStream fis =
            new FileInputStream(Paths.get(args[0]).toAbsolutePath().toFile())) {
          shell.start(fis);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      shell.errorStream.println("Usage");
    }
  }

  /**
   * start of shell.
   * @param inputStream input stream to work with
   */
  private void start(final InputStream inputStream) {
    // Unfortunately {@link java.util.Scanner} could not explicitly detect
    // end of file. However files are used here only for test purposes and there
    // is nothing about them in initial requirements
    try (Scanner scanner = new Scanner(inputStream)) {
      while (!exit) {
        try {
          output(getPrompt(), false);
          String line;
          line = scanner.nextLine();

          if (!System.in.equals(inputStream)) {
            output(line);
          }
          if (line == null) {
            continue;
          }
          String trimmedLine = line.trim();
          if (trimmedLine.isEmpty()) {
            continue;
          }
          // currently commands are simple and do not contain spaces
          // in case the requirement change the logic should be adapted
          int firstSpaceIndex = trimmedLine.indexOf(" ");
          String commandName = trimmedLine;
          if (firstSpaceIndex != -1) {
            commandName = trimmedLine.substring(0, firstSpaceIndex);
          }
          CommandHandler commandHandler = commandHandlerMap.get(commandName);
          if (commandHandler != null) {
            if (Objects.equals(commandName, trimmedLine)) {
              commandHandler.execute("");
            } else {
              commandHandler.execute(line.substring(line.indexOf(commandName) + commandName.length() + 1));
            }
          } else {
            output(getLocMessage("unknown-command", commandName));
          }
        } catch (Throwable t) {
          handleException(t);
        }
      }
    }
  }

  /**
   * Prompt.
   * @return prompt string.
   */
  private String getPrompt() {
    return "enter command: ";
  }

  /**
   * Exit.
   */
  public void exit() {
    exit = true;
  }

  /**
   * Create new canvas for the shell with the specified width and height.
   * @param w       width of the canvas
   * @param h       height of the canvas
   */
  public void createBoard(final int w, final int h) {
    this.canvas = new Canvas(w, h);
  }

  public Canvas getCanvas() {
    return canvas;
  }

  /**
   * Print current canvas. Print null if canvas is null.
   */
  public void printCanvas() {
    output(String.valueOf(canvas));
  }

  /**
   * Print the specified message to the console and add a new line in the end.
   *
   * @param msg     the message to print
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

  public void handleException(Throwable e) {
    while (e instanceof InvocationTargetException) {
      e = ((InvocationTargetException) e).getTargetException();
    }
    e.printStackTrace(errorStream);
  }

  public String getLocMessage(String res, Object ... params) {
    try {
      return MessageFormat.format(RESOURCE_BUNDLE.getString(res), params);
    } catch (Exception e) {
      e.printStackTrace(errorStream);

      try {
        return res + ": " + Arrays.toString(params);
      } catch (Exception e2) {
        return res;
      }
    }
  }
}
