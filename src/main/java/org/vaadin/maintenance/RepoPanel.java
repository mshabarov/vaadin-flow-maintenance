package org.vaadin.maintenance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class RepoPanel extends AccordionPanel {

    public RepoPanel(Map.Entry<String, List<Issue>> entry, IssueType issueType) {
        int size = entry.getValue().size();

        Span issuesNumber = new Span("" + size);
        issuesNumber.getElement().getThemeList().add("badge success");

        HorizontalLayout title = new HorizontalLayout();
        title.add(issuesNumber);

        String repoName = getRepoTitle(entry.getKey());
        String type = issueType.toString().toLowerCase();
        String url = "https://github.com/" + GitHubService.OWNER + "/" + entry.getKey() + "/" + type;
        Anchor repoLink = new Anchor(url, repoName, AnchorTarget.BLANK);
        title.add(repoLink);

        Collection<Component> cards = entry.getValue().stream()
                .map(Card::new).collect(Collectors.toList());


        setSummary(title);
//        accordionPanel.getElement().getStyle().setBackground("#DAF7A6")
        String color;
        switch (issueType) {
            case PULLS -> color = "lightgreen";
            case ISSUES -> color = "lightblue";
            default -> color = "white";
        }
        getElement().getStyle().setBackground(color)
                .setBorderRadius("10px").setPadding("10px").setMargin("10px");
        add(cards);
        addThemeVariants(DetailsVariant.REVERSE);

    }

    private String getRepoTitle(String repo) {
        return "Vaadin " + repo.substring(0, 1).toUpperCase() + repo.substring(1);
    }
}
