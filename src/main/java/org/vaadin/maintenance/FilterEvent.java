package org.vaadin.maintenance;

import com.vaadin.flow.component.ComponentEvent;

public class FilterEvent extends ComponentEvent<FilterComponent> {

    private final Filter filter;

    public FilterEvent(FilterComponent source, Filter filter) {
        super(source, false);
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }
}
