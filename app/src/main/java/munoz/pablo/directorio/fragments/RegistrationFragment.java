package munoz.pablo.directorio.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import munoz.pablo.directorio.R;
import munoz.pablo.directorio.activities.MainActivity;
import munoz.pablo.directorio.models.ContractorCategory;
import munoz.pablo.directorio.models.ModelBuilder;
import munoz.pablo.directorio.services.APIRequest;
import munoz.pablo.directorio.services.APIRequest2;
import munoz.pablo.directorio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {
    private EditText emailEt;
    private EditText passwordEt;
    private EditText passwordConfEt;
    private CheckBox isContractorCb;
    private LinearLayout contractorOnlyInputs;
    private EditText firstNameEt;
    private EditText middleNameEt;
    private EditText lastNamesEt;
    private Spinner categoriesSp;
    private EditText phoneEt;
    private EditText websiteEt;
    private EditText addressEt;
    private Button registerButton;
    private ArrayList<ContractorCategory> contractorCategoryList;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        this.emailEt = (EditText) view.findViewById(R.id.account_registration_email);
        this.passwordEt = (EditText) view.findViewById(R.id.account_registration_password);
        this.passwordConfEt = (EditText) view.findViewById(R.id.account_registration_password_conf);
        this.registerButton = (Button) view.findViewById(R.id.account_registration_button);
        this.isContractorCb = (CheckBox) view.findViewById(R.id.account_registration_contractor_checkbox);
        this.contractorOnlyInputs = (LinearLayout) view.findViewById(R.id.account_registration_contractor_only) ;
        this.firstNameEt = (EditText) view.findViewById(R.id.account_registration_first_name);
        this.middleNameEt = (EditText) view.findViewById(R.id.account_registration_middle_name);
        this.lastNamesEt = (EditText) view.findViewById(R.id.account_registration_last_names);
        this.categoriesSp = (Spinner) view.findViewById(R.id.account_registration_category_select);
        this.phoneEt = (EditText) view.findViewById(R.id.account_registration_phone);
        this.websiteEt = (EditText) view.findViewById(R.id.account_registration_website) ;
        this.addressEt = (EditText) view.findViewById(R.id.account_registration_address);

        contractorOnlyInputs.setVisibility(View.GONE);

        // Show or hide the extra, contractor only inputs.
        this.isContractorCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    contractorOnlyInputs.setVisibility(View.VISIBLE);
                } else {
                    contractorOnlyInputs.setVisibility(View.GONE);
                }
            }
        });

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAccountCreation();
            }
        });

        pullContractorCategoryData();

        return view;
    }

    void pullContractorCategoryData() {
        APIRequest2 req = new APIRequest2.Builder()
                .url(Constants.API_URL + "/api/v1/contractor_category")
                .method(APIRequest2.METHOD_GET)
                .callback(new APIRequest2.Callback() {
                    @Override
                    public void onResult(int responseCode, JSONObject response) {
                        if (responseCode == 200) {
                            ModelBuilder<ContractorCategory> modelBuilder = new ModelBuilder<>();
                            contractorCategoryList = modelBuilder.instantiateMany(response);
                            populateCategorySpinner();
                        } else {
                            Log.e("RegistrationFragment", "Could not retrieve contractor category data");
                        }
                    }
                })
                .build();

        req.execute();
    }

    void populateCategorySpinner() {
        ArrayAdapter<ContractorCategory> adapter = new ArrayAdapter<ContractorCategory>(
                getActivity(), R.layout.contractor_category_spinner, this.contractorCategoryList);
        categoriesSp.setAdapter(adapter);
        categoriesSp.setSelection(0);
    }

    void attemptAccountCreation() {
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();
        String passwordConf = passwordConfEt.getText().toString();

        boolean isContractor = isContractorCb.isChecked();

        if (!password.equals(passwordConf)) {
            Toast.makeText(
                    getView().getContext(),
                    "Las contraseñas no coinciden.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject relationships = new JSONObject();
        JSONObject contractorRelationship = new JSONObject();

        try {
            data.put("email", email);
            data.put("password", password);
            json.put("type", "account");
            json.put("data", data);

            if (isContractor) {
                String firstName = firstNameEt.getText().toString();
                String middleName = middleNameEt.getText().toString();
                String lastNames = lastNamesEt.getText().toString();
                String category = ((ContractorCategory) categoriesSp.getSelectedItem()).getId();
                String phone = phoneEt.getText().toString();
                String website = websiteEt.getText().toString();
                String address = addressEt.getText().toString();

                contractorRelationship.put("first_name", firstName);
                contractorRelationship.put("middle_name", middleName);
                contractorRelationship.put("last_names", lastNames);
                contractorRelationship.put("phone", phone);
                contractorRelationship.put("website", website);
                contractorRelationship.put("address", address);

                relationships.put("contractor", contractorRelationship);

                JSONArray categories = new JSONArray();
                categories.put(category);

                relationships.put("contractor_category", categories);

                json.put("relationships", relationships);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.changeContentFragment(new Login());
            }

            @Override
            public void onError(String errorMessage, int code) {
                Toast.makeText(
                        getView().getContext(),
                        "Error en el registro, favor de intentar más tarde.",
                        Toast.LENGTH_LONG).show();
            }
        });

        apiRequest.execute(APIRequest.HTTP_POST, Constants.API_URL + "/api/v1/auth/register", null, json.toString());
    }
}
