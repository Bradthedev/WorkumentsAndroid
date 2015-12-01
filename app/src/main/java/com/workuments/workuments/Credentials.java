package com.workuments.workuments;

/**
 * Created by Bradley on 12/1/2015.
 */
public class Credentials {

    private String url;
    private String username;
    private String password;

    public Credentials(){ }

    public Credentials(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
