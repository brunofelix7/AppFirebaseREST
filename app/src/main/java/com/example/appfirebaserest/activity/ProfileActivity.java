package com.example.appfirebaserest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;

/**
 * Activity do perfil do usuário
 */
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    //  NÃO CLIQUE NA IMAGEM DO PERFIL DO USUÁRIO
    public void teFode(View view){
        new MaterialDialog.Builder(this)
                .title("Te Fode!")
                .content("")
                .cancelable(true)
                .show();
    }

}
