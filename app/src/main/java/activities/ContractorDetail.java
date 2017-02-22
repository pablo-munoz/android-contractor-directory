package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import models.Contractor;
import models.ModelBuilder;
import munoz.pablo.directorio.R;
import services.APIRequest;

public class ContractorDetail extends AppCompatActivity {
    private TextView nameTv;
    private TextView idTv;
    private ImageView portraitIv;
    private Contractor contractor;
    private ModelBuilder<Contractor> modelBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_detail);

        nameTv = (TextView) findViewById(R.id.contractor_detail_name);
        idTv = (TextView) findViewById(R.id.contractor_detail_id);
        portraitIv = (ImageView) findViewById(R.id.contractor_detail_img);

        this.modelBuilder = new ModelBuilder<>();

        Intent intent = getIntent();
        String contractorId = intent.getStringExtra("contractorId");
        this.pullContractorData(contractorId);
    }

    private void pullContractorData(String contractorId) {
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                ContractorDetail activity = ContractorDetail.this;

                try {
                    activity.contractor =  activity.modelBuilder.resourceFromJson(json);
                    activity.updateView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.e("ContractorDetail", "Error pulling contractor data from the API");
            }
        });

        apiRequest.execute(APIRequest.HTTP_GET, "http://192.168.33.10:3000/api/v1/contractor/" + contractorId);
    }

    public void updateView() {
        nameTv.setText(contractor.getFullName());
        idTv.setText("" + contractor.getId());

        Glide.with(ContractorDetail.this)
                .load(contractor.getPortrait())
                .fitCenter()
                .into(portraitIv);
    }
}
