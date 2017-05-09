package munoz.pablo.directorio.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.text.GetChars;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import munoz.pablo.directorio.R;
import munoz.pablo.directorio.activities.MainActivity;
import munoz.pablo.directorio.models.Account;
import munoz.pablo.directorio.models.Contractor;
import munoz.pablo.directorio.services.APIRequest;
import munoz.pablo.directorio.services.APIRequest2;
import munoz.pablo.directorio.utils.AndroidContractorDirectoryApp;
import munoz.pablo.directorio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ScrollView contractorDataLayout;
    private TextView idTv;
    private TextView emailTv;
    private EditText firstNameEt;
    private EditText middleNameEt;
    private EditText lastNamesEt;
    private EditText phoneEt;
    private EditText addressEt;
    private EditText websiteEt;
    private Button goToContractorPageBtn;
    private Button saveChangesBtn;


    public AccountDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountDetail newInstance(String param1, String param2) {
        AccountDetail fragment = new AccountDetail();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_detail, container, false);

        contractorDataLayout = (ScrollView) view.findViewById(R.id.account_contractor_data) ;
        idTv = (TextView) view.findViewById(R.id.account_id);
        emailTv = (TextView) view.findViewById(R.id.account_email);
        firstNameEt = (EditText) view.findViewById(R.id.account_contractor_first_name);
        middleNameEt = (EditText) view.findViewById(R.id.account_contractor_middle_name);
        lastNamesEt = (EditText) view.findViewById(R.id.account_contractor_last_names);
        phoneEt = (EditText) view.findViewById(R.id.account_contractor_phone);
        addressEt = (EditText) view.findViewById(R.id.account_contractor_address);
        websiteEt = (EditText) view.findViewById(R.id.account_contractor_website);
        goToContractorPageBtn = (Button) view.findViewById(R.id.account_go_to_contractor_page);
        saveChangesBtn = (Button) view.findViewById(R.id.account_save_changes);

        AndroidContractorDirectoryApp app = (AndroidContractorDirectoryApp) getActivity().getApplication();

        Account userAccount = app.getUserAccount();
        Contractor userContractorData = userAccount.getContractor();

        idTv.setText(userAccount.getId());
        emailTv.setText(userAccount.getEmail());

        if (userContractorData != null) {
            contractorDataLayout.setVisibility(View.VISIBLE);

            firstNameEt.setText(userContractorData.getFirstName());
            middleNameEt.setText(userContractorData.getMiddleName());
            lastNamesEt.setText(userContractorData.getLastName());
            phoneEt.setText(userContractorData.getPhone());
            addressEt.setText(userContractorData.getAddress());
            websiteEt.setText(userContractorData.getWebsite());
        } else {
            contractorDataLayout.setVisibility(View.GONE);
        }

        goToContractorPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToContractorDetailPage();
            }
        });

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccountData();
            }
        });

        return view;
    }

    private void updateAccountData() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("first_name", firstNameEt.getText());
            payload.put("middle_name", middleNameEt.getText());
            payload.put("last_names", lastNamesEt.getText());
            payload.put("phone", phoneEt.getText());
            payload.put("address", addressEt.getText());
            payload.put("website", websiteEt.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String headers = String.format("{ \"Content-Type\": \"application/json\", \"Authorization\": \"Bearer %s\" }",
                ((AndroidContractorDirectoryApp) getActivity().getApplication()).getUserAccount().getToken());

        APIRequest2 req = new APIRequest2.Builder()
                .method(APIRequest2.METHOD_PATCH)
                .url(Constants.API_URL + "/" + Constants.API_VERSION + "/account")
                .headers(headers)
                .payload(payload)
                .callback(new APIRequest2.Callback() {
                    @Override
                    public void onResult(int responseCode, JSONObject response) {
                        if (responseCode == 200) {
                            Toast.makeText(getActivity(), "Se han actualizado tus datos.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "No se pudo actualizar tus datos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();

        req.execute();
    }

    private void sendToContractorDetailPage() {
        AndroidContractorDirectoryApp app = (AndroidContractorDirectoryApp) getActivity().getApplication();

        Account userAccount = app.getUserAccount();
        Contractor userContractorData = userAccount.getContractor();

        ContractorDetail fragment = ContractorDetail.newInstance(userContractorData.getId());
        ((MainActivity) getActivity()).changeContentFragment(fragment);
    }

}
