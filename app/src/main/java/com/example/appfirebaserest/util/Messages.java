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
public class Messages {

    public static void snackbarDefault(String message, Context context, View view){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public static void snackbarSuccess(String message, Context context, View view){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.green500));
        snackbar.show();
    }

    public static void snackbarError(String message, Context context, View view){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.redCustom));
        snackbar.show();
    }

    public static void toastDefault(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void toastSuccess(String message, Context context){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackgroundColor(ContextCompat.getColor(context, R.color.green500));
        toast.show();
    }

    public static void toastError(String message, Context context){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackgroundColor(ContextCompat.getColor(context, R.color.redCustom));
        toast.show();
    }

}
