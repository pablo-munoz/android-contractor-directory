package munoz.pablo.directorio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by pablo on 1/28/2017.
 */

public class ContractorCategoryAdapter extends ArrayAdapter<ContractorCategory> {

    Context mContext;
    ArrayList<ContractorCategory> categoryList;

    public ContractorCategoryAdapter(Context context, ArrayList<ContractorCategory> categoryList) {
        super(context, 0, categoryList);
        this.mContext = context;
        this.categoryList = categoryList;
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
        TextView name = (TextView) convertView.findViewById(R.id.category_card_name);
        TextView count = (TextView) convertView.findViewById(R.id.category_card_num_contacts);
        ImageView img = (ImageView) convertView.findViewById(R.id.category_card_img);

        name.setText(category.getName());
        count.setText(category.getNumContacts() + " contactos");

        Glide.with(getContext())
                .load("http://i.imgur.com/ERwnaPj.png")
                .fitCenter()
                .into(img);

        return convertView;
    }
}
