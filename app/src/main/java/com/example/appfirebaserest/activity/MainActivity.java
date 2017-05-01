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
import android.widget.TextView;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

/**
 * Activity principal
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //  Firebase
    private FirebaseAuth mAuth;
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
    private TextView tv_empty;
    private ListView lv_list;
    private MaterialDialog materialDialog;
    private SwipeRefreshLayout srl_refresh;

    //  Arrays
    private MyAdapter myAdapter;
    private HashMap<String, Solicitation> mSolicitations;

    //  Get From SQLite
    private Solicitation solicitation;
    private HashMap<String, Solicitation> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUserInstance();

        lv_list = (ListView) findViewById(R.id.lv_list);
        tv_empty = (TextView) findViewById(R.id.tv_empty);

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

        //  VAI PARA TELA DE SOLICITAÇÕES
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

    //  MÉTODO PADRÃO DO MENU DRAWER
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //  MÉTODO PADRÃO DO OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //  MÉTODO PADRÃO DO OPTIONS MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //  MÉTODO PADRÃO DO MENU DRAWER
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_solicitations) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_info) {
            new MaterialDialog.Builder(this)
                    .title("Equipe")
                    .content("Bruno Felix\nEmerson Lemos\nJoão Marcus")
                    .cancelable(true)
                    .show();
        } else if (id == R.id.nav_exit) {
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //  REALIZA O LOGOUT DA APLICAÇÃO
    public void signOut() {
        new MaterialDialog.Builder(this)
            .title("Deseja realmente sair?")
            .content("Você será deslogado.")
            .positiveText("Sim")
            .negativeText("Não")
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    //  APAGA A TABELA user DO SQLITE,
                    sqLiteFactory = new SQLiteFactory(MainActivity.this);
                    sqLiteFactory.dropTableUser();

                    //  LIMPA AS PREFERÊNCIAS DO USUÁRIO (token)
                    preferencesFactory = new SharedPreferencesFactory();
                    preferencesFactory.deletePreferences(MainActivity.this);

                    //  CHAMA O MÉTODO signOut() NATIVO DO FIREBASE
                    mAuth.signOut();

                    //  E VOLTA PARA TELA DE LOGIN
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).show();

    }

    //  MÉTODO USADO NO SwipeRefreshLayout PARA ATUALIZAR O ListView
    private void refresh(){
        lv_list.invalidateViews();
        srl_refresh.setRefreshing(true);
        getSolicitations();
    }

    //  MÉTODO CHAMADO CASO O USUÁRIO ESTEJA CONECTADO A INTERNET
    private void listFromFirebase(){
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Por favor, aguarde")
                .content("Carregando...")
                .cancelable(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        //  CRIA A BASE DE DADOS
        sqLiteFactory = new SQLiteFactory(MainActivity.this);

        //  EXCLUÍ A TABELA SE ELA JÁ EXISTIR
        sqLiteFactory.dropTable();

        //  CRIA A TABELA NOVAMENTE
        sqLiteFactory.createTable();

        //  REALIZA EM UMA OUTRA THREAD A CHAMADA SINCRONA A API DO FIREBASE
        //  EVITA QUE A THREAD PRINCIPAL FIQUE TRAVADA ENQUANTO ESSA OPERAÇÃO NÃO SEJA CONCLUÍDA
        new Thread(){
            @Override
            public void run() {
                super.run();
                firebaseAPI = FirebaseAPIConnection.getConnection().create(FirebaseAPI.class);
                Call<HashMap<String, Solicitation>> request = firebaseAPI.getSolicitations();
                try {
                    //  RECUPERA O CORPO DA MINHA REQUISIÇÃO
                    mSolicitations = request.execute().body();
                } catch (IOException e) {
                    Log.d(Constants.TAG, "IOException " + e.getMessage());
                }

                //  ATUALIZA A THREAD PRINCIPAL DO USUÁRIO AO TÉRMINO DA CHAMADA
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mSolicitations != null) {
                            lv_list.setVisibility(View.VISIBLE);
                            tv_empty.setVisibility(View.GONE);
                            Log.d(Constants.TAG, "Body: " + mSolicitations);
                            for (Map.Entry<String, Solicitation> entry : mSolicitations.entrySet()) {
                                //  RECUPERO AS CHAVES, NO CASO OS HASHES GERADOS PELO FIREBASE PARA CADA SOLICITAÇÃO
                                String key = entry.getKey();

                                //  RECUPERO OS DADOS DAS SOLICITAÇÕES
                                Solicitation solicitation = entry.getValue();

                                //  SALVA NO SQLITE
                                sqLiteFactory.save(key, solicitation.getStatus(), solicitation.getUrgency(), solicitation.getNivel_consciencia(), solicitation.getNivel_respiracao(), solicitation.getLatitude(), solicitation.getLongitude(), solicitation.getDate());
                                Log.d(Constants.TAG, "Keys: " + key + "\n");
                                Log.d(Constants.TAG, "Values: " + solicitation + "\n");
                            }

                            //  SETA MINHA LISTA DE SOLICITAÇÕES NO MEU ADAPTER
                            myAdapter = new MyAdapter(MainActivity.this, mSolicitations);

                            //  SETA MEU ADAPTER NA MINHA ListView
                            lv_list.setAdapter(myAdapter);
                        }else{
                            //  CASO A BASE DO FIREBASE ESTEJA VAZIA
                            tv_empty.setVisibility(View.VISIBLE);
                            lv_list.setVisibility(View.GONE);
                            tv_empty.setText("Você não possuí solicitações.");

                            //  REMOVE AS ANIMAÇÕES DE PROGRESSO
                            materialDialog.dismiss();
                            srl_refresh.setRefreshing(false);
                            Log.d(Constants.TAG, "Lista vazia!!!");
                        }

                        //  CAPTURA A POSIÇÃO DOS CLIQUE NO ListView (Não utilizado)
                        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(MainActivity.this, "Posição: " + position, Toast.LENGTH_SHORT).show();
                            }
                        });

                        //  REMOVE AS ANIMAÇÕES DE PROGRESSO
                        materialDialog.dismiss();
                        srl_refresh.setRefreshing(false);
                    }
                });
            }
        }.start();

    }

    //  MÉTODO CHAMADO CASO O USUÁRIO NÃO ESTEJA CONECTADO A INTERNET
    private void listFromSQLite(){
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Por favor, aguarde")
                .content("Carregando...")
                .cancelable(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        //  ACESSA O SQLITE E FAZ UMA BUSCA NA TABELA solicitation
        sqLiteFactory = new SQLiteFactory(this);
        Cursor result = sqLiteFactory.findAll();
        hashMap = new HashMap<>();

        //  CASO A BASE DO SQLITE ESTEJA VAZIA
        if(result.getCount() == 0){
            Log.d(Constants.TAG, "Lista vazia!!!");
            tv_empty.setVisibility(View.VISIBLE);
            lv_list.setVisibility(View.GONE);
            tv_empty.setText("Você não possuí solicitações.");
        }
        //  CASO CONTENHA DADOS SQLITE
        if (result.moveToFirst()) {
            lv_list.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);

            //  FAZ O PROCESSO DE MOVER O CURSOR ATÉ A ÚLTIMA LINHA DO BANCO DE DADOS
            while (!result.isAfterLast()) {
                solicitation = new Solicitation();
                solicitation.setFirebaseId(result.getString(1));
                solicitation.setStatus(result.getString(2));
                solicitation.setUrgency(result.getString(3));
                solicitation.setNivel_consciencia(result.getString(4));
                solicitation.setNivel_respiracao(result.getString(5));
                solicitation.setDate(result.getString(8));

                //  VAI ADICIONANDO OS OBJETOS EM UM HashMap<Chave doFirebase, Dados da solicitação>
                hashMap.put(result.getString(1), solicitation);
                result.moveToNext();
            }
            //  SETA MINHA LISTA DE SOLICITAÇÕES VINDAS DO SQLITE NO MEU ADAPTER
            myAdapter = new MyAdapter(MainActivity.this, hashMap);

            //  SETA MEU ADAPTER NA MINHA ListView
            lv_list.setAdapter(myAdapter);
        }

        //  CAPTURA A POSIÇÃO DOS CLIQUE NO ListView (Não utilizado)
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Posição: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        //  REMOVE AS ANIMAÇÕES DE PROGRESSO
        materialDialog.dismiss();
        srl_refresh.setRefreshing(false);
    }

    //  REALIZA A CHAMADA DE CADA MÉTODO ESPECÍFICO, DEPENDENDO SE O USUÁRIO TEM CONEXÃO COM A INTERNET OU NÃO
    private void getSolicitations(){
        checkNetworkConnection = new CheckNetworkConnection(this);

        //  SE NÃO TEM INTERNET, LISTA DO SQLITE
        if(!checkNetworkConnection.isConnected()){
            listFromSQLite();
            Log.d(Constants.TAG, "Sem internet");
        }
        //  SE TEM INTERNET, LISTA DIRETO DA MINHA API DO FIREBASE
        else{
            listFromFirebase();
            Log.d(Constants.TAG, "Conectado");
        }
    }

    //  MÉTODO UTILIZADO APENAS PARA DEBUGAR SE A INSTÂNCIA DO USUÁRIO EXISTE OU NÃO JUNTO AO FIREBASE
    private void checkUserInstance(){
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  CHAMA O MÉTODO SEMPRE QUE ELE ABRE A APLICAÇÃO
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
