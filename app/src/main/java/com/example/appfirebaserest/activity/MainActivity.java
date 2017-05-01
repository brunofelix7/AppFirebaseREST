package com.example.appfirebaserest.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.appfirebaserest.util.CheckNetworkConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //  Firebase
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //  Retrofit
    private FirebaseAPI firebaseAPI;

    //  SharedPreferences
    private SharedPreferencesFactory preferencesFactory;

    //  Check Network Connection
    private CheckNetworkConnection checkNetworkConnection;

    //  SQLite
    private SQLiteFactory sqLiteFactory;

    //  Layouts
    private ListView lv_list;
    private MaterialDialog materialDialog;
    private SwipeRefreshLayout srl_refresh;

    //  Arrays
    private MyAdapter myAdapter;
    private HashMap<String, Solicitation> map;

    //  Get From SQLite
    private Solicitation solicitation;
    private HashMap<String, Solicitation> hashMap;

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

        //  ListView
        lv_list = (ListView) findViewById(R.id.lv_list);

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
        getSolicitations();
        lv_list.invalidateViews();
        srl_refresh.setRefreshing(false);
    }

    private void listFromFirebase(){
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Por favor, aguarde")
                .content("Carregando...")
                .cancelable(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        //  Cria a base de dados
        sqLiteFactory = new SQLiteFactory(MainActivity.this);

        //  Exclui a tabela se ela já existir
        sqLiteFactory.dropTable();

        //  Cria a tabela novamente
        sqLiteFactory.createTable();

        new Thread(){
            @Override
            public void run() {
                super.run();
                firebaseAPI = FirebaseAPIConnection.getConnection().create(FirebaseAPI.class);
                Call<HashMap<String, Solicitation>> request = firebaseAPI.getSolicitations();
                try {
                    map = request.execute().body();
                } catch (IOException e) {
                    Log.d(Constants.TAG, "IOException " + e.getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(map != null) {
                            Log.d(Constants.TAG, "Body: " + map);
                            for (Map.Entry<String, Solicitation> entry : map.entrySet()) {
                                String key = entry.getKey();
                                Solicitation solicitation = entry.getValue();

                                sqLiteFactory.save(key, solicitation.getStatus(), solicitation.getUrgency(), solicitation.getNivel_consciencia(), solicitation.getNivel_respiracao(), solicitation.getLatitude(), solicitation.getLongitude(), solicitation.getDate());
                                Log.d(Constants.TAG, "Keys: " + key + "\n");
                                Log.d(Constants.TAG, "Values: " + solicitation + "\n");
                            }
                            myAdapter = new MyAdapter(MainActivity.this, map);
                            lv_list.setAdapter(myAdapter);
                        }
                        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(MainActivity.this, "Posição: " + position, Toast.LENGTH_SHORT).show();
                            }
                        });
                        materialDialog.dismiss();
                    }
                });
            }
        }.start();

    }

    private void listFromSQLite(){
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Por favor, aguarde")
                .content("Carregando...")
                .cancelable(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        sqLiteFactory = new SQLiteFactory(this);
        Cursor result = sqLiteFactory.findAll();

        hashMap = new HashMap<>();

        if (result.moveToFirst()) {
            while (!result.isAfterLast()) {
                solicitation = new Solicitation();
                solicitation.setFirebaseId(result.getString(1));
                solicitation.setStatus(result.getString(2));
                solicitation.setUrgency(result.getString(3));
                solicitation.setNivel_consciencia(result.getString(4));
                solicitation.setNivel_respiracao(result.getString(5));
                solicitation.setDate(result.getString(8));
                hashMap.put(result.getString(1), solicitation);
                result.moveToNext();
            }
            myAdapter = new MyAdapter(MainActivity.this, hashMap);
            lv_list.setAdapter(myAdapter);
        }
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Posição: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        materialDialog.dismiss();
    }

    private void getSolicitations(){
        checkNetworkConnection = new CheckNetworkConnection(this);
        if(!checkNetworkConnection.isConnected()){
            listFromSQLite();
            Log.d(Constants.TAG, "Sem internet");
        }else{
            listFromFirebase();
            Log.d(Constants.TAG, "Conectado");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSolicitations();
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
