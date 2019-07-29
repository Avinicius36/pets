package com.vinicius.pets.web.rest;

import com.vinicius.pets.PetsApp;
import com.vinicius.pets.domain.Pet;
import com.vinicius.pets.domain.Pessoa;
import com.vinicius.pets.repository.PetRepository;
import com.vinicius.pets.service.PetService;
import com.vinicius.pets.service.dto.PetDTO;
import com.vinicius.pets.service.mapper.PetMapper;
import com.vinicius.pets.web.rest.errors.ExceptionTranslator;
import com.vinicius.pets.service.dto.PetCriteria;
import com.vinicius.pets.service.PetQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.vinicius.pets.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.vinicius.pets.domain.enumeration.GeneroPet;
/**
 * Integration tests for the {@Link PetResource} REST controller.
 */
@SpringBootTest(classes = PetsApp.class)
public class PetResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final GeneroPet DEFAULT_GENERO = GeneroPet.MACHO;
    private static final GeneroPet UPDATED_GENERO = GeneroPet.FEMEA;

    private static final LocalDate DEFAULT_DATA_NASCIMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASCIMENTO = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private PetService petService;

    @Autowired
    private PetQueryService petQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPetMockMvc;

    private Pet pet;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PetResource petResource = new PetResource(petService, petQueryService);
        this.restPetMockMvc = MockMvcBuilders.standaloneSetup(petResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pet createEntity(EntityManager em) {
        Pet pet = new Pet()
            .nome(DEFAULT_NOME)
            .genero(DEFAULT_GENERO)
            .dataNascimento(DEFAULT_DATA_NASCIMENTO);
        return pet;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pet createUpdatedEntity(EntityManager em) {
        Pet pet = new Pet()
            .nome(UPDATED_NOME)
            .genero(UPDATED_GENERO)
            .dataNascimento(UPDATED_DATA_NASCIMENTO);
        return pet;
    }

    @BeforeEach
    public void initTest() {
        pet = createEntity(em);
    }

    @Test
    @Transactional
    public void createPet() throws Exception {
        int databaseSizeBeforeCreate = petRepository.findAll().size();

        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);
        restPetMockMvc.perform(post("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(petDTO)))
            .andExpect(status().isCreated());

        // Validate the Pet in the database
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeCreate + 1);
        Pet testPet = petList.get(petList.size() - 1);
        assertThat(testPet.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPet.getGenero()).isEqualTo(DEFAULT_GENERO);
        assertThat(testPet.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void createPetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = petRepository.findAll().size();

        // Create the Pet with an existing ID
        pet.setId(1L);
        PetDTO petDTO = petMapper.toDto(pet);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPetMockMvc.perform(post("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(petDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = petRepository.findAll().size();
        // set the field null
        pet.setNome(null);

        // Create the Pet, which fails.
        PetDTO petDTO = petMapper.toDto(pet);

        restPetMockMvc.perform(post("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(petDTO)))
            .andExpect(status().isBadRequest());

        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPets() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList
        restPetMockMvc.perform(get("/api/pets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pet.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].genero").value(hasItem(DEFAULT_GENERO.toString())))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())));
    }
    
    @Test
    @Transactional
    public void getPet() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get the pet
        restPetMockMvc.perform(get("/api/pets/{id}", pet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pet.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.genero").value(DEFAULT_GENERO.toString()))
            .andExpect(jsonPath("$.dataNascimento").value(DEFAULT_DATA_NASCIMENTO.toString()));
    }

    @Test
    @Transactional
    public void getAllPetsByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where nome equals to DEFAULT_NOME
        defaultPetShouldBeFound("nome.equals=" + DEFAULT_NOME);

        // Get all the petList where nome equals to UPDATED_NOME
        defaultPetShouldNotBeFound("nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    public void getAllPetsByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where nome in DEFAULT_NOME or UPDATED_NOME
        defaultPetShouldBeFound("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME);

        // Get all the petList where nome equals to UPDATED_NOME
        defaultPetShouldNotBeFound("nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    public void getAllPetsByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where nome is not null
        defaultPetShouldBeFound("nome.specified=true");

        // Get all the petList where nome is null
        defaultPetShouldNotBeFound("nome.specified=false");
    }

    @Test
    @Transactional
    public void getAllPetsByGeneroIsEqualToSomething() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where genero equals to DEFAULT_GENERO
        defaultPetShouldBeFound("genero.equals=" + DEFAULT_GENERO);

        // Get all the petList where genero equals to UPDATED_GENERO
        defaultPetShouldNotBeFound("genero.equals=" + UPDATED_GENERO);
    }

    @Test
    @Transactional
    public void getAllPetsByGeneroIsInShouldWork() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where genero in DEFAULT_GENERO or UPDATED_GENERO
        defaultPetShouldBeFound("genero.in=" + DEFAULT_GENERO + "," + UPDATED_GENERO);

        // Get all the petList where genero equals to UPDATED_GENERO
        defaultPetShouldNotBeFound("genero.in=" + UPDATED_GENERO);
    }

    @Test
    @Transactional
    public void getAllPetsByGeneroIsNullOrNotNull() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where genero is not null
        defaultPetShouldBeFound("genero.specified=true");

        // Get all the petList where genero is null
        defaultPetShouldNotBeFound("genero.specified=false");
    }

    @Test
    @Transactional
    public void getAllPetsByDataNascimentoIsEqualToSomething() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where dataNascimento equals to DEFAULT_DATA_NASCIMENTO
        defaultPetShouldBeFound("dataNascimento.equals=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the petList where dataNascimento equals to UPDATED_DATA_NASCIMENTO
        defaultPetShouldNotBeFound("dataNascimento.equals=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void getAllPetsByDataNascimentoIsInShouldWork() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where dataNascimento in DEFAULT_DATA_NASCIMENTO or UPDATED_DATA_NASCIMENTO
        defaultPetShouldBeFound("dataNascimento.in=" + DEFAULT_DATA_NASCIMENTO + "," + UPDATED_DATA_NASCIMENTO);

        // Get all the petList where dataNascimento equals to UPDATED_DATA_NASCIMENTO
        defaultPetShouldNotBeFound("dataNascimento.in=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void getAllPetsByDataNascimentoIsNullOrNotNull() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where dataNascimento is not null
        defaultPetShouldBeFound("dataNascimento.specified=true");

        // Get all the petList where dataNascimento is null
        defaultPetShouldNotBeFound("dataNascimento.specified=false");
    }

    @Test
    @Transactional
    public void getAllPetsByDataNascimentoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where dataNascimento greater than or equals to DEFAULT_DATA_NASCIMENTO
        defaultPetShouldBeFound("dataNascimento.greaterOrEqualThan=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the petList where dataNascimento greater than or equals to UPDATED_DATA_NASCIMENTO
        defaultPetShouldNotBeFound("dataNascimento.greaterOrEqualThan=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void getAllPetsByDataNascimentoIsLessThanSomething() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the petList where dataNascimento less than or equals to DEFAULT_DATA_NASCIMENTO
        defaultPetShouldNotBeFound("dataNascimento.lessThan=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the petList where dataNascimento less than or equals to UPDATED_DATA_NASCIMENTO
        defaultPetShouldBeFound("dataNascimento.lessThan=" + UPDATED_DATA_NASCIMENTO);
    }


    @Test
    @Transactional
    public void getAllPetsByDonoIsEqualToSomething() throws Exception {
        // Initialize the database
        Pessoa dono = PessoaResourceIT.createEntity(em);
        em.persist(dono);
        em.flush();
        pet.setDono(dono);
        petRepository.saveAndFlush(pet);
        Long donoId = dono.getId();

        // Get all the petList where dono equals to donoId
        defaultPetShouldBeFound("donoId.equals=" + donoId);

        // Get all the petList where dono equals to donoId + 1
        defaultPetShouldNotBeFound("donoId.equals=" + (donoId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPetShouldBeFound(String filter) throws Exception {
        restPetMockMvc.perform(get("/api/pets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pet.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].genero").value(hasItem(DEFAULT_GENERO.toString())))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())));

        // Check, that the count call also returns 1
        restPetMockMvc.perform(get("/api/pets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPetShouldNotBeFound(String filter) throws Exception {
        restPetMockMvc.perform(get("/api/pets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPetMockMvc.perform(get("/api/pets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPet() throws Exception {
        // Get the pet
        restPetMockMvc.perform(get("/api/pets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePet() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        int databaseSizeBeforeUpdate = petRepository.findAll().size();

        // Update the pet
        Pet updatedPet = petRepository.findById(pet.getId()).get();
        // Disconnect from session so that the updates on updatedPet are not directly saved in db
        em.detach(updatedPet);
        updatedPet
            .nome(UPDATED_NOME)
            .genero(UPDATED_GENERO)
            .dataNascimento(UPDATED_DATA_NASCIMENTO);
        PetDTO petDTO = petMapper.toDto(updatedPet);

        restPetMockMvc.perform(put("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(petDTO)))
            .andExpect(status().isOk());

        // Validate the Pet in the database
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeUpdate);
        Pet testPet = petList.get(petList.size() - 1);
        assertThat(testPet.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPet.getGenero()).isEqualTo(UPDATED_GENERO);
        assertThat(testPet.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void updateNonExistingPet() throws Exception {
        int databaseSizeBeforeUpdate = petRepository.findAll().size();

        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPetMockMvc.perform(put("/api/pets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(petDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePet() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        int databaseSizeBeforeDelete = petRepository.findAll().size();

        // Delete the pet
        restPetMockMvc.perform(delete("/api/pets/{id}", pet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pet> petList = petRepository.findAll();
        assertThat(petList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pet.class);
        Pet pet1 = new Pet();
        pet1.setId(1L);
        Pet pet2 = new Pet();
        pet2.setId(pet1.getId());
        assertThat(pet1).isEqualTo(pet2);
        pet2.setId(2L);
        assertThat(pet1).isNotEqualTo(pet2);
        pet1.setId(null);
        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PetDTO.class);
        PetDTO petDTO1 = new PetDTO();
        petDTO1.setId(1L);
        PetDTO petDTO2 = new PetDTO();
        assertThat(petDTO1).isNotEqualTo(petDTO2);
        petDTO2.setId(petDTO1.getId());
        assertThat(petDTO1).isEqualTo(petDTO2);
        petDTO2.setId(2L);
        assertThat(petDTO1).isNotEqualTo(petDTO2);
        petDTO1.setId(null);
        assertThat(petDTO1).isNotEqualTo(petDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(petMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(petMapper.fromId(null)).isNull();
    }
}
