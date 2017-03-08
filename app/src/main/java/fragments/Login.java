package fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import activities.Constants;
import activities.MainActivity;
import munoz.pablo.directorio.R;
import services.APIRequest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        this.mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);

        this.mPasswordView = (EditText) view.findViewById(R.id.password);
        this.mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        this.mLoginFormView = view.findViewById(R.id.login_form);
        this.mProgressView = view.findViewById(R.id.login_progress);

        return view;
    }


    // private void populateAutoComplete() {
    //     if (!mayRequestContacts()) {
    //         return;
    //     }

    //     getLoaderManager().initLoader(0, null, this);
    // }

    // private boolean mayRequestContacts() {
    //     if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
    //         return true;
    //     }
    //     if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
    //         return true;
    //     }
    //     if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
    //         Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
    //                 .setAction(android.R.string.ok, new View.OnClickListener() {
    //                     @Override
    //                     @TargetApi(Build.VERSION_CODES.M)
    //                     public void onClick(View v) {
    //                         requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
    //                     }
    //                 });
    //     } else {
    //         requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
    //     }
    //     return false;
    // }

    /**
     * Callback received when a permissions request has been completed.
     */
    // @Override
    // public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
    //                                        @NonNull int[] grantResults) {
    //     if (requestCode == REQUEST_READ_CONTACTS) {
    //         if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //             populateAutoComplete();
    //         }
    //     }
    // }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        JSONObject body = new JSONObject();

        try {
            body.put("email", this.mEmailView.getText().toString());
            body.put("password", this.mPasswordView.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                Toast.makeText(Login.this.getView().getContext(), "Login successful", Toast.LENGTH_LONG).show();
                MainActivity mainActivity = (MainActivity) Login.this.getActivity();
                mainActivity.changeContentFragment(new ContractorCategoryMenu());
            }

            @Override
            public void onError(String errorMessage, int code) {
                Toast.makeText(Login.this.getView().getContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
            }
        });

        apiRequest.execute(APIRequest.HTTP_POST, Constants.API_URL + "/api/v1/auth/login", body.toString());
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
