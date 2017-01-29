package munoz.pablo.directorio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pablo on 1/29/2017.
 */

public class ContractorAdapter extends ArrayAdapter<Contractor> {
    private Context mContext;
    private ArrayList<Contractor> contractorList;

    public ContractorAdapter(Context context, ArrayList<Contractor> contractorList) {
        super(context, 0, contractorList);
        this.mContext = context;
        this.contractorList = contractorList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get data item for this position
        Contractor contractor = contractorList.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contractor_card, parent, false);
        }

        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.contractor_card_name);
        LinearLayout rating = (LinearLayout) convertView.findViewById(R.id.contractor_card_rating);

        // Draw as many filled stars as the contractor rating
        for (int i = 0; i < contractor.getRating(); i++) {
            ImageView starIcon = new ImageView(this.mContext);
            starIcon.setImageResource(R.drawable.ic_star_black_24dp);
            rating.addView(starIcon);
        }

        // Draw any possible remaining stars blank
        for (int i = 0; i < 5 - contractor.getRating(); i++) {
            ImageView starIcon = new ImageView(this.mContext);
            starIcon.setImageResource(R.drawable.ic_star_border_black_24dp);
            rating.addView(starIcon);
        }

        name.setText(contractor.getFullName());

        return convertView;
    }
}
