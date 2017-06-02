package com.example.albert.projetop3;

/**
 * Created by albert on 01/06/17.
 */

public class Pessoa {
    private String nome;
    private String telefone;
    public Pessoa(){}

    public String getNome(){
        return this.nome;
    }

    public void setNome(String s){
        this.nome = s;
    }

    public String getTelefone(){
        return this.telefone;
    }

    public void setTelefone(String s){
        this.telefone = s;
    }
}
