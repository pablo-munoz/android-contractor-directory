package munoz.pablo.directorio.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import munoz.pablo.directorio.R;
import munoz.pablo.directorio.activities.MainActivity;
import munoz.pablo.directorio.adapters.JSONArrayAdapter;
import munoz.pablo.directorio.models.Account;
import munoz.pablo.directorio.models.Contractor;
import munoz.pablo.directorio.models.ModelBuilder;
import munoz.pablo.directorio.services.APIRequest;
import munoz.pablo.directorio.services.APIRequest2;
import munoz.pablo.directorio.utils.AndroidContractorDirectoryApp;
import munoz.pablo.directorio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContractorDetail#newInstance} factory method to
 * create an instance of this fragment.
 *
 * TODO: Make it so that the your rating stars reflect the actual rating you have given.
 *
 */
public class ContractorDetail extends Fragment implements OnMapReadyCallback {
    private MainActivity mainActivity;
    private TextView nameTv;
    //private TextView idTv;
    private TextView phoneTv;
    private TextView emailTv;
    private TextView websiteTv;
    private ImageView portraitIv;
    private RatingBar overallRatingBar;
    private RatingBar myRatingBar;
    private ListView commentsLv;
    private EditText commentEt;
    private Button addCommentBtn;
    private MapView mapView;
    private Button callBtn;
    private Button addToFavoritesBtn;
    private Button sendMessageBtn;
    private ProgressBar progressBar;
    private Geocoder geocoder;
    private LatLng addressLatLng;
    private GoogleMap googleMap;

    private JSONArrayAdapter commentsAdapter;

    private Contractor contractor;
    private ModelBuilder<Contractor> modelBuilder;

    // the fragment initialization parameters
    private static final String ARG_contractorId = "contractorId";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private String contractorId;

