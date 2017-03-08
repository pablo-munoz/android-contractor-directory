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

public class AccountRegistration extends AppCompatActivity {

    private EditText emailEt;
    private EditText passwordEt;
    private EditText passwordConfEt;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_registration);

        this.emailEt = (EditText) this.findViewById(R.id.account_registration_email);
        this.passwordEt = (EditText) this.findViewById(R.id.account_registration_password);
        this.passwordConfEt = (EditText) this.findViewById(R.id.account_registration_password_conf);
        this.registerButton = (Button) this.findViewById(R.id.account_registration_button);

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            AccountRegistration activity = AccountRegistration.this;

            @Override
            public void onClick(View v) {
                String email = activity.emailEt.getText().toString();
                String password = activity.passwordEt.getText().toString();
                String passwordConf = activity.passwordConfEt.getText().toString();

                if (!password.equals(passwordConf)) {
                    Toast.makeText(activity, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(activity, "Error en el registro, favor de intentar más tarde.", Toast.LENGTH_LONG).show();
                    }
                });

                apiRequest.execute(APIRequest.HTTP_POST, Constants.API_URL + "/api/v1/auth/register", json.toString());
            }
        });
    }
}
