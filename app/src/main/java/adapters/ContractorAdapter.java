package adapters;

import android.content.Context;
import android.media.Rating;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import models.Contractor;
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
        // Get data item for this position
        Contractor contractor = this.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contractor_card, parent, false);
        }

        // Lookup view for data population
        TextView nameTv = (TextView) convertView.findViewById(R.id.contractor_card_name);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.contractor_card_rating);

        nameTv.setText(contractor.getFullName());

        ratingBar.setRating(contractor.getRating());

        TextView phoneTv = (TextView) convertView.findViewById(R.id.contractor_card_phone);
        phoneTv.setText(contractor.getPhone());

        TextView emailTv = (TextView) convertView.findViewById(R.id.contractor_card_email);
        emailTv.setText(contractor.getEmail());

        TextView websiteTv = (TextView) convertView.findViewById(R.id.contractor_card_website);
        websiteTv.setText(contractor.getWebsite());

        return convertView;
    }
}
