package munoz.pablo.directorio.utils;

import org.json.JSONObject;

/**
 * Created by pablo on 4/18/2017.
 */

public class JSONHelper {
    /** Return the value mapped by the given key, or {@code null} if not present or null. */
    public static String optString(JSONObject json, String key)
    {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null);
    }
}
