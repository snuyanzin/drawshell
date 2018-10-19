package ru.nuyanzin.properties;

/**
 * Definition of property that may be specified for Drawing shell.
 */
public interface DrawingShellProperty {
  String DEFAULT = "default";
  String propertyName();

  Object defaultValue();

  boolean isReadOnly();

  boolean couldBeStored();

  Type type();

  /**
   * Property writer.
   */
  @FunctionalInterface
  interface DrawingShellPropertyWrite {
    void write(String value);
  }

  /** Data type of property. */
  enum Type {
    BOOLEAN,
    CHAR,
    STRING,
    INTEGER;
  }

}

// End DrawingShellProperty.java
