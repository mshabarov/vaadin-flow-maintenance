package org.vaadin.maintenance;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
@Route("issues")
public class IssuesView extends VerticalLayout {

    private final Accordion accordion = new Accordion();
    private Map<String, List<Issue>> issues;

    private final GitHubService github;

    public IssuesView(@Autowired GitHubService github) {
        this.github = github;

        FilterComponent filters = new FilterComponent();
        filters.addFilterListener(event -> {
            Filter filter = event.getFilter();
            try {
                accordion.getChildren().forEach(accordion::remove);
                renderIssues(filter);
            } catch (IOException e) {
                showErrorNotification(e);
            }
        });

        add(filters);
        accordion.setSizeFull();

        try {
            renderIssues(new Filter());
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
            showErrorNotification(e);
        }
    }

    private static void showErrorNotification(IOException e) {
        Notification notification = new Notification("Error: " + e.getMessage());
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    @SuppressWarnings("unchecked")
    private void renderIssues(Filter filter) throws IOException {
        if (issues == null) {
            issues = new HashMap<>();
            fetchPulls();
        }

        Map<String, List<Issue>> filterIssues = filterIssues(filter);

        accordion.removeFromParent();

        if (filterIssues.isEmpty()) {
            add(new Span("No issues found"));
        } else {
            add(accordion);
        }


        filterIssues.entrySet().stream().sorted(Comparator.comparingLong(entry ->
                        ((Map.Entry<String, List<Issue>>) entry).getValue().size()).reversed())
                .forEach(entry -> {
                    RepoPanel repoPanel = new RepoPanel(entry, IssueType.ISSUES);
                    accordion.add(repoPanel);
                });

    }

    private void fetchPulls() {
        Repos.REPOS.forEach(repo -> {
            try {
                issues.put(repo, github.getOpenIssues(repo));
            } catch (IOException | InterruptedException e) {
                getLogger().error(e.getMessage(), e);
                Notification.show("Could not fetch pull requests");
            }
        });
    }

    private Map<String, List<Issue>> filterIssues(Filter filter) {
        Map<String, List<Issue>> filtered = new HashMap<>();
        issues.forEach((key, value) -> {
            List<Issue> filteredPulls = value.stream()
                    .filter(record -> record.getCreatedAt().isAfter(filter.getCreatedAt()))
                    .filter(record -> record.getUpdatedAt().isAfter(filter.getUpdatedAt()))
                    .filter(record -> {
                        if (filter.getStatus() == Status.NEW) {
                            return record.getCreatedAt().isAfter(Instant.now().minus(7, ChronoUnit.DAYS));
                        } else if (filter.getStatus() == Status.UPDATED) {
                            return record.getUpdatedAt().isAfter(Instant.now().minus(7, ChronoUnit.DAYS));
                        }
                        return true;
                    }).toList();
            filtered.put(key, filteredPulls);
        });
        return filtered;
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(IssuesView.class);
    }
}
