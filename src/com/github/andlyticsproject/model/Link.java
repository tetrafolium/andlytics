package com.github.andlyticsproject.model;

public class Link {

    public Long id;

    private String name;

    private String url;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getURL() {
        return url;
    }

    public void setURL(final String url) {
        this.url = url;
    }
}
