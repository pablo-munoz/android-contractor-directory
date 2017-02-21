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
import services.RESTCallback;
import services.RESTService;

public class ContractorDetail extends AppCompatActivity {
    private TextView nameTv;
    private TextView idTv;
    private ImageView portraitIv;
    private Contractor contractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_detail);

        // Get references to layout views
        nameTv = (TextView) findViewById(R.id.contractor_detail_name);
        idTv = (TextView) findViewById(R.id.contractor_detail_id);
        portraitIv = (ImageView) findViewById(R.id.contractor_detail_img);

        Intent intent = getIntent();
        String contractorId = intent.getStringExtra("contractorId");
        this.loadContractorData(contractorId);
    }

    private void loadContractorData(String contractorId) {
        RESTService restApi = new RESTService();

        restApi.get("http://192.168.33.10:3000/api/v1/contractor/" + contractorId,
                new RESTCallback() {
                    Contractor contractor;

                    @Override
                    public void onSuccess(JSONObject responseJson) {
                        try {
                            ContractorDetail.this.contractor = contractor = (Contractor)
                                    ModelBuilder.resourceFromJson(Contractor.class, responseJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (contractor == null) {
                                    System.out.println("Error, could not display contractor data, it was null");
                                } else {
                                    nameTv.setText(contractor.getFullName());
                                    idTv.setText("" + contractor.getId());

                                    Glide.with(ContractorDetail.this)
                                            .load(contractor.getPortrait())
                                            .fitCenter()
                                            .into(portraitIv);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String rawResponse) {
                        Log.e("ContractorDetail", "Error pulling contractor data from the API");
                    }
                });
    }
}
