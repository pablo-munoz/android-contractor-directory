package munoz.pablo.directorio.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import munoz.pablo.directorio.R;
import munoz.pablo.directorio.models.Account;

/**
 * Created by pablo on 4/9/2017.
 */

public class FavoritesAdapter extends BaseAdapter {
    private JSONArray data;
    private Activity activity;

    public FavoritesAdapter(JSONArray data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.contractor_card, parent, false);
        }

        JSONObject contractorData = null;
        try {
            contractorData = data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (contractorData != null) {
            TextView nameTv = (TextView) convertView.findViewById(R.id.contractor_card_name);
            try {
                nameTv.setText(contractorData.getString("first_name") + " " + contractorData.getString("last_names"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }
}
