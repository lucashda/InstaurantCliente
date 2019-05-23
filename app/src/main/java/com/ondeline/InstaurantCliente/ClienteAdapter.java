package com.ondeline.InstaurantCliente;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private ArrayList<Cliente> clientes;
    private boolean checkBoxVisible;
    private Context context;
    private ClienteListener clienteListener;

    public ClienteAdapter(ArrayList<Cliente> clientes, Context context) {
        this.clientes = clientes;
        this.context = context;
        this.clienteListener = (ClienteListener) context;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_3, viewGroup, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, final int position) {
        holder.txtNomeCliente.setText(clientes.get(position).getNome());
        holder.txtContaCliente.setText("R$" + String.format("%.2f", clientes.get(position).getValor()));
        if (checkBoxVisible) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    clientes.get(position).setSelected(isChecked);
                }
            });
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.checkBox.setChecked(false);
        }

        holder.backgroundCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clientes.get(position).getItens() == null || clientes.get(position).getItens().isEmpty()){
                    Toast.makeText(context, "Nenhum item dividido com este cliente.", Toast.LENGTH_SHORT).show();
                } else {
                    dialogremoveItem(position);
                }
            }
        });
        holder.itensCliente.removeAllViewsInLayout();
        holder.itensCliente.setMinimumHeight(0);
        if(clientes.get(position).getItens().size() > 0) {
            for(ItemCardapio itemCardapio : clientes.get(position).getItens()){
                TextView txtNome = new TextView(context);
                txtNome.setText(" - " + itemCardapio.getNomeItem());
                TextView txtValor = new TextView(context);
                txtValor.setText("      R$" + String.format("%.2f", Double.parseDouble(itemCardapio.getValorItem())));
                LinearLayout itemValor = new LinearLayout(context);
                itemValor.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                itemValor.setOrientation(LinearLayout.HORIZONTAL);
                itemValor.addView(txtNome);
                itemValor.addView(txtValor);
                holder.itensCliente.addView(itemValor);
            }
        }
    }

    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    public void setCheckBoxVisible(boolean visible) {
        this.checkBoxVisible = visible;
        notifyDataSetChanged();
    }

    public void divideValue(String nome, String valor, int qtde) {
        double value = Double.parseDouble(valor);
        int counter = 0;
        boolean loop = true;
        while (true) {
            for (Cliente cliente : clientes) {
                if (cliente.isSelected()) {
                    if (loop) {
                        counter++;
                    } else {
                        cliente.setItem(new ItemCardapio(nome, String.valueOf((value * qtde) / counter)));
                        cliente.setValor(cliente.getValor() + (value * qtde)/ counter);
                        cliente.setSelected(false);
                    }
                }
            }
            if (!loop) {
                notifyDataSetChanged();
                break;
            }
            loop = false;
        }
    }

    private boolean undoDivide(ItemCardapio item) {
        int i = 0;
        while (true){
            if(i == clientes.size()){
                return true;
            } else {
                for (ItemCardapio itemCardapio : clientes.get(i).getItens()) {
                    if (item.getNomeItem().equals(itemCardapio.getNomeItem())) {
                        return false;
                    }
                }
                i++;
            }
        }
    }

    private void dialogremoveItem(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(clientes.get(position).getNome());
        builder.setMessage("Selecione os itens que deseja remover da sua conta:");
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for(ItemCardapio itemCardapio : clientes.get(position).getItens()) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(itemCardapio.getNomeItem());
            checkBox.setTag(itemCardapio.getNomeItem());
            linearLayout.addView(checkBox);
        }
        builder.setView(linearLayout);
        builder.setCancelable(false);
        builder.setPositiveButton("Remover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<ItemCardapio> itens = clientes.get(position).getItens();
                Log.i("Size", String.valueOf(itens.size()));
                int i = itens.size();
                while (i > 0){
                    if(((CheckBox)linearLayout.getChildAt(i - 1)).isChecked()){
                        ItemCardapio itemCardapio = itens.get(i - 1);
                        itens.remove(i - 1);
                        if( undoDivide(itemCardapio)) {
                            clienteListener.undoDivided(itemCardapio, true);
                        }
                    }
                    i--;
                }
                clientes.get(position).setItens(itens);
                clientes.get(position).setValor(0);
                notifyItemChanged(position);
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

    static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeCliente, txtContaCliente;
        ConstraintLayout backgroundCliente;
        CheckBox checkBox;
        LinearLayout itensCliente;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtNomeCliente = itemView.findViewById(R.id.txtNomeCliente);
            this.txtContaCliente = itemView.findViewById(R.id.txtContaCliente);
            this.checkBox = itemView.findViewById(R.id.checkBoxCliente);
            this.itensCliente = itemView.findViewById(R.id.itensCliente);
            this.backgroundCliente = itemView.findViewById(R.id.backgroundCliente);
        }
    }

    public interface ClienteListener{
        void undoDivided(ItemCardapio itemCardapio, boolean isDivided);
    }
}
