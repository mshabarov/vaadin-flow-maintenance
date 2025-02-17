package org.vaadin.maintenance;

import com.vaadin.flow.component.ComponentEvent;

public class FilterEvent extends ComponentEvent<FilterComponent> {

    private final Filter filter;
    private final boolean fetch;

    public FilterEvent(FilterComponent source, Filter filter) {
        this(source, filter, false);
    }

    public FilterEvent(FilterComponent source, Filter filter, boolean fetch) {
        super(source, false);
        this.filter = filter;
        this.fetch = fetch;
    }

    public Filter getFilter() {
        return filter;
    }

    public boolean isFetch() {
        return fetch;
    }
}
