package activities;

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

import java.util.ArrayList;

import adapters.ContractorCategoryAdapter;
import models.ContractorCategory;
import munoz.pablo.directorio.R;
import services.RESTCallback;
import services.RESTService;

public class MainActivity extends AppCompatActivity {

    ContractorCategoryAdapter categoriesAdapter;
    ArrayList<ContractorCategory> items;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        items = new ArrayList<>();

        this.categoriesAdapter = new ContractorCategoryAdapter(this, items);

        listView = (ListView) findViewById(R.id.main_categories);
        listView.setAdapter(categoriesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContractorCategory category = items.get(position);

                Intent intent = new Intent(MainActivity.this, ContractorsByCategory.class);
                intent.putExtra("categoryId", category.getId()) ;

                startActivity(intent);
            }
        });

        RESTService contractorCategoryService = new RESTService();
        contractorCategoryService.getMany(
                ContractorCategory.class,
                new RESTCallback() {
                    @Override
                    public void onSuccess(Object modelInstance, String rawResponse) {
                        items = (ArrayList<ContractorCategory>) modelInstance;
                        MainActivity.this.categoriesAdapter.addAll(items);
                        Log.d("MainActivity", items.get(0).getName() + " " + items.get(0).getId());
                    }

                    @Override
                    public void onFailure(String rawResponse) {
                        Log.d("MainActivity", "Failed to retrieve contractor categories");
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_register:
                this.goToContractorRegistrationActivity();
                return true;

            case R.id.action_settings:
                Toast.makeText(this, "TODO: go to settings activity.", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToContractorRegistrationActivity() {
       Intent intent = new Intent(this, ContractorRegistration.class);
       startActivity(intent);
    }
}
