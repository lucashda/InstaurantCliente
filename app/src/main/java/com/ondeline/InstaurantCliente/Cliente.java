package com.ondeline.InstaurantCliente;

import java.util.ArrayList;

public class Cliente {
    private String nome, sobrenome, telefone;
    private ArrayList<ItemCardapio> itens = new ArrayList<>();
    private boolean isSelected = false;
    private double valor;

    public Cliente(String nome, String sobrenome, String telefone) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.telefone = telefone;
    }

    public Cliente(String nome, double valor){
        this.nome = nome;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ArrayList<ItemCardapio> getItens() {
        return itens;
    }

    public void setItens(ArrayList<ItemCardapio> itens) {
        this.itens = itens;
    }

    public ItemCardapio getItemAtposition(int position){
        if(!itens.isEmpty() || position >= itens.size()) {
            return this.itens.get(position);
        } else {
            return null;
        }
    }

    public void setItem(ItemCardapio item) {
        this.itens.add(item);
    }
}
