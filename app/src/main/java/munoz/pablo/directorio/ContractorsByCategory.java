package munoz.pablo.directorio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContractorsByCategory extends AppCompatActivity {
    TextView title;
    ListView contractorListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractors_by_category);

        Intent intent = getIntent();
        int categoryId = intent.getIntExtra("categoryId", -1);

        if (categoryId == -1) {
            Toast.makeText(this, "Unknown contractor category error.", Toast.LENGTH_SHORT).show();
        }

        ContractorCategory category = ContractorCategory.makeExample().get(categoryId - 1);

        title = (TextView) this.findViewById(R.id.contractors_by_category_title);
        title.setText(category.getName());

        final ArrayList<Contractor> contractorList = Contractor.makeExample();

        ContractorAdapter adapter = new ContractorAdapter(this, contractorList);

        contractorListView = (ListView) this.findViewById(R.id.contractors_by_category_list);
        contractorListView.setAdapter(adapter);

        contractorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContractorsByCategory.this, ContractorDetail.class);
                intent.putExtra("contractorId", contractorList.get(position).getId());
                startActivity(intent);
            }
        });
    }
}
