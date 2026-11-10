package com.receitadevoinha.api.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receitas")
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(length = 255)
    private String imagem;

    @Column(nullable = false)
    private double nota;

    @Column(nullable = false, length = 20)
    private String tempo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ElementCollection
    @CollectionTable(name = "receita_ingredientes", joinColumns = @JoinColumn(name = "receita_id"))
    @OrderColumn(name = "posicao")
    @Column(name = "ingrediente", nullable = false, length = 255)
    private List<String> ingredientes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "receita_preparo", joinColumns = @JoinColumn(name = "receita_id"))
    @OrderColumn(name = "posicao")
    @Column(name = "passo", nullable = false, length = 1000)
    private List<String> preparo = new ArrayList<>();

    protected Receita() {
        // exigido pelo JPA
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public double getNota() {
        return nota;
    }

    public String getTempo() {
        return tempo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public List<String> getPreparo() {
        return preparo;
    }
}
