package com.example.appfirebaserest.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.core.Constants;
import com.example.appfirebaserest.core.MainController;
import com.example.appfirebaserest.core.SolicitationController;
import com.example.appfirebaserest.model.Solicitation;
import com.example.appfirebaserest.util.CheckNetworkConnection;
import com.example.appfirebaserest.util.Messages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity {

    //  Firebase
    private String id;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    //  Firebase Storage
    private Uri uri;
    private Bitmap bitmap;
    private StorageReference storageRef;
    private StorageReference storageRefChild;
    private ByteArrayOutputStream byteArray;
    private UploadTask uploadTask;
    private String photoName;
    private byte[] data;

    //  Layouts
    private Button b_get_photo;
    private Button b_send_photo;
    private ImageView iv_photo;
    private LinearLayout ll_photo;
    private MaterialDialog materialDialog;

    //  Intent Camera
    private Bitmap photoCaptured;

    //  Get Extras
    private Double lat = 0.0;
    private Double lng = 0.0;
    private String address = "null";
    private String urgencia = "null";
    private String consciencia = "null";
    private String respiracao = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        findViews();
        b_get_photo.setVisibility(View.VISIBLE);
        b_send_photo.setVisibility(View.GONE);

        storageRef = FirebaseStorage.getInstance().getReference();

    }

    private void findViews(){
        ll_photo = (LinearLayout) findViewById(R.id.ll_photo);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        b_get_photo = (Button) findViewById(R.id.b_get_photo);
        b_send_photo = (Button) findViewById(R.id.b_send_photo);
    }

    /**
     * Abre a câmera do smartphone
     */
    public void getPhoto(View view){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.REQUEST_CODE_IMAGE_CAPTURE);
    }

    /**
     * Recupera o retorno da intent (a foto), e coloca na ImageView
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_CODE_IMAGE_CAPTURE && resultCode == RESULT_OK){
            b_get_photo.setVisibility(View.GONE);
            b_send_photo.setVisibility(View.VISIBLE);
            uri = data.getData();
            Bundle extras = data.getExtras();
            photoCaptured = (Bitmap) extras.get("data");
            iv_photo.setImageBitmap(photoCaptured);
            ll_photo.setBackgroundColor(getResources().getColor(R.color.green500));
            //  Messages.toastDefault("Photo take successfully!", this);
        }
    }

    /**
     * Realiza o envio da imagem capturada para o Firebase Storage
     */
    public void sendPhoto(View view){
        int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        if(!CheckNetworkConnection.isNetworkAvailable(this, type)){
            Messages.snackbarError("Ops! Você está sem internet", this, view);
        }else{
            materialDialog = new MaterialDialog.Builder(this)
                    .content("Enviando...")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
            String dateFormat = simpleDateFormat.format(date);

            //  RECUPERA A INSTÂNCIA DA MINHA BASE DO FIREBASE
            mDatabase = FirebaseDatabase.getInstance();
            myRef = mDatabase.getReference();

            //  GERA UM HASH ÚNICO
            id = myRef.push().getKey();

            //  RECUPERA OS PARÂMETROS
            lat = MainController.getInstance().getLat();
            lng = MainController.getInstance().getLng();
            address = MainController.getInstance().getAddress();
            urgencia = SolicitationController.getInstance().getUrgencia();
            consciencia = SolicitationController.getInstance().getConsciencia();
            respiracao = SolicitationController.getInstance().getRespiracao();

            //  CRIA UM OBJETO RECEBENDO OS PARÂMETROS DA SOLICITAÇÃO
            Solicitation solicitation = new Solicitation(address, lat, lng, urgencia, consciencia, respiracao, "Pendente", dateFormat);

            //  CRIA UM NOVO FILHO (ocorrencias), UM OUTRO FILHO COM O HASH E SALVA O OBJETO COM OS DADOS DA SOLICITAÇÃO
            myRef.child("ocorrencias").child(id).setValue(solicitation);

            photoName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            storageRefChild = storageRef.child("photos/").child(id + "_" + photoName);
            iv_photo.setDrawingCacheEnabled(true);
            iv_photo.buildDrawingCache();
            bitmap = iv_photo.getDrawingCache();
            byteArray = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
            data = byteArray.toByteArray();

            uploadTask = storageRefChild.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Messages.toastError("Ops! Algo deu errado :/", PhotoActivity.this);
                    materialDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Messages.toastSuccess("Sucesso.", PhotoActivity.this);
                    materialDialog.dismiss();
                    new MaterialDialog.Builder(PhotoActivity.this)
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
            });
        }
    }
}
