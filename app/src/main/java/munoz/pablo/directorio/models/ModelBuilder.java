package munoz.pablo.directorio.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pablo on 2/14/2017.
 *
 * Important notes:
 * Rather than using JSONObject's getString method use this class static method
 * getJSONString as getString parses null as the string "null" and not the value null.
 *
 */

public class ModelBuilder<ModelType>  {

    public ModelType resourceFromJson(JSONObject apiJson) throws JSONException {
        JSONObject data;
        JSONObject attributes;

        if (apiJson == null) {
            throw new JSONException("Given json cannot be null.");
        } else if ((data = apiJson.getJSONObject("data")) == null) {
            throw new JSONException("Malformed json, 'data' property missing.");
        } else if ((attributes = data.getJSONObject("attributes")) == null) {
            throw new JSONException("Malformed json, 'data.attributes' property missing.");
        }

        if (!data.has("id")) {
            throw new JSONException("Incorrect json, 'data.id' value missing.");
        }

        attributes.put("id", data.getString("id"));

        ModelType modelInstance = null;
        String modelType = data.getString("type");

        JSONObject relationships = null;
        try {
            relationships = apiJson.getJSONObject("relationships");
        } catch(JSONException e) {
            e.printStackTrace();
        }

        modelInstance =  this.instantiateModel(modelType, attributes, relationships);

        return modelInstance;
    }


    public ArrayList<ModelType> resourceListFromJson(JSONObject apiJson) throws JSONException {
        JSONArray data;
        JSONObject datum;
        JSONObject attributes;
        String instanceId;

        if (apiJson == null) {
            throw new JSONException("Given json cannot be null.");
        } else if ((data = apiJson.getJSONArray("data")) == null) {
            throw new JSONException("Malformed json, 'data' property missing.");
        }

        ArrayList<ModelType> modelInstanceList = new ArrayList<>();
        String modelType;

        for (int i = 0; i < data.length(); i++) {
            if ((datum = data.getJSONObject(i)) == null) {
                throw new JSONException("Incorrect json, element of data doesn't is not a json object.");
            } else if ((attributes = datum.getJSONObject("attributes")) == null) {
                throw new JSONException("Incorrect json, element of datum 'attributes' property.");
            } else if ((instanceId = datum.getString("id")) == null) {
                throw new JSONException("Incorrect json, element of data doesn't have 'id' property.");
            }

            attributes.put("id", instanceId);
            modelType = datum.getString("type");

            JSONObject relationships = null;
            try {
                relationships = apiJson.getJSONObject("relationships");
            } catch(JSONException e) {
                e.printStackTrace();
            }

            modelInstanceList.add(this.instantiateModel(modelType, attributes, relationships));
        }

        return modelInstanceList;
    }


    private ModelType instantiateModel(String modelType, JSONObject resourceAttributes, JSONObject relationships)
            throws JSONException {
        Object modelInstance;

        if (modelType.equals("contractor_category")) {
            modelInstance = new ContractorCategory(
                    ModelBuilder.getJSONString(resourceAttributes, "id"),
                    ModelBuilder.getJSONString(resourceAttributes, "name"),
                    ModelBuilder.getJSONString(resourceAttributes, "short_name"),
                    ModelBuilder.getJSONString(resourceAttributes, "img")
            );
        }

        else if (modelType.equals("contractor")) {
            JSONArray comments = new JSONArray("[]");

            try {
                comments = relationships.getJSONObject("comment").getJSONArray("data");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            modelInstance = new Contractor(
                    ModelBuilder.getJSONString(resourceAttributes, "id"),
                    ModelBuilder.getJSONString(resourceAttributes, "first_name"),
                    ModelBuilder.getJSONString(resourceAttributes, "middle_name"),
                    ModelBuilder.getJSONString(resourceAttributes, "last_names"),
                    ModelBuilder.getJSONString(resourceAttributes, "email"),
                    ModelBuilder.getJSONString(resourceAttributes, "phone"),
                    ModelBuilder.getJSONString(resourceAttributes, "website"),
                    "http://ewic.org/wp-content/themes/ewic/images/Construction%20Worker.png",
                    resourceAttributes.getDouble("avg_rating"),
                    ModelBuilder.getJSONString(resourceAttributes, "account_id")
            );
        } else {
            throw new JSONException("I don't know how to parse the given model type.");
        }

        return (ModelType) modelInstance;
    }

    private static String getJSONString(JSONObject json, String fieldName) throws JSONException {
        if (json == null) return null;

        String value = json.getString(fieldName);
        if (value == "null") {
            return null;
        } else {
            return value;
        }
    }


}
