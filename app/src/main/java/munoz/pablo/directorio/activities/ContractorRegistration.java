package munoz.pablo.directorio.activities;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import munoz.pablo.directorio.models.ContractorCategory;
import munoz.pablo.directorio.models.ModelBuilder;
import munoz.pablo.directorio.R;
import munoz.pablo.directorio.services.APIRequest;
import munoz.pablo.directorio.utils.Constants;

public class ContractorRegistration extends AppCompatActivity {

    private Spinner categorySp;
    private EditText firstNameEt;
    private EditText secondNameEt;
    private EditText lastNameEt;
    private EditText phoneEt;
    private EditText emailEt;
    private Button registerBtn;
    private ArrayList<ContractorCategory> contractorCategoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_registration);

        this.categorySp = (Spinner) this.findViewById(R.id.contractor_registration_category);
        this.firstNameEt = (EditText) this.findViewById(R.id.first_name_field);
        this.secondNameEt = (EditText) this.findViewById(R.id.second_name_field);
        this.lastNameEt = (EditText) this.findViewById(R.id.last_name_field);
        this.phoneEt = (EditText) this.findViewById(R.id.phone_field);
        this.emailEt = (EditText) this.findViewById(R.id.email_field);

        this.registerBtn = (Button) this.findViewById(R.id.register_button);

        this.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        this.contractorCategoryList = new ArrayList<>();

        this.pullContractorCategoryData();

        this.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContractorRegistration.this.submitNewContractorData();
            }
        });
    }

    void pullContractorCategoryData() {
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            ContractorRegistration activity = ContractorRegistration.this;

            @Override
            public void onSuccess(JSONObject json, int code) {
                ModelBuilder<ContractorCategory> modelBuilder = new ModelBuilder<>();
                try {
                    activity.contractorCategoryList = modelBuilder.resourceListFromJson(json);
                    activity.populateCategorySpinner();
                    Log.d("GET CATEGORIES", ""+activity.contractorCategoryList.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, int code) {

            }
        });

        apiRequest.execute(APIRequest.HTTP_GET, Constants.API_URL + "/api/v1/contractor_category");
    }

    void populateCategorySpinner() {
        ContractorCategory category;
        ArrayAdapter<ContractorCategory> adapter = new ArrayAdapter<ContractorCategory>(
                getApplicationContext(), R.layout.contractor_category_spinner, this.contractorCategoryList);
        this.categorySp.setAdapter(adapter);
    }

    void submitNewContractorData() {
        JSONObject json = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            JSONObject attributes = new JSONObject();
            data.put("attributes", attributes);

            attributes.put("first_name", this.firstNameEt.getText().toString());
            attributes.put("middle_name", this.secondNameEt.getText().toString());
            attributes.put("last_names", this.lastNameEt.getText().toString());
            attributes.put("phone", this.phoneEt.getText().toString());
            attributes.put("email", this.emailEt.getText().toString());

            JSONObject relationships = new JSONObject();
            JSONObject categoryData = new JSONObject();
            JSONObject relationData = new JSONObject();

            relationData.put("type", "contractor_category");
            ContractorCategory selectedCategory = (ContractorCategory) this.categorySp.getSelectedItem();
            categoryData.put("id", selectedCategory.getId());
            relationData.put("data", categoryData);

            relationships.put("contractor_category", relationData);
            json.put("relationships", relationships);

            json.put("data", data);

            Log.d("THE JSON", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            ContractorRegistration activity = ContractorRegistration.this;

            @Override
            public void onSuccess(JSONObject json, int code) {
                // Intent intent = new Intent(activity, MainActivity2.class);
                // startActivity(intent);
            }

            @Override
            public void onError(String errorMessage, int code) {
                Toast.makeText(activity, "No se puedo registrar con los datos ingresados.", Toast.LENGTH_LONG).show();
            }
        });

        apiRequest.execute(APIRequest.HTTP_POST, Constants.API_URL + "/api/v1/contractor", json.toString());
    }
}
