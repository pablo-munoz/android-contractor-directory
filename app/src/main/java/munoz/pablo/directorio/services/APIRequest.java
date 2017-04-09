package munoz.pablo.directorio.services;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

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

    private void setConnectionHeaders(HttpURLConnection connection, String headers) {
        JSONObject jsonHeaders;

        try {
           jsonHeaders = new JSONObject(headers);

            Iterator<String> headerNameIterator = jsonHeaders.keys();

            String headerName;
            while (headerNameIterator.hasNext()) {
                headerName = headerNameIterator.next();
                connection.setRequestProperty(headerName, jsonHeaders.getString(headerName));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("APIRequest", "Must pass headers as a json valid string.");
        }
    }

    @Override
    /**
     * params[0] is the HTTP method (see this class's public constants)
     * params[1] is the url (string)
     * params[2] is the headers in string form, must be valid json.
     * params[3] is the payload in string form, must be valid json.
     */
    protected JSONObject doInBackground(String... params) {
        JSONObject result = null;
        JSONObject headers = null;

        boolean hasHeaders = params.length >= 3 && params[2] != null;

        try {
            URL url = new URL(params[1]);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (hasHeaders) this.setConnectionHeaders(connection, params[2]);

            if (params[0].equals(APIRequest.HTTP_GET)) {

            } else if (params[0].equals(APIRequest.HTTP_POST)) {
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(params[3]);
                wr.flush();
            }


            connection.connect();

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
