package com.example.appfirebaserest.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.api.FirebaseAPI;
import com.example.appfirebaserest.database.SQLiteFactory;
import com.example.appfirebaserest.database.SharedPreferencesFactory;
import com.example.appfirebaserest.util.CheckNetworkConnection;
import com.example.appfirebaserest.util.MyListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity principal
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //  Layouts
    private FloatingActionButton fab;
    private View network_disconnect;

    //  Google Maps API
    private GoogleMap googleMaps;
    private CameraUpdate cameraUpdate;
    private MarkerOptions markerOptions;
    private Marker marker;
    private LatLng latLng;
    private double latitude;
    private double longitude;

    //  Google API Client
    private GoogleApiClient googleApiClient;

    //  Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //  Retrofit
    private FirebaseAPI firebaseAPI;

    //  SharedPreferences
    private SharedPreferencesFactory preferencesFactory;

    //  SQLite
    private SQLiteFactory sqLiteFactory;

    //  BroadcastReceiver
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    //  GPS
    private LocationManager locationManager;
    private MyListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        mapInit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    protected void onResume() {
        super.onResume();
        checkInternet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void findViews() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        network_disconnect = findViewById(R.id.network_disconnect);
        network_disconnect.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
    }

    private void getLocation() {
        listener = new MyListener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    /**
     * Verifica em realtime o status da internet
     */
    private void checkInternet(){
        intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
                if(CheckNetworkConnection.isNetworkAvailable(context, type)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            network_disconnect.setVisibility(View.GONE);
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
                    //  Messages.toastSuccess("Conectado", MainActivity.this);
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            network_disconnect.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.GONE);
                        }
                    });
                    //  Messages.toastError("Sem internet", MainActivity.this);
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onMapReady(GoogleMap googleMaps) {
        this.googleMaps = googleMaps;
        double lat = -7.1194169;
        double lng = -34.8592634;
        goTolocation(lat, lng, 16f);
        setMarker("Joao Pessoa", "Estou aqui", new LatLng(lat, lng));
        //  ADICIONA UMA JANELA PERSONALIZADA NO MAPA
        if (this.googleMaps != null) {
            this.googleMaps.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.map_info, null);

                    TextView tv_location = (TextView) view.findViewById(R.id.tv_location);
                    TextView tv_snnipet = (TextView) view.findViewById(R.id.tv_snnipet);
                    TextView tv_lat_lng = (TextView) view.findViewById(R.id.tv_lat_lng);

                    LatLng latLng = marker.getPosition();
                    tv_location.setText(marker.getTitle());
                    tv_snnipet.setText(marker.getSnippet());
                    tv_lat_lng.setText(latLng.latitude + ", " + latLng.longitude);

                    return view;
                }
            });
            this.googleMaps.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    marker.hideInfoWindow();
                }
            });
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMaps.setMyLocationEnabled(true);
        googleMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    /**
     * Inicialização do Google Maps
     */
    private void mapInit(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Move a câmera para a localização desejada
     */
    private void goTolocation(double latitude, double longitude, float zoom){
        latLng = new LatLng(latitude, longitude);
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        googleMaps.animateCamera(cameraUpdate);
    }

    /**
     * Adiciona um Marker no mapa
     */
    public void setMarker(String location, String snippet, LatLng latLng){
        //  Garante que vai existir apenas um marcador
        if(marker != null){
            marker.remove();
        }
        markerOptions = new MarkerOptions()
                .title(location)
                .position(latLng)
                .snippet(snippet)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user));
        marker = this.googleMaps.addMarker(markerOptions);
        marker.showInfoWindow();
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
        } else if (id == R.id.action_map){
            if(googleMaps.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                googleMaps.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }else if(googleMaps.getMapType() == GoogleMap.MAP_TYPE_SATELLITE){
                googleMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //  MÉTODO PADRÃO DO MENU DRAWER
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_account) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_solicitations){
            Intent intent = new Intent(MainActivity.this, MySolicitationsActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_camera){
            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
