package org.vaadin.maintenance;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Filter {
    private Instant createdAt;
    private Instant updatedAt;
    private Status status;
    private boolean hideDraft;

    public Filter() {
        createdAt = Instant.now().minus(10000, ChronoUnit.DAYS);
        updatedAt = Instant.now().minus(10000, ChronoUnit.DAYS);
        status = Status.ALL;
        hideDraft = false;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isHideDraft() {
        return hideDraft;
    }

    public void setHideDraft(boolean hideDraft) {
        this.hideDraft = hideDraft;
    }
}
