package com.vinicius.pets.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;

import com.vinicius.pets.domain.enumeration.GeneroPet;

/**
 * A Pet.
 */
@Entity
@Table(name = "pet")
public class Pet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private GeneroPet genero;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToOne
    @JsonIgnoreProperties("pets")
    private Pessoa dono;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public Pet nome(String nome) {
        this.nome = nome;
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public GeneroPet getGenero() {
        return genero;
    }

    public Pet genero(GeneroPet genero) {
        this.genero = genero;
        return this;
    }

    public void setGenero(GeneroPet genero) {
        this.genero = genero;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Pet dataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Pessoa getDono() {
        return dono;
    }

    public Pet dono(Pessoa pessoa) {
        this.dono = pessoa;
        return this;
    }

    public void setDono(Pessoa pessoa) {
        this.dono = pessoa;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pet)) {
            return false;
        }
        return id != null && id.equals(((Pet) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Pet{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", genero='" + getGenero() + "'" +
            ", dataNascimento='" + getDataNascimento() + "'" +
            "}";
    }
}
