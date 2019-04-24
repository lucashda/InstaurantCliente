package com.ondeline.InstaurantCliente;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Menu menu;
    SubMenu subMenu;
    ArrayList<ItemCardapio> itens;
    ArrayList<String> idArrayList = new ArrayList<>();
    ArrayList<ListaFragment> fragments = new ArrayList<>();
    RecyclerView.Adapter adapter;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    final int SUBMENU_CATEGORIA_ID = 1234567;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        getDatabase();
        menu = navigationView.getMenu();
        createNavViewMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        updateUI(firebaseUser);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } /*else if (id == R.id.action_sign_out) {

            firebaseAuth.signOut();
            onStart();

        }*/
        return super.onOptionsItemSelected(item);
    }

    private void createNavViewMenu(Menu menu){
        Task<QuerySnapshot> querySnapshotTask = db.collection("Categorias")
                .orderBy("nomeCategoria", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });

        boolean complete = true;
        do {
            if (querySnapshotTask.isComplete()) {
                Random random = new Random();
                int order = 0, id;
                for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                    while(true) {
                        id = random.nextInt();
                        if (id > 0 && !idArrayList.contains(String.valueOf(id)) && id != SUBMENU_CATEGORIA_ID) {
                            idArrayList.add(String.valueOf(id));
                            break;
                        }
                    }
                    String categoria = documentSnapshot.getString("nomeCategoria");
                    ArrayList<ItemCardapio> itensPorCategoria = new ArrayList<>();
                    for(ItemCardapio itemCardapio : itens) {
                        if(itemCardapio.getCategoriaItem().equals(categoria)){
                            itensPorCategoria.add(itemCardapio);
                        }
                    }

                    menu.add(SUBMENU_CATEGORIA_ID, Integer.parseInt(idArrayList.get(order)), order, categoria);
                    CardapioFragment fragment = CardapioFragment.newInstance(categoria, itensPorCategoria);
                    ListaFragment listaFragment = new ListaFragment(fragment, idArrayList.get(order));
                    fragments.add(listaFragment);
                    order++;
                }

                complete = false;
            }
        } while (complete);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.i("FRAGM", String.valueOf(id));

        if(true){

            Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
            //Fragment fragment = getFragment(idFragment);
            //getSupportFragmentManager().beginTransaction()
            //        .replace(R.id.cardapioFrameLayout, fragment)
            //        .commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getDatabase(){
        itens = new ArrayList<>();
        Task<QuerySnapshot> querySnapshot = db.collection("Itens")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });
        int i = 0;
        boolean complete = true;
        do {
            if(querySnapshot.isComplete()) {
                for (DocumentSnapshot documentSnapshot : querySnapshot.getResult()) {
                    ItemCardapio item = new ItemCardapio(
                            documentSnapshot.getString("nomeItem"),
                            documentSnapshot.getString("categoria"),
                            documentSnapshot.getString("descricaoItem"),
                            documentSnapshot.getString("valorItem"),
                            documentSnapshot.getString("imagem")
                    );
                    itens.add(item);
                }
                complete = false;
            }
        } while (complete);
    }

    private CardapioFragment getFragment(String idFragment) {
        CardapioFragment cardapioFragment = null;
        for(ListaFragment listaFragment : fragments){
            if(idFragment.equals(listaFragment.id)){
                cardapioFragment = listaFragment.fragment;
            }
        }
        return cardapioFragment;
    }

    private void updateUI(FirebaseUser user){
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    static class ListaFragment {
        CardapioFragment fragment;
        String id;

        public ListaFragment(CardapioFragment fragment, String id) {
            this.fragment = fragment;
            this.id = id;
        }
    }
}
