package munoz.pablo.directorio.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import munoz.pablo.directorio.activities.MainActivity;
import munoz.pablo.directorio.adapters.ContractorAdapter;
import munoz.pablo.directorio.models.Contractor;
import munoz.pablo.directorio.models.ContractorCategory;
import munoz.pablo.directorio.models.ModelBuilder;
import munoz.pablo.directorio.R;
import munoz.pablo.directorio.services.APIRequest;
import munoz.pablo.directorio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryContractors#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryContractors extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_contractorCategoryId = "contractorCategoryId";

    // Fragment parameters
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
    // TODO: Rename and change types and number of parameters
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

        contractorList = new ArrayList<>();
        categoryModelBuilder = new ModelBuilder<>();
        contractorModelBuilder = new ModelBuilder<>();

        adapter = new ContractorAdapter(view.getContext(), contractorList);

        contractorListView = (ListView) view.findViewById(R.id.category_contractors_list_view);
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

        return view;
    }

    private void requestCategoryDataFromApi() {
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                try {
                    contractorCategory = categoryModelBuilder.resourceFromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (contractorCategory != null) {
                    titleTv.setText(contractorCategory.getName());
                    pullContractorsInCategoryData();
                }
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.e("ContractorsByCategory", "Error loading contractor category data.");
            }
        });

        apiRequest.execute(APIRequest.HTTP_GET,
                Constants.API_URL + "/api/v1/contractor_category/" + contractorCategoryId);
    }

    private void pullContractorsInCategoryData() {
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                try {
                    int count = json.getJSONObject("meta").getInt("count");

                    if (count != 0) {
                        contractorList = contractorModelBuilder.resourceListFromJson(json);
                        adapter.addAll(contractorList);
                    } else {
                        emptyQueryTv.setText("No se ha encontrado nada.");
                    }

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.e("ContractorsByCategory", "Error loading contractor data.");
            }
        });

        apiRequest.execute(APIRequest.HTTP_GET, Constants.API_URL + "/api/v1/contractor?contractor_category=" + contractorCategory.getId());
    }
}
