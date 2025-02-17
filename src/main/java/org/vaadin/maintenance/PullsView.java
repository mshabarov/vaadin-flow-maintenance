package org.vaadin.maintenance;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
@RouteAlias("")
@Route("pulls")
public class PullsView extends VerticalLayout {

    private final Accordion accordion = new Accordion();
    private Map<String, List<Issue>> pulls;

    private final GitHubService github;

    private Button fetch;

    public PullsView(@Autowired GitHubService github) {
        this.github = github;

        FilterComponent filters = new FilterComponent();
        filters.addFilterListener(event -> {
            Filter filter = event.getFilter();
            try {
                accordion.getChildren().forEach(accordion::remove);
                renderPulls(filter);
            } catch (IOException e) {
                getLogger().error(e.getMessage(), e);
                showErrorNotification(e);
            }
        });

        add(filters);
        accordion.setSizeFull();



        fetch = new Button("Fetch pulls", click -> {
            try {
                renderPulls(new Filter());
                fetch.setVisible(false);
            } catch (IOException e) {
                getLogger().error(e.getMessage(), e);
                showErrorNotification(e);
            }
        });

        fetch.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(new HorizontalLayout(fetch) {{
            getStyle().set("justify-content", "center");
            setWidthFull();
        }});

        getStyle().set("width", "75%").set("margin", "0 auto");
    }

    private static void showErrorNotification(Exception e) {
        Notification notification = new Notification("Error: " + e.getMessage());
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    @SuppressWarnings("unchecked")
    private void renderPulls(Filter filter) throws IOException {
        if (pulls == null) {
            pulls = new HashMap<>();
            fetchPulls(filter.isWithStarters());
        }

        Map<String, List<Issue>> filteredPulls = filterPulls(filter);

        accordion.removeFromParent();

        if (filteredPulls.isEmpty()) {
            add(new Span("No pull requests found"));
        } else {
            add(accordion);
        }

        filteredPulls.entrySet().stream().sorted(Comparator.comparingLong(entry ->
                        ((Map.Entry<String, List<Issue>>) entry).getValue().size()).reversed())
                .filter(entry -> !entry.getValue().isEmpty())
                .forEach(entry -> {
                    RepoPanel repoPanel = new RepoPanel(entry, IssueType.PULLS);
                    accordion.add(repoPanel);
        });

    }

    private Map<String, List<Issue>> filterPulls(Filter filter) {
        Map<String, List<Issue>> filtered = new HashMap<>();
        pulls.forEach((key, value) -> {
            List<Issue> filteredPulls = value.stream()
                    .filter(record -> !record.isDraft() || !filter.isHideDraft())
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

    private void fetchPulls(boolean withStarters) {
        List<String> repos = new ArrayList<>(Repos.REPOS);
        if (withStarters) {
            repos.addAll(Repos.STARTERS);
        }
        repos.forEach(repo -> {
            try {
                pulls.put(repo, github.getOpenPullRequests(repo));
            } catch (IOException | InterruptedException | JSONException e) {
                getLogger().error(e.getMessage(), e);
                showErrorNotification(repo);
            }
        });
    }

    private static void showErrorNotification(String url) {
        Notification notification = new Notification("Could not fetch pull request");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(0);
        notification.add(new Anchor(url, "Open in a new tab", AnchorTarget.BLANK));
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(PullsView.class);
    }

}
