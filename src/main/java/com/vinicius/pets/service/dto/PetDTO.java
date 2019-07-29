package com.vinicius.pets.service.dto;
import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.vinicius.pets.domain.enumeration.GeneroPet;

/**
 * A DTO for the {@link com.vinicius.pets.domain.Pet} entity.
 */
public class PetDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String nome;

    private GeneroPet genero;

    private LocalDate dataNascimento;


    private Long donoId;

    private String donoNome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public GeneroPet getGenero() {
        return genero;
    }

    public void setGenero(GeneroPet genero) {
        this.genero = genero;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Long getDonoId() {
        return donoId;
    }

    public void setDonoId(Long pessoaId) {
        this.donoId = pessoaId;
    }

    public String getDonoNome() {
        return donoNome;
    }

    public void setDonoNome(String pessoaNome) {
        this.donoNome = pessoaNome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PetDTO petDTO = (PetDTO) o;
        if (petDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), petDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PetDTO{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", genero='" + getGenero() + "'" +
            ", dataNascimento='" + getDataNascimento() + "'" +
            ", dono=" + getDonoId() +
            ", dono='" + getDonoNome() + "'" +
            "}";
    }
}
