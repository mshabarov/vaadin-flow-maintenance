package org.vaadin.maintenance;

import java.time.Instant;
import java.util.Objects;

public class Issue {

    private User user;
    private String title;
    private String body;
    private String url;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean draft;

    public Issue(User user, String title, String body, String url, Instant createdAt, Instant updatedAt) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.url = url;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Issue that = (Issue) o;
        return Objects.equals(user, that.user) && Objects.equals(title, that.title) && Objects.equals(body, that.body) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(user);
        result = 31 * result + Objects.hashCode(title);
        result = 31 * result + Objects.hashCode(body);
        result = 31 * result + Objects.hashCode(url);
        return result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getCreatedAt() {

        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Issue{" +
               "user=" + user +
               ", title='" + title + '\'' +
               ", body='" + body + '\'' +
               ", url='" + url + '\'' +
               '}';
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }
}
