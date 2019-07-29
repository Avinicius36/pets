package com.vinicius.pets.web.rest;

import com.vinicius.pets.PetsApp;
import com.vinicius.pets.domain.Pessoa;
import com.vinicius.pets.domain.Pet;
import com.vinicius.pets.repository.PessoaRepository;
import com.vinicius.pets.service.PessoaService;
import com.vinicius.pets.service.dto.PessoaDTO;
import com.vinicius.pets.service.mapper.PessoaMapper;
import com.vinicius.pets.web.rest.errors.ExceptionTranslator;
import com.vinicius.pets.service.dto.PessoaCriteria;
import com.vinicius.pets.service.PessoaQueryService;

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

/**
 * Integration tests for the {@Link PessoaResource} REST controller.
 */
@SpringBootTest(classes = PetsApp.class)
public class PessoaResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_NASCIMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASCIMENTO = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaMapper pessoaMapper;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private PessoaQueryService pessoaQueryService;

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

    private MockMvc restPessoaMockMvc;

    private Pessoa pessoa;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PessoaResource pessoaResource = new PessoaResource(pessoaService, pessoaQueryService);
        this.restPessoaMockMvc = MockMvcBuilders.standaloneSetup(pessoaResource)
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
    public static Pessoa createEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa()
            .nome(DEFAULT_NOME)
            .email(DEFAULT_EMAIL)
            .dataNascimento(DEFAULT_DATA_NASCIMENTO);
        return pessoa;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pessoa createUpdatedEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa()
            .nome(UPDATED_NOME)
            .email(UPDATED_EMAIL)
            .dataNascimento(UPDATED_DATA_NASCIMENTO);
        return pessoa;
    }

    @BeforeEach
    public void initTest() {
        pessoa = createEntity(em);
    }

    @Test
    @Transactional
    public void createPessoa() throws Exception {
        int databaseSizeBeforeCreate = pessoaRepository.findAll().size();

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);
        restPessoaMockMvc.perform(post("/api/pessoas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isCreated());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate + 1);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPessoa.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPessoa.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void createPessoaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pessoaRepository.findAll().size();

        // Create the Pessoa with an existing ID
        pessoa.setId(1L);
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPessoaMockMvc.perform(post("/api/pessoas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pessoaRepository.findAll().size();
        // set the field null
        pessoa.setNome(null);

        // Create the Pessoa, which fails.
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        restPessoaMockMvc.perform(post("/api/pessoas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPessoas() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList
        restPessoaMockMvc.perform(get("/api/pessoas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pessoa.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())));
    }
    
    @Test
    @Transactional
    public void getPessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get the pessoa
        restPessoaMockMvc.perform(get("/api/pessoas/{id}", pessoa.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pessoa.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.dataNascimento").value(DEFAULT_DATA_NASCIMENTO.toString()));
    }

    @Test
    @Transactional
    public void getAllPessoasByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome equals to DEFAULT_NOME
        defaultPessoaShouldBeFound("nome.equals=" + DEFAULT_NOME);

        // Get all the pessoaList where nome equals to UPDATED_NOME
        defaultPessoaShouldNotBeFound("nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    public void getAllPessoasByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome in DEFAULT_NOME or UPDATED_NOME
        defaultPessoaShouldBeFound("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME);

        // Get all the pessoaList where nome equals to UPDATED_NOME
        defaultPessoaShouldNotBeFound("nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    public void getAllPessoasByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome is not null
        defaultPessoaShouldBeFound("nome.specified=true");

        // Get all the pessoaList where nome is null
        defaultPessoaShouldNotBeFound("nome.specified=false");
    }

    @Test
    @Transactional
    public void getAllPessoasByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where email equals to DEFAULT_EMAIL
        defaultPessoaShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the pessoaList where email equals to UPDATED_EMAIL
        defaultPessoaShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllPessoasByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultPessoaShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the pessoaList where email equals to UPDATED_EMAIL
        defaultPessoaShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllPessoasByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where email is not null
        defaultPessoaShouldBeFound("email.specified=true");

        // Get all the pessoaList where email is null
        defaultPessoaShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    public void getAllPessoasByDataNascimentoIsEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dataNascimento equals to DEFAULT_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.equals=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the pessoaList where dataNascimento equals to UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.equals=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void getAllPessoasByDataNascimentoIsInShouldWork() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dataNascimento in DEFAULT_DATA_NASCIMENTO or UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.in=" + DEFAULT_DATA_NASCIMENTO + "," + UPDATED_DATA_NASCIMENTO);

        // Get all the pessoaList where dataNascimento equals to UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.in=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void getAllPessoasByDataNascimentoIsNullOrNotNull() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dataNascimento is not null
        defaultPessoaShouldBeFound("dataNascimento.specified=true");

        // Get all the pessoaList where dataNascimento is null
        defaultPessoaShouldNotBeFound("dataNascimento.specified=false");
    }

    @Test
    @Transactional
    public void getAllPessoasByDataNascimentoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dataNascimento greater than or equals to DEFAULT_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.greaterOrEqualThan=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the pessoaList where dataNascimento greater than or equals to UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.greaterOrEqualThan=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void getAllPessoasByDataNascimentoIsLessThanSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dataNascimento less than or equals to DEFAULT_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.lessThan=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the pessoaList where dataNascimento less than or equals to UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.lessThan=" + UPDATED_DATA_NASCIMENTO);
    }


    @Test
    @Transactional
    public void getAllPessoasByPetsIsEqualToSomething() throws Exception {
        // Initialize the database
        Pet pets = PetResourceIT.createEntity(em);
        em.persist(pets);
        em.flush();
        pessoa.addPets(pets);
        pessoaRepository.saveAndFlush(pessoa);
        Long petsId = pets.getId();

        // Get all the pessoaList where pets equals to petsId
        defaultPessoaShouldBeFound("petsId.equals=" + petsId);

        // Get all the pessoaList where pets equals to petsId + 1
        defaultPessoaShouldNotBeFound("petsId.equals=" + (petsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPessoaShouldBeFound(String filter) throws Exception {
        restPessoaMockMvc.perform(get("/api/pessoas?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pessoa.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())));

        // Check, that the count call also returns 1
        restPessoaMockMvc.perform(get("/api/pessoas/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPessoaShouldNotBeFound(String filter) throws Exception {
        restPessoaMockMvc.perform(get("/api/pessoas?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPessoaMockMvc.perform(get("/api/pessoas/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPessoa() throws Exception {
        // Get the pessoa
        restPessoaMockMvc.perform(get("/api/pessoas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Update the pessoa
        Pessoa updatedPessoa = pessoaRepository.findById(pessoa.getId()).get();
        // Disconnect from session so that the updates on updatedPessoa are not directly saved in db
        em.detach(updatedPessoa);
        updatedPessoa
            .nome(UPDATED_NOME)
            .email(UPDATED_EMAIL)
            .dataNascimento(UPDATED_DATA_NASCIMENTO);
        PessoaDTO pessoaDTO = pessoaMapper.toDto(updatedPessoa);

        restPessoaMockMvc.perform(put("/api/pessoas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isOk());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoa.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPessoa.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    public void updateNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPessoaMockMvc.perform(put("/api/pessoas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeDelete = pessoaRepository.findAll().size();

        // Delete the pessoa
        restPessoaMockMvc.perform(delete("/api/pessoas/{id}", pessoa.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pessoa.class);
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setId(1L);
        Pessoa pessoa2 = new Pessoa();
        pessoa2.setId(pessoa1.getId());
        assertThat(pessoa1).isEqualTo(pessoa2);
        pessoa2.setId(2L);
        assertThat(pessoa1).isNotEqualTo(pessoa2);
        pessoa1.setId(null);
        assertThat(pessoa1).isNotEqualTo(pessoa2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaDTO.class);
        PessoaDTO pessoaDTO1 = new PessoaDTO();
        pessoaDTO1.setId(1L);
        PessoaDTO pessoaDTO2 = new PessoaDTO();
        assertThat(pessoaDTO1).isNotEqualTo(pessoaDTO2);
        pessoaDTO2.setId(pessoaDTO1.getId());
        assertThat(pessoaDTO1).isEqualTo(pessoaDTO2);
        pessoaDTO2.setId(2L);
        assertThat(pessoaDTO1).isNotEqualTo(pessoaDTO2);
        pessoaDTO1.setId(null);
        assertThat(pessoaDTO1).isNotEqualTo(pessoaDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(pessoaMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(pessoaMapper.fromId(null)).isNull();
    }
}
