
package com.githubapi.service;

import com.githubapi.exception.ApiLimitExceededException;
import com.githubapi.exception.CustomException;
import com.githubapi.model.BranchInfo;
import com.githubapi.model.RepositoryInfo;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

@Service
public class GithubService {
    private final RestTemplate restTemplate;
    private final String githubToken;

    public GithubService(RestTemplate restTemplate, @Value("${github.pat}") String githubToken) {
        this.restTemplate = restTemplate;
        this.githubToken = githubToken;
    }

    public List<RepositoryInfo> getUserRepositories(String username) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            String reposUrl = "https://api.github.com/users/" + username + "/repos";

            ResponseEntity<RepositoryInfo[]> response = restTemplate.exchange(
                    reposUrl, HttpMethod.GET, entity, RepositoryInfo[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.stream(response.getBody())
                        .map(repo -> {
                            List<BranchInfo> branches = getBranchesForRepo(username, repo.getName());
                            repo.setBranches(branches);
                            return repo;
                        })
                        .collect(Collectors.toList());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiLimitExceededException("API rate limit exceeded");
            }
            throw new CustomException("Error fetching repositories for user: " + username);
        } catch (ResourceAccessException e) {
            throw new CustomException("Failed to fetch repositories due to network issues");
        }
        return Collections.emptyList();
    }



    private List<BranchInfo> getBranchesForRepo(String username, String repoName) {
        String branchesUrl = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";
        BranchInfo[] branches = restTemplate.getForObject(branchesUrl, BranchInfo[].class);

        if (branches != null) {
            return Arrays.asList(branches);
        } else {
            return Collections.emptyList();
        }
    }

}
