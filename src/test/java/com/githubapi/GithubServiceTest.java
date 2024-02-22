package com.githubapi;

import com.githubapi.exception.ApiLimitExceededException;
import com.githubapi.exception.CustomException;
import com.githubapi.model.Owner;
import com.githubapi.model.RepositoryInfo;
import com.githubapi.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.ArgumentMatchers.any;
import java.util.List;

public class GithubServiceTest {

    @InjectMocks
    private GithubService githubService;

    @Mock
    private RestTemplate restTemplate;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        RepositoryInfo[] repositoryInfos = { new RepositoryInfo("repo-name", new Owner("owner-login"), null, false) };
        ResponseEntity<RepositoryInfo[]> responseEntity = new ResponseEntity<>(repositoryInfos, HttpStatus.OK);

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RepositoryInfo[].class)))
                .thenReturn(responseEntity);
    }
    @Test
    public void getUserRepositories_ShouldReturnRepositories() {
        RepositoryInfo[] repositoryInfos = { new RepositoryInfo("repo-name", new Owner("owner-login"), null, false) };
        when(restTemplate.getForObject(anyString(), eq(RepositoryInfo[].class))).thenReturn(repositoryInfos);

        List<RepositoryInfo> result = githubService.getUserRepositories("user");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("repo-name", result.get(0).getName());
        assertEquals("owner-login", result.get(0).getOwner().getLogin());
    }

    @Test
    public void getUserRepositories_WhenNetworkIssue_ShouldThrowCustomException() {
        // Имитируем ResourceAccessException вместо RuntimeException
        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RepositoryInfo[].class)))
                .thenThrow(new ResourceAccessException("Network error"));

        // Проверяем, что сервис выбрасывает CustomException при обработке ResourceAccessException
        Exception exception = assertThrows(CustomException.class, () -> {
            githubService.getUserRepositories("user");
        });

        String expectedMessage = "Failed to fetch repositories due to network issues";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void getUserRepositories_WhenNoRepositories_ShouldReturnEmptyList() {
        ResponseEntity<RepositoryInfo[]> responseEntity = new ResponseEntity<>(new RepositoryInfo[]{}, HttpStatus.OK);

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RepositoryInfo[].class)))
                .thenReturn(responseEntity);

        List<RepositoryInfo> result = githubService.getUserRepositories("userWithoutRepos");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    public void getUserRepositories_WhenApiLimitExceeded_ShouldThrowApiLimitExceededException() {
        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RepositoryInfo[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "API rate limit exceeded"));

        Exception exception = assertThrows(ApiLimitExceededException.class, () -> {
            githubService.getUserRepositories("userWithExceededLimit");
        });

        assertEquals("API rate limit exceeded", exception.getMessage());
    }




}

