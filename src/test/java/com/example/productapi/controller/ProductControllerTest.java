package com.example.productapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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
class ProductControllerTest {

    private static final String LAPTOP =
            "{\"name\":\"Ordinateur Portable\",\"description\":\"Laptop 15 pouces\",\"price\":1200.50,\"quantity\":10}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long createLaptop() throws Exception {
        String body = mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(LAPTOP))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void postCreeUnProduit_201() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(LAPTOP))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Ordinateur Portable")))
                .andExpect(jsonPath("$.price", is(1200.50)))
                .andExpect(jsonPath("$.quantity", is(10)));
    }

    @Test
    void postSansNom_400() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\":10,\"quantity\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Le nom est obligatoire")));
    }

    @Test
    void postPrixNegatifEtQuantiteNegative_400() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"price\":-5,\"quantity\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price", is("Le prix doit être positif")))
                .andExpect(jsonPath("$.quantity", is("La quantité ne peut pas être négative")));
    }

    @Test
    void getListe_200() throws Exception {
        createLaptop();
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void getParId_200() throws Exception {
        long id = createLaptop();
        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("Ordinateur Portable")));
    }

    @Test
    void getIdInexistant_404() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Produit introuvable avec l'id: 999")));
    }

    @Test
    void putModifie_200() throws Exception {
        long id = createLaptop();
        mockMvc.perform(put("/api/products/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ordinateur Portable Pro\",\"price\":1300.00,\"quantity\":8}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("Ordinateur Portable Pro")))
                .andExpect(jsonPath("$.price", is(1300.0)))
                .andExpect(jsonPath("$.quantity", is(8)));
    }

    @Test
    void putIdInexistant_404() throws Exception {
        mockMvc.perform(put("/api/products/999").contentType(MediaType.APPLICATION_JSON).content(LAPTOP))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Produit introuvable avec l'id: 999")));
    }

    @Test
    void deleteRenvoie204_puisGetRenvoie404() throws Exception {
        long id = createLaptop();
        mockMvc.perform(delete("/api/products/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/products/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void deleteIdInexistant_404() throws Exception {
        mockMvc.perform(delete("/api/products/999")).andExpect(status().isNotFound());
    }

    @Test
    void searchParNom_insensibleALaCasse() throws Exception {
        createLaptop();
        mockMvc.perform(get("/api/products/search").param("name", "PORTABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].name", everyItem(containsStringIgnoringCase("portable"))));
        mockMvc.perform(get("/api/products/search").param("name", "zzz-introuvable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void quantiteSuperieureA1000_400() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Stock\",\"price\":5,\"quantity\":1001}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("La quantité ne peut pas dépasser 1000")));
    }

    @Test
    void putQuantiteSuperieureA1000_400() throws Exception {
        long id = createLaptop();
        mockMvc.perform(put("/api/products/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Stock\",\"price\":5,\"quantity\":5000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("1000")));
    }
}
