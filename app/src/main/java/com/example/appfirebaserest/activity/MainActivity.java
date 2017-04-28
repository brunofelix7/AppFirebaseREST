package com.example.appfirebaserest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.api.FirebaseAPI;
import com.example.appfirebaserest.api.FirebaseAPIConnection;
import com.example.appfirebaserest.core.Constants;
import com.example.appfirebaserest.database.SharedPreferencesFactory;
import com.example.appfirebaserest.model.Solicitation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    //  Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    //  Retrofit
    private FirebaseAPI firebaseAPI;

    //  SharedPreferences
    private SharedPreferencesFactory preferencesFactory;

    //  Layouts
    private ListView lv_list;
    private SwipeRefreshLayout srl_refresh;

    //  Arrays
    private ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        list.add("test1");
        list.add("test2");

        //  ListView
        lv_list = (ListView) findViewById(R.id.lv_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Posição: " + position, Toast.LENGTH_SHORT).show();
            }
        });

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
                    preferencesFactory = new SharedPreferencesFactory();
                    preferencesFactory.deletePreferences(MainActivity.this);
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).show();

    }

    private void refresh(){
        saveData();
        sendRequest();
        list.add("test3");
        lv_list.invalidateViews();
        srl_refresh.setRefreshing(false);
    }

    private void saveData(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        String dateFormat = simpleDateFormat.format(date);

        Solicitation solicitation = new Solicitation(-37.44, -57.11666, "Atropelamento", "Pendente", dateFormat);
        solicitation.save();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        String id = myRef.push().getKey();
        myRef.child("ocorrencias").child(id).setValue(solicitation);
    }

    private void sendRequest(){
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
                        /*for (String keys: hashMap.keySet()){
                            Log.d(Constants.TAG, "Keys: " + keys + "\n");
                        }
                        for (Solicitation solicitations : hashMap.values()){
                            Log.d(Constants.TAG, "Values: " + solicitations + "\n");
                        }*/
                        for ( Map.Entry<String, Solicitation> entry : hashMap.entrySet()) {
                            String key = entry.getKey();
                            Solicitation solicitation = entry.getValue();
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

}
