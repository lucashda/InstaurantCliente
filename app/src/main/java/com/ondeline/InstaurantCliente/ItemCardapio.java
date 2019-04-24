package com.ondeline.InstaurantCliente;


public class ItemCardapio {

    private String nomeItem;
    private String categoriaItem;
    private String descricaoItem;
    private String valorItem;
    private String urlImagem;

    public ItemCardapio(String nome, String categoriaItem,String descricao, String valor, String urlImagem) {
        this.nomeItem = nome;
        this.descricaoItem = descricao;
        this.valorItem = valor;
        this.urlImagem = urlImagem;
        this.categoriaItem = categoriaItem;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public String getCategoriaItem(){
        return categoriaItem;
    }

    public String getDescricaoItem() {
        return descricaoItem;
    }

    public String getValorItem() {
        return valorItem;
    }

    public String getUrlImagem() {
        return urlImagem;
    }
}
