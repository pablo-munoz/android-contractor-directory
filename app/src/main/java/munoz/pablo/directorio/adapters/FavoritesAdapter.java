package munoz.pablo.directorio.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

import munoz.pablo.directorio.R;
import munoz.pablo.directorio.models.Contractor;

/**
 * Created by pablo on 4/9/2017.
 */

public class FavoritesAdapter extends BaseAdapter {
    private ArrayList<Contractor> data;
    private Activity activity;

    public FavoritesAdapter(ArrayList<Contractor> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Contractor getItem(int position) {
        return data.get(position);
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

        data.get(position).populateContractorCard(convertView);

        return convertView;
    }
}
