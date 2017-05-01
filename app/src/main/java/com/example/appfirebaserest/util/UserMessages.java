package com.example.appfirebaserest.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;
import com.example.appfirebaserest.R;

/**
 * Classe responsável por emitir menssagens ao usuário (Toast e Snackbar)
 */
public class UserMessages {

    public void snackbarDefault(String message, Context context, View view){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void snackbarSuccess(String message, Context context, View view){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.green500));
        snackbar.show();
    }

    public void snackbarError(String message, Context context, View view){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.redCustom));
        snackbar.show();
    }

    public void toastDefault(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void toastSuccess(String message, Context context){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackgroundColor(ContextCompat.getColor(context, R.color.green500));
        toast.show();
    }

    public void toastError(String message, Context context){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackgroundColor(ContextCompat.getColor(context, R.color.redCustom));
        toast.show();
    }

}
