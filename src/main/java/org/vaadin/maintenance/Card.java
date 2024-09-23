package org.vaadin.maintenance;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Card extends Div {

    private static final int DESCRIPTION_LENGTH = 500;

    public Card(Issue issue) {
        String body = issue.getBody();
        if (body.length() > DESCRIPTION_LENGTH) {
            body = body.substring(0, DESCRIPTION_LENGTH) + "...";
        }

        // Card container
        addClassName("card");  // Add card CSS class

        // Title
        Div titleDiv = new Div();
        titleDiv.addClassName("card-header");
        titleDiv.setText(issue.getTitle());

        Avatar avatar = new Avatar(issue.getUser().getLogin(), issue.getUser().getAvatarUrl());
        HorizontalLayout header = new HorizontalLayout();

        HorizontalLayout titleGroup = new HorizontalLayout();
        if (issue.getCreatedAt().isAfter(Instant.now().minus(7, ChronoUnit.DAYS))) {
            Span newPull = new Span("New");
            newPull.getElement().getThemeList().add("badge success");
            titleGroup.add(newPull);
        } else if (issue.getUpdatedAt().isAfter(Instant.now().minus(7, ChronoUnit.DAYS))) {
            Span updated = new Span("Updated");
            updated.getElement().getThemeList().add("badge contrast");
            titleGroup.add(updated);
        }

        titleGroup.add(titleDiv);
        titleGroup.setAlignItems(FlexComponent.Alignment.START);

        // Add the buttons to the layout
        header.add(titleGroup, avatar);

        // Set justify content mode to space between
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Set the layout to full width
        header.setWidthFull();
        add(header);

        // Content
        Div contentDiv = new Div();
        contentDiv.addClassName("card-content");
        contentDiv.setText(body);

        // Footer
        HorizontalLayout footer = new HorizontalLayout();
        footer.addClassName("card-footer");

        // Add footer text and a button to the footer
        footer.add(new Anchor(issue.getUrl(), "Open in a new tab", AnchorTarget.BLANK));

        // Add titleDiv, content, and footer to the card
        add(header, contentDiv, footer);
    }
}
