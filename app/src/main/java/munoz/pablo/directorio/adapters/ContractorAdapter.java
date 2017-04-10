package munoz.pablo.directorio.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import munoz.pablo.directorio.models.Contractor;
import munoz.pablo.directorio.R;

/**
 * Created by pablo on 1/29/2017.
 */

public class ContractorAdapter extends ArrayAdapter<Contractor> {
    private Context mContext;

    public ContractorAdapter(Context context, ArrayList<Contractor> contractorList) {
        super(context, 0, contractorList);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contractor_card, parent, false);
        }

        // Get data item for this position
        Contractor contractor = this.getItem(position);
        contractor.populateContractorCard(convertView);

        return convertView;
    }
}
