package com.githubapi.controller;

import com.githubapi.dto.ApiResponse;
import com.githubapi.exception.RateLimitExceededException;
import com.githubapi.exception.UserNotFoundException;
import com.githubapi.model.RepositoryInfo;
import com.githubapi.service.GithubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubController {
    private final GithubService githubService;
    private static final Logger logger = LoggerFactory.getLogger(GithubController.class);

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repositories")
    public ResponseEntity<ApiResponse> getUserRepositories(@PathVariable String username) {
        try {
            logger.info("Fetching repositories for user: {}", username);
            List<RepositoryInfo> repositories = githubService.getUserRepositories(username);
            return ResponseEntity.ok(new ApiResponse(200, "Repositories fetched successfully", repositories));
        } catch (RateLimitExceededException e) {
            logger.error("API rate limit exceeded for user: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(403, "API rate limit exceeded", null));
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, "User not found", null));
        } catch (Exception e) {
            logger.error("Error fetching repositories for user: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Internal Server Error", null));
        }
    }

}
