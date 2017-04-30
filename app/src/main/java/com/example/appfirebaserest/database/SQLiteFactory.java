package com.example.appfirebaserest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe de definição e conexão com o SQLite
 */
public class SQLiteFactory extends SQLiteOpenHelper {

    //  SQLite
    private Cursor result;
    private ContentValues contentValues;

    //  PARÂMETROS DO BANCO DE DADOS
    private static final String DATABASE = "firebaseapp.db";
    private static final String TABLE_SOLICITATION = "solicitation";

    //  PARÂMETROS DA TABELA SOLICITATION
    private static final String ID = "id";
    private static final String FIREBASE_ID = "firebase_id";
    private static final String STATUS = "status";
    private static final String URGENCY = "urgency";
    private static final String NIVEL_CONSCIENCIA = "nivel_consciencia";
    private static final String NIVEL_RESPIRACAO = "nivel_respiracao";
    private static final String DATE = "date";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    //  QUERYS
    private static final String SQL_QUERY_SELECT = "SELECT * FROM " + TABLE_SOLICITATION;
    private static final String SQL_QUERY_DROP = "DROP TABLE IF EXISTS " + TABLE_SOLICITATION;
    private static final String SQL_QUERY_CREATE_TABLE_USER = "CREATE TABLE " + TABLE_SOLICITATION + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIREBASE_ID + " TEXT, " +
            STATUS + " TEXT, " +
            URGENCY + " TEXT, " +
            NIVEL_CONSCIENCIA + " TEXT, " +
            NIVEL_RESPIRACAO + " TEXT, " +
            LATITUDE + " REAL, " +
            LONGITUDE + " REAL, " +
            DATE + " TEXT" +
            ")";

    public SQLiteFactory(Context context){
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_QUERY_CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_QUERY_DROP);
        onCreate(db);
    }

    public boolean save(String firebaseId, String status, String urgency, String nivel_consciencia, String nivel_respiracao, Double latitude, Double longitude, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put(FIREBASE_ID, firebaseId);
        contentValues.put(STATUS, status);
        contentValues.put(URGENCY, urgency);
        contentValues.put(NIVEL_CONSCIENCIA, nivel_consciencia);
        contentValues.put(NIVEL_RESPIRACAO, nivel_respiracao);
        contentValues.put(LATITUDE, latitude);
        contentValues.put(LONGITUDE, longitude);
        contentValues.put(DATE, date);
        Long result = db.insert(TABLE_SOLICITATION, null, contentValues);
        return result != -1;
    }

    public Cursor findAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        result = db.rawQuery(SQL_QUERY_SELECT, null);
        return result;
    }

    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_QUERY_DROP);
    }

    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_QUERY_CREATE_TABLE_USER);
    }

}
