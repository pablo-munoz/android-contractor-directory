package services;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import models.ModelBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pablo on 2/14/2017.
 */

public class RESTService {
    private OkHttpClient httpClient;


    public RESTService() {
        httpClient = new OkHttpClient();
    }

    public void get(final Type modelType, String id, final RESTCallback callback) {
        this.makeRequest("http://192.168.33.10:3000/api/v1/contractor_category/" + id)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure("Error");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String rawResponse = response.body().string();

                        JSONObject responseJson;
                        JSONObject resourceData;
                        Object modelInstance = null;
                        try {
                            responseJson = new JSONObject(rawResponse);
                            // The data pertaining to the resource will always be located
                            // in the top-level *data* attribute per jsonapi specification.
                            resourceData = responseJson.getJSONObject("data");

                            modelInstance = RESTService.this.deserializeModel(modelType, resourceData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        callback.onSuccess(modelInstance, rawResponse);
                    }
                });
    }

    public void getMany(final Type modelType, final RESTCallback callback) {
        this.makeRequest("http://192.168.33.10:3000/api/v1/contractor_category")
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure("Error");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String rawResponse = response.body().string();

                        JSONObject responseJson;
                        JSONArray resourceDataList;
                        ArrayList<Object> modelInstanceList = new ArrayList<Object>();
                        try {
                            responseJson = new JSONObject(rawResponse);
                            // The data pertaining to the resource list will always be located
                            // in the top-level *data* attribute per jsonapi specification.
                            resourceDataList = responseJson.getJSONArray("data");

                            for (int i = 0; i < resourceDataList.length(); i++) {
                                JSONObject resourceData = resourceDataList.getJSONObject(i);
                                modelInstanceList.add(
                                        RESTService.this.deserializeModel(modelType, resourceData)
                                );
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        callback.onSuccess(modelInstanceList, rawResponse);
                    }
                });
    }

    private Call makeRequest(String url) {
        Request httpRequest = new Request.Builder().url(url).build();
        return this.httpClient.newCall(httpRequest);
    }

    private Object deserializeModel(Type modelType, JSONObject jsonData) throws JSONException {
        Object modelInstance = null;

        // Each fields' data is located in the *attributes* property of the
        // data object of the response. With the exception of *id* which is
        // a child of *data* itself.
        JSONObject resourceAttributes = jsonData.getJSONObject("attributes");
        resourceAttributes.put("id", jsonData.getString("id"));
        modelInstance = ModelBuilder.fromJson(modelType, resourceAttributes);

        return modelInstance;
    }

}
