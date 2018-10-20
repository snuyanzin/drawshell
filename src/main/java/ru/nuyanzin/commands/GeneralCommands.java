package ru.nuyanzin.commands;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import ru.nuyanzin.DrawingShell;
import ru.nuyanzin.DrawingShellOpts;
import ru.nuyanzin.Loc;
import ru.nuyanzin.canvas.Canvas;
import ru.nuyanzin.properties.DrawingShellPropertiesEnum;

/**
 * Class for general commands.
 */
public final class GeneralCommands implements Commands {
  /**
   * Integer instance to parse integers only.
   * It is NOT threadsafe but here there is
   * no multithreading => that is currently ok.
   */
  private static final NumberFormat INTEGER_INSTANCE;

  static {
    INTEGER_INSTANCE = NumberFormat.getIntegerInstance();
    INTEGER_INSTANCE.setParseIntegerOnly(false);
  }

  /**
   * Regex to split command line arguments.
   */
  private static final String COMMAND_OPTIONS_REGEX = "\\s+";
  /**
   * Instance of the shell.
   */
  private final DrawingShell shell;

  /**
   * Constructor.
   *
   * @param shell instance of the shell.
   */
  public GeneralCommands(final DrawingShell shell) {
    this.shell = shell;
  }

  /**
   * Command B filling area connected to (x, y).
   * By default there is used a 4-dots way
   *
   * @param line full command line
   */
  public void b(final String line) throws IOException {
    b4(line, "B");
  }

  /**
   * Command B filling area connected to (x, y) in 4-dots way.
   *
   * @param line        full command line
   * @param commandName command name to execute
   */
  private void b4(final String line,
                  final String commandName) throws IOException {
    bCommand(line, commandName, true);
  }

  /**
   * Command B filling area connected to (x, y) in 4-dots way.
   *
   * @param line full command line
   */
  public void b4(final String line) throws IOException {
    bCommand(line, "B4", true);
  }

  /**
   * Command B filling area connected to (x, y) in 8-dots way.
   *
   * @param line full command line
   */
  public void b8(final String line) throws IOException {
    bCommand(line, "B8", false);
  }

  /**
   * Command B filling area connected to (x, y) in canvas.
   *
   * @param line        full command line
   * @param commandName command name to execute
   * @param isB4        specify if 4-dots way should be used.
   *                    if true then 4-dots way will be used
   *                    if false then 8-dots way will be used
   */
  private void bCommand(final String line,
                        final String commandName,
                        final boolean isB4) throws IOException {
    Canvas canvas = shell.getCanvas();
    if (canvas == null) {
      shell.output(Loc.getLocMessage("canvas-required"));
      return;
    }
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);

