package com.example.appfirebaserest.activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.api.FirebaseAPI;
import com.example.appfirebaserest.util.CheckNetworkConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {

    //  Firebase
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //  Retrofit
    private FirebaseAPI firebaseAPI;
    private Retrofit retrofit;

    //  Layouts
    private EditText et_email;
    private EditText et_password;
    private EditText et_confirm_password;
    private TextInputLayout til_email;
    private TextInputLayout til_password;
    private TextInputLayout til_confirm_password;
    private CheckBox cb_save_token;
    private MaterialDialog materialDialog;

    //  Parâmetros
    private String email;
    private String password;
    private String confirm_password;

    //  Check Network Connection
    private CheckNetworkConnection checkNetworkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_save_token = (CheckBox) findViewById(R.id.cb_save_token);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        til_confirm_password = (TextInputLayout) findViewById(R.id.til_confirm_password);

    }

    public void signUp(View view){
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        confirm_password = et_confirm_password.getText().toString();
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);
        til_confirm_password.setErrorEnabled(false);

        checkNetworkConnection = new CheckNetworkConnection(this);

        if(!checkNetworkConnection.isConnected()){
            Snackbar.make(view, "Sem internet", Snackbar.LENGTH_LONG).show();
            return;
        }if(email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()){
            if(email.isEmpty()){
                til_email.setError("Por favor, digite seu email");
            }if(password.isEmpty()){
                til_password.setError("Por favor, digite sua senha");
            }if(confirm_password.isEmpty()){
                til_confirm_password.setError("Por favor, confirme sua senha");
            }if(email.isEmpty() && password.isEmpty() && confirm_password.isEmpty()){
                Snackbar.make(view, "Preencha todos os campos", Snackbar.LENGTH_LONG).show();
            }
            return;
        }if(!password.equals(confirm_password)){
            Snackbar.make(view, "As senhas não são iguais", Snackbar.LENGTH_LONG).show();
            return;
        }if(password.length() < 6){
            Snackbar.make(view, "A senha deve conter no mínimo 6 caracteres", Snackbar.LENGTH_LONG).show();
            return;
        }if(!email.isEmpty() && !password.isEmpty() && !confirm_password.isEmpty()){
            materialDialog = new MaterialDialog.Builder(this)
                    .content("Aguarde...")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //  SweepAlert
                        Toast.makeText(SignUpActivity.this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        //  SweepAlert
                        Toast.makeText(SignUpActivity.this, "Ops, por favor tente novamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);
        til_confirm_password.setErrorEnabled(false);
    }
}
