package com.vinicius.pets.service;

import com.vinicius.pets.domain.Pet;
import com.vinicius.pets.repository.PetRepository;
import com.vinicius.pets.service.dto.PetDTO;
import com.vinicius.pets.service.mapper.PetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Pet}.
 */
@Service
@Transactional
public class PetService {

    private final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;

    private final PetMapper petMapper;

    public PetService(PetRepository petRepository, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
    }

    /**
     * Save a pet.
     *
     * @param petDTO the entity to save.
     * @return the persisted entity.
     */
    public PetDTO save(PetDTO petDTO) {
        log.debug("Request to save Pet : {}", petDTO);
        Pet pet = petMapper.toEntity(petDTO);
        pet = petRepository.save(pet);
        return petMapper.toDto(pet);
    }

    /**
     * Get all the pets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PetDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Pets");
        return petRepository.findAll(pageable)
            .map(petMapper::toDto);
    }


    /**
     * Get one pet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PetDTO> findOne(Long id) {
        log.debug("Request to get Pet : {}", id);
        return petRepository.findById(id)
            .map(petMapper::toDto);
    }

    /**
     * Delete the pet by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Pet : {}", id);
        petRepository.deleteById(id);
    }
}
