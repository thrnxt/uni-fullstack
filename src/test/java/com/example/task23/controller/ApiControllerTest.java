package com.example.task23.controller;

import com.example.task23.model.University;
import com.example.task23.model.User;
import com.example.task23.repository.UniversityRepository;
import com.example.task23.repository.UserRepository;
import com.example.task23.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private UniversityRepository universityRepository;
    @MockitoBean
    private JwtService jwtService;
    @Test
    void getUserByIdTest() throws Exception {
        University university = new University();
        university.setId(1L);
        university.setTitle("iitu");

        User user = new User();
        user.setId(1L);
        user.setUsername("Takhir");
        user.setPassword("123");
        user.setUniversity(university);
        user.setRoles(Set.of("User"));


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Takhir"));
    }

    @Test
    void createUserTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("Takhir");
        user.setPassword("123");
        user.setRoles(Set.of("User"));

        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"username":"Takhir","password":"123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Takhir"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void addUniversityToUserTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("Takhir");

        University university = new University();
        university.setId(2L);
        university.setTitle("IITU");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(universityRepository.findById(2L)).thenReturn(Optional.of(university));
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/add/1/university/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Takhir"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() throws Exception {
        University uni = new University();
        uni.setId(10L);
        uni.setTitle("IITU");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("Old");
        existingUser.setPassword("pass");

        User updated = new User();
        updated.setId(1L);
        updated.setUsername("NewName");
        updated.setPassword("newPass");
        updated.setUniversity(uni);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"NewName","password":"newPass","university":{"id":10,"title":"IITU"}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("NewName"))
                .andExpect(jsonPath("$.password").value("newPass"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTestBadRequestIfNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/user/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"Ghost","password":"none"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Пользователь с id 99 не найден"));
    }
    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь с id 1 успешно удален"));

        verify(userRepository, times(1)).deleteById(1L);
    }
}
