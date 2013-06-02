package myschedule.quartz.extra.util;

import org.apache.commons.lang.text.StrSubstitutor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


/**
 * An advance string map. It's an extension of {@code java.util.Map<String, String>}
 * <p/>
 * This map should work similar to {@code java.util.Properties} but with many more advance features. We limit the storage to
 * only key=String and value=String, thus calling this a String Map, or Props. This class also provides many basic types
 * conversion getter methods for convenience.
 * <p/>
 * This map can load and auto expand any <code>${variable}</code> using existing values as lookup. The
 * System Properties values can be used to override lookup. If you were to create empty map then load properties later,
 * then you must call {@link #expandVariables()} explicitly. Once variables are expanded, there is no going back.
 * <p/>
 * For convenience, we allow user to use 'classpath' URL protocol as file input. This means you can write code such as
 * {@code new Props("classpath://my.properties")}.
 *
 * @author Zemian Deng <saltnlight5@gmail.com>
 */
public class Props extends HashMap<String, String> {
    private static final long serialVersionUID = 2211405016738281987L;

    public Props() {
    }

    public Props(String url) {
        load(url);
        expandVariables();
    }

    public Props(URL url) {
        load(url);
        expandVariables();
    }

    public Props(Properties props) {
        fromProperties(props);
        expandVariables();
    }

    public Props(Map<String, String> map) {
        putAll(map);
        expandVariables();
    }

    public List<String> getGroupKeys(String groupKey) {
        List<String> result = new ArrayList<String>();
        for (String key : keySet()) {
            if (key.startsWith(groupKey))
                result.add(key);
        }
        return result;
    }

    public String getString(String key) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("Map key not found: " + key);
        }
        String result = get(key);
        return result;
    }

    public String getString(String key, String def) {
        String result = get(key);
        if (result == null)
            result = def;
        return result;
    }

    public int getInt(String key) {
        String val = getString(key);
        int ret = Integer.parseInt(val);
        return ret;
    }

    public int getInt(String key, int def) {
        String val = getString(key, "" + def);
        int ret = Integer.parseInt(val);
        return ret;
    }

    public boolean getBoolean(String key) {
        String val = getString(key);
        return Boolean.parseBoolean(val);
    }

    public boolean getBoolean(String key, boolean def) {
        String val = getString(key, "" + def);
        return Boolean.parseBoolean(val);
    }

    public long getLong(String key) {
        String val = getString(key);
        long ret = Long.parseLong(val);
        return ret;
    }

    public long getLong(String key, long def) {
        String val = getString(key, "" + def);
        long ret = Long.parseLong(val);
        return ret;
    }

    public double getDouble(String key) {
        String val = getString(key);
        double ret = Double.parseDouble(val);
        return ret;
    }

    public double getDouble(String key, double def) {
        String val = getString(key, "" + def);
        double ret = Double.parseDouble(val);
        return ret;
    }

    public void load(String url) {
        URL urlObj = ClasspathURLStreamHandler.createURL(url);
        load(urlObj);
    }

    public void load(URL url) {
        try {
            load(url.openStream()); // load() should call close on stream.
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to open " + url, e);
        }
    }

    public void load(InputStream inStream) {
        Properties props = null;
        try {
            props = new Properties();
            props.load(inStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to close input stream", e);
                }
            }
        }
        fromProperties(props);
    }

    /**
     * Auto expand any ${variable} in expandProps using a lookupProps for existing variable definitions. This method
     * will automatically search the System Properties space for lookup as well.
     * <p/>
     * Note: There is no going back after you call this method!
     */
    public void expandVariables() {
        // We will allow System Properties for override when doing lookup.
        Props lookupProps = new Props();
        lookupProps.putAll(this);
        lookupProps.fromProperties(System.getProperties());

        StrSubstitutor substitutor = new StrSubstitutor(lookupProps);
        for (Map.Entry<String, String> entry : entrySet()) {
            String name = entry.getKey();
            String val = entry.getValue();
            if (val == null)
                continue;
            String newVal = substitutor.replace(val);
            if (!newVal.equals(val)) {
                put(name, newVal);
            }
        }
    }

    public void fromProperties(Properties props) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, String> map = (Map) props;
        super.putAll(map);
    }

    /**
     * Clone this map and return it as java.util.Properties
     */
    @SuppressWarnings("unchecked")
    public Properties toProperties() {
        Properties properties = new Properties();
        properties.putAll((Map<String, String>) this.clone());
        return properties;
    }
}
