package com.github.andlyticsproject.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppInfo {

  private Long id;

  private String account;

  private String packageName;

  private Date lastUpdate;

  private String name;

  private String iconUrl;

  private List<AppStats> history = new ArrayList<AppStats>();

  private AppStats latestStats;

  private boolean isDraftOnly;

  // TODO -- make this an enum? Currently not used
  private int publishState;

  private boolean ghost;

  private boolean skipNotification;

  private boolean ratingDetailsExpanded;

  private String versionName;

  private AdmobStats admobStats;

  private String admobAccount;
  private String admobSiteId;
  private String admobAdUnitId;

  private Date lastCommentsUpdate;

  private AppDetails details;

  private String developerId;
  private String developerName;

  private RevenueSummary totalRevenueSummary;

  public Long getId() { return id; }

  public void setId(final Long id) { this.id = id; }

  public String getAccount() { return account; }

  public void setAccount(final String account) { this.account = account; }

  public String getPackageName() { return packageName; }

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public Date getLastUpdate() { return lastUpdate; }

  public void setLastUpdate(final Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public String getName() { return name; }

  public void setName(final String name) { this.name = name; }

  public void setIconUrl(final String iconUrl) { this.iconUrl = iconUrl; }

  public String getIconUrl() { return iconUrl; }

  public String getIconName() {
    String iconName = packageName;
    if (iconUrl != null) {
      int slash = iconUrl.lastIndexOf('/');
      if (slash > -1) {
        iconName = iconUrl.substring(slash + 1, iconUrl.length());
      }
    }

    return iconName;
  }

  public void setHistory(final List<AppStats> history) {
    this.history = history;
  }

  public List<AppStats> getHistory() { return history; }

  public void addToHistory(final AppStats stats) { history.add(stats); }

  public void setLatestStats(final AppStats latestStats) {
    this.latestStats = latestStats;
  }

  public AppStats getLatestStats() { return latestStats; }

  public void setDraftOnly(final boolean isDraftOnly) {
    this.isDraftOnly = isDraftOnly;
  }

  public boolean isDraftOnly() { return isDraftOnly; }

  public int getPublishState() { return publishState; }

  public void setPublishState(final int publishState) {
    this.publishState = publishState;
  }

  public void setGhost(final boolean ghost) { this.ghost = ghost; }

  public boolean isGhost() { return ghost; }

  public void setRatingDetailsExpanded(final boolean ratingDetailsExpanded) {
    this.ratingDetailsExpanded = ratingDetailsExpanded;
  }

  public boolean isRatingDetailsExpanded() { return ratingDetailsExpanded; }

  public void setSkipNotification(final boolean skipNotification) {
    this.skipNotification = skipNotification;
  }

  public boolean isSkipNotification() { return skipNotification; }

  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }

  public String getVersionName() { return versionName; }

  public void setAdmobStats(final AdmobStats admobStats) {
    this.admobStats = admobStats;
  }

  public AdmobStats getAdmobStats() { return admobStats; }

  public String getAdmobAccount() { return admobAccount; }

  public void setAdmobAccount(final String admobAccount) {
    this.admobAccount = admobAccount;
  }

  public String getAdmobSiteId() { return admobSiteId; }

  public void setAdmobSiteId(final String admobSiteId) {
    this.admobSiteId = admobSiteId;
  }

  public String getAdmobAdUnitId() { return admobAdUnitId; }

  public void setAdmobAdUnitId(final String admobAdUnitId) {
    this.admobAdUnitId = admobAdUnitId;
  }

  public Date getLastCommentsUpdate() {
    return lastCommentsUpdate == null ? null : (Date)lastCommentsUpdate.clone();
  }

  public void setLastCommentsUpdate(final Date lastCommentsUpdate) {
    this.lastCommentsUpdate =
        lastCommentsUpdate == null ? null : (Date)lastCommentsUpdate.clone();
  }

  public AppDetails getDetails() { return details; }

  public void setDetails(final AppDetails details) { this.details = details; }

  public String getDeveloperId() { return developerId; }

  public void setDeveloperId(final String developerId) {
    this.developerId = developerId;
  }

  public String getDeveloperName() { return developerName; }

  public void setDeveloperName(final String developerName) {
    this.developerName = developerName;
  }

  public RevenueSummary getTotalRevenueSummary() { return totalRevenueSummary; }

  public void setTotalRevenueSummary(final RevenueSummary totalRevenueSummary) {
    this.totalRevenueSummary = totalRevenueSummary;
  }

  // XXX -- do we need to compare everything?
  // an app should be uniquely identified by the package name alone
  // (this is enforced by the Play Store)
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((account == null) ? 0 : account.hashCode());
    result =
        prime * result + ((developerId == null) ? 0 : developerId.hashCode());
    result = prime * result + ((history == null) ? 0 : history.hashCode());
    result = prime * result + ((iconUrl == null) ? 0 : iconUrl.hashCode());
    result =
        prime * result + ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
    result =
        prime * result + ((latestStats == null) ? 0 : latestStats.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result =
        prime * result + ((packageName == null) ? 0 : packageName.hashCode());
    result =
        prime * result + ((admobAccount == null) ? 0 : admobAccount.hashCode());
    result =
        prime * result + ((admobSiteId == null) ? 0 : admobSiteId.hashCode());
    result = prime * result +
             ((lastCommentsUpdate == null) ? 0 : lastCommentsUpdate.hashCode());
    return result;
  }

  // XXX -- do we need to compare everything?
  // an app should be uniquely identified by the package name alone
  // (this is enforced by the Play Store)
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AppInfo other = (AppInfo)obj;
    if (account == null) {
      if (other.account != null)
        return false;
    } else if (!account.equals(other.account))
      return false;
    if (developerId == null) {
      if (other.developerId != null)
        return false;
    } else if (!developerId.equals(other.developerId))
      return false;
    if (history == null) {
      if (other.history != null)
        return false;
    } else if (!history.equals(other.history))
      return false;
    if (iconUrl == null) {
      if (other.iconUrl != null)
        return false;
    } else if (!iconUrl.equals(other.iconUrl))
      return false;
    if (lastUpdate == null) {
      if (other.lastUpdate != null)
        return false;
    } else if (!lastUpdate.equals(other.lastUpdate))
      return false;
    if (latestStats == null) {
      if (other.latestStats != null)
        return false;
    } else if (!latestStats.equals(other.latestStats))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (packageName == null) {
      if (other.packageName != null)
        return false;
    } else if (!packageName.equals(other.packageName))
      return false;
    if (admobAccount == null) {
      if (other.admobAccount != null) {
        return false;
      }
    } else if (!admobAccount.equals(other.admobAccount)) {
      return false;
    }
    if (admobSiteId == null) {
      if (other.admobSiteId != null) {
        return false;
      }
    } else if (!admobSiteId.equals(other.admobSiteId)) {
      return false;
    }
    if (lastCommentsUpdate == null) {
      if (other.lastCommentsUpdate != null) {
        return false;
      }
    } else if (!lastCommentsUpdate.equals(other.lastCommentsUpdate)) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return String.format("AppInfo [account=%s, developerId=%s, packageName=%s]",
                         account, developerId, packageName);
  }

  public boolean isIncomplete() {
    return name == null || versionName == null || iconUrl == null;
  }
}