    String commandBUsageMessage =
        Loc.getLocMessage(
            "usage-b", commandName, canvas.getWidth(), canvas.getHeight());
    // length 3 as 3 arguments are required
    if (parts.length != 3) {
      shell.output(commandBUsageMessage);
      return;
    }
    int[] args = parseIntegersOrThrow(commandBUsageMessage,
        Arrays.copyOfRange(parts, 0, parts.length - 1));
    if (args == null) {
      // just return as exception message printed from parseIntegersOrThrow
      return;
    }
    int x = args[0];
    int y = args[1];
    if (x < 1 || args[1] < 1
        || x > canvas.getWidth() || y > canvas.getHeight()
        || parts[parts.length - 1].length() > 1) {
      shell.output(commandBUsageMessage);
      return;
    }
    canvas.fill(x, args[1], parts[parts.length - 1].charAt(0), isB4);
    printCanvas(canvas);
  }

  /**
   * Command C for new canvas creation.
   *
   * @param line full command line
   */
  public void c(final String line) throws IOException {
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    String commandCUsageMessage =
        Loc.getLocMessage("usage-c", Canvas.CANVAS_DIMENSION_LIMIT);
    // length 2 as 2 arguments are required
    if (parts.length != 2) {
      shell.output(commandCUsageMessage);
      return;
    }
    int[] args = parseIntegersOrThrow(commandCUsageMessage, parts);
    if (args == null) {
      // just return as exception message printed from parseIntegersOrThrow
      return;
    }
    int w = args[0];
    int h = args[1];
    if (w < 1 || h < 1
        // Could be helpful in case of Canvas.CANVAS_DIMENSION_LIMIT
        // is lower than Integer.MAX_VALUE
        || w > Canvas.CANVAS_DIMENSION_LIMIT
        || h > Canvas.CANVAS_DIMENSION_LIMIT) {
      shell.output(commandCUsageMessage);
      return;
    }
    shell.createBoard(w, h);
    printCanvas(shell.getCanvas());
  }

  /**
   * Command P to print current canvas.
   *
   * @param line full command line
   */
  public void p(final String line) throws IOException {
    Canvas canvas = shell.getCanvas();
    if (canvas == null) {
      shell.output(Loc.getLocMessage("canvas-required"));
      return;
    }
    canvas.printTo(shell.getOutput());
  }

  /**
   * Command H.
   *
   * @param line full command line.
   */
  public void h(final String line) {
    shell.output(Loc.getLocMessage("help"));
  }

  /**
   * Command L for drawing horizontal and vertical lines on canvas.
   *
   * @param line full command line
   */
  public void l(final String line) throws IOException {
    Canvas canvas = shell.getCanvas();
    if (canvas == null) {
      shell.output(Loc.getLocMessage("canvas-required"));
      return;
    }
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    String commandLUsageMessage = Loc.getLocMessage("usage-l");
    // length 4 or 5 as 4 or 5 arguments are required
    if ((parts.length != 4 && parts.length != 5)
        || (parts.length == 5 && parts[4].length() > 1)) {
      shell.output(commandLUsageMessage);
      return;
    }
    int[] args = parseIntegersOrThrow(
        commandLUsageMessage, Arrays.copyOfRange(parts, 0, 4));
    if (args == null) {
      // just return as exception message printed from parseIntegersOrThrow
      return;
    }

    int x1 = args[0];
    int y1 = args[1];
    int x2 = args[2];
    int y2 = args[3];
    if (x1 != x2 && y1 != y2) {
      // non horizontal and non vertical line detected
      shell.output(Loc.getLocMessage("draw-line-not-supported"));
    } else {
      if (parts.length == 4) {
        canvas.drawLine(x1, y1, x2, y2);
      } else {
        canvas.drawLine(x1, y1, x2, y2, parts[4].charAt(0));
      }
      printCanvas(canvas);
    }
  }

  private void printCanvas(Canvas canvas) throws IOException {
    if (shell.getOpts()
        .getBoolean(DrawingShellPropertiesEnum.SHOW_CANVAS_AFTER_COMMAND)) {
      canvas.printTo(shell.getOutput());
    }
  }

  /**
   * Exit command Q.
   *
   * @param line full command line.
   */
  public void q(final String line) {
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    if (parts.length > 0 && !parts[0].isEmpty()) {
      shell.output(Loc.getLocMessage("usage-q"));
      return;
    }
    shell.exit();
  }

  /**
   * Command R for drawing rectangles on canvas.
   *
   * @param line full command line
   */
  public void r(final String line) throws IOException {
    Canvas canvas = shell.getCanvas();
    if (canvas == null) {
      shell.output(Loc.getLocMessage("canvas-required"));
      return;
    }
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    String commandRUsageMessage = Loc.getLocMessage("usage-r");
    // length 4 or 5 as 4 or 5 arguments are required
    if ((parts.length != 4 && parts.length != 5)
        || (parts.length == 5 && parts[4].length() > 1)) {
      shell.output(commandRUsageMessage);
      return;
    }
    int[] args = parseIntegersOrThrow(
        commandRUsageMessage, Arrays.copyOfRange(parts, 0, 4));
    if (args == null) {
      // just return as exception message printed from parseIntegersOrThrow
      return;
    }
    if (parts.length == 4) {
      canvas.drawRectangle(args[0], args[1], args[2], args[3]);
    } else {
      canvas.drawRectangle(
          args[0], args[1], args[2], args[3], parts[4].charAt(0));
    }
    printCanvas(canvas);
  }

  /**
   * Command SET for setting properties.
   *
   * @param line full command line
   */
  public void set(final String line) throws IOException {
    String[] parts = line.trim().split(COMMAND_OPTIONS_REGEX);
    String commandSETUsageMessage = Loc.getLocMessage("usage-set");
    // length 2 arguments are required
    if (parts.length != 2 && parts.length != 0
        && !(parts.length == 1 && parts[0].isEmpty())) {
      shell.output(commandSETUsageMessage);
      return;
    }
    if (parts.length <= 1) {
      Properties props = shell.getOpts().toProperties();
      Set<String> keys = new TreeSet<>(((Map) props).keySet());
      for (String key : keys) {
        final String substring =
            key.substring(DrawingShellOpts.PROPERTY_PREFIX.length());
        String value = props.getProperty(key);
        shell.output(substring
                + String.format(Locale.ROOT,
            "%1$" + (50 - substring.length() - (10 - value.length())) + "s",
            value));
      }
    } else {
      shell.getOpts().set(parts[0], parts[1]);
    }
  }

  /**
   * Validate if args are numbers otherwise throw NumberFormatException.
   *
   * @param args args to validate
   * @return array of parsed numbers if valid
   */
  private int[] parseIntegersOrThrow(final String failMessage,
                                     final String... args) {
    INTEGER_INSTANCE.setParseIntegerOnly(false);
    if (args == null || args.length == 0) {
      return null;
    }
    int[] result = new int[args.length];
    try {
      for (int i = 0; i < args.length; i++) {
        Number parsedNumber = INTEGER_INSTANCE.parse(args[i]);
        // {@line DecimalFormat} parses into Long or Double
        if (!(parsedNumber instanceof Long)) {
          shell.output(failMessage);
          return null;
        }
        result[i] = parsedNumber.intValue();
      }
    } catch (ParseException e) {
      shell.output(failMessage);
      return null;
    }
    return result;
  }
}
