package top.aengus.panther.security;

public class AdminRequestPrincipal {

    private final String username;

    public AdminRequestPrincipal(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
