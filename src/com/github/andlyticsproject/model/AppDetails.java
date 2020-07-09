package com.github.andlyticsproject.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AppDetails {

    private Long id;

    private String description;
    private String changelog;
    private Date lastStoreUpdate;

    private List<Link> links = new ArrayList<Link>();

    public AppDetails(final String description, final String changelog, final Date lastStoreUpdate) {
        this.description = description;
        this.changelog = changelog;
        this.lastStoreUpdate = lastStoreUpdate == null ? null : (Date) lastStoreUpdate.clone();
    }

    public AppDetails(final String description, final String changelog, final Long lastStoreUpdate) {
        this.description = description;
        this.changelog = changelog;
        this.lastStoreUpdate = lastStoreUpdate == null ? null : new Date(lastStoreUpdate);
    }

    public AppDetails(final String description) {
        this(description, null, (Date) null);
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(final String changelog) {
        this.changelog = changelog;
    }

    public Date getLastStoreUpdate() {
        return lastStoreUpdate == null ? null : (Date) lastStoreUpdate.clone();
    }

    public void setLastStoreUpdate(final Date lastStoreUpdate) {
        this.lastStoreUpdate = lastStoreUpdate == null ? null : (Date) lastStoreUpdate.clone();
    }

    public List<Link> getLinks() {
        return Collections.unmodifiableList(links);
    }

    public void setLinks(final List<Link> links) {
        this.links = links;
    }

    public void addLink(final Link link) {
        links.add(link);
    }

}
