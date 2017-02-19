package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.ContractorAdapter;
import adapters.ContractorCategoryAdapter;
import models.Contractor;
import models.ContractorCategory;
import models.ModelBuilder;
import munoz.pablo.directorio.R;
import services.RESTCallback;
import services.RESTService;

public class ContractorsByCategory extends AppCompatActivity {
    private TextView title;

    private ContractorCategory contractorCategory;
    private ArrayList<Contractor> contractorList;
    private ListView contractorListView;
    private ContractorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractors_by_category);

        contractorCategory = null;

        this.pullDataFromAPI();

        this.title = (TextView) this.findViewById(R.id.contractors_by_category_title);

        this.contractorList = new ArrayList<>();

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
        RESTService restApi = new RESTService();
        String categoryId = getIntent().getStringExtra("categoryId");

        restApi.get("http://192.168.33.10:3000/api/v1/contractor_category/" + categoryId,
                new RESTCallback() {
                    ContractorCategory category;

                    @Override
                    public void onSuccess(JSONObject responseJson) {
                        try {
                            ContractorsByCategory.this.contractorCategory = category = (ContractorCategory)
                                    ModelBuilder.resourceFromJson(Contractor.class, responseJson);
                            ContractorsByCategory.this.title.setText(category.getName());
                            ContractorsByCategory.this.loadContractorsData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(String rawResponse) {
                        Log.e("ContractorsByCategory", "Error loading contractor category data.");
                    }
                });
    }


    private void loadContractorsData() {
        RESTService restApi = new RESTService();

        restApi.get("http://192.168.33.10:3000/api/v1/contractor?contractor_category=" + contractorCategory.getId(),
                new RESTCallback() {
                    @Override
                    public void onSuccess(JSONObject responseJson) {
                        try {
                            // Hack. Cannot cast directly from ArrayList<Object> to ArrayList<Contractor>
                            ContractorsByCategory.this.contractorList = (ArrayList<Contractor>)(ArrayList<?>)
                                    ModelBuilder.resourceListFromJson(Contractor.class, responseJson);
                            ContractorsByCategory.this.adapter.addAll(ContractorsByCategory.this.contractorList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String rawResponse) {
                        Log.e("ContractorsByCategory", "Error loading contractor data.");
                    }
                });
    }
}
