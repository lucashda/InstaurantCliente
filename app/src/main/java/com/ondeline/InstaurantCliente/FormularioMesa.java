package com.ondeline.InstaurantCliente;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class FormularioMesa extends AppCompatActivity {

    int pessoas;
    String[] acomp;
    LinearLayout form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_mesa);
        form = findViewById(R.id.formularioLayout);
        pessoas = getIntent().getExtras().getInt("pessoas");
        for(int i = 1; i <= pessoas; i++) {
            EditText editText = new EditText(this);
            editText.setTag("pessoa" + i);
            editText.setHint((i) + "ª Pessoa");
            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            form.addView(editText);
        }

        acomp = new String[pessoas];
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
                for(int i = 0; i < pessoas; i++){
                    EditText editText = form.findViewWithTag("pessoa" + (i + 1));
                    acomp[i] = editText.getText().toString();
                }
                alertDialogConfirmar(acomp);
            }
        });
    }

    private void alertDialogCancelar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FormularioMesa.this);
        builder.setCancelable(true);
        builder.setTitle("Aviso!")
                .setMessage("Essa ação não poderá ser desfeita.")
                .setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FormularioMesa.this, MainActivity.class);
                        setResult(RESULT_CANCELED, intent);
                        finish();
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

    private void alertDialogConfirmar(final String[] acomp){
        StringBuilder pes = new StringBuilder();
        pes.append("Você escolheru dividir a conta com:\n");
        for(String p : acomp){
            pes.append(p);
            if(p.equals(acomp[acomp.length - 1])){
                pes.append(".");
            } else {
                pes.append(", ");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(FormularioMesa.this);
        builder.setCancelable(true);
        builder.setTitle("Aviso!").setMessage(pes)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FormularioMesa.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("repeticoes", (pessoas + 1));
                        bundle.putStringArray("pessoas", acomp);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
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
}
