package activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import munoz.pablo.directorio.R;
import services.APIRequest;

public class LoginScreen extends AppCompatActivity {

    private EditText emailEt;
    private EditText passwordEt;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        this.emailEt = (EditText) this.findViewById(R.id.login_screen_email_input);
        this.passwordEt = (EditText) this.findViewById(R.id.login_screen_password_input);
        this.loginBtn = (Button) this.findViewById(R.id.login_screen_login_button);

        this.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginScreen.this.attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        JSONObject body = new JSONObject();

        try {
            body.put("email", this.emailEt.getText().toString());
            body.put("password", this.passwordEt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIRequest apiRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                // Toast.makeText(LoginScreen.this, "Login successful", Toast.LENGTH_LONG).show();
                // Intent intent = new Intent(LoginScreen.this, MainActivity2.class);
                // startActivity(intent);
            }

            @Override
            public void onError(String errorMessage, int code) {
                Toast.makeText(LoginScreen.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            }
        });

        apiRequest.execute(APIRequest.HTTP_POST, Constants.API_URL + "/api/v1/auth/login", body.toString());
    }
}
