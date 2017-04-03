package adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import munoz.pablo.directorio.R;

/**
 * Created by pablo on 4/2/2017.
 */

public class JSONArrayAdapter extends BaseAdapter {
    private Activity activity;
    private JSONArray data;
    private ViewBuilder viewBuilder;

    public JSONArrayAdapter(Activity activity, JSONArray data, ViewBuilder viewBuilder) {
        this.activity = activity;
        this.data = data;
        this.viewBuilder = viewBuilder;
    }

    @Override
    public int getCount() {
        return this.data.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return this.data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.viewBuilder.construct(this.data, position, convertView, parent);
    }

    public interface ViewBuilder {
        View construct(JSONArray data, int position, View view, ViewGroup parent);
    }
}
