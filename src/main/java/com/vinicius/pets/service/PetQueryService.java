package com.vinicius.pets.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.vinicius.pets.domain.Pet;
import com.vinicius.pets.domain.*; // for static metamodels
import com.vinicius.pets.repository.PetRepository;
import com.vinicius.pets.service.dto.PetCriteria;
import com.vinicius.pets.service.dto.PetDTO;
import com.vinicius.pets.service.mapper.PetMapper;

/**
 * Service for executing complex queries for {@link Pet} entities in the database.
 * The main input is a {@link PetCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PetDTO} or a {@link Page} of {@link PetDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PetQueryService extends QueryService<Pet> {

    private final Logger log = LoggerFactory.getLogger(PetQueryService.class);

    private final PetRepository petRepository;

    private final PetMapper petMapper;

    public PetQueryService(PetRepository petRepository, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
    }

    /**
     * Return a {@link List} of {@link PetDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PetDTO> findByCriteria(PetCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Pet> specification = createSpecification(criteria);
        return petMapper.toDto(petRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PetDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PetDTO> findByCriteria(PetCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pet> specification = createSpecification(criteria);
        return petRepository.findAll(specification, page)
            .map(petMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PetCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Pet> specification = createSpecification(criteria);
        return petRepository.count(specification);
    }

    /**
     * Function to convert ConsumerCriteria to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */    
    private Specification<Pet> createSpecification(PetCriteria criteria) {
        Specification<Pet> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Pet_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Pet_.nome));
            }
            if (criteria.getGenero() != null) {
                specification = specification.and(buildSpecification(criteria.getGenero(), Pet_.genero));
            }
            if (criteria.getDataNascimento() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataNascimento(), Pet_.dataNascimento));
            }
            if (criteria.getDonoId() != null) {
                specification = specification.and(buildSpecification(criteria.getDonoId(),
                    root -> root.join(Pet_.dono, JoinType.LEFT).get(Pessoa_.id)));
            }
        }
        return specification;
    }
}