    private AndroidContractorDirectoryApp application;


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
            contractorId = getArguments().getString(ARG_contractorId);
        }

        mainActivity = (MainActivity) getActivity();

        application = (AndroidContractorDirectoryApp) mainActivity.getApplication();

        geocoder = new Geocoder(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contractor_detail, container, false);

        Log.d("ContractorDetail", "onCreateView called");

        this.obtainViewReferences(view);

        commentEt.setVisibility(View.INVISIBLE);

        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCommentInputVisibility();
            }
        });

        addToFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContractorToFavorites();
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Got", "here");
                ChatConversation chatConversation = ChatConversation.newInstance(contractor.getAccountId(), "None");
                ((MainActivity) getActivity()).changeContentFragment(chatConversation);
            }
        });

        myRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!fromUser) return;
                rateContractor(rating);
            }
        });


        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);

                intent.setData(Uri.parse("tel:" + contractor.getPhone()));
                startActivity(intent);
            }
        });

        modelBuilder = new ModelBuilder<>();

        requestContractorDataFromApi(contractorId);

        setUpMap(savedInstanceState);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Uri openMapIntent = Uri.parse(String.format("geo:%f,%f?q=%f,%f(%s)",
                        addressLatLng.latitude, addressLatLng.longitude,
                        addressLatLng.latitude, addressLatLng.longitude, contractor.getFullName()));
                Intent intent = new Intent(Intent.ACTION_VIEW, openMapIntent);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        this.googleMap = googleMap;

        if (contractor != null) {
            displayContractorAddressInMap();
        }
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    private void obtainViewReferences(View view) {
        nameTv = (TextView) view.findViewById(R.id.contractor_detail_name);
        //idTv = (TextView) view.findViewById(R.id.contractor_detail_id);
        emailTv = (TextView) view.findViewById(R.id.contractor_detail_email);
        phoneTv = (TextView) view.findViewById(R.id.contractor_detail_phone);
        websiteTv = (TextView) view.findViewById(R.id.contractor_detail_website);
        portraitIv = (ImageView) view.findViewById(R.id.contractor_detail_img);
        overallRatingBar = (RatingBar) view.findViewById(R.id.contractor_detail_rating_bar);
        commentsLv = (ListView) view.findViewById(R.id.contractor_detail_lv);
        callBtn = (Button) view.findViewById(R.id.contractor_detail_call_btn);
        addToFavoritesBtn = (Button) view.findViewById(R.id.contractor_detail_add_favorites);
        progressBar = (ProgressBar) view.findViewById(R.id.contractor_detail_loading);
        commentEt = (EditText) view.findViewById(R.id.contractor_detail_comment_edit);
        addCommentBtn = (Button) view.findViewById(R.id.contractor_detail_add_comment_btn);
        sendMessageBtn = (Button) view.findViewById(R.id.contractor_detail_send_message);
        myRatingBar = (RatingBar) view.findViewById(R.id.contractor_detail_my_rating_bar);
        mapView = (MapView) view.findViewById(R.id.contractor_detail_map);
    }

    private void toggleCommentInputVisibility() {
        if (commentEt.getVisibility() == View.VISIBLE) {
            publishCommentAboutContractor();
        } else {
            commentEt.setVisibility(View.VISIBLE);
        }
    }

    private void setUpMap(Bundle savedInstanceState) {
        MapsInitializer.initialize(getActivity());
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private void addContractorToFavorites() {
        String endpoint = String.format("%s/%s/account/%s/favorites/%s/add",
                Constants.API_URL, Constants.API_VERSION, application.getUserAccount().getId(), contractor.getId());

        JSONObject headers = new JSONObject();
        application.injectAuthorizationHeader(headers);

        APIRequest2 req = new APIRequest2.Builder()
                .url(endpoint)
                .method(APIRequest2.METHOD_POST)
                .headers(headers)
                .callback(new APIRequest2.Callback() {
                    @Override
                    public void onResult(int responseCode, JSONObject response) {
                        Toast.makeText(mainActivity, contractor.getFirstName() + " añadido a favoritos.", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .build();

        req.execute();
    }

    private void rateContractor(float rating) {
        JSONObject headers = new JSONObject();
        application.injectAuthorizationHeader(headers);

        APIRequest2 req = new APIRequest2.Builder()
                .url(Constants.API_URL + "/api/v1/contractor/" + contractorId + "/rate/" + rating)
                .method(APIRequest2.METHOD_POST)
                .headers(headers)
                .callback(new APIRequest2.Callback() {
                    @Override
                    public void onResult(int responseCode, JSONObject response) {
                        requestContractorDataFromApi(contractorId);
                    }
                })
                .build();

        req.execute();
    }

    private void publishCommentAboutContractor() {
        JSONObject headers = new JSONObject();
        application.injectAuthorizationHeader(headers);

        JSONObject payload = new JSONObject();

        try {
            headers.put("Content-Type", "application/json");
            payload.put("content", commentEt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        APIRequest2 req = new APIRequest2.Builder()
                .url(Constants.API_URL + "/api/v1/contractor/" + contractorId + "/comment")
                .method(APIRequest2.METHOD_POST)
                .headers(headers)
                .payload(payload)
                .callback(new APIRequest2.Callback() {
                    @Override
                    public void onResult(int responseCode, JSONObject response) {
                        if (responseCode == 200) {
                            requestContractorDataFromApi(contractorId);
                            commentEt.setVisibility(View.INVISIBLE);
                            commentEt.setText("");
                        } else {
                            Log.e("ContractorDetail", ""+responseCode);
                            Toast.makeText(getActivity(), "No se pudo guardar tu comentario.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();

        req.execute();
    }

    private void requestContractorDataFromApi(String contractorId) {
        APIRequest2 req = new APIRequest2.Builder()
                .url(Constants.API_URL + "/api/v1/contractor/" + contractorId)
                .method(APIRequest2.METHOD_GET)
                .callback(new APIRequest2.Callback() {
                    @Override
                    public void onResult(int responseCode, JSONObject response) {
                        if (responseCode == 200) {
                            contractor = modelBuilder.instantiateOne(response);
                            updateView();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .build();

        req.execute();
    }

    private void updateView() {
        if (contractor != null) {
            nameTv.setText(contractor.getFullName());
            //idTv.setText("" + contractor.getId());
            emailTv.setText(contractor.getEmail());
            phoneTv.setText(contractor.getPhone());
            websiteTv.setText(contractor.getWebsite());
            overallRatingBar.setRating((float) contractor.getRating());
            //myRatingBar.setRating(5);
            //Log.d("CONTRACTOR PROTRAIT",  contractor.getPortrait().toString() +" ");

            if(contractor.getPortrait() != null){
                new DownLoadImageTask(portraitIv).execute(contractor.getPortrait());
            }
            else{
                new DownLoadImageTask(portraitIv).execute("http://ewic.org/wp-content/themes/ewic/images/Construction%20Worker.png  ");
            }



            //Glide.with(ContractorDetail.this)
                    //.load(contractor.getPortrait())
                    //.fitCenter()
                    //.into(portraitIv);

            commentsAdapter = new JSONArrayAdapter(getActivity(), contractor.getComments(),
                    new JSONArrayAdapter.ViewBuilder() {
                        @Override
                        public View construct(JSONArray data, int position, View view, ViewGroup parent) {
                            if (view == null) {
                                view = getActivity().getLayoutInflater().inflate(R.layout.contractor_detail_comment, parent, false);
                            }

                            TextView contentTv = (TextView) view.findViewById(R.id.contractor_detail_comment_content);
                            try {
                                contentTv.setText(data.getJSONObject(position).getString("content"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            return view;
                        }
                    });

            commentsLv.setAdapter(commentsAdapter);

            if (googleMap != null) {
                displayContractorAddressInMap();
            }
        }
    }

    private void displayContractorAddressInMap() {
        // Get the LatLng corresponding to the string address that the
        // contractor wrote in their profile.
        List<Address> address;
        addressLatLng = null;
        try {
            Log.d("THE ADDR", contractor.getAddress());
            address = geocoder.getFromLocationName(contractor.getAddress(), 5);

            if (address != null) {
                Address location = address.get(0);
                addressLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addressLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(addressLatLng).title(contractor.getFullName()));
            // Focus around added marker
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng, 15));
            // Second argument is zoom level, the bigger the closer.
        } else {
            Toast.makeText(getActivity(), "No se pudo mostrar la ubicación del contratista.", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }


}
