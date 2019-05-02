package com.ondeline.InstaurantCliente;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormularioMesa extends AppCompatActivity {

    FirebaseFirestore db;

    EditText nome, sobrenome, telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_mesa);

        db = FirebaseFirestore.getInstance();

        nome = findViewById(R.id.cadNome);
        sobrenome = findViewById(R.id.cadSobrenome);
        telefone = findViewById(R.id.cadTelefone);
        Button btnCancelar = findViewById(R.id.btnFormCancel);
        Button btnConfirmar = findViewById(R.id.btnFormConfirm);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogCancelar();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogConfirmar();
            }
        });
    }

    private void alertDialogCancelar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FormularioMesa.this);
        builder.setCancelable(true);
        builder.setTitle("Cancelar cadastro")
                .setMessage("Deseja cancelar seu cadastro?\nVoce pode se cadastrar depois acessando o menu de opções no canto superior direito.")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FormularioMesa.this, MainActivity.class);
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void alertDialogConfirmar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FormularioMesa.this);
        builder.setCancelable(true);
        builder.setTitle("Confirmar Cadastro").setMessage("Deseja confirmar seu cadastro?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cadastrarCliente();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void cadastrarCliente(){

        Cliente cliente = new Cliente(
                nome.getText().toString(),
                sobrenome.getText().toString(),
                telefone.getText().toString()
        );

        Map<String, Cliente> mapCliente = new HashMap<>();
        mapCliente.put("cliente", cliente);

        db.collection("Clientes")
                .add(mapCliente)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(FormularioMesa.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("nomeCliente", nome.getText().toString());
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.getMessage();
            }
        });
    }
}
