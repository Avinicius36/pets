package com.vinicius.pets.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.vinicius.pets.domain.enumeration.GeneroPet;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.vinicius.pets.domain.Pet} entity. This class is used
 * in {@link com.vinicius.pets.web.rest.PetResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PetCriteria implements Serializable, Criteria {
    /**
     * Class for filtering GeneroPet
     */
    public static class GeneroPetFilter extends Filter<GeneroPet> {

        public GeneroPetFilter() {
        }

        public GeneroPetFilter(GeneroPetFilter filter) {
            super(filter);
        }

        @Override
        public GeneroPetFilter copy() {
            return new GeneroPetFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private GeneroPetFilter genero;

    private LocalDateFilter dataNascimento;

    private LongFilter donoId;

    public PetCriteria(){
    }

    public PetCriteria(PetCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.nome = other.nome == null ? null : other.nome.copy();
        this.genero = other.genero == null ? null : other.genero.copy();
        this.dataNascimento = other.dataNascimento == null ? null : other.dataNascimento.copy();
        this.donoId = other.donoId == null ? null : other.donoId.copy();
    }

    @Override
    public PetCriteria copy() {
        return new PetCriteria(this);
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

    public GeneroPetFilter getGenero() {
        return genero;
    }

    public void setGenero(GeneroPetFilter genero) {
        this.genero = genero;
    }

    public LocalDateFilter getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDateFilter dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LongFilter getDonoId() {
        return donoId;
    }

    public void setDonoId(LongFilter donoId) {
        this.donoId = donoId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PetCriteria that = (PetCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(genero, that.genero) &&
            Objects.equals(dataNascimento, that.dataNascimento) &&
            Objects.equals(donoId, that.donoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        nome,
        genero,
        dataNascimento,
        donoId
        );
    }

    @Override
    public String toString() {
        return "PetCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (nome != null ? "nome=" + nome + ", " : "") +
                (genero != null ? "genero=" + genero + ", " : "") +
                (dataNascimento != null ? "dataNascimento=" + dataNascimento + ", " : "") +
                (donoId != null ? "donoId=" + donoId + ", " : "") +
            "}";
    }

}
