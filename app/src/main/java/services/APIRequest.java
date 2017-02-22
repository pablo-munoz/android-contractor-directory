package services;


import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pablo on 2/14/2017.
 */

public class APIRequest extends AsyncTask<String, Void, JSONObject> {

    private APIRequestCallback listener;
    private boolean hasErrors;
    private int code;

    public APIRequest(APIRequestCallback listener) {
        this.hasErrors = false;
        this.listener = listener;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result = null;
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            this.code = connection.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK ||
                    code == HttpURLConnection.HTTP_CREATED) {
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String currentLine = "";

                while((currentLine = br.readLine()) != null) {
                    sb.append(currentLine);
                }

                result = new JSONObject(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.hasErrors = true;
        } catch (JSONException e) {
            e.printStackTrace();
            this.hasErrors = true;
        }

        return result;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        if (this.hasErrors) {
            this.listener.onError("Error fetching data", code);
        } else {
            this.listener.onSuccess(jsonObject, code);
        }
    }

    public interface APIRequestCallback {
        public void onSuccess(JSONObject json, int code);
        public void onError(String errorMessage, int code);
    }
}
