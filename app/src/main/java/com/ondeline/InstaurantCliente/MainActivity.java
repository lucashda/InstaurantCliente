package com.ondeline.InstaurantCliente;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CardapioFragment.OnFragmentInteractionListener, ItemAdapter.RecyclerViewListener {

    String cliente;
    String cpfCliente;

    Menu menu;
    ArrayList<ItemCardapio> itens = new ArrayList<>();
    ArrayList<String> idArrayList = new ArrayList<>();
    FragmentManager fragmentManager;

    String atualCategoria;
    Button btnLimpar;
    Button btnFazerPedido;
    Map<String, Double> valoresMap = new HashMap<>();
    Map<String, Adapters> limparSelecaoMap = new HashMap<>();
    Map<String, ArrayList<ItemAdapter.ItemViewHolder>> fazerPedido = new HashMap<>();
    TextView valorTotal;
    double acc;
    Intent detalhesPedido;
    Bundle bundle;
    Context context = this;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    final int SUBMENU_CATEGORIA_ID = 1234567;
    final int DIALOG = 100;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        btnFazerPedido = findViewById(R.id.btnFazerPedido);
        btnLimpar = findViewById(R.id.btnLimpar);
        valorTotal = findViewById(R.id.txtValorTotal);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        fragmentManager = getSupportFragmentManager();
        menu = navigationView.getMenu();
        getDatabase();
        detalhesPedido = new Intent(MainActivity.this, DetalhesPedido.class);
        bundle = new Bundle();
        dialogoInicial();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        UpdateUI.updateUI(firebaseUser, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == DIALOG && resultCode == RESULT_OK){
            dialogoFinal();
        } else if(requestCode == DIALOG && resultCode == RESULT_CANCELED){
            dialogoFinal();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

    private void createNavViewMenu(Menu menu) {
        Task<QuerySnapshot> querySnapshotCategoria = db.collection("Categorias")
                .orderBy("nomeCategoria", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot : task.getResult()) {
                            Log.i("Categoria", documentSnapshot.getString("nomeCategoria"));
                        }
                    }
                });

        boolean complete = true;
        do {
            if (querySnapshotCategoria.isComplete()) {
                Random random = new Random();
                int order = 0, id;
                for (QueryDocumentSnapshot documentSnapshot : querySnapshotCategoria.getResult()) {
                    while (true) {
                        id = random.nextInt();
                        if (id > 0 && !idArrayList.contains(String.valueOf(id)) && id != SUBMENU_CATEGORIA_ID) {
                            break;
                        }
                    }
                    String categoria = documentSnapshot.getString("nomeCategoria");
                    ArrayList<String> nomes = new ArrayList<>();
                    ArrayList<String> descricoes = new ArrayList<>();
                    ArrayList<String> valores = new ArrayList<>();
                    ArrayList<String> imagens = new ArrayList<>();
                    for (ItemCardapio itemCardapio : itens) {
                        if (itemCardapio.getCategoriaItem().equals(categoria)) {
                            nomes.add(itemCardapio.getNomeItem());
                            descricoes.add(itemCardapio.getDescricaoItem());
                            valores.add(itemCardapio.getValorItem());
                            imagens.add(itemCardapio.getUrlImagem());
                        }
                    }

                    menu.add(SUBMENU_CATEGORIA_ID, id, order, categoria);
                    CardapioFragment fragment = CardapioFragment.newInstance(nomes, descricoes, valores, imagens);
                    fragment.setContext(this);
                    fragment.setBtnLimpar(btnLimpar);
                    fragment.setBtnFazerPedido(btnFazerPedido);
                    fragmentManager.beginTransaction().add(R.id.mainFrameLayout, fragment, categoria).commit();
                    fragmentManager.beginTransaction().hide(fragment).commit();
                    order++;
                }

                complete = false;
            }
        } while (complete);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;
        //fragment = fragmentManager.getFragment(bundle, atualCategoria);
        if (fragmentManager.findFragmentByTag(atualCategoria) != null) {
            fragment = fragmentManager.findFragmentByTag(atualCategoria);
            fragmentManager.beginTransaction().hide(fragment).commit();
        }

        fragment = fragmentManager.findFragmentByTag(item.getTitle().toString());
        fragmentManager.beginTransaction().show(fragment).commit();
        atualCategoria = fragment.getTag();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getDatabase() {
        db.collection("Itens")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                ItemCardapio item = new ItemCardapio(
                                        documentSnapshot.getString("nomeItem"),
                                        documentSnapshot.getString("categoria"),
                                        documentSnapshot.getString("descricaoItem"),
                                        documentSnapshot.getString("valorItem"),
                                        documentSnapshot.getString("imagem")
                                );
                                Log.i("Itens", item.getNomeItem());
                                itens.add(item);
                            }
                            createNavViewMenu(menu);
                        }
                    }
                });
    }

    private void dialogoInicial(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seja Bem Vindo");
        builder.setMessage("Antes de fazer seu pedido, precisamos que informe seu nome completo e c.p.f nos campos abaixo:");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogDadosCliente();
            }
        });
        builder.create();
        builder.show();
    }

    private void dialogDadosCliente(){
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.nome_cpf, null);
        final EditText inputNome = view.findViewById(R.id.inputNome);
        final EditText inputCpf = view.findViewById(R.id.inputCpf);
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Limpar", (DialogInterface.OnClickListener) null);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Próximo", (DialogInterface.OnClickListener) null);
        dialog.show();

        Button limpar = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        limpar.setOnClickListener(new DialogButtonClickWrapper(dialog) {
            @Override
            protected boolean onClicked() {
                inputNome.setText("");
                inputCpf.setText("");
                return false;
            }
        });

        Button proximo = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        proximo.setOnClickListener(new DialogButtonClickWrapper(dialog) {
            @Override
            protected boolean onClicked() {
                boolean isValid = (validarCliente(view, inputNome.getText().toString(), inputCpf.getText().toString()));
                if(isValid){
                    cliente = inputNome.getText().toString();
                    cpfCliente = inputCpf.getText().toString();
                    dialogDividirContas();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void dialogDividirContas(){
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.quantidade_pessoas, null);
        final EditText nPessoas = view.findViewById(R.id.qtdePessoas);
        nPessoas.setGravity(Gravity.CENTER_HORIZONTAL);
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view);
        dialog.setTitle("Dividir Conta");
        dialog.setMessage("Se desejar dividir a conta, informe a quantidade de pessoas que irão dividir a conta com você:");
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Vou Pagar Tudo!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogoFinal();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Quero Rachar!", (DialogInterface.OnClickListener) null);
        dialog.show();
        Button dividir = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        dividir.setOnClickListener(new DialogButtonClickWrapper(dialog) {
            @Override
            protected boolean onClicked() {
                String p = nPessoas.getText().toString();
                if(p.equals("") || p.equals("0")){
                    Snackbar.make(view, "Informe um numero válido maior 0!", Snackbar.LENGTH_SHORT).show();
                    return false;
                } else {
                    Intent intent = new Intent(MainActivity.this, FormularioMesa.class);
                    bundle.putInt("pessoas", Integer.parseInt(p));
                    intent.putExtras(bundle);
                    bundle.clear();
                    startActivityForResult(intent, DIALOG);
                    return true;
                }
            }
        });
    }

    private void dialogoFinal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tudo Pronto!");
        builder.setMessage("Já podemos começar. Selecione uma categoria a aba lateral");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        builder.create().show();
    }

    private boolean validarCliente(View v, String nome, String cpf){
        if(nome.length() <= 1){
            Snackbar.make(v, "Nome Inválido!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if(!ValidarCPF.isCPF(cpf)){
            Snackbar.make(v, "C.P.F Inválido!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void getValores(String categoria,double valor) {
        if(valoresMap.get(categoria) == null) {
            valoresMap.put(categoria, valor);
        } else {
            valoresMap.remove(categoria);
            valoresMap.put(categoria, valor);
        };
        acc = 0;
        Collection<Double> valores = valoresMap.values();
        for(double d : valores){
            acc += d;
            Log.i("TAG", String.valueOf(d));
        }
        valorTotal.setText(getString(R.string.total_a_pagar_r) + String.format("%.2f", acc));
    }

    @Override
    public void limparSelecao(String categoria, ItemAdapter adapter, ArrayList<ItemAdapter.ItemViewHolder> holders) {
        if(limparSelecaoMap.get(categoria) == null) {
            limparSelecaoMap.put(categoria, new Adapters(adapter, holders));
        }
        final Collection<Adapters> adapters = limparSelecaoMap.values();
        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Adapters adp : adapters) {
                    adp.adapter.limparSelecao(adp.holders);
                }
            }
        });
    }

    @Override
    public void fazerPedido(String categoria, ArrayList<ItemAdapter.ItemViewHolder> holders) {
        if(fazerPedido.get(categoria) == null) {
            fazerPedido.put(categoria, holders);
        }
        btnFazerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> nomes = new ArrayList<>();
                ArrayList<String> valores = new ArrayList<>();
                Collection<ArrayList<ItemAdapter.ItemViewHolder>> collection = fazerPedido.values();
                for (ArrayList<ItemAdapter.ItemViewHolder> hld : collection) {
                    for (ItemAdapter.ItemViewHolder holder : hld) {
                        nomes.add(holder.nomeItem.getText().toString());
                        valores.add(holder.valorItem.getText().toString());
                    }
                }
                if(nomes.isEmpty() || valores.isEmpty()){
                    Toast.makeText(context, "Selecione pelomenos 1 item do menu!", Toast.LENGTH_SHORT).show();
                } else {
                    bundle.putStringArrayList("nomeItem", nomes);
                    bundle.putStringArrayList("valorItem", valores);
                    bundle.putString("valorTotal", String.valueOf(acc));
                    detalhesPedido.putExtras(bundle);
                    startActivity(detalhesPedido);
                }
            }
        });
    }

    static class Adapters {
        ItemAdapter adapter;
        ArrayList<ItemAdapter.ItemViewHolder> holders;

        public Adapters(ItemAdapter adapter, ArrayList<ItemAdapter.ItemViewHolder> holders) {
            this.adapter = adapter;
            this.holders = holders;
        }
    }
}