package munoz.pablo.directorio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ContractorDetail extends AppCompatActivity {
    TextView nameTv;
    TextView idTv;
    ImageView portraitIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_detail);

        // Get references to layout views
        nameTv = (TextView) findViewById(R.id.contractor_detail_name);
        idTv = (TextView) findViewById(R.id.contractor_detail_id);
        portraitIv = (ImageView) findViewById(R.id.contractor_detail_img);

        Intent intent = getIntent();
        Contractor contractor = this.getContractor(intent);

        if (contractor == null) {
            System.out.println("Error, could not display contractor data, it was null");
        } else {
            nameTv.setText(contractor.getFullName());
            idTv.setText("" + contractor.getId());

            Glide.with(this)
                    .load(contractor.getPortrait())
                    .fitCenter()
                    .into(portraitIv);
        }
    }

    private Contractor getContractor(Intent intent) {
        int contractorId = intent.getIntExtra("contractorId", -1);

        if (contractorId == -1) {
            System.out.println("Error, invalid contractor id in intent");
            return null;
        } else {
            return Contractor.makeExample().get(contractorId - 1);
        }
    }
}
