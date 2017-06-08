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
    private static final String TABLE_USER = "userauth";

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
    private static final String ADDRESS = "address";

    //  PARâMETROS DA TABELA USER
    private static final String USER_ID = "id";
    private static final String USER_UID = "uid";
    private static final String USER_EMAIL = "email";

    //  QUERYS USER
    private static final String SQL_QUERY_DROP_USER = "DROP TABLE IF EXISTS " + TABLE_USER;
    private static final String SQL_QUERY_CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "(" +
            USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_UID + " TEXT, " +
            USER_EMAIL + " TEXT" +
            ")";

    //  QUERYS SOLICITATION
    private static final String SQL_QUERY_SELECT = "SELECT * FROM " + TABLE_SOLICITATION;
    private static final String SQL_QUERY_DROP = "DROP TABLE IF EXISTS " + TABLE_SOLICITATION;
    private static final String SQL_QUERY_CREATE_TABLE_SOLICITATION = "CREATE TABLE " + TABLE_SOLICITATION + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIREBASE_ID + " TEXT, " +
            STATUS + " TEXT, " +
            URGENCY + " TEXT, " +
            NIVEL_CONSCIENCIA + " TEXT, " +
            NIVEL_RESPIRACAO + " TEXT, " +
            LATITUDE + " REAL, " +
            LONGITUDE + " REAL, " +
            ADDRESS + " TEXT, " +
            DATE + " TEXT" +
            ")";

    //  CRIA A BASE DE DADOS AO CRIAR UMA INSTÂNCIA DESSA CLASSE
    public SQLiteFactory(Context context){
        super(context, DATABASE, null, 1);
    }

    //  CRIA AS TABELAS
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_QUERY_CREATE_TABLE_SOLICITATION);
        db.execSQL(SQL_QUERY_CREATE_TABLE_USER);
    }


    //  ATUALIZA AS DEFINIÇÕES DO BANCO DE DADOS CASO A VERSÃO SEJA ALTERADA
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_QUERY_DROP);
        db.execSQL(SQL_QUERY_DROP_USER);
        onCreate(db);
    }

    //  SALVA O UID E EMAIL DO USUÁRIO DO FIREBASE NA TABELA user
    public boolean saveUser(String uid, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put(USER_UID, uid);
        contentValues.put(USER_EMAIL, email);
        Long result = db.insert(TABLE_USER, null, contentValues);
        return result != -1;
    }

    //  EXCLUÍ A TABELA user
    public void dropTableUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_QUERY_DROP_USER);
    }

    //  CRIA A TABELA user
    public void createTableUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_QUERY_CREATE_TABLE_USER);
    }

    //  SAVA OS DADOS DA SOLICITAÇÃO NA TABELA solicitation
    public boolean save(String firebaseId, String status, String urgency, String nivel_consciencia, String nivel_respiracao, Double latitude, Double longitude, String address, String date){
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

    //  BUSCA TODAS AS SOLICITAÇÕES NA TABELA solicitation
    public Cursor findAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        result = db.rawQuery(SQL_QUERY_SELECT, null);
        return result;
    }

    //  EXCLUÍ A TABELA solicitation
    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_QUERY_DROP);
    }

    //  CRIA A TABELA solicitation
    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_QUERY_CREATE_TABLE_SOLICITATION);
    }

}
