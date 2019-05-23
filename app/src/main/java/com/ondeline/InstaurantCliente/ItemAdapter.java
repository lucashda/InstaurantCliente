package com.ondeline.InstaurantCliente;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
    ArrayList<ItemViewHolder> holders = new ArrayList<>();
    private int recyclerViewId;

    private ArrayList<File> paths = new ArrayList<>();
    private Context context;
    private File localFile;
    RecyclerViewListener recyclerViewListener;

    View.OnClickListener add, sub;

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

    public ItemAdapter(Context context, int recyclerViewId, ArrayList<String> nomes, ArrayList<String> imagens, ArrayList<String> valores) {
        this.recyclerViewId = recyclerViewId;
        this.nomes = nomes;
        this.valores = valores;
        this.imagens = imagens;
        this.context = context;
        recyclerViewListener = (RecyclerViewListener) context;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        if(!holders.contains(holder)) {
            holders.add(holder);
        } else {
            holders.remove(holder);
            holders.add(holder);
        }
        holder.nomeItem.setText(nomes.get(position));
        holder.valorItem.setText(valores.get(position));
        if(holder.getItemViewType() == 0){
            getImageStorage(imagens.get(position), holder, position);
            holder.foreground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewListener.setEscolhas(nomes.get(position), valores.get(position), paths.get(position).getAbsolutePath(),holder);
                }
            });
        } else {
            holder.imagemItem.setImageBitmap(BitmapFactory.decodeFile(imagens.get(position)));
            holder.foreground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(holder.isDivided) {
                        Toast.makeText(context, "Você já dividiu este item", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        recyclerViewListener.dividirItem(nomes.get(position), valores.get(position), holder.qtde, holder);
                        return true;
                    }
                }
            });

            add = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.qtde += 1;
                    holder.txtQtde.setText(String.valueOf(holder.qtde));
                    recyclerViewListener.setQtde(holder);
                }
            };

            sub = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.qtde > 1) {
                        holder.qtde -= 1;
                        holder.txtQtde.setText(String.valueOf(holder.qtde));
                        recyclerViewListener.setQtde(holder);
                    } else {
                        Toast.makeText(context, "Escolha deve conter pelomenos 1 item!", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            if(!holder.isDivided) {
                holder.qtde = 1;
                holder.txtQtde.setText(String.valueOf(holder.qtde));
                holder.addQtde.setOnClickListener(add);
                holder.subQtde.setOnClickListener(sub);
                holder.foreground.setForeground(null);
            } else {
                holder.foreground.setForeground(context.getResources().getDrawable(R.drawable.foreground_is_divided));
                holder.addQtde.setOnClickListener(null);
                holder.subQtde.setOnClickListener(null);
            }
        }
    }

    public void setDivided(ItemViewHolder holder, boolean isDivided){
        holder.isDivided = isDivided;
        holder.foreground.setForeground(context.getResources().getDrawable(R.drawable.foreground_is_divided));
        holder.addQtde.setOnClickListener(null);
        holder.subQtde.setOnClickListener(null);
    }

    public void removeItem(int position){
        nomes.remove(position);
        valores.remove(position);
        imagens.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(String nome, String imagem, String valor, int position){
        nomes.add(position, nome);
        valores.add(position, valor);
        imagens.add(position, imagem);
        notifyItemRemoved(position);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == 0) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_view_item, viewGroup, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_view_item_2, viewGroup, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return nomes.size();
    }

    public void undoDivided(ItemCardapio itemCardapio) {
        for(ItemViewHolder holder : holders){
            if(itemCardapio.getNomeItem().equals(holder.nomeItem.getText().toString())){
                holder.isDivided = true;
                notifyItemChanged(holder.getAdapterPosition());
            }
        }
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
        boolean isDivided;
        int qtde;

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
        }else {
            return 1;
        }
    }

    public interface RecyclerViewListener {
        void dividirItem(String nome, String valor, int qtde, ItemViewHolder holder);
        void setEscolhas(String nome, String valor, String imagem, ItemViewHolder holder);
        void setQtde(ItemViewHolder holder);
    }
}
