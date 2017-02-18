package models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by pablo on 2/14/2017.
 */

public class ModelBuilder {

    public static Object resourceFromJson(Type modelType, JSONObject responseJson) throws JSONException {
        if (responseJson == null) {
            Log.e("ModelBuilder", "ResponseJson argument cannot be null.");
            return null;
        }

        Object modelInstance = null;

        JSONObject resourceAttributes = ModelBuilder.parseResourceAttributes(responseJson);

        if (resourceAttributes == null) {
            Log.e("ModelBuilder", "Parsed resource attributes turned out to be null.");
        }

        modelInstance = ModelBuilder.instantiateModel(ContractorCategory.class, resourceAttributes);

        return modelInstance;
    }


    public static ArrayList<Object> resourceListFromJson(Type modelType, JSONObject responseJson)
            throws JSONException {
        if (responseJson == null) {
            Log.e("ModelBuilder", "ResponseJson argument cannot be null.");
            return null;
        }

        JSONArray resourceDataList;
        ArrayList<Object> modelInstanceList = new ArrayList<Object>();

        try {
            // The data pertaining to the resource list will always be located
            // in the top-level *data* attribute per jsonapi specification.
            resourceDataList = responseJson.getJSONArray("data");

            for (int i = 0; i < resourceDataList.length(); i++) {
                JSONObject resourceData = resourceDataList.getJSONObject(i);
                JSONObject resourceAttributes = ModelBuilder.parseResourceAttributes(resourceData);
                modelInstanceList.add(
                        ModelBuilder.instantiateModel(modelType, resourceAttributes)
                );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return modelInstanceList;
    }

    private static JSONObject parseResourceAttributes(JSONObject json) throws JSONException {
        JSONObject resourceData = null;
        JSONObject resourceAttributes = null;

        try {
            // The data pertaining to the resource will always be located
            // in the top-level *data* attribute per jsonapi specification.
            if (json.has("data")) {
                resourceData = json.getJSONObject("data");
            } else {
                resourceData = json;
            }

            // Each fields' data is located in the *attributes* property of the
            // data object of the response. With the exception of *id* which is
            // a child of *data* itself.
            resourceAttributes = resourceData.getJSONObject("attributes");
            resourceAttributes.put("id", resourceData.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resourceAttributes;
    }

    private static Object instantiateModel(Type modelType, JSONObject resourceAttributes) {
        Object modelInstance = null;

        if (modelType == ContractorCategory.class) {
            try {
                modelInstance = new ContractorCategory(
                        resourceAttributes.getString("id"),
                        resourceAttributes.getString("name"),
                        resourceAttributes.getString("short_name"),
                        resourceAttributes.getString("img")
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return modelInstance;
    }

}
