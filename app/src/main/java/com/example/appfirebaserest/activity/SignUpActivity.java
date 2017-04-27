package com.example.appfirebaserest.activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appfirebaserest.R;
import com.example.appfirebaserest.api.FirebaseAPI;
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
    private CheckBox cb_save_token;
    private ProgressDialog progressDialog;

    //  Parâmetros
    private String email;
    private String password;
    private String confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_save_token = (CheckBox) findViewById(R.id.cb_save_token);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);

    }

    public void signUp(View view){
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        confirm_password = et_confirm_password.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm_password)){
            Toast.makeText(SignUpActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }if(!password.equals(confirm_password)){
            Toast.makeText(SignUpActivity.this, "As senhas não são compatíveis", Toast.LENGTH_SHORT).show();
            return;
        }if(password.length() < 6){
            Toast.makeText(SignUpActivity.this, "A senha deve conter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }else{
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Aguarde...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(SignUpActivity.this, "Ops, por favor tente novamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }
}
