package org.vaadin.maintenance;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.shared.Registration;

public class FilterComponent extends VerticalLayout {

    private FormLayout form = new FormLayout();
    private HorizontalLayout buttons = new HorizontalLayout();
    private Select<String> createdAt = new Select<>();
    private Select<String> updatedAt = new Select<>();
    private Select<Status> status = new Select<>();
    private Checkbox draft = new Checkbox(true);
    private Checkbox withStarters = new Checkbox(true);
    private Button reset = new Button("Reset Filters");
    private Filter filter = new Filter();
    private Button load = new Button("Load");

    public FilterComponent() {
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        form.setColspan(createdAt, 1);
        form.setColspan(updatedAt, 1);
        form.setColspan(status, 1);
        form.setColspan(draft, 1);
        form.setColspan(withStarters, 1);
        form.setColspan(reset, 1);
        form.getElement().getStyle().setBorder("1px solid lightgray");
        form.getElement().getStyle().setBorderRadius("10px");
        form.getElement().getStyle().setPadding("30px");
        form.getElement().getStyle().setMargin("10px");

        buttons.getElement().getStyle().set("justify-content", "flex-end");
        buttons.setWidthFull();

        createdAt.setItems("Last day", "Last 3 days", "Last week", "Last month", "All");
        createdAt.setValue("Last week");

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
        updatedAt.setValue("Last week");
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
        status.setValue(Status.NEW);

        reset.addClickListener(click -> {
            createdAt.clear();
            updatedAt.clear();
            status.clear();
            draft.clear();
            withStarters.clear();
            filter = new Filter();
            fireEvent(new FilterEvent(this, filter));
        });
        reset.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_WARNING);

        load.addClickListener(click -> {
            fireEvent(new FilterEvent(this, filter, true));
            load.setEnabled(false);
        });
        load.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        draft.setLabel("Hide drafts");
        draft.addValueChangeListener(event -> {
           filter.setHideDraft(event.getValue());
           fireEvent(new FilterEvent(this, filter));
        });

        withStarters.setLabel("With starters");
        withStarters.addValueChangeListener(event -> {
            filter.setWithStarters(event.getValue());
            // do not trigger an initial fetch when this is ticked
        });

        form.add(createdAt, updatedAt, status, draft, withStarters);
        buttons.add(reset, load);

        add(form, buttons);
    }

    public Registration addFilterListener(
            ComponentEventListener<FilterEvent> listener) {
        return addListener(FilterEvent.class, listener);
    }
}
