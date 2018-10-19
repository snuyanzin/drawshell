package ru.nuyanzin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ru.nuyanzin.properties.DrawingShellPropertiesEnum;
import ru.nuyanzin.properties.DrawingShellProperty;

/**
 * Session options.
 */
public class DrawingShellOpts {
  public static final String PROPERTY_PREFIX = "drawingshell.";
  public static final String BASE_DIR = "x.drawingshell.basedir";
  public static final String PROPERTY_NAME_EXIT =
      PROPERTY_PREFIX + "system.exit";
  private DrawingShell shell;
  private File rcFile = new File(saveDir(), "drawingshell.properties");
  private Set<String> propertyNames;

  private final Map<DrawingShellProperty, Object> propertiesMap =
      new HashMap<>();

  public DrawingShellOpts(DrawingShell shell) {
    this.shell = shell;
  }

  public DrawingShellOpts(DrawingShell shell, Properties props) {
    this(shell);
    loadProperties(props);
  }

  /**
   * The save directory if HOME/.sqlline/ on UNIX, and HOME/sqlline/ on
   * Windows.
   *
   * @return save directory
   */
  public static File saveDir() {
    String dir = System.getProperty("sqlline.rcfile");
    if (dir != null && dir.length() > 0) {
      return new File(dir);
    }

    String baseDir = System.getProperty(BASE_DIR);
    if (baseDir != null && baseDir.length() > 0) {
      File saveDir = new File(baseDir).getAbsoluteFile();
      saveDir.mkdirs();
      return saveDir;
    }

    File f =
        new File(
            System.getProperty("user.home"),
            ((System.getProperty("os.name")
                .toLowerCase(Locale.ROOT).contains("windows"))
                ? "" : ".") + "sqlline")
            .getAbsoluteFile();
    try {
      f.mkdirs();
    } catch (Exception e) {
      // ignore
    }

    return f;
  }

  public void save() throws IOException {
    OutputStream out = new FileOutputStream(rcFile);
    save(out);
    out.close();
  }

  public void save(OutputStream out) {
    try {
      Properties props = toProperties(true);
      props.store(out, "drawingshell");
    } catch (Exception e) {
      shell.handleException(e);
    }
  }

  public Set<String> propertyNames() {
    if (propertyNames != null) {
      return propertyNames;
    }
    // properties names do not change at runtime
    // cache for further re-use
    Set<String> set = Arrays.stream(DrawingShellPropertiesEnum.values())
        .map(t -> t.propertyName().toLowerCase(Locale.ROOT))
        .collect(Collectors.toCollection(TreeSet::new));
    propertyNames = Collections.unmodifiableSet(set);
    return propertyNames;
  }

  public Properties toProperties() {
    return toProperties(false);
  }

  public Properties toProperties(boolean toSave) {
    Properties props = new Properties();

    for (DrawingShellPropertiesEnum property
        : DrawingShellPropertiesEnum.values()) {
      if (!toSave || property.couldBeStored()) {
        props.setProperty(PROPERTY_PREFIX + property.propertyName(),
            String.valueOf(
                propertiesMap.getOrDefault(property, property.defaultValue())));
      }
    }

    return props;
  }

  public void load() throws IOException {
    if (rcFile.exists()) {
      InputStream in = new FileInputStream(rcFile);
      load(in);
      in.close();
    }
  }

  public void load(InputStream fin) throws IOException {
    Properties p = new Properties();
    p.load(fin);
    loadProperties(p);
  }

  public void loadProperties(Properties props) {
    Map<String, String> mapProps = (Map) props;
    for (String key : mapProps.keySet()) {
      if (key.equals(PROPERTY_NAME_EXIT)) {
        // fix for sf.net bug 879422
        continue;
      }
      if (key.startsWith(PROPERTY_PREFIX)) {
        set(key.substring(PROPERTY_PREFIX.length()), props.getProperty(key));
      }
    }
  }

  public void set(String key, String value) {
    set(key, value, false);
  }

  public boolean set(String key, String value, boolean quiet) {
    DrawingShellProperty property =
        DrawingShellPropertiesEnum.valueOf(key, true);
    if (property != null) {
      if (property.isReadOnly()) {
        if (!quiet) {
          shell.output(Loc.getLocMessage("property-readonly", key));
        }
        return false;
      } else {
        set(property, value);
        return true;
      }
    } else {
      if (!quiet) {
        // need to use System.err here because when bad command args
        // are passed this is called before init is done, meaning
        // that sqlline's error() output chokes because it depends
        // on properties like text coloring that can get set in
        // arbitrary order.
        System.err.println(Loc.getLocMessage("unknown-prop", key));
      }
      return false;
    }
  }

  public boolean hasProperty(String name) {
    try {
      return propertyNames().contains(name);
    } catch (Exception e) {
      // this should not happen
      // since property names are retrieved
      // based on available getters in this class
      return false;
    }
  }

  public String get(DrawingShellProperty key) {
    return String.valueOf(propertiesMap.getOrDefault(key, key.defaultValue()));
  }

  public char getChar(DrawingShellProperty key) {
    if (key.type() == DrawingShellProperty.Type.CHAR) {
      return (char) propertiesMap.getOrDefault(key, key.defaultValue());
    } else {
      throw new IllegalArgumentException(
          Loc.getLocMessage("wrong-prop-type", key.propertyName(), key.type()));
    }
  }

  public int getInt(DrawingShellProperty key) {
    if (key.type() == DrawingShellProperty.Type.INTEGER) {
      return (int) propertiesMap.getOrDefault(key, key.defaultValue());
    } else {
      throw new IllegalArgumentException(
          Loc.getLocMessage("wrong-prop-type", key.propertyName(), key.type()));
    }
  }

  public boolean getBoolean(DrawingShellProperty key) {
    if (key.type() == DrawingShellProperty.Type.BOOLEAN) {
      return (boolean) propertiesMap.getOrDefault(key, key.defaultValue());
    } else {
      throw new IllegalArgumentException(
          Loc.getLocMessage("wrong-prop-type", key.propertyName(), key.type()));
    }
  }

  public String get(String key) {
    DrawingShellProperty property =
        DrawingShellPropertiesEnum.valueOf(key, true);
    return property == null
        ? null
        : String.valueOf(
            propertiesMap.getOrDefault(property, property.defaultValue()));
  }

  public void set(DrawingShellProperty key, Object value) {
    Object valueToSet = value;
    String strValue;
    switch (key.type()) {
    case STRING:
      strValue = value instanceof String
          ? (String) value : String.valueOf(value);
      valueToSet = DrawingShellProperty.DEFAULT.equalsIgnoreCase(strValue)
          ? key.defaultValue() : value;
      break;
    case INTEGER:
      try {
        valueToSet = value instanceof Integer || value.getClass() == int.class
          ? value : Integer.parseInt(String.valueOf(value));
      } catch (Exception ignored) {
      }
      break;
    case BOOLEAN:
      if (value instanceof Boolean || value.getClass() == boolean.class) {
        valueToSet = value;
      } else {
        strValue = String.valueOf(value);
        valueToSet = "true".equalsIgnoreCase(strValue)
            || (true + "").equalsIgnoreCase(strValue)
            || "1".equalsIgnoreCase(strValue)
            || "on".equalsIgnoreCase(strValue)
            || "yes".equalsIgnoreCase(strValue);
      }
      break;
    }
    propertiesMap.put(key, valueToSet);
  }

  public File getPropertiesFile() {
    return rcFile;
  }

}

// End DrawingShellOpts.java
