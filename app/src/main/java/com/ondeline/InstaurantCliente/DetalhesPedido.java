package com.ondeline.InstaurantCliente;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DetalhesPedido extends AppCompatActivity {

    TextView txtEscolhas, valorTotal;
    FirebaseFirestore db;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_pedido);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        txtEscolhas = findViewById(R.id.txtEscolhas);
        valorTotal = findViewById(R.id.txtValorPedido);
        escolhas(bundle);
        calendar = Calendar.getInstance();

       db = FirebaseFirestore.getInstance();

        Button btnConfimrarPedido = findViewById(R.id.btnConfirmarPedido);

        btnConfimrarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> pedido = new HashMap<>();

                pedido.put("numeroMesa", "12");
                pedido.put("pedidoCliente", bundle.getStringArrayList("nomeItem"));
                pedido.put("valorTotal", bundle.getString("valorTotal"));
                pedido.put("horario", calendar.getTime());
                db.collection("Pedidos")
                        .add(pedido)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Successo", "Item adicionado com o ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Falha", "Erro ao adicionar item à base de dados.", e);
                            }
                        });
                Intent intent = new Intent(DetalhesPedido.this, PedidoConfirmado.class);
                startActivity(intent);
            }
        });
    }

    public void escolhas(Bundle bundle) {
        String txt = "";
        ArrayList<String> escolhas = bundle.getStringArrayList("nomeItem"),
                valores = bundle.getStringArrayList("valorItem");
        for (String field : escolhas) {
            txt += "    - " + field + "     (" + valores.get(escolhas.indexOf(field)) + ")\n";
        }
        txtEscolhas.setText(txt);
        valorTotal.setText(bundle.getString("valorTotal"));
    }
}
