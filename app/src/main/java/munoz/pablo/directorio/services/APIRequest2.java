package munoz.pablo.directorio.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by pablo on 4/16/2017.
 */

public class APIRequest2 extends AsyncTask<String, Void, JSONObject> {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    private String url;
    private String method;
    private String headers;
    private String payload;
    private APIRequest2.Callback callback;
    private int responseCode;

    public APIRequest2(
            String url,
            String method,
            String headers,
            String payload,
            APIRequest2.Callback callback) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.payload = payload;
        this.callback = callback;
        this.responseCode = -1;
    }

    public static class Builder {
        private String url;
        private String method;
        private String headers;
        private String payload;
        private APIRequest2.Callback callback;

        public Builder() {
            this.url = null;
            this.method = METHOD_GET;
            this.headers = "";
            this.payload = "";
            this.callback = null;
        }

        public APIRequest2.Builder url(String url) {
            this.url = url;
            return this;
        }

        public APIRequest2.Builder method(String method) {
            this.method = method;
            return this;
        }

        public APIRequest2.Builder headers(String headers) {
            this.headers = headers;
            return this;
        }

        public APIRequest2.Builder headers(JSONObject headers) {
            this.headers = headers.toString();
            return this;
        }

        public APIRequest2.Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public APIRequest2.Builder payload(JSONObject payload) {
            this.payload = payload.toString();
            return this;
        }

        public APIRequest2.Builder callback(APIRequest2.Callback callback) {
            this.callback = callback;
            return this;
        }

        public APIRequest2 build() {
            return new APIRequest2(
                    this.url,
                    this.method,
                    this.headers,
                    this.payload,
                    this.callback);
        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        Log.d("ApiRequest2", "Entered doInBackground()...");
        Log.d("ApiRequest2", "url: " + this.url);
        Log.d("ApiRequest2", "method: " + this.method);
        Log.d("ApiRequest2", "headers: " + this.headers);
        Log.d("ApiRequest2", "payload: " + this.payload);

        URL endpoint = null;
        HttpURLConnection httpUrlConn = null;

        try {
            endpoint = new URL(this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            httpUrlConn = (HttpURLConnection) endpoint.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Log.d("APIRequest2", "Instantiated connection");

        try {
            httpUrlConn.setRequestMethod(this.method);
        } catch (ProtocolException e) {
            Log.d("APIRequest2", "Set request method.");
            e.printStackTrace();
            return null;
        }

        boolean hasHeaders = this.headers.length() > 0;

        if (hasHeaders) {
            Log.d("APIRequest2", "Set headers");
            JSONObject jsonHeaders = null;
            try {
                jsonHeaders = new JSONObject(this.headers);
                Iterator<String> headerNames = jsonHeaders.keys();
                String key = null;
                while ( headerNames.hasNext() ) {
                    key = headerNames.next();
                    httpUrlConn.setRequestProperty(key, jsonHeaders.getString(key));
                }
            } catch (JSONException e) {
                Log.d("APIRequest2", "Error setting headers.");
                e.printStackTrace();
                return null;
            }
        }

        boolean requestSendsData = this.payload.length() > 0;

        httpUrlConn.setDoInput(true);

        if (requestSendsData) {
            Log.d("APIRequest2", "Sent data.");
            httpUrlConn.setDoOutput(true);
            try {
                OutputStreamWriter wr = new OutputStreamWriter(httpUrlConn.getOutputStream());
                wr.write(this.payload);
                wr.flush();
            } catch (IOException e) {
                Log.e("APIRequest2", "Error writing input.");
                e.printStackTrace();
                return null;
            }
        }

        JSONObject responseJson = null;

        try {
            httpUrlConn.connect();
            responseCode = httpUrlConn.getResponseCode();
            Log.d("Response code", ""+responseCode);

            String line = null;
            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConn.getInputStream()));

            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

            responseJson = new JSONObject(stringBuilder.toString());

        } catch (IOException e) {
            Log.e("APIRequest2", "Error recieving input.");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.e("APIRequest2", "Error parsing response json.");
            e.printStackTrace();
            return null;
        }

        httpUrlConn.disconnect();

        return responseJson;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);

        if (this.callback != null) {
            this.callback.onResult(responseCode, jsonObject);
        }
    }

    public interface Callback {
        void onResult(int responseCode, JSONObject response);
    }

    public void execute() {
        super.execute("");
    }
}
