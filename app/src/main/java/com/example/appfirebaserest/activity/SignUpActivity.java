package com.example.appfirebaserest.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.util.CheckNetworkConnection;
import com.example.appfirebaserest.util.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity realiza o cadastro do usuário no Firebase
 */
public class SignUpActivity extends AppCompatActivity {

    //  Firebase
    private FirebaseAuth mAuth;

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

    //  REALIZA O CADASTRO DO USUÁRIO
    public void signUp(View view){
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        confirm_password = et_confirm_password.getText().toString();
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);
        til_confirm_password.setErrorEnabled(false);

        checkNetworkConnection = new CheckNetworkConnection(this);

        //  VALIDAÇÕES
        if(!checkNetworkConnection.isConnected()){
            Messages.snackbarDefault("Sem internet", this, view);
            return;
        }if(!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Messages.snackbarError("Formato de email inválido", this, view);
            return;
        }if(email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()){
            if(email.isEmpty()){
                til_email.setError("Por favor, digite seu email");
            }if(password.isEmpty()){
                til_password.setError("Por favor, digite sua senha");
            }if(confirm_password.isEmpty()){
                til_confirm_password.setError("Por favor, confirme sua senha");
            }if(email.isEmpty() && password.isEmpty() && confirm_password.isEmpty()){
                Messages.snackbarError("Preencha todos os campos", this, view);
            }
            return;
        }if(!password.equals(confirm_password)){
            Messages.snackbarError("As senhas sao diferentes", this, view);
            return;
        }if(password.length() < 6){
            Messages.snackbarError("A senha deve conter no mínimo 6 caracteres", this, view);
            return;
        }if(!email.isEmpty() && !password.isEmpty() && !confirm_password.isEmpty()){
            materialDialog = new MaterialDialog.Builder(this)
                    .content("Aguarde...")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            //  CRIA UM NOVO USUÁRIO NO FIREBASE
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Messages.toastSuccess("Cadastrado com sucesso", SignUpActivity.this);
                        finish();
                    }else{
                        Messages.toastError("Ops, por favor tente novamente", SignUpActivity.this);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  EVITA QUE OS ERROS NOS EditText CONTINUEM CASO O USUÁRIO SAIA E ABRA A APLICAÇÃO NOVAMENTE
        til_email.setErrorEnabled(false);
        til_password.setErrorEnabled(false);
        til_confirm_password.setErrorEnabled(false);
    }
}
