package com.workuments.workuments;

/**
 * Created by bradcollins on 11/19/15.
 */
public class SiteResults {
    private String id = "";
    private byte[] icon = null;
    private String name = "";
    private String url = "";
    private String username = "";

    SiteResults(String _id, byte[] _icon, String _name, String _url, String _username){
        id = _id;
        icon = _icon;
        name = _name;
        url = _url;
        username = _username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setIcon(byte[] _icon){this.icon = _icon;}

    public byte[] getIcon() {
        return icon;
    }
}
