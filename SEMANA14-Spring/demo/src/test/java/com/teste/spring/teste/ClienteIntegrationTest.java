package com.teste.spring.teste;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.spring.teste.model.Cliente;
import com.teste.spring.teste.repository.ClienteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClienteIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ClienteRepository repo;

    @BeforeEach
    void limparBanco() {
        repo.deleteAll();
    }

    @Test
    @Order(1)
    void deveCriarCliente() throws Exception {
        Cliente c = new Cliente();
        c.setNome("Carlos");
        c.setEmail("CarlosHelo@email.com");
        c.setTelefone("123456");

        mvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(c)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Carlos"));
    }

    @Test
    @Order(2)
    void deveListarClientes() throws Exception {

        Cliente c = new Cliente();
        c.setNome("Helo");
        c.setEmail("HeloCarlos@gmail.com");
        c.setTelefone("111231");
        repo.save(c);

        mvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Helo"));
    }


    @Test
    @Order(3)
    void deveBuscarPorId() throws Exception {
        Cliente c = new Cliente();
        c.setNome("sla");
        c.setEmail("sla@teste.com");
        c.setTelefone("1111");
        repo.save(c);

        mvc.perform(get("/api/clientes/" + c.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("sla"));
    }

    @Test
    @Order(4)
    void deveAtualizarCliente() throws Exception {
        Cliente c = new Cliente();
        c.setNome("Antigo");
        c.setEmail("antigo@yahouuu.com");
        c.setTelefone("11111231");
        Cliente antigo = repo.save(c);

        Cliente novo = new Cliente();
        novo.setNome("Novo");
        novo.setEmail("novo@teste.com");
        novo.setTelefone("2222");

        mvc.perform(put("/api/clientes/" + antigo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo"))
                .andExpect(jsonPath("$.email").value("novo@teste.com"));
    }

    @Test
    @Order(5)
    void deveExcluirCliente() throws Exception {
        Cliente c = new Cliente();
        c.setNome("Cliente Tal");
        c.setEmail("Tal@OTalDoEmail.com");
        c.setTelefone("123456789");
        Cliente deletado = repo.save(c);

        mvc.perform(delete("/api/clientes/" + deletado.getId()))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(repo.findById(deletado.getId()).isPresent());
    }

    @Test
    @Order(6)
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        mvc.perform(get("/api/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Cliente não encontrado")));
    }
}
