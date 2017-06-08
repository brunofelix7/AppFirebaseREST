package com.example.appfirebaserest.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.core.Constants;
import com.example.appfirebaserest.core.MainController;
import com.example.appfirebaserest.core.SolicitationController;
import com.example.appfirebaserest.model.Solicitation;
import com.example.appfirebaserest.util.CheckNetworkConnection;
import com.example.appfirebaserest.util.Messages;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity realiza o envio de uma solicitação para o Firebase
 */
public class SolicitationActivity extends AppCompatActivity {

    //  Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    //  Layouts
    private EditText et_urgency;
    private Spinner s_consciencia, s_respiracao;

    //  Arrays
    private ArrayAdapter<String> arrayAdapterS1;
    private ArrayAdapter<String> arrayAdapterS2;
    private String[] list_consciencia = new String[4];
    private String[] list_respiracao = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitation);

        s_consciencia = (Spinner) findViewById(R.id.s_consciencia);
        s_respiracao = (Spinner) findViewById(R.id.s_respiracao);
        et_urgency = (EditText) findViewById(R.id.et_urgency);

        //  ARRAYS ADAPTER QUE SERÃO SETADOS NOS MEUS Apinner
        arrayAdapterS1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterS2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);

        //  MINHAS LISTAS DE STRINGS RECEBEM MEUS ARRAYS DOS MEUS RESOURCES (strings.xml)
        list_consciencia = getResources().getStringArray(R.array.niveis_consciencia);
        list_respiracao = getResources().getStringArray(R.array.niveis_respiracao);

        //  SETA OS ADAPTERS NOS Spinners
        s_consciencia.setAdapter(arrayAdapterS1);
        s_respiracao.setAdapter(arrayAdapterS2);

        //  ADICIONA OS ELEMENTOS DINAMICAMENTE NOS Spinners
        for (int i = 0; i < list_consciencia.length; i++) {
            arrayAdapterS1.add(list_consciencia[i]);
        }
        for (int i = 0; i < list_respiracao.length; i++) {
            arrayAdapterS2.add(list_respiracao[i]);
        }
    }


    /**
     * Direciona para a próxima página
     * @param view
     */
    public void nextPage(View view){
        int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        if(!CheckNetworkConnection.isNetworkAvailable(this, type)){
            Messages.snackbarError("Ops! Você está sem internet", this, view);
            return;
        }if(et_urgency.getText().toString().isEmpty() || s_consciencia.getSelectedItem().toString().equals("--Selecione--") || s_respiracao.getSelectedItem().toString().equals("--Selecione--")){
            Messages.snackbarError("Preencha todos os campos", this, view);
            return;
        }else {
            SolicitationController.getInstance().setUrgencia(et_urgency.getText().toString());
            SolicitationController.getInstance().setConsciencia(s_consciencia.getSelectedItem().toString());
            SolicitationController.getInstance().setRespiracao(s_respiracao.getSelectedItem().toString());
            Intent intent = new Intent(this, PhotoActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
