package com.githubapi;

import com.githubapi.controller.GithubController;
import com.githubapi.exception.RateLimitExceededException;
import com.githubapi.exception.UserNotFoundException;
import com.githubapi.model.Owner;
import com.githubapi.model.RepositoryInfo;
import com.githubapi.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
public class GithubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubService githubService;

    @Test
    public void getUserRepositories_ShouldReturnRepositories() throws Exception {
        List<RepositoryInfo> repositories = List.of(new RepositoryInfo("repo-name", new Owner("owner-login"), null, false));
        when(githubService.getUserRepositories("user")).thenReturn(repositories);

        mockMvc.perform(get("/api/github/users/user/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name", is("repo-name")))
                .andExpect(jsonPath("$.data[0].owner.login", is("owner-login")));

    }

    @Test
    public void getUserRepositories_WhenNoRepositories_ShouldReturnEmptyList() throws Exception {
        when(githubService.getUserRepositories("emptyUser")).thenReturn(List.of());

        mockMvc.perform(get("/api/github/users/emptyUser/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(List.of())));
    }
    @Test
    public void getUserRepositories_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(githubService.getUserRepositories("nonexistentUser")).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/github/users/nonexistentUser/repositories"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User not found")));
    }
    @Test
    public void getUserRepositories_WhenServerError_ShouldReturnServerError() throws Exception {
        when(githubService.getUserRepositories("user")).thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(get("/api/github/users/user/repositories"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Internal Server Error")));
    }
    @Test
    public void getUserRepositories_WhenRateLimitExceeded_ShouldReturnForbidden() throws Exception {
        when(githubService.getUserRepositories("rateLimitedUser")).thenThrow(new RateLimitExceededException("API rate limit exceeded"));

        mockMvc.perform(get("/api/github/users/rateLimitedUser/repositories"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("API rate limit exceeded")));
    }


}

