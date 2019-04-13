package com.ondeline.InstaurantCliente;


public class ItemCardapio {

    private String nomeItem;
    private String descricaoItem;
    private String valorItem;

    public ItemCardapio(String nome, String descricao, String valor) {
        this.nomeItem = nome;
        this.descricaoItem = descricao;
        this.valorItem = valor;
    }

    public ItemCardapio(String nomeItem, String valorItem) {
        this.nomeItem = nomeItem;
        this.valorItem = valorItem;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public String getDescricaoItem() {
        return descricaoItem;
    }

    public String getValorItem() {
        return valorItem;
    }

}
