package com.vinicius.pets.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.vinicius.pets.domain.Pessoa} entity. This class is used
 * in {@link com.vinicius.pets.web.rest.PessoaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pessoas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PessoaCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter email;

    private LocalDateFilter dataNascimento;

    private LongFilter petsId;

    public PessoaCriteria(){
    }

    public PessoaCriteria(PessoaCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.nome = other.nome == null ? null : other.nome.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.dataNascimento = other.dataNascimento == null ? null : other.dataNascimento.copy();
        this.petsId = other.petsId == null ? null : other.petsId.copy();
    }

    @Override
    public PessoaCriteria copy() {
        return new PessoaCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNome() {
        return nome;
    }

    public void setNome(StringFilter nome) {
        this.nome = nome;
    }

    public StringFilter getEmail() {
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public LocalDateFilter getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDateFilter dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LongFilter getPetsId() {
        return petsId;
    }

    public void setPetsId(LongFilter petsId) {
        this.petsId = petsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PessoaCriteria that = (PessoaCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(email, that.email) &&
            Objects.equals(dataNascimento, that.dataNascimento) &&
            Objects.equals(petsId, that.petsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        nome,
        email,
        dataNascimento,
        petsId
        );
    }

    @Override
    public String toString() {
        return "PessoaCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (nome != null ? "nome=" + nome + ", " : "") +
                (email != null ? "email=" + email + ", " : "") +
                (dataNascimento != null ? "dataNascimento=" + dataNascimento + ", " : "") +
                (petsId != null ? "petsId=" + petsId + ", " : "") +
            "}";
    }

}
