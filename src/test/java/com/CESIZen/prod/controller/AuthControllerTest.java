//package com.CESIZen.prod.controller;
//
//import com.CESIZen.prod.dto.MessageDTO;
//import com.CESIZen.prod.dto.TokenDTO;
//import com.CESIZen.prod.dto.user.LoginDTO;
//import com.CESIZen.prod.dto.user.RegisterDTO;
//import com.CESIZen.prod.dto.user.RegisterWithRoleDTO;
//import com.CESIZen.prod.entity.RoleEnum;
//import com.CESIZen.prod.service.AuthService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@WebMvcTest(AuthController.class)
//class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AuthService authService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void login_shouldReturnToken() throws Exception {
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setUsername("user");
//        loginDTO.setPassword("pass");
//
//        TokenDTO tokenDTO = new TokenDTO();
//        tokenDTO.setToken("jwt-token");
//
//        when(authService.login(any(LoginDTO.class))).thenReturn(tokenDTO);
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("jwt-token"));
//    }
//
//    @Test
//    void register_shouldReturnMessage() throws Exception {
//        RegisterDTO registerDTO = new RegisterDTO();
//        registerDTO.setUsername("newuser");
//        registerDTO.setEmail("email@example.com");
//        registerDTO.setPassword("password");
//
//        MessageDTO messageDTO = new MessageDTO();
//        messageDTO.setMessage("Utilisateur créé");
//
//        when(authService.register(any(RegisterDTO.class))).thenReturn(messageDTO);
//
//        mockMvc.perform(post("/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registerDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Utilisateur créé"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void registerWithRole_asAdmin_shouldReturnMessage() throws Exception {
//        RegisterWithRoleDTO dto = new RegisterWithRoleDTO();
//        dto.setUsername("adminuser");
//        dto.setEmail("admin@example.com");
//        dto.setPassword("password");
//        dto.setRole(RoleEnum.USER);
//
//        MessageDTO messageDTO = new MessageDTO();
//        messageDTO.setMessage("Utilisateur avec rôle créé");
//
//        when(authService.registerWithRole(any(RegisterWithRoleDTO.class), any())).thenReturn(messageDTO);
//
//        mockMvc.perform(post("/auth/admin/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Utilisateur avec rôle créé"));
//    }
//}
