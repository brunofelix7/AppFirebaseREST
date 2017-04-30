package com.example.appfirebaserest.activity;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.appfirebaserest.R;
import com.example.appfirebaserest.model.Solicitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        arrayAdapterS1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterS2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        list_consciencia = getResources().getStringArray(R.array.niveis_consciencia);
        list_respiracao = getResources().getStringArray(R.array.niveis_respiracao);

        s_consciencia.setAdapter(arrayAdapterS1);
        s_respiracao.setAdapter(arrayAdapterS2);

        for(int i = 0; i < list_consciencia.length; i++){
            arrayAdapterS1.add(list_consciencia[i]);
        }

        for(int i = 0; i < list_respiracao.length; i++){
            arrayAdapterS2.add(list_respiracao[i]);
        }
    }

    private void saveData(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        String dateFormat = simpleDateFormat.format(date);

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();

        String id = myRef.push().getKey();
        Solicitation solicitation = new Solicitation(-37.44, -57.11666, "Atropelamento", null, null, "Pendente", dateFormat);

        myRef.child("ocorrencias").child(id).setValue(solicitation);
    }

}
