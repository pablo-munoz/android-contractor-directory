package fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import activities.Constants;
import munoz.pablo.directorio.R;
import services.APIRequest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText emailEt;
    private EditText passwordEt;
    private EditText passwordConfEt;
    private Button registerButton;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        this.emailEt = (EditText) view.findViewById(R.id.account_registration_email);
        this.passwordEt = (EditText) view.findViewById(R.id.account_registration_password);
        this.passwordConfEt = (EditText) view.findViewById(R.id.account_registration_password_conf);
        this.registerButton = (Button) view.findViewById(R.id.account_registration_button);

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            RegistrationFragment self = RegistrationFragment.this;

            @Override
            public void onClick(View v) {
                String email = self.emailEt.getText().toString();
                String password = self.passwordEt.getText().toString();
                String passwordConf = self.passwordConfEt.getText().toString();

                if (!password.equals(passwordConf)) {
                    Toast.makeText(
                            getView().getContext(),
                            "Las contraseñas no coinciden.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("email", email);
                    json.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject json, int code) {
                        // Toast.makeText(activity, "Registro exitoso.", Toast.LENGTH_LONG).show();
                        // Intent intent = new Intent(activity, MainActivity2.class);
                        // startActivity(intent);
                    }

                    @Override
                    public void onError(String errorMessage, int code) {
                        Toast.makeText(
                                getView().getContext(),
                                "Error en el registro, favor de intentar más tarde.",
                                Toast.LENGTH_LONG).show();
                    }
                });

                apiRequest.execute(APIRequest.HTTP_POST, Constants.API_URL + "/api/v1/auth/register", null, json.toString());
            }
        });

        return view;
    }
}
