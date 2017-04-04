package fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import activities.Constants;
import activities.MainActivity;
import adapters.ContractorCategoryAdapter;
import models.ContractorCategory;
import models.ModelBuilder;
import munoz.pablo.directorio.R;
import services.APIRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContractorCategoryMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContractorCategoryMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ContractorCategoryAdapter categoriesAdapter;
    private ArrayList<ContractorCategory> contractorCategoryList;
    private ListView listView;
    private ModelBuilder<ContractorCategory> modelBuilder;
    private ViewFlipper viewFlipper;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ContractorCategoryMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContractorCategoryMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static ContractorCategoryMenu newInstance(String param1, String param2) {
        ContractorCategoryMenu fragment = new ContractorCategoryMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_contractor_category_menu, container, false);

        this.modelBuilder = new ModelBuilder<>();

        this.contractorCategoryList = new ArrayList<>();
        this.categoriesAdapter = new ContractorCategoryAdapter(view.getContext(), contractorCategoryList);

        this.listView = (ListView) view.findViewById(R.id.category_list_view);

        this.listView.setAdapter(categoriesAdapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity activity = (MainActivity) getActivity();
                Fragment categoryContractors = CategoryContractors.newInstance(contractorCategoryList.get(position).getId());
                activity.changeContentFragment(categoryContractors);
            }
        });

        this.viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);
        this.viewFlipper.setInAnimation(view.getContext(),R.anim.left_out);
        this.viewFlipper.setOutAnimation(view.getContext(),R.anim.right_enter);
        this.viewFlipper.startFlipping();

        this.pullContractorCategoriesData();

        return view;
    }

    private void pullContractorCategoriesData() {
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                try {
                    contractorCategoryList = modelBuilder.resourceListFromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                categoriesAdapter.clear();
                categoriesAdapter.addAll(contractorCategoryList);
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.d("MainActivity2", "Failed to retrieve contractor categories");
            }
        });

        apiRequest.execute(APIRequest.HTTP_GET, Constants.API_URL + "/api/v1/contractor_category");
    }
}
