package services;

/**
 * Created by pablo on 2/14/2017.
 */

public interface RESTCallback {

    void onSuccess(Object modelInstance, String rawResponse);
    void onFailure(String rawResponse);

}
