package com.github.andlyticsproject.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// XXX this is a bit of a kludge...
public class Comment extends Statistic {

  private boolean isReply = false;

  // used for replies
  // looks like this: 'gp:AOqpTOGnebkY.....'
  private String uniqueId;

  private String title;

  // this is either the translated text, or the same as originalText,
  // depending on display language (current locale)
  private String text;

  private String originalTitle;

  // text in original language
  private String originalText;

  // language of the original comment
  private String language;

  private Date originalCommentDate;

  private int rating;

  private String user;

  private String appVersion;

  private String device;

  private Comment reply;

  public Comment() {}

  public Comment(final boolean isReply) { this.isReply = isReply; }

  public String getUniqueId() { return uniqueId; }

  public void setUniqueId(final String uniqueId) { this.uniqueId = uniqueId; }

  public String getTitle() { return title; }

  public void setTitle(final String title) { this.title = title; }

  public String getText() { return text; }

  public void setText(final String text) { this.text = text; }

  public String getOriginalTitle() { return originalTitle; }

  public void setOriginalTitle(final String originalTitle) {
    this.originalTitle = originalTitle;
  }

  public String getOriginalText() { return originalText; }

  public void setOriginalText(final String originalText) {
    this.originalText = originalText;
  }

  public String getLanguage() { return language; }

  public void setLanguage(final String language) { this.language = language; }

  /**
   * Date of the original comment that this reply refers to
   * Only valid for replies
   *
   * @return
   */
  public Date getOriginalCommentDate() { return originalCommentDate; }

  public void setOriginalCommentDate(final Date date) {
    this.originalCommentDate = date;
  }

  public int getRating() { return rating; }

  public void setRating(final int rating) { this.rating = rating; }

  public String getUser() { return user; }

  public void setUser(final String user) { this.user = user; }

  public void setAppVersion(final String appVersion) {
    this.appVersion = appVersion;
  }

  public String getAppVersion() { return appVersion; }

  public void setDevice(final String device) { this.device = device; }

  public String getDevice() { return device; }

  public Comment getReply() { return reply; }

  public void setReply(final Comment reply) { this.reply = reply; }

  public boolean isReply() { return isReply; }

  public static List<Comment> expandReplies(final List<Comment> result) {
    List<Comment> withReplies = new ArrayList<Comment>();
    for (Comment comment : result) {
      withReplies.add(comment);
      if (comment.getReply() != null) {
        withReplies.add(comment.getReply());
      }
    }

    return withReplies;
  }

  public boolean isTranslated() {
    return language != null &&
        !language.contains(Locale.getDefault().getLanguage());
  }
}
