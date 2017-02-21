package services;

import org.json.JSONObject;

/**
 * Created by pablo on 2/14/2017.
 */

public interface RESTCallback {

    void onSuccess(JSONObject responseJson);
    void onFailure(String rawResponse);

}
