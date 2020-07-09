package com.github.andlyticsproject.model;

import java.text.DecimalFormat;

public class AdmobStats extends Statistic {

  private static final int XK_cent = 0x00a2; /* U+00A2 CENT SIGN */
  private static final DecimalFormat centsFormatter =
      new DecimalFormat("0.00" + ((char)XK_cent));

  private String siteId;
  private Integer requests = 0;
  private Integer houseadRequests = 0;
  private Integer interstitialRequests = 0;
  private Integer impressions = 0;
  private Float fillRate = .0f;
  private Float houseadFillRate = .0f;
  private Float overallFillRate = .0f;
  private Integer clicks = 0;
  private Integer houseAdClicks = 0;
  private Float ctr = .0f;
  private Float ecpm = .0f;
  private Float revenue = .0f;
  private Float cpcRevenue = .0f;
  private Float cpmRevenue = .0f;
  private Integer exchangeDownloads = 0;
  private String currencyCode;

  public String getSiteId() { return siteId; }

  public void setSiteId(final String siteId) { this.siteId = siteId; }

  public Integer getRequests() { return requests; }

  public void setRequests(final Integer requests) { this.requests = requests; }

  public Integer getHouseadRequests() { return houseadRequests; }

  public void setHouseadRequests(final Integer houseadRequests) {
    this.houseadRequests = houseadRequests;
  }

  public Integer getInterstitialRequests() { return interstitialRequests; }

  public void setInterstitialRequests(final Integer interstitialRequests) {
    this.interstitialRequests = interstitialRequests;
  }

  public Integer getImpressions() { return impressions; }

  public void setImpressions(final Integer impressions) {
    this.impressions = impressions;
  }

  public Float getFillRate() { return fillRate; }

  public void setFillRate(final Float fillRate) { this.fillRate = fillRate; }

  public Float getHouseadFillRate() { return houseadFillRate; }

  public void setHouseadFillRate(final Float houseadFillRate) {
    this.houseadFillRate = houseadFillRate;
  }

  public Float getOverallFillRate() { return overallFillRate; }

  public void setOverallFillRate(final Float overallFillRate) {
    this.overallFillRate = overallFillRate;
  }

  public Integer getClicks() { return clicks; }

  public void setClicks(final Integer clicks) { this.clicks = clicks; }

  public Integer getHouseAdClicks() { return houseAdClicks; }

  public void setHouseAdClicks(final Integer houseAdClicks) {
    this.houseAdClicks = houseAdClicks;
  }

  public Float getCtr() { return ctr; }

  public void setCtr(final Float ctr) { this.ctr = ctr; }

  public Float getEcpm() { return ecpm; }

  public String getEpcCents() { return centsFormatter.format(getEpc()); }

  public Float getEpc() { return clicks > 0 ? (revenue * 100.f / clicks) : 0; }

  public void setEcpm(final Float ecpm) { this.ecpm = ecpm; }

  public Float getRevenue() { return revenue; }

  public void setRevenue(final Float revenue) { this.revenue = revenue; }

  public Float getCpcRevenue() { return cpcRevenue; }

  public void setCpcRevenue(final Float cpcRevenue) {
    this.cpcRevenue = cpcRevenue;
  }

  public Float getCpmRevenue() { return cpmRevenue; }

  public void setCpmRevenue(final Float cpmRevenue) {
    this.cpmRevenue = cpmRevenue;
  }

  public void setExchangeDownloads(final Integer exchangeDownloads) {
    this.exchangeDownloads = exchangeDownloads;
  }

  public Integer getExchangeDownloads() { return exchangeDownloads; }

  public String getCurrencyCode() { return currencyCode; }

  public void setCurrencyCode(final String currencyCode) {
    this.currencyCode = currencyCode;
  }
}
