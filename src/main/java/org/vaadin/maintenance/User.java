package org.vaadin.maintenance;

import java.util.Objects;

public class User {
    private String login;
    private String avatarUrl;

    public User(String login, String avatarUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return Objects.equals(login, user.login) && Objects.equals(avatarUrl, user.avatarUrl);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(login);
        result = 31 * result + Objects.hashCode(avatarUrl);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
               "login='" + login + '\'' +
               ", avatarUrl='" + avatarUrl + '\'' +
               '}';
    }
}
