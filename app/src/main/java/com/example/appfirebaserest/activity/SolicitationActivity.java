package com.example.appfirebaserest.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
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

    //  Check Network Connection
    private CheckNetworkConnection checkNetworkConnection;

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
        for(int i = 0; i < list_consciencia.length; i++){
            arrayAdapterS1.add(list_consciencia[i]);
        }
        for(int i = 0; i < list_respiracao.length; i++){
            arrayAdapterS2.add(list_respiracao[i]);
        }
    }

    //  MÉTODO QUE ENVIA UMA SOLICITAÇÃO PARA O FIREBASE
    public void sendSolicitation(View view){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String dateFormat = simpleDateFormat.format(date);

        //  VALIDAÇÕES
        if(et_urgency.getText().toString().isEmpty() || s_consciencia.getSelectedItem().toString().equals("--Selecione--") || s_respiracao.getSelectedItem().toString().equals("--Selecione--")){
            Messages.snackbarError("Preencha todos os campos", this, view);
            return;
        }else {

            //  RECUPERA A INSTÂNCIA DA MINHA BASE DO FIREBASE
            mDatabase = FirebaseDatabase.getInstance();
            myRef = mDatabase.getReference();

            //  GERA UM HASH ÚNICO
            String id = myRef.push().getKey();

            //  CRIA UM OBJETO RECEBENDO OS PARÂMETROS DA SOLICITAÇÃO
            Solicitation solicitation = new Solicitation(-37.44, -57.11666, et_urgency.getText().toString(), s_consciencia.getSelectedItem().toString(), s_respiracao.getSelectedItem().toString(), "Pendente", dateFormat);

            //  CRIA UM NOVO FILHO (ocorrencias), UM OUTRO FILHO COM O HASH E SALVA O OBJETO COM OS DADOS DA SOLICITAÇÃO
            myRef.child("ocorrencias").child(id).setValue(solicitation);

            //  CASO O USUÁRIO NÃO POSSUA INTERNET, DE FORMA NATIVA, O FIREBASE GUARDA A REQUISIÇÃO
            //  E FAZ O ENVIO ASSIM QUE O USUÁRIO TIVER UMA CONEXÃO COM A INTERNET NOVAMENTE
            checkNetworkConnection = new CheckNetworkConnection(this);
            if (!checkNetworkConnection.isConnected()) {

                //  NOTIFICA AO USUÁRIO
                new MaterialDialog.Builder(this)
                        .title("Ops! Sem internet.")
                        .content("Sua solicitação será enviada quando uma conexão com a internet for estabelecida.")
                        .positiveText("OK")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        }).show();
            } else {

                //  NOTIFICA AO USUÁRIO
                new MaterialDialog.Builder(this)
                        .title("Enviado!")
                        .content("Solicitação encaminhada com sucesso.")
                        .positiveText("OK")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        }).show();
            }
        }

    }

}
