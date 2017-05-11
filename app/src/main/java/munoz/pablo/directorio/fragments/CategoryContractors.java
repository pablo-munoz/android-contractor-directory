package munoz.pablo.directorio.fragments;


import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import munoz.pablo.directorio.activities.MainActivity;
import munoz.pablo.directorio.adapters.ContractorAdapter;
import munoz.pablo.directorio.models.Contractor;
import munoz.pablo.directorio.models.ContractorCategory;
import munoz.pablo.directorio.models.ModelBuilder;
import munoz.pablo.directorio.R;
import munoz.pablo.directorio.services.APIRequest2;
import munoz.pablo.directorio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryContractors#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryContractors extends Fragment {
    private static final String ARG_contractorCategoryId = "contractorCategoryId";

    private String contractorCategoryId;

    private ContractorCategory contractorCategory;
    private ArrayList<Contractor> contractorList;
    private ListView contractorListView;
    private ContractorAdapter adapter;
    private ModelBuilder<ContractorCategory> categoryModelBuilder;
    private ModelBuilder<Contractor> contractorModelBuilder;

    private ProgressBar progressBar;
    private TextView titleTv;
    private TextView emptyQueryTv;

    public CategoryContractors() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contractorCategoryId The id of a contractor category.
     */
    public static CategoryContractors newInstance(String contractorCategoryId) {
        CategoryContractors fragment = new CategoryContractors();
        Bundle args = new Bundle();
        args.putString(ARG_contractorCategoryId, contractorCategoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contractorCategoryId = getArguments().getString(ARG_contractorCategoryId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_contractors, container, false);

        titleTv = (TextView) view.findViewById(R.id.category_contractors_title);
        emptyQueryTv = (TextView) view.findViewById(R.id.category_contractors_empty_query_label);
        progressBar = (ProgressBar) view.findViewById(R.id.category_contractors_loading);
        contractorListView = (ListView) view.findViewById(R.id.category_contractors_list_view);

        contractorList = new ArrayList<>();
        categoryModelBuilder = new ModelBuilder<>();
        contractorModelBuilder = new ModelBuilder<>();

        adapter = new ContractorAdapter(view.getContext(), contractorList);

        contractorListView.setAdapter(this.adapter);

        contractorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contractor contractor = contractorList.get(position);
                ContractorDetail newFragment = ContractorDetail.newInstance(contractor.getId());

                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.changeContentFragment(newFragment);
            }
        });

        requestCategoryDataFromApi();
        //Toast.makeText(this, ""+contractorCategory.getShortName(), Toast.LENGTH_SHORT).show();



        return view;
    }

    private void requestCategoryDataFromApi() {
        APIRequest2 req = new APIRequest2.Builder()
                .url(Constants.API_URL + "/api/v1/contractor_category/" + contractorCategoryId)
                .method(APIRequest2.METHOD_GET)
                .callback(new APIRequest2.Callback() {
                    @Override
                    public void onResult(int responseCode, JSONObject response) {
                        if (responseCode == 200) {
                            try {
                                response.getJSONObject("data").getJSONObject("attributes").put("count", 0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            contractorCategory = categoryModelBuilder.instantiateOne(response);

                            if (contractorCategory != null) {
                                titleTv.setText(contractorCategory.getName());
                                requestContractorInCategoryDataFromApi();
                            }
                        }
                    }
                })
                .build();

        req.execute();
    }

    private void requestContractorInCategoryDataFromApi() {
        APIRequest2 req = new APIRequest2.Builder()
                .url(Constants.API_URL + "/api/v1/contractor?contractor_category=" + contractorCategory.getId())
                .method(APIRequest2.METHOD_GET)
                .callback(new APIRequest2.Callback() {
                    @Override
                    public void onResult(int responseCode, JSONObject response) {
                        if (responseCode == 200) {
                            int count = 0;
                            try {
                                count = response.getJSONObject("meta").getInt("count");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (count != 0) {
                                contractorList = contractorModelBuilder.instantiateMany(response);
                                adapter.addAll(contractorList);
                            } else {
                                emptyQueryTv.setText("No se ha encontrado nada.");
                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .build();

        req.execute();
    }
}
