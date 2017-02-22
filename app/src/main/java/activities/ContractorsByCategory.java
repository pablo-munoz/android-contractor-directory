package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.ContractorAdapter;
import models.Contractor;
import models.ContractorCategory;
import models.ModelBuilder;
import munoz.pablo.directorio.R;
import services.APIRequest;

public class ContractorsByCategory extends AppCompatActivity {
    private TextView titleTv;

    private ContractorCategory contractorCategory;
    private ArrayList<Contractor> contractorList;
    private ListView contractorListView;
    private ContractorAdapter adapter;
    private ModelBuilder<ContractorCategory> categoryModelBuilder;
    private ModelBuilder<Contractor> contractorModelBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractors_by_category);

        this.titleTv = (TextView) this.findViewById(R.id.contractors_by_category_title);
        this.contractorList = new ArrayList<>();
        this.categoryModelBuilder = new ModelBuilder<>();
        this.contractorModelBuilder = new ModelBuilder<>();

        this.pullDataFromAPI();


        this.adapter = new ContractorAdapter(this, contractorList);

        this.contractorListView = (ListView) this.findViewById(R.id.contractors_by_category_list);
        this.contractorListView.setAdapter(this.adapter);

        contractorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContractorsByCategory.this, ContractorDetail.class);
                intent.putExtra("contractorId", contractorList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void pullDataFromAPI() {
        String categoryId = this.getIntent().getStringExtra("categoryId");
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                ContractorsByCategory activity = ContractorsByCategory.this;

                try {
                    activity.contractorCategory = activity.categoryModelBuilder.resourceFromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (activity.contractorCategory != null) {
                    ContractorsByCategory.this.titleTv.setText(activity.contractorCategory.getName());
                    ContractorsByCategory.this.loadContractorsData();
                }
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.e("ContractorsByCategory", "Error loading contractor category data.");
            }
        });

        apiRequest.execute("http://192.168.33.10:3000/api/v1/contractor_category/" + categoryId);
    }


    private void loadContractorsData() {
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                ContractorsByCategory activity = ContractorsByCategory.this;

                try {
                    // Hack. Cannot cast directly from ArrayList<Object> to ArrayList<Contractor>
                    activity.contractorList = activity.contractorModelBuilder.resourceListFromJson(json);
                    activity.adapter.addAll(ContractorsByCategory.this.contractorList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, int code) {
                    Log.e("ContractorsByCategory", "Error loading contractor data.");
            }
        });

        apiRequest.execute("http://192.168.33.10:3000/api/v1/contractor?contractor_category=" + contractorCategory.getId());
    }
}
