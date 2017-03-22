package de.papke.docker.hub.updater.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model class for the login response.
 *
 * @author Christoph Papke (info@papke.it)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
