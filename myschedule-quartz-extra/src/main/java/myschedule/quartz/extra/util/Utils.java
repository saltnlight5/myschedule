package myschedule.quartz.extra.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A general utilities class.
 *
 * @author Zemian Deng
 */
public class Utils {
    /**
     * Convert a pair of key and value array of objects into map.
     */
    public static Map<String, Object> toMap(Object... pairs) {
        if (pairs.length % 2 != 0)
            throw new IllegalArgumentException("Can not create map object because input parameters are not even " +
                    "(key, value) pairs.");

        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0, max = pairs.length; i < max; i++) {
            Object key = pairs[i];
            Object value = pairs[++i];
            result.put(key.toString(), value);
        }
        return result;
    }
}
