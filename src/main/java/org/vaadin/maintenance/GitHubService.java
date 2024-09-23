package org.vaadin.maintenance;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonString;
import elemental.json.impl.JsonUtil;

@Service
public class GitHubService {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/";

    public static final String OWNER = "vaadin";

    public List<Issue> getOpenPullRequests(String repo)
            throws IOException, InterruptedException, JSONException {
        String url = GITHUB_API_URL + OWNER + "/" + repo + "/pulls";

        HttpResponse<String> response = fetch(url);

        if (response.statusCode() == 200) {
            // Parse and print the pull requests
            return parsePullRequests(response.body());
        } else {
            getLogger().error("Failed to fetch pull requests. HTTP error code: {}, body: {}",
                    response.statusCode(), response.body());
            Notification.show("Failed to fetch pull requests. HTTP error code: "
                              + response.statusCode() + ", body: " + response.body());
            return new ArrayList<>();
        }
    }

    public List<Issue> getOpenIssues(String repo)
            throws IOException, InterruptedException {
        String url = GITHUB_API_URL + OWNER + "/" + repo + "/issues?state=open&per_page=100&page=1";

        HttpResponse<String> response = fetch(url);

        if (response.statusCode() == 200) {
            // Parse and print the pull requests
            return parseIssues(response.body());
        } else {
            getLogger().error("Failed to fetch issues. HTTP error code: {}, body: {}",
                    response.statusCode(), response.body());

            Notification notification = new Notification("Failed to fetch issues. HTTP error code: "
                                                         + response.statusCode() + ", body: " + response.body());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return new ArrayList<>();
        }
    }

    private static HttpResponse<String> fetch(String url) throws IOException, InterruptedException {
        // Create an HTTP Client
        HttpClient client = HttpClient.newHttpClient();

        // Build the HTTP Request
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(url))
                .header("Accept", "application/vnd.github+json");

        String token = System.getProperty("github.token", "");
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        HttpRequest request = builder.build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    // Parse the JSON response and print the pull request information
    private List<Issue> parsePullRequests(String responseBody) {
        List<Issue> result = new ArrayList<>();
        JsonArray pullRequests = JsonUtil.parse(responseBody);

        if (pullRequests.length() == 0) {
            return result;
        }

        for (int i = 0; i < pullRequests.length(); i++) {
            JsonObject pull = pullRequests.getObject(i);
            JsonObject user = pull.get("user");
            User userObject = new User(user.getString("login"), user.getString("avatar_url"));
            Instant createdAt = Instant.parse(pull.getString("created_at"));
            Instant updatedAt = Instant.parse(pull.getString("updated_at"));
            Issue pullRequest = new Issue(userObject, pull.getString("title"),
                    (pull.get("body") != null && pull.get("body") instanceof JsonString) ? pull.getString("body") : "",
                    pull.getString("html_url"),
                    createdAt, updatedAt);
            if (pull.hasKey("draft") && pull.getBoolean("draft")) {
                pullRequest.setDraft(true);
            }
            result.add(pullRequest);
        }

        return result;
    }

    private List<Issue> parseIssues(String responseBody) {
        List<Issue> result = new ArrayList<>();
        JsonArray issues = JsonUtil.parse(responseBody);

        if (issues.length() == 0) {
            return result;
        }

        for (int i = 0; i < issues.length(); i++) {
            JsonObject issue = issues.getObject(i);
            if (!issue.hasKey("pull_request")) {
                JsonObject user = issue.get("user");
                User userObject = new User(user.getString("login"), user.getString("avatar_url"));
                Instant createdAt = Instant.parse(issue.getString("created_at"));
                Instant updatedAt = Instant.parse(issue.getString("updated_at"));
                Issue issueEntity = new Issue(userObject, issue.getString("title"),
                        (issue.get("body") != null && issue.get("body") instanceof JsonString) ? issue.getString("body") : "",
                        issue.getString("html_url"),
                        createdAt, updatedAt);
                result.add(issueEntity);
            }
        }

        return result;
    }

    private static List<Issue> fetchPullRequestsFromStub() throws IOException {
        JsonArray pulls = readStub("data/pulls-stub.json");
        List<Issue> pullRequests = new ArrayList<>();
        for (int i = 0; i < pulls.length(); i++) {
            JsonObject pull = pulls.getObject(i);
            JsonObject user = pull.get("user");
            User userObject = new User(user.getString("login"), user.getString("avatar_url"));
            Instant createdAt = Instant.parse(pull.getString("created_at"));
            Instant updatedAt = Instant.parse(pull.getString("updated_at"));
            Issue pullRequest = new Issue(userObject, pull.getString("title"),
                    (pull.get("body") != null && pull.get("body") instanceof JsonString) ? pull.getString("body") : "",
                    pull.getString("html_url"),
                    createdAt, updatedAt);
            pullRequests.add(pullRequest);
        }
        return pullRequests;
    }

    private static JsonArray readStub(String fileName) throws IOException {
        Resource resource = new ClassPathResource(fileName);

        // Read the resource file as a String
        byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
        return JsonUtil.parse(new String(bytes));
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(GitHubService.class);
    }
}

