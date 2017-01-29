package munoz.pablo.directorio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ContractorsByCategory extends AppCompatActivity {
    TextView title;

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
    }
}
