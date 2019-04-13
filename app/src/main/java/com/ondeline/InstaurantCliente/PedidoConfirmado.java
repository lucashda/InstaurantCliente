package com.ondeline.InstaurantCliente;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PedidoConfirmado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_confirmado);

        Button btnFazerOutroPedido = findViewById(R.id.btnOutroPedido);
        Button btnFecharConta = findViewById(R.id.btnFecharConta);

        btnFazerOutroPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PedidoConfirmado.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
