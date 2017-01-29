package munoz.pablo.directorio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ContractorCategoryAdapter categoriesAdapter;
    ArrayList<ContractorCategory> items;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = ContractorCategory.makeExample();


        this.categoriesAdapter =
                new ContractorCategoryAdapter(this, items);

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
    }
}
