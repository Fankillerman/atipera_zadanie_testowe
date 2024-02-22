package com.githubapi;

import com.githubapi.controller.GithubController;
import com.githubapi.exception.RateLimitExceededException;
import com.githubapi.service.GithubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;



@ExtendWith(SpringExtension.class)
@WebMvcTest(GithubController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubService githubService;

    @Test
    public void getUserRepositories_WhenRateLimitExceeded_ShouldReturnForbidden() throws Exception {
        when(githubService.getUserRepositories(anyString())).thenThrow(new RateLimitExceededException("API rate limit exceeded"));

        mockMvc.perform(get("/api/github/users/user/repositories"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.message", is("API rate limit exceeded")));
    }
}
