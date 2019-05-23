package com.ondeline.InstaurantCliente;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
        implements NavigationView.OnNavigationItemSelectedListener,
        CardapioFragment.OnFragmentInteractionListener,
        ItemAdapter.RecyclerViewListener,
        RecyclerItemTouchHelperListener,
        ClienteAdapter.ClienteListener {

    ArrayList<Cliente> clientes = new ArrayList<>();
    CoordinatorLayout rootLayout;

    Menu menu;
    ArrayList<ItemCardapio> itens = new ArrayList<>();
    ArrayList<String> idArrayList = new ArrayList<>();
    ArrayList<String> nomes = new ArrayList<>();
    ArrayList<String> valores = new ArrayList<>();
    ArrayList<String> imagens = new ArrayList<>();
    Map<String, ItemAdapter.ItemViewHolder> escolhas = new HashMap<>();
    FragmentManager fragmentManager;

    String atualCategoria;
    Button btnLimpar;
    Button btnFazerPedido;
    ImageButton btnCliente;
    TextView valorTotal;
    double acc;
    Intent detalhesPedido;
    Bundle bundle;
    Context context = this;

    RecyclerView listaEscolhas, divisaoContas;
    ItemAdapter adapter;
    ClienteAdapter clienteAdapter;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    final int SUBMENU_CATEGORIA_ID = 1234567;
    final int CADASTRO = 100;

    DrawerLayout drawer;
    ActionBar actionBar;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        rootLayout = findViewById(R.id.CoordinatorLayout);
        listaEscolhas = findViewById(R.id.listaEscolhas);
        divisaoContas = findViewById(R.id.divisaoContas);
        btnFazerPedido = findViewById(R.id.btnFazerPedido);
        btnLimpar = findViewById(R.id.btnLimpar);
        btnCliente = findViewById(R.id.addCliente);
        valorTotal = findViewById(R.id.txtValorTotal);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        fragmentManager = getSupportFragmentManager();
        menu = navigationView.getMenu();
        getDatabase();
        detalhesPedido = new Intent(MainActivity.this, DetalhesPedido.class);
        bundle = new Bundle();
        initListaEscolhas();
        initDivisaoContas();
        dialogoInicial();

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomes.clear();
                imagens.clear();
                valores.clear();
                escolhas.clear();
                adapter.notifyDataSetChanged();
                valorTotal.setText("Total a Pagar: R$" + String.format("%.2f",0.0));
            }
        });

        btnFazerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmarPedido();
            }
        });

        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoAdicionarCliente();
            }
        });
    }

    private void initListaEscolhas(){
        adapter = new ItemAdapter(context,listaEscolhas.getId(), nomes, imagens, valores);
        listaEscolhas.setAdapter(adapter);
        listaEscolhas.setItemAnimator( new DefaultItemAnimator());
        listaEscolhas.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        listaEscolhas.setLayoutManager(new LinearLayoutManager(context));
        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(item).attachToRecyclerView(listaEscolhas);
    }

    private void initDivisaoContas(){
        clienteAdapter = new ClienteAdapter(clientes, context);
        divisaoContas.setAdapter(clienteAdapter);
        divisaoContas.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder itemViewHolder, int direction, final int position) {
        if(itemViewHolder instanceof ItemAdapter.ItemViewHolder){
            final ItemAdapter.ItemViewHolder holder = (ItemAdapter.ItemViewHolder) itemViewHolder;
            final int index = itemViewHolder.getAdapterPosition();
            final String nome = adapter.nomes.get(position);
            final String imagem = adapter.imagens.get(position);
            final String valor = adapter.valores.get(position);
            escolhas.remove(nome);
            adapter.removeItem(index);
            setValorTotal(escolhas);

            Snackbar snackbar = Snackbar.make(rootLayout, holder.nomeItem.getText().toString() + " foi Removido!", Snackbar.LENGTH_LONG);
            snackbar.setAction("DESFAZER", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(nome, imagem, valor, index);
                    escolhas.put(nome, holder);
                    setValorTotal(escolhas);
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            snackbar.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        UpdateUI.updateUI(firebaseUser, this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CADASTRO && resultCode == RESULT_OK){
            bundle = data.getExtras();
            //cliente = bundle.getString("nomeCliente");
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
        Fragment fragment;
        if (fragmentManager.findFragmentByTag(atualCategoria) != null) {
            fragment = fragmentManager.findFragmentByTag(atualCategoria);
            if(fragment.isVisible()) {
                fragmentManager.beginTransaction().hide(fragment).commit();
            }
        }

        fragment = fragmentManager.findFragmentByTag(item.getTitle().toString());
        fragmentManager.beginTransaction().show(fragment).commit();
        atualCategoria = fragment.getTag();
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
        builder.setMessage("Você já é cadastrado?");
        View view = getLayoutInflater().inflate(R.layout.login_senha, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, FormularioMesa.class);
                startActivityForResult(intent, CADASTRO);
            }
        }).setNeutralButton("Pular", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cliente cliente = new Cliente("Você", 0);
                clientes.add(cliente);
                clienteAdapter.notifyDataSetChanged();
                dialogoFinal();
            }
        });
        builder.create();
        builder.show();
    }

    private void dialogConfirmarPedido(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirme seu Pedido,");
        final StringBuilder opcoes = new StringBuilder();
        int j = 0;
        for(String opcao : nomes){
            opcoes.append(opcao);
            opcoes.append(" - ");
            opcoes.append(valores.get(j));
            opcoes.append("\n");
            j++;
        }
        builder.setMessage(opcoes);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Collection<ItemAdapter.ItemViewHolder> holders = escolhas.values();
                ArrayList<String> nomes = new ArrayList<>();
                ArrayList<String> quantidades = new ArrayList<>();
                ArrayList<String> valores = new ArrayList<>();
                for(ItemAdapter.ItemViewHolder holder : holders){
                    nomes.add(holder.nomeItem.getText().toString());
                    quantidades.add(String.valueOf(holder.qtde));
                    valores.add(holder.valorItem.getText().toString());
                }
                bundle.putStringArrayList("nomes", nomes);
                bundle.putStringArrayList("quantidades", quantidades);
                bundle.putStringArrayList("valores", valores);
                bundle.putDouble("valorTotal", acc);
                detalhesPedido.putExtras(bundle);
                startActivity(detalhesPedido);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                opcoes.delete(0, opcoes.length());
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
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

    private void dialogoAdicionarCliente(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar Pessoa");
        final View view = getLayoutInflater().inflate(R.layout.adicionar_cliente, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText nomeCliente = view.findViewById(R.id.edtNomeCliente);
                Cliente cliente = new Cliente(nomeCliente.getText().toString(), 0);
                clientes.add(cliente);
                clienteAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void setEscolhas(String nome, String valor, String imagem, ItemAdapter.ItemViewHolder holder){
        if(escolhas.get(nome) == null) {
            nomes.add(nome);
            valores.add(valor);
            imagens.add(imagem);
            escolhas.put(nome, holder);
            setValorTotal(escolhas);
            adapter.notifyDataSetChanged();
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(atualCategoria)).commit();
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(getApplicationContext(), "Voce já adicionou este item", Toast.LENGTH_SHORT).show();
        }
    }

    private void setValorTotal(Map<String, ItemAdapter.ItemViewHolder> arrayList){
        acc = 0;
        Collection<ItemAdapter.ItemViewHolder> collection = arrayList.values();
        for(ItemAdapter.ItemViewHolder holder : collection){
            acc += holder.qtde * Double.parseDouble(holder.valorItem.getText().toString());

        }

        valorTotal.setText("Total a Pagar: R$" + String.format("%.2f",acc));
    }

    @Override
    public void setQtde(ItemAdapter.ItemViewHolder holder) {
        String key = holder.nomeItem.getText().toString();
        if(escolhas.containsKey(key)){
            escolhas.remove(key);
            escolhas.put(key, holder);
            setValorTotal(escolhas);
        }
    }

    @Override
    public void dividirItem(final String nome, final String valor, final int qtde, final ItemAdapter.ItemViewHolder holder) {
        final View view = LayoutInflater.from(context).inflate(R.layout.custom_view_action_bar, null);
        customActionBar(true, view);
        ImageButton cancel = view.findViewById(R.id.cancel);
        clienteAdapter.setCheckBoxVisible(true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customActionBar(false, view);
                clienteAdapter.setCheckBoxVisible(false);
            }
        });
        ImageButton confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int counter = 0;
                for(Cliente cliente : clienteAdapter.getClientes()){
                    if(cliente.isSelected()){
                        counter++;
                    }
                }
                if(counter > 0) {
                    clienteAdapter.divideValue(nome, valor, qtde);
                    clienteAdapter.setCheckBoxVisible(false);
                    adapter.setDivided(holder, true);
                    customActionBar(false, view);
                } else {
                    Toast.makeText(getApplicationContext(), "Escolha um ou mais clientes para dividir", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void customActionBar(boolean visible, View v){

        final Drawable icon  = toolbar.getOverflowIcon();
        if(visible){
            toolbar.setOverflowIcon(null);
            actionBar.setDisplayShowTitleEnabled(false);
            toggle.setDrawerIndicatorEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(v, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        } else {
            toolbar.setOverflowIcon(icon);
            actionBar.setDisplayShowTitleEnabled(true);
            toggle.setDrawerIndicatorEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void undoDivided(ItemCardapio itemCardapio, boolean isDivided) {
        if(isDivided) {
            adapter.undoDivided(itemCardapio);
        }
    }
}