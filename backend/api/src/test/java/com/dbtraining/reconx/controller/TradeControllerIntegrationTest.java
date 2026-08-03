package com.dbtraining.reconx.controller;

import com.dbtraining.reconx.dto.TradeRequest;
import com.dbtraining.reconx.dto.TradeResponse;
import com.dbtraining.reconx.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "reconx.security.jwt.secret=test-secret-key-test-secret-key-test-secret"
        }
)
class TradeControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void fullStack_createTrade_worksWithValidToken() {
        // 1. Mint a real, cryptographically valid JWT for a test TRADER
        String token = jwtTokenProvider.generate("integration-trader@reconx.com", "TRADER");

        // 2. Attach the token to the HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Prepare the Trade payload
        TradeRequest tradeReq = new TradeRequest(
                "INT-TRD-001",
                1L,
                1L,
                "EQUITY",
                "BUY",
                new BigDecimal("500.00"),
                new BigDecimal("10.50"),
                LocalDate.now()
        );
        HttpEntity<TradeRequest> request = new HttpEntity<>(tradeReq, headers);

        // 4. Send a real HTTP POST request to the running server
        ResponseEntity<TradeResponse> response = restTemplate.exchange(
                "/api/v1/trades", 
                HttpMethod.POST, 
                request, 
                TradeResponse.class
        );

        // 5. Assert the entire system (Filter -> Controller -> Service -> DB) worked
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().tradeRef()).isEqualTo("INT-TRD-001");
        
        // Ensure the real Database actually assigned it a primary key!
        assertThat(response.getBody().id()).isGreaterThan(0L); 
    }
}