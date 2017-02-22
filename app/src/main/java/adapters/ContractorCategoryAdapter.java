package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import models.ContractorCategory;
import munoz.pablo.directorio.R;

/**
 * Created by pablo on 1/28/2017.
 */

public class ContractorCategoryAdapter extends ArrayAdapter<ContractorCategory> {

    Context mContext;

    public ContractorCategoryAdapter(Context context, ArrayList<ContractorCategory> categoryList) {
        super(context, 0, categoryList);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get data item for this position
        ContractorCategory category = this.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_card, parent, false);
        }

        // Lookup view for data population
        TextView nameTv = (TextView) convertView.findViewById(R.id.category_card_name);
        TextView countTv = (TextView) convertView.findViewById(R.id.category_card_num_contacts);
        ImageView categoryIv = (ImageView) convertView.findViewById(R.id.category_card_img);

        nameTv.setText(category.getName());

        Glide.with(getContext())
                .load(category.getImg())
                .fitCenter()
                .crossFade(100)
                .into(categoryIv);

        return convertView;
    }
}
