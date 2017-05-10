package munoz.pablo.directorio.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import munoz.pablo.directorio.models.ContractorCategory;
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
        ImageView categoryImg = (ImageView) convertView.findViewById(R.id.category_image);

        nameTv.setText(category.getName().toUpperCase());

        if(category.getShortName().equals("elec")){
            categoryImg.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.electricity_icon));
        }
        else if(category.getShortName().equals("acc")){
            categoryImg.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.blacksmith_icon));
        }
        else if(category.getShortName().equals("paint") ){
            categoryImg.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.paint_icon));
        }
        else if(category.getShortName().equals("alb")){
            categoryImg.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wood_icon));
        }

        return convertView;
    }
}
