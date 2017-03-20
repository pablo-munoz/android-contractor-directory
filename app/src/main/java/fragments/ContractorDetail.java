package fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import activities.Constants;
import models.Contractor;
import models.ModelBuilder;
import munoz.pablo.directorio.AuthHelper;
import munoz.pablo.directorio.R;
import services.APIRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContractorDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContractorDetail extends Fragment {
    private TextView nameTv;
    private TextView idTv;
    private TextView phoneTv;
    private TextView emailTv;
    private ImageView portraitIv;
    private RatingBar overallRatingBar;
    private RatingBar myRatingBar;

    private Contractor contractor;
    private ModelBuilder<Contractor> modelBuilder;

    // the fragment initialization parameters
    private static final String ARG_contractorId = "contractorId";

    private String contractorId;


    public ContractorDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contractorId Parameter 1.
     * @return A new instance of fragment ContractorDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static ContractorDetail newInstance(String contractorId) {
        ContractorDetail fragment = new ContractorDetail();
        Bundle args = new Bundle();
        args.putString(ARG_contractorId, contractorId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.contractorId = getArguments().getString(ARG_contractorId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contractor_detail, container, false);

        Log.d("ContractorDetail", "Fragment created with contractorId = " + this.contractorId);

        this.nameTv = (TextView) view.findViewById(R.id.contractor_detail_name);
        this.idTv = (TextView) view.findViewById(R.id.contractor_detail_id);
        this.emailTv = (TextView) view.findViewById(R.id.contractor_detail_email);
        this.phoneTv = (TextView) view.findViewById(R.id.contractor_detail_phone);
        this.portraitIv = (ImageView) view.findViewById(R.id.contractor_detail_img);
        this.overallRatingBar = (RatingBar) view.findViewById(R.id.contractor_detail_rating_bar);

        this.myRatingBar = (RatingBar) view.findViewById(R.id.contractor_detail_my_rating_bar);
        this.myRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!fromUser) return;

                String token = AuthHelper.getAuthToken(getActivity());

                APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject json, int code) {
                        Toast.makeText(getActivity(), "Rating updated succesfully.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage, int code) {

                    }
                });

                String url = Constants.API_URL + "/api/v1/contractor/" + contractorId + "/rate/" + rating;
                Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();

                apiRequest.execute(
                        APIRequest.HTTP_POST,
                        url,
                        "{ \"Authorization\": \"Bearer " + token + "\" }",
                        "{}");
            }
        });

        this.modelBuilder = new ModelBuilder<>();

        this.pullContractorData(contractorId);

        return view;
    }

    private void pullContractorData(String contractorId) {
        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                ContractorDetail self = ContractorDetail.this;

                try {
                    self.contractor =  self.modelBuilder.resourceFromJson(json);
                    self.updateView();
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
        this.nameTv.setText(this.contractor.getFullName());
        this.idTv.setText("" + this.contractor.getId());
        this.emailTv.setText(this.contractor.getEmail());
        this.phoneTv.setText(this.contractor.getPhone());
        this.overallRatingBar.setRating(this.contractor.getRating());
        this.myRatingBar.setRating(4);

        Glide.with(ContractorDetail.this)
                .load(this.contractor.getPortrait())
                .fitCenter()
                .into(this.portraitIv);
    }
}
