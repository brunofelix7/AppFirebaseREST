package com.example.appfirebaserest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.core.Constants;
import com.example.appfirebaserest.util.Messages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class PhotoActivity extends AppCompatActivity {

    private Uri uri;
    private Button b_get_photo;
    private Button b_send_photo;
    private ImageView iv_photo;
    private Bitmap photoCaptured;
    private StorageReference mStorageRef;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        findViews();
        b_get_photo.setVisibility(View.VISIBLE);
        b_send_photo.setVisibility(View.GONE);

        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    private void findViews(){
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        b_get_photo = (Button) findViewById(R.id.b_get_photo);
        b_send_photo = (Button) findViewById(R.id.b_send_photo);
    }

    public void getPhoto(View view){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.REQUEST_CODE_IMAGE_CAPTURE);
    }

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
            Messages.toastDefault("Photo take successfully!", this);
        }
    }

    public void sendPhoto(View view){
        materialDialog = new MaterialDialog.Builder(this)
                .content("Enviando...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        StorageReference ref = mStorageRef.child("photos").child(uri.getLastPathSegment());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Messages.toastSuccess("Upload successfully", PhotoActivity.this);
                materialDialog.dismiss();
            }
        });
    }
}
