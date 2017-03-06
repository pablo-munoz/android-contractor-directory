package activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import fragments.LoadingFragment;
import models.Contractor;
import models.ModelBuilder;
import munoz.pablo.directorio.R;
import services.APIRequest;

public class ContractorDetail extends AppCompatActivity {
    private TextView nameTv;
    private TextView idTv;
    private TextView phoneTv;
    private TextView emailTv;
    private ImageView portraitIv;
    private Contractor contractor;
    private ModelBuilder<Contractor> modelBuilder;
    private FragmentManager fragmentManager;
    private LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_detail);

        this.nameTv = (TextView) findViewById(R.id.contractor_detail_name);
        this.idTv = (TextView) findViewById(R.id.contractor_detail_id);
        this.phoneTv = (TextView) findViewById(R.id.contractor_detail_phone);
        this.emailTv = (TextView) findViewById(R.id.contractor_detail_email);
        this.portraitIv = (ImageView) findViewById(R.id.contractor_detail_img);

        this.fragmentManager = this.getFragmentManager();
        this.loadingFragment = LoadingFragment.newInstance("Cargando información del contratista");
        this.loadingFragment.addToManager(this.fragmentManager, R.id.contractor_detail_container);

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
                    activity.loadingFragment.removeFromManager(activity.fragmentManager);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.e("ContractorDetail", "Error pulling contractor data from the API");
            }
        });

        apiRequest.execute(APIRequest.HTTP_GET, Constants.API_URL + "/api/v1/contractor/" + contractorId);
    }

    public void updateView() {
        this.nameTv.setText(contractor.getFullName());
        this.idTv.setText("" + contractor.getId());
        this.emailTv.setText(contractor.getEmail());
        this.phoneTv.setText(contractor.getPhone());

        Glide.with(ContractorDetail.this)
                .load(contractor.getPortrait())
                .fitCenter()
                .into(portraitIv);
    }
}
