package activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import munoz.pablo.directorio.R;

public class ContractorRegistration extends AppCompatActivity {

    EditText firstNameEt;
    EditText secondNameEt;
    EditText lastNameEt;
    EditText phoneEt;
    EditText emailEt;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_registration);

        firstNameEt = (EditText) this.findViewById(R.id.first_name_field);
        secondNameEt = (EditText) this.findViewById(R.id.second_name_field);
        lastNameEt = (EditText) this.findViewById(R.id.last_name_field);
        phoneEt = (EditText) this.findViewById(R.id.phone_field);
        emailEt = (EditText) this.findViewById(R.id.email_field);

        registerBtn = (Button) this.findViewById(R.id.register_button);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
