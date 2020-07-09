
package com.github.andlyticsproject;

public class AppStatsDiff {

  private String appName;

  private String packageName;

  private String iconName;

  int commentsChange;

  int downloadsChange;

  int activeInstallsChange;

  float avgRatingChange;

  int rating1Change;

  int rating2Change;

  int rating3Change;

  int rating4Change;

  int rating5Change;

  private boolean skipNotification;

  private String versionName;

  public boolean hasChanges() {

    if (commentsChange == 0 && downloadsChange == 0 &&
        activeInstallsChange == 0 && avgRatingChange == 0.0f) {
      return false;
    } else {
      return true;
    }
  }

  public int getCommentsChange() { return commentsChange; }

  public void setCommentsChange(final int commentsChange) {
    this.commentsChange = commentsChange;
  }

  public int getDownloadsChange() { return downloadsChange; }

  public void setDownloadsChange(final int downloadsChange) {
    this.downloadsChange = downloadsChange;
  }

  public int getActiveInstallsChange() { return activeInstallsChange; }

  public void setActiveInstallsChange(final int activeInstallsChange) {
    this.activeInstallsChange = activeInstallsChange;
  }

  public float getAvgRatingChange() { return avgRatingChange; }

  public void setAvgRatingChange(final float avgRatingChange) {
    this.avgRatingChange = avgRatingChange;
  }

  public int getRating1Change() { return rating1Change; }

  public void setRating1Change(final int rating1Change) {
    this.rating1Change = rating1Change;
  }

  public int getRating2Change() { return rating2Change; }

  public void setRating2Change(final int rating2Change) {
    this.rating2Change = rating2Change;
  }

  public int getRating3Change() { return rating3Change; }

  public void setRating3Change(final int rating3Change) {
    this.rating3Change = rating3Change;
  }

  public int getRating4Change() { return rating4Change; }

  public void setRating4Change(final int rating4Change) {
    this.rating4Change = rating4Change;
  }

  public int getRating5Change() { return rating5Change; }

  public void setRating5Change(final int rating5Change) {
    this.rating5Change = rating5Change;
  }

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public String getPackageName() { return packageName; }

  public void setAppName(final String appName) { this.appName = appName; }

  public String getAppName() { return appName; }

  public void setIconName(final String iconName) { this.iconName = iconName; }

  public String getIconName() { return iconName; }

  public void setSkipNotification(final boolean skipNotification) {
    this.skipNotification = skipNotification;
  }

  public boolean isSkipNotification() { return skipNotification; }

  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }

  public String getVersionName() { return versionName; }
}
