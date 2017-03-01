package activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.ContractorCategoryAdapter;
import fragments.LoadingFragment;
import models.ContractorCategory;
import models.ModelBuilder;
import munoz.pablo.directorio.R;
import services.APIRequest;

public class MainActivity extends AppCompatActivity {

    private ContractorCategoryAdapter categoriesAdapter;
    private ArrayList<ContractorCategory> contractorCategoryList;
    private ListView listView;
    private ModelBuilder<ContractorCategory> modelBuilder;

    private FragmentManager fragmentManager;
    private LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fragmentManager = this.getFragmentManager();
        this.loadingFragment = LoadingFragment.newInstance("Cargando información de categorías");
        this.loadingFragment.addToManager(this.fragmentManager, R.id.activity_main_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.modelBuilder = new ModelBuilder<>();

        this.contractorCategoryList = new ArrayList<>();
        this.categoriesAdapter = new ContractorCategoryAdapter(this, contractorCategoryList);

        listView = (ListView) findViewById(R.id.main_categories);
        listView.setAdapter(categoriesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContractorCategory category = contractorCategoryList.get(position);

                Intent intent = new Intent(MainActivity.this, ContractorsByCategory.class);
                intent.putExtra("categoryId", category.getId()) ;

                startActivity(intent);
            }
        });

        this.pullContractorCategoriesData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch(item.getItemId()) {
            case R.id.action_register:
                this.goToContractorRegistrationActivity();
                return true;

            case R.id.action_settings:
                Toast.makeText(this, "TODO: go to settings activity.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_refresh:
                if (this.loadingFragment.isDetached()) {
                    MainActivity.this.loadingFragment.addToManager(MainActivity.this.fragmentManager, R.id.activity_main_container);
                }
                MainActivity.this.pullContractorCategoriesData();
                return true;

            case R.id.action_login:
                intent = new Intent(this, LoginScreen.class);
                startActivity(intent);
                return true;

            case R.id.action_register_account:
                intent = new Intent(this, AccountRegistration.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToContractorRegistrationActivity() {
        Intent intent = new Intent(this, ContractorRegistration.class);
        startActivity(intent);
    }

    private void pullContractorCategoriesData() {
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                MainActivity activity = MainActivity.this;

                try {
                    activity.contractorCategoryList = activity.modelBuilder.resourceListFromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                activity.loadingFragment.removeFromManager(activity.fragmentManager);
                activity.categoriesAdapter.clear();
                activity.categoriesAdapter.addAll(activity.contractorCategoryList);
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.d("MainActivity", "Failed to retrieve contractor categories");
            }
        });

        apiRequest.execute(APIRequest.HTTP_GET, "http://192.168.33.10:3000/api/v1/contractor_category");
    }

}
