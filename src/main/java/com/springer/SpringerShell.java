package com.springer;

import com.springer.commands.GeneralCommands;
import com.springer.commands.ReflectiveCommandHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * A shell for drawing on text canvas.
 */
public final class SpringerShell {

  /**
   * Sleeping tie interval in millis to wait if no new command appears.
   */
  public static final long SLEEP_TIME = 100L;
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
   * SpringerShell constructor could be called only from this class.
   */
  private SpringerShell() {
     final GeneralCommands commands = new GeneralCommands(this);
     commandHandlerMap =
         Collections.unmodifiableMap(new HashMap<String, CommandHandler>() {{
           put("Q", new ReflectiveCommandHandler<>(commands, "Q"));
         }});
  }

  /**
   * @param args startup args
   */
  public static void main(final String[] args) {
    SpringerShell shell = new SpringerShell();
    if (args == null) {
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
    try (Scanner scanner = new Scanner(inputStream)) {
      while (!exit) {
        try {
          outputStream.write("enter command: ".getBytes());
          String line;
          while (!scanner.hasNext()) {
            Thread.sleep(SLEEP_TIME);
          }
          line = scanner.nextLine();
          if (!System.in.equals(inputStream)) {
            outputStream.write((line + "\n").getBytes());
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
            commandHandler.execute(line);
          } else {
            errorStream.println("Unknown command: " + commandName);
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Exit.
   */
  public void exit() {
    exit = true;
  }
}
