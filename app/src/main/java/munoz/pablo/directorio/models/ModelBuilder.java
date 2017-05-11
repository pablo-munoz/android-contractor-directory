package munoz.pablo.directorio.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import munoz.pablo.directorio.utils.JSONHelper;

/**
 * Created by pablo on 2/14/2017.
 *
 * Important notes:
 * Rather than using JSONObject's getString method use this class static method
 * getJSONString as getString parses null as the string "null" and not the value null.
 *
 */

public class ModelBuilder<ModelType>  {

    public ArrayList<ModelType> instantiateMany(JSONObject apiResponseJson) {
        ArrayList<ModelType> instances = new ArrayList<>();

        try {
            JSONArray data = apiResponseJson.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject datum = data.getJSONObject(i);
                String type = datum.getString("type");
                String id = datum.getString("id");
                JSONObject attributes = datum.getJSONObject("attributes");

                JSONObject dummyRelationships = new JSONObject();
                dummyRelationships.put("comment", new JSONObject());
                dummyRelationships.getJSONObject("comment").put("data", new JSONArray());

                ModelType nextInstance = instantiate(type, id, attributes, dummyRelationships);
                instances.add(nextInstance);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return instances;
    }

    public ModelType instantiateOne(JSONObject apiResponseJson) {
        JSONObject data = null;
        JSONObject relationships = null;
        JSONObject attributes = null;
        String type;
        String id;

        try {
            data = apiResponseJson.getJSONObject("data");

            if (apiResponseJson.has("relationships"))
                relationships = apiResponseJson.getJSONObject("relationships");

            attributes = data.getJSONObject("attributes");
            type = data.getString("type");
            id = data.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return instantiate(type, id, attributes, relationships);
    }

    private ModelType instantiate(String type, String id, JSONObject attributes, JSONObject relationships) {
         Object instance = null;

        try {
            switch (type) {
                case "contractor_category":
                    instance = new ContractorCategory(
                            id,
                            attributes.getString("name"),
                            attributes.getString("short_name"),
                            attributes.getString("img"),
                            attributes.getInt("count")
                    );
                    break;

                case "contractor":
                    instance = new Contractor(
                            id,
                            attributes.getString("first_name"),
                            JSONHelper.optString(attributes, "middle_name"),
                            attributes.getString("last_names"),
                            JSONHelper.optString(attributes, "email"),
                            JSONHelper.optString(attributes, "phone"),
                            JSONHelper.optString(attributes, "website"),
                            JSONHelper.optString(attributes, "portrait"),
                            attributes.getDouble("avg_rating"),
                            attributes.getString("account_id"),
                            relationships.getJSONObject("comment").getJSONArray("data"),
                            JSONHelper.optString(attributes, "address")
                    );
                    break;

                default:
                    Log.d("ModelBuilder", "Model builder received an unknown resrouce type: " + type);
                    return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return (ModelType) instance;
    }

}
