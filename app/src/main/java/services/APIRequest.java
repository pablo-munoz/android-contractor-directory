package services;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by pablo on 2/14/2017.
 */

public class APIRequest extends AsyncTask<String, Void, JSONObject> {
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";

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
            URL url = new URL(params[1]);

            if (params[0].equals(APIRequest.HTTP_GET)) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                this.code = connection.getResponseCode();

                if (code == HttpURLConnection.HTTP_OK ||
                        code == HttpURLConnection.HTTP_CREATED) {
                    InputStream is = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String currentLine = "";

                    while ((currentLine = br.readLine()) != null) {
                        sb.append(currentLine);
                    }

                    result = new JSONObject(sb.toString());
                } else {
                    this.hasErrors = true;
                }
            } else if (params[0].equals(APIRequest.HTTP_POST)) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(params[2]);
                wr.flush();

                this.code = connection.getResponseCode();
                Log.d("CODE", ""+this.code);

                if (this.code == HttpURLConnection.HTTP_OK ||
                        this.code == HttpURLConnection.HTTP_CREATED) {
                                        InputStream is = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String currentLine = "";

                    while ((currentLine = br.readLine()) != null) {
                        sb.append(currentLine);
                    }

                    result = new JSONObject(sb.toString());
                } else {
                    this.hasErrors = true;
                }
            }
        } catch(MalformedURLException e) {
            e.printStackTrace();
            Log.e("APIRequest", "Malformed url");
            this.hasErrors = true;
        } catch(IOException e) {
            e.printStackTrace();
            this.hasErrors = true;
        } catch(JSONException e) {
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
