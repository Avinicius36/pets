package com.vinicius.pets.service.mapper;

import com.vinicius.pets.domain.*;
import com.vinicius.pets.service.dto.PetDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pet} and its DTO {@link PetDTO}.
 */
@Mapper(componentModel = "spring", uses = {PessoaMapper.class})
public interface PetMapper extends EntityMapper<PetDTO, Pet> {

    @Mapping(source = "dono.id", target = "donoId")
    @Mapping(source = "dono.nome", target = "donoNome")
    PetDTO toDto(Pet pet);

    @Mapping(source = "donoId", target = "dono")
    Pet toEntity(PetDTO petDTO);

    default Pet fromId(Long id) {
        if (id == null) {
            return null;
        }
        Pet pet = new Pet();
        pet.setId(id);
        return pet;
    }
}
