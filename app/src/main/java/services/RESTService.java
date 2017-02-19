package services;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    public void get(String url, final RESTCallback callback) {
        this.makeRequest(url)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure("Error");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String rawResponse = response.body().string();

                        JSONObject responseJson = null;
                        try {
                            responseJson = new JSONObject(rawResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        callback.onSuccess(responseJson);
                    }
                });
    }

    private Call makeRequest(String url) {
        Request httpRequest = new Request.Builder().url(url).build();
        return this.httpClient.newCall(httpRequest);
    }
}
