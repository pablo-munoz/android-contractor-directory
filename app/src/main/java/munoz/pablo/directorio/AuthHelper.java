package munoz.pablo.directorio;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import munoz.pablo.directorio.R;

/**
 * Created by pablo on 3/20/2017.
 */

public class AuthHelper {

    public static String getAuthToken(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String token = sharedPref.getString(activity.getString(R.string.shared_preferences_token_attr_name), null);
        return token;
    }

}
