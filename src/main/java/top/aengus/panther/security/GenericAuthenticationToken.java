package top.aengus.panther.security;

import org.apache.shiro.authc.AuthenticationToken;

public class GenericAuthenticationToken implements AuthenticationToken {

    private Object principal;

    private Object credentials;

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
