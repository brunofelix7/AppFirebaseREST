package com.example.appfirebaserest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.adapter.MyAdapter;
import com.example.appfirebaserest.api.FirebaseAPI;
import com.example.appfirebaserest.api.FirebaseAPIConnection;
import com.example.appfirebaserest.core.Constants;
import com.example.appfirebaserest.database.SQLiteFactory;
import com.example.appfirebaserest.database.SharedPreferencesFactory;
import com.example.appfirebaserest.model.Solicitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_URGENCY = "urgency";
    public static final String KEY_CONSCIENCIA = "nivel_consciencia";
    public static final String KEY_RESPIRACAO = "nivel_respiracao";
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATE = "date";

    //  Firebase
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //  Retrofit
    private FirebaseAPI firebaseAPI;

    //  SharedPreferences
    private SharedPreferencesFactory preferencesFactory;

    //  SQLite
    private SQLiteFactory sqLiteFactory;

    //  Layouts
    private ListView lv_list;
    private MaterialDialog materialDialog;
    private SwipeRefreshLayout srl_refresh;

    //  Arrays
    private MyAdapter myAdapter;
    private ArrayList<HashMap<String, String>> mList;
    private ArrayList<HashMap<String, Solicitation>> mListBody;
    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    Log.d(Constants.TAG, "User exist");
                }else{
                    Log.d(Constants.TAG, "User null");
                }
            }
        };

        /*//  Enquanto carrega minha lista
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Por favor, aguarde")
                .content("Carregando...")
                .cancelable(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();*/

        new Thread(){
            @Override
            public void run() {
                super.run();
                firebaseAPI = FirebaseAPIConnection.getConnection().create(FirebaseAPI.class);
                Call<HashMap<String, Solicitation>> request = firebaseAPI.getSolicitations();
                try {
                    HashMap<String, Solicitation> requestBody = request.execute().body();
                    if(requestBody != null) {
                        Log.d(Constants.TAG, "Body: " + requestBody);
                        for (Map.Entry<String, Solicitation> entry : requestBody.entrySet()) {
                            String key = entry.getKey();
                            Solicitation solicitation = entry.getValue();
                            mListBody = new ArrayList<>();
                            mListBody.add(requestBody);

                            Log.d(Constants.TAG, "Keys: " + key + "\n");
                            Log.d(Constants.TAG, "Values: " + solicitation + "\n");
                        }
                    }
                } catch (IOException e) {
                    Log.d(Constants.TAG, "IOException " + e.getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter = new MyAdapter(MainActivity.this, mListBody);
                        lv_list = (ListView) findViewById(R.id.lv_list);
                        lv_list.setAdapter(myAdapter);
                        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(MainActivity.this, "Posição: " + position, Toast.LENGTH_SHORT).show();
                            }
                        });
                        //materialDialog.dismiss();
                    }
                });
            }
        }.start();


        /*//  ListView
        mList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            map = new HashMap<>();
            map.put(KEY_URGENCY, "Urgência");
            map.put(KEY_CONSCIENCIA, "Nível de Consciência");
            map.put(KEY_RESPIRACAO, "Nível de Respiração");
            map.put(KEY_STATUS, "Pendente");
            map.put(KEY_DATE, "30/04/2017 - 00:30:00");
            mList.add(map);
        }
        myAdapter = new MyAdapter(this, mList);
        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.setAdapter(myAdapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Posição: " + position, Toast.LENGTH_SHORT).show();
            }
        });*/

        //  SwipeRefreshLayout
        srl_refresh = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        srl_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SolicitationActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_solicitations) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        new MaterialDialog.Builder(this)
            .title("Deseja realmente sair?")
            .content("Você será deslogado.")
            .positiveText("Sim")
            .negativeText("Não")
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    mAuth.signOut();
                    preferencesFactory = new SharedPreferencesFactory();
                    preferencesFactory.deletePreferences(MainActivity.this);
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).show();

    }

    private void refresh(){
        //  VERIFICAR SE TEM CONEXÃO
        sendSolicitation();
        getFromFirebase();
        lv_list.invalidateViews();
        srl_refresh.setRefreshing(false);
    }

    private void sendSolicitation(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        String dateFormat = simpleDateFormat.format(date);

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();

        String id = myRef.push().getKey();
        Solicitation solicitation = new Solicitation(-7.1858017, -34.8901212, "Atropelamento", "Consciênte", "Fraca", "Pendente", dateFormat);

        myRef.child("ocorrencias").child(id).setValue(solicitation);
    }

    private void getFromFirebase(){
        //  Cria a base de dados
        sqLiteFactory = new SQLiteFactory(MainActivity.this);

        //  Exclui a tabela se ela já existir
        sqLiteFactory.dropTable();

        //  Cria a tabela novamente
        sqLiteFactory.createTable();
        firebaseAPI = FirebaseAPIConnection.getConnection().create(FirebaseAPI.class);
        Call<HashMap<String, Solicitation>> request = firebaseAPI.getSolicitations();
        request.enqueue(new Callback<HashMap<String, Solicitation>>() {

            @Override
            public void onResponse(Call<HashMap<String, Solicitation>> call, Response<HashMap<String, Solicitation>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){

                        HashMap<String, Solicitation> hashMap;
                        hashMap = response.body();
                        Log.d(Constants.TAG, "Body: " + hashMap);
                        for ( Map.Entry<String, Solicitation> entry : hashMap.entrySet()) {
                            String key = entry.getKey();
                            Solicitation solicitation = entry.getValue();

                            //  Salva os dados atualizados no SQLite
                            sqLiteFactory.save(key, solicitation.getStatus(), solicitation.getUrgency(), solicitation.getNivel_consciencia(), solicitation.getNivel_respiracao(), solicitation.getLatitude(), solicitation.getLongitude(), solicitation.getDate());
                            Log.d(Constants.TAG, "Keys: " + key + "\n");
                            Log.d(Constants.TAG, "Values: " + solicitation + "\n");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Solicitation>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
