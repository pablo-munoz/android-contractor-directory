package models;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by pablo on 2/14/2017.
 */

public class ModelBuilder {

    public static Object fromJson(Type modelType, JSONObject json) throws JSONException {
        Object modelInstance = null;

        if (modelType == ContractorCategory.class) {
            modelInstance = new ContractorCategory(
                    json.getString("id"),
                    json.getString("name"),
                    json.getString("short_name"),
                    json.getString("img")
            );
        }

        return modelInstance;
    }

}
