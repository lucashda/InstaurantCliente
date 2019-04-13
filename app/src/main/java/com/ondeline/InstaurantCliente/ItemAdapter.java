package com.ondeline.InstaurantCliente;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private ArrayList<ItemCardapio> itens;
    private ArrayList<ItemViewHolder> holders = new ArrayList<>();
    private Context context;
    private TextView valorTotal;
    private double acc;
    private Button btnLimpar;
    private Button btnFazerPedido;

    public ItemAdapter(ArrayList<ItemCardapio> itens, TextView textView, Button btnLimpar, Button btnFazerPedido, Context context) {
        this.itens = itens;
        this.context = context;
        this.valorTotal = textView;
        this.btnLimpar = btnLimpar;
        this.btnFazerPedido = btnFazerPedido;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.checkBoxNomeItem.setText(itens.get(position).getNomeItem());
        holder.valorItem.setText("R$" + String.format("%.2f", Double.parseDouble(itens.get(position).getValorItem())));
        holders.add(holder);

        final int i = position;
        holder.checkBoxNomeItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    double valor = Double.parseDouble(itens.get(i).getValorItem());
                    acc += valor;
                    valorTotal.setText("Total a Pagar: R$" + String.format("%.2f", acc));

                } else {
                    double valor = Double.parseDouble(itens.get(i).getValorItem());
                    acc -= valor;
                    valorTotal.setText("Total a Pagar: R$" + String.format("%.2f", acc));
                }
            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparSelecao(holders);
            }
        });

        btnFazerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerPedido(holders, valorTotal);
            }
        });
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_item, viewGroup, false);

        ItemViewHolder holder = new ItemViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public void limparSelecao(ArrayList<ItemViewHolder> escolhas) {
        for (int j = 0; j < escolhas.size(); j++) {
            escolhas.get(j).checkBoxNomeItem.setChecked(false);
        }
    }

    public void fazerPedido(ArrayList<ItemViewHolder> escolhas, TextView valorTotal){

        Intent intent = new Intent(context, DetalhesPedido.class);

        Bundle bundle = new Bundle();

        ArrayList<String> nomeItem = new ArrayList<>(),
                valorItem = new ArrayList<>();

        for(int j = 0; j < escolhas.size(); j++){
            if(escolhas.get(j).checkBoxNomeItem.isChecked()){
                nomeItem.add(escolhas.get(j).checkBoxNomeItem.getText().toString());
                valorItem.add(escolhas.get(j).valorItem.getText().toString());
            }
        }
        bundle.putStringArrayList("nomeItem", nomeItem);
        bundle.putStringArrayList("valorItem", valorItem);
        bundle.putString("valorTotal", valorTotal.getText().toString());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBoxNomeItem;
        TextView valorItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            checkBoxNomeItem = itemView.findViewById(R.id.checkBoxItem);
            valorItem = itemView.findViewById(R.id.txtValor);
        }
    }
}
