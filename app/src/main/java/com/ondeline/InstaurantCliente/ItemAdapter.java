package com.ondeline.InstaurantCliente;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private ArrayList<String> nomes;
    private ArrayList<String> imagens;
    private ArrayList<String> valores;
    private ArrayList<ItemViewHolder> holders = new ArrayList<>();
    private String categoria;
    private ArrayList<File> paths = new ArrayList<>();
    private Context context;
    private File localFile;
    RecyclerViewListener recyclerViewListener;
    private double acc = 0;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference reference = firebaseStorage.getReference();

    public ItemAdapter(ArrayList<String> nomes, ArrayList<String> imagens, ArrayList<String> valores, Context context) {
        this.nomes = nomes;
        this.imagens = imagens;
        this.valores = valores;
        this.context = context;
        recyclerViewListener = (RecyclerViewListener) context;
    }

    public void setCategoria(String categoria){
        this.categoria = categoria;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        getImageStorage(imagens.get(position), holder, position);
        holder.nomeItem.setText(nomes.get(position));
        holder.valorItem.setText(valores.get(position));
        holder.checkBoxItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    acc += Double.parseDouble(holder.valorItem.getText().toString());
                    recyclerViewListener.getValores(categoria, acc);
                    holders.add(holder);
                } else {
                    acc -= Double.parseDouble(holder.valorItem.getText().toString());
                    recyclerViewListener.getValores(categoria,acc);
                    holders.remove(holder);
                }
            }
        });

        recyclerViewListener.limparSelecao(categoria, new ItemAdapter(nomes, imagens, valores, context), holders);
        recyclerViewListener.fazerPedido(categoria, holders);

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
        return nomes.size();
    }


    public void limparSelecao(ArrayList<ItemViewHolder> escolhas) {
        for (int j = 0; j < escolhas.size(); j++) {
            escolhas.get(j).checkBoxItem.setChecked(false);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBoxItem;
        TextView valorItem;
        TextView nomeItem;
        ImageView imagemItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            checkBoxItem = itemView.findViewById(R.id.checkBoxItem);
            valorItem = itemView.findViewById(R.id.txtValor);
            nomeItem = itemView.findViewById(R.id.txtNome);
            imagemItem = itemView.findViewById(R.id.imagemItem);
        }
    }

    private void getImageStorage(String url, final ItemViewHolder holder, final int position){
        reference = firebaseStorage.getReferenceFromUrl(url);
        localFile = null;
        try {
            localFile = File.createTempFile("images", ".JPEG");
            paths.add(localFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        reference.getFile(paths.get(position))
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(paths.get(position).getAbsolutePath());
                        holder.imagemItem.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    public interface RecyclerViewListener {
        void getValores(String categoria, double valor);
        void limparSelecao(String categoria, ItemAdapter adapter, ArrayList<ItemViewHolder> holders);
        void fazerPedido(String categoria, ArrayList<ItemViewHolder> holders);
    }
}
