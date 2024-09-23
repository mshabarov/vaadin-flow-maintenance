package org.vaadin.maintenance;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.shared.Registration;

public class FilterComponent extends FormLayout {

    private Select<String> createdAt = new Select<>();
    private Select<String> updatedAt = new Select<>();
    private Select<Status> status = new Select<>();
    private Checkbox draft = new Checkbox();
    private Button reset = new Button("Reset");
    private Filter filter = new Filter();

    public FilterComponent() {
        setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        setColspan(createdAt, 1);
        setColspan(updatedAt, 1);
        setColspan(status, 1);
        setColspan(draft, 2);
        setColspan(reset, 1);
        getElement().getStyle().setBorder("1px solid lightgray");
        getElement().getStyle().setBorderRadius("10px");
        getElement().getStyle().setPadding("30px");
        getElement().getStyle().setMargin("10px");

        createdAt.setItems("Last day", "Last 3 days", "Last week", "Last month", "All");

        createdAt.setLabel("Filter by creation date:");
        createdAt.addValueChangeListener(event -> {
            long daysToSubtract = switch (event.getValue()) {
                case "Last day" -> 1;
                case "Last 3 days" -> 3;
                case "Last week" -> 7;
                case "Last month" -> 31;
                case "All" -> 10000;
                default -> Long.MAX_VALUE;
            };

            Instant createdAt = Instant.now().minus(daysToSubtract, ChronoUnit.DAYS);
            filter.setCreatedAt(createdAt);

            fireEvent(new FilterEvent(this, filter));
        });

        updatedAt.setLabel("Filter by update date:");
        updatedAt.setItems("Last day", "Last 3 days", "Last week", "Last month", "All");
        updatedAt.addValueChangeListener(event -> {

            long daysToSubtract = switch (event.getValue()) {
                case "Last day" -> 1;
                case "Last 3 days" -> 3;
                case "Last week" -> 7;
                case "Last month" -> 31;
                case "All" -> 10000;
                default -> Long.MAX_VALUE;
            };

            Instant updatedAt = Instant.now().minus(daysToSubtract, ChronoUnit.DAYS);
            filter.setUpdatedAt(updatedAt);

            fireEvent(new FilterEvent(this, filter));
        });

        status.setLabel("Filter by status:");
        status.setItems(Status.values());
        status.addValueChangeListener(event -> {
            filter.setStatus(event.getValue());
            fireEvent(new FilterEvent(this, filter));
        });

        reset.addClickListener(click -> {
            createdAt.clear();
            updatedAt.clear();
            status.clear();
            draft.clear();
            filter = new Filter();
            fireEvent(new FilterEvent(this, filter));
        });
        reset.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_WARNING);

        draft.setLabel("Hide drafts");
        draft.addValueChangeListener(event -> {
           filter.setHideDraft(event.getValue());
           fireEvent(new FilterEvent(this, filter));
        });

        add(createdAt, updatedAt, status, draft, reset);
    }

    public Registration addFilterListener(
            ComponentEventListener<FilterEvent> listener) {
        return addListener(FilterEvent.class, listener);
    }
}
