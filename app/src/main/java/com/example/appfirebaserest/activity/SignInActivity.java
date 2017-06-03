package com.example.appfirebaserest.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.example.appfirebaserest.database.SQLiteFactory;
import com.example.appfirebaserest.database.SharedPreferencesFactory;
import com.example.appfirebaserest.util.CheckNetworkConnection;
import com.example.appfirebaserest.util.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

/**
 * Activity realiza a autenticação do usuário no Firebase
 */
public class SignInActivity extends AppCompatActivity {

    //  Firebase
    private FirebaseUser user;
    private FirebaseAuth mAuth;
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

    //  SQLiteFactory
    private SQLiteFactory sqLiteFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_save_token = (CheckBox) findViewById(R.id.cb_save_token);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_password = (TextInputLayout) findViewById(R.id.til_password);

        //  CHAMADA DE MÉTODOS
        checkUserInstance();
        checkToken();

    }

    //  REALIZA A AUTENTICAÇÃO DO USUÁRIO
    public void signIn(final View view){
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);

        checkNetworkConnection = new CheckNetworkConnection(this);

        //  VALIDAÇÕES
        if(!checkNetworkConnection.isConnected()){
            Messages.snackbarDefault("Sem internet", this, view);
            return;
        }if(!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Messages.snackbarError("Formato de email inválido", this, view);
            return;
        }if(email.isEmpty() || password.isEmpty()){
            if(email.isEmpty()){
                til_email.setError("Por favor, digite seu email");
            }if(password.isEmpty()){
                til_password.setError("Por favor, digite sua senha");
            }if(email.isEmpty() && password.isEmpty()){
                Messages.snackbarError("Preencha todos os campos", this, view);
            }
            return;
        }if(!email.isEmpty() && !password.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            materialDialog = new MaterialDialog.Builder(this)
                    .content("Aguarde...")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            //  REALIZA A AUTENTICAÇÃO COM O FIREBASE
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Messages.snackbarError("Email/Senha inválidos", SignInActivity.this, view);
                        materialDialog.dismiss();
                    }else{
                        materialDialog.dismiss();
                        //  VERIFICA SE O USUÁRIO MARCOU O CheckBox, SE SIM, O TOKEN É SALVO NAS PREFERÊNCIAS DO USUARIO
                        //  E SALVA TAMBÉM O UID E EMAIL DO USUÁRIO CADASTRADO NO FIREBASE
                        if(cb_save_token.isChecked()){
                            getToken();
                            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                Log.d(Constants.TAG, "User not exist");
                            }else{
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                if(uid != null && email != null){
                                    sqLiteFactory = new SQLiteFactory(SignInActivity.this);
                                    sqLiteFactory.createTableUser();
                                    sqLiteFactory.saveUser(uid, email);
                                }
                            }
                        }
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    //  VAI PARA TELA DE CADASTRO
    public void toSignUp(View view){
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    //  PEGA O TOKEN GERADO PELO FIREBASE E SALVA NAS PREFERÊNCIAS
    //  O user.getToken(false) É PARA QUE O FIREBASE NÃO ATUALIZE O TOKEN GERANDO UM NOVO
    private void getToken(){
        user = FirebaseAuth.getInstance().getCurrentUser();
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

    //  MÉTODO UTILIZADO APENAS PARA DEBUGAR SE A INSTÂNCIA DO USUÁRIO EXISTE OU NÃO JUNTO AO FIREBASE
    private void checkUserInstance(){
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
    }

    //  VERIFICA SE EXISTE UM TOKEN NAS PREFERÊNCIAS DO USUÁRIO, SE SIM, LIBERA O USUÁRIO PARA TELA PRINCIPAL
    private void checkToken(){
        preferencesFactory = new SharedPreferencesFactory();
        getToken = preferencesFactory.getToken(this);
        if(getToken != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  EVITA QUE OS ERROS NOS EditText CONTINUEM CASO O USUÁRIO SAIA E ABRA A APLICAÇÃO NOVAMENTE
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
