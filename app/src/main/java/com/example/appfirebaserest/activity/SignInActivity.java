package com.example.appfirebaserest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.api.FirebaseAPI;
import com.example.appfirebaserest.core.Constants;
import com.example.appfirebaserest.database.SharedPreferencesFactory;
import com.example.appfirebaserest.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignInActivity extends AppCompatActivity {

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
    private CheckBox cb_save_token;
    private ProgressDialog progressDialog;

    //  Parâmetros
    private String email;
    private String password;
    private String token;

    //  SharedPreferences
    private SharedPreferencesFactory preferencesFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_save_token = (CheckBox) findViewById(R.id.cb_save_token);

    }

    public void signIn(View view){
        email = et_email.getText().toString();
        password = et_password.getText().toString();

        //  VERIFICAR SE ESTÁ MARCADO O CHECKBOX
        //  SE TRUE, SALVAR TOKEN NAS PREFERÊNCIAS
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(SignInActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
        }if(cb_save_token.isChecked()){
            getToken();
            getUID();
        }else{
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Aguarde...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(SignInActivity.this, "Email/Senha inválidos.", Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog.dismiss();
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

    private void session(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void sendRequest(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        firebaseAPI = retrofit.create(FirebaseAPI.class);
        Call<Message> request = firebaseAPI.getMessages();
        request.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    Log.d(Constants.TAG, "Body: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    private void getUID(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(Constants.TAG, "UID:" + user.getUid() + user.getToken(false));
                } else {
                    Log.d(Constants.TAG, "Nenhuma UID encontrada");
                }
            }
        };
    }

    private void getToken(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
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


    /*private void setData(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        myRef.setValue("hello");
    }

    private void getData(){
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(Constants.TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(Constants.TAG, "Failed to read value.", error.toException());
            }
        });
    }*/


}
