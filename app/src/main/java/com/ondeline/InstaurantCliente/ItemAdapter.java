package com.ondeline.InstaurantCliente;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    ArrayList<String> nomes;
    ArrayList<String> imagens;
    ArrayList<String> valores;
    ArrayList<String> quantidades;
    private int recyclerViewId;

    private ArrayList<ItemViewHolder> holders = new ArrayList<>();
    private String categoria;
    private ArrayList<File> paths = new ArrayList<>();
    private Context context;
    private File localFile;
    RecyclerViewListener recyclerViewListener;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference reference = firebaseStorage.getReference();

    public ItemAdapter(int recyclerViewId, ArrayList<String> nomes, ArrayList<String> imagens, ArrayList<String> valores, Context context) {
        this.recyclerViewId = recyclerViewId;
        this.nomes = nomes;
        this.imagens = imagens;
        this.valores = valores;
        this.context = context;
        recyclerViewListener = (RecyclerViewListener) context;
    }

    public ItemAdapter(Context context, int recyclerViewId, ArrayList<String> nomes, ArrayList<String> valores, ArrayList<String> quantidades) {
        this.recyclerViewId = recyclerViewId;
        this.nomes = nomes;
        this.valores = valores;
        this.quantidades = quantidades;
        this.context = context;
    }

    public void setCategoria(String categoria){
        this.categoria = categoria;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

        holder.nomeItem.setText(nomes.get(position));
        holder.valorItem.setText(valores.get(position));
        holder.foreground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(holder.getItemViewType() == 0) {
                    ClipData clipData = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(clipData, shadowBuilder, null, View.TEXT_ALIGNMENT_CENTER);
                    recyclerViewListener.dropItens(holder, nomes.get(position), valores.get(position), paths.get(position).getAbsolutePath());
                } else if (holder.getItemViewType() == 2){

                }
                return true;
            }
        });
        if(holder.getItemViewType() == 0) {
            getImageStorage(imagens.get(position), holder, position);
        } else if (holder.getItemViewType() == 1){
            holder.imagemItem.setImageBitmap(BitmapFactory.decodeFile(imagens.get(position)));
        } else {
            holder.txtQtde.setText(quantidades.get(position));
        }

        if(holder.getItemViewType() == 1){
            holders.add(holder);
            holder.txtQtde.setText(String.valueOf(holder.qtde));
            holder.addQtde.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.qtde += 1;
                    holder.txtQtde.setText(String.valueOf(holder.qtde));
                    recyclerViewListener.setQtde(holder);
                }
            });
            holder.subQtde.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.qtde > 1) {
                        holder.qtde -= 1;
                        holder.txtQtde.setText(String.valueOf(holder.qtde));
                        recyclerViewListener.setQtde(holder);
                    } else {
                        Toast.makeText(context, "Escolha deve conter pelomenos 1 item!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void removeItem(int position){
        holders.remove(position);
        nomes.remove(position);
        valores.remove(position);
        imagens.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(ItemViewHolder holder, String nome, String imagem, String valor, int position){
        holders.add(holder);
        nomes.add(position, nome);
        valores.add(position, valor);
        imagens.add(position, imagem);
        notifyItemRemoved(position);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == 0) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_view_item, viewGroup, false);
            return new ItemViewHolder(view);
        } else if(viewType == 1){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_view_item_2, viewGroup, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_view_item_3, viewGroup, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return nomes.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout background;
        CardView foreground;
        TextView valorItem;
        TextView nomeItem;
        ImageView imagemItem;
        ImageButton addQtde;
        ImageButton subQtde;
        TextView txtQtde;
        int qtde = 1;

        public ItemViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            foreground = itemView.findViewById(R.id.foreground);
            valorItem = itemView.findViewById(R.id.txtValor);
            nomeItem = itemView.findViewById(R.id.txtNome);
            imagemItem = itemView.findViewById(R.id.imagemItem);
            addQtde = itemView.findViewById(R.id.addQtde);
            subQtde = itemView.findViewById(R.id.subQtde);
            txtQtde = itemView.findViewById(R.id.txtQtde);
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

    @Override
    public int getItemViewType(int position) {
        if(recyclerViewId == R.id.recyclerViewCardapio){
            return 0;
        }else if(recyclerViewId == R.id.listaEscolhas){
            return 1;
        } else {
            return 2;
        }
    }

    public interface RecyclerViewListener {
        void dropItens(ItemViewHolder holder, String nome, String valor, String imagem);
        void setQtde(ItemViewHolder holder);
    }
}
