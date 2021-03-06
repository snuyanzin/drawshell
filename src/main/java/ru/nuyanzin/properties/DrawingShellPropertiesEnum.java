package ru.nuyanzin.properties;

/**
 * Properties that may be specified for drawing shell.
 */
public enum DrawingShellPropertiesEnum implements DrawingShellProperty {

  HORIZONTAL_BORDER("hBorder", Type.CHAR, '-'),
  DEFAULT_DRAW_CHAR("defaultDrawChar", Type.CHAR, 'x'),
  DEFAULT_EMPTY_CHAR("defaultEmptyChar", Type.CHAR, ' '),
  SHOW_CANVAS_AFTER_COMMAND("showCanvasAfterCommand", Type.BOOLEAN, true),
  VERTICAL_BORDER("vBorder", Type.CHAR, '|');

  private final String propertyName;
  private final Type type;
  private final Object defaultValue;
  private final boolean isReadOnly;
  private final boolean couldBeStored;

  DrawingShellPropertiesEnum(
      String propertyName, Type type, Object defaultValue) {
    this(propertyName, type, defaultValue, true, false);
  }

  DrawingShellPropertiesEnum(
      String propertyName,
      Type type,
      Object defaultValue,
      boolean couldBeStored,
      boolean isReadOnly) {
    this.propertyName = propertyName;
    this.type = type;
    this.defaultValue = defaultValue;
    this.isReadOnly = isReadOnly;
    this.couldBeStored = couldBeStored;
  }

  @Override
  public String propertyName() {
    return propertyName;
  }

  @Override
  public Object defaultValue() {
    return defaultValue;
  }

  @Override
  public boolean isReadOnly() {
    return isReadOnly;
  }

  @Override
  public boolean couldBeStored() {
    return couldBeStored;
  }

  @Override
  public Type type() {
    return type;
  }

  public static DrawingShellProperty valueOf(
      String propertyName, boolean ignoreCase) {
    for (DrawingShellProperty property : values()) {
      if ((ignoreCase && property.propertyName().equalsIgnoreCase(propertyName))
          || property.propertyName().equals(propertyName)) {
        return property;
      }
    }
    return null;
  }
}

// End DrawingShellPropertiesEnum.java
