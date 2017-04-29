package com.example.appfirebaserest.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.api.FirebaseAPI;
import com.example.appfirebaserest.core.Constants;
import com.example.appfirebaserest.database.SharedPreferencesFactory;
import com.example.appfirebaserest.util.CheckNetworkConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignInActivity extends AppCompatActivity {

    //  Firebase
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //  Retrofit
    private FirebaseAPI firebaseAPI;

    //  Layouts
    private EditText et_email;
    private EditText et_password;
    private TextInputLayout til_email;
    private TextInputLayout til_password;
    private CheckBox cb_save_token;
    private MaterialDialog materialDialog;

    //  Parâmetros
    private String email;
    private String password;
    private String token;
    private String getToken;

    //  SharedPreferences
    private SharedPreferencesFactory preferencesFactory;

    //  Check Network Connection
    private CheckNetworkConnection checkNetworkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_save_token = (CheckBox) findViewById(R.id.cb_save_token);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_password = (TextInputLayout) findViewById(R.id.til_password);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    Log.d(Constants.TAG, "User exist");
                }else{
                    Log.d(Constants.TAG, "User null");
                }
            }
        };

        preferencesFactory = new SharedPreferencesFactory();
        getToken = preferencesFactory.getToken(this);
        if(getToken != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void signIn(final View view){
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);

        checkNetworkConnection = new CheckNetworkConnection(this);

        if(!checkNetworkConnection.isConnected()){
            Snackbar.make(view, "Sem internet", Snackbar.LENGTH_LONG).show();
            return;
        }if(email.isEmpty() || password.isEmpty()){
            if(email.isEmpty()){
                til_email.setError("Por favor, digite seu email");
            }if(password.isEmpty()){
                til_password.setError("Por favor, digite sua senha");
            }if(email.isEmpty() && password.isEmpty()){
                Snackbar.make(view, "Preencha todos os campos", Snackbar.LENGTH_LONG).show();
            }
            return;
        }if(!email.isEmpty() && !password.isEmpty()){
            materialDialog = new MaterialDialog.Builder(this)
                    .content("Aguarde...")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Snackbar.make(view, "Email/Senha inválidos", Snackbar.LENGTH_LONG).show();
                        materialDialog.dismiss();
                    }else{
                        materialDialog.dismiss();
                        if(cb_save_token.isChecked()){
                            getToken();
                            getUID();
                        }
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    public void toSignUp(View view){
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void getToken(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.getToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        token = task.getResult().getToken();
                        preferencesFactory = new SharedPreferencesFactory();
                        preferencesFactory.saveToken(SignInActivity.this, token);
                        Log.d(Constants.TAG, "Token: " + token);
                    } else {
                        Log.d(Constants.TAG, "Nenhum token encontrado");
                    }
                }
            });
        }
    }

    private void getUID(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(Constants.TAG, "UID:" + user.getUid() + "Email: " + user.getEmail() + "Nome: " + user.getDisplayName());
                } else {
                    Log.d(Constants.TAG, "Nenhuma UID encontrada");
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
