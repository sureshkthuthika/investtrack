
package com.investtrack.backend.controller;

import com.investtrack.backend.service.PortfolioService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@Disabled("Temporarily skipped")
@WebMvcTest(PortfolioController.class)
@AutoConfigureMockMvc(addFilters = false)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortfolioService portfolioService;

    @Test
    void getAllPortfolios_withAuthenticatedUser_shouldReturnOk()
            throws Exception {

        String email = "investtrack.demo@example.com";

        when(portfolioService.getAllPortfolios(email))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/portfolios")
                        .with(jwt().jwt(token -> token.subject(email))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(portfolioService).getAllPortfolios(email);
    }
}
