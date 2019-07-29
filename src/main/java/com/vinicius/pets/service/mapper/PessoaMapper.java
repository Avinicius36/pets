package com.vinicius.pets.service.mapper;

import com.vinicius.pets.domain.*;
import com.vinicius.pets.service.dto.PessoaDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pessoa} and its DTO {@link PessoaDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PessoaMapper extends EntityMapper<PessoaDTO, Pessoa> {


    @Mapping(target = "pets", ignore = true)
    @Mapping(target = "removePets", ignore = true)
    Pessoa toEntity(PessoaDTO pessoaDTO);

    default Pessoa fromId(Long id) {
        if (id == null) {
            return null;
        }
        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        return pessoa;
    }
}
