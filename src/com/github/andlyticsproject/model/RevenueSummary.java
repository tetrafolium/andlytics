package com.github.andlyticsproject.model;

import java.util.Date;

public class RevenueSummary {

private Long id;

private Revenue.Type type;
private String currency;
private Date date;

private Revenue lastDay;
private Revenue last7Days;
private Revenue last30Days;
private Revenue overall;

public static RevenueSummary
createTotal(final String currency, final Date date, final double lastDay,
            final double last7Days, final double last30Days,
            final double overall) {
	return new RevenueSummary(Revenue.Type.TOTAL, currency, date, lastDay,
	                          last7Days, last30Days, overall);
}

public static RevenueSummary
createSales(final String currency, final Date date, final double lastDay,
            final double last7Days, final double last30Days,
            final double overall) {
	return new RevenueSummary(Revenue.Type.APP_SALES, currency, date, lastDay,
	                          last7Days, last30Days, overall);
}

public static RevenueSummary
createInApp(final String currency, final Date date, final double lastDay,
            final double last7Days, final double last30Days,
            final double overall) {
	return new RevenueSummary(Revenue.Type.IN_APP, currency, date, lastDay,
	                          last7Days, last30Days, overall);
}

public RevenueSummary(final Revenue.Type type, final String currency,
                      final Date date, final double lastDay,
                      final double last7Days, final double last30Days,
                      final double overall) {
	this.type = type;
	this.date = (Date)date.clone();
	this.currency = currency;
	this.lastDay = new Revenue(type, lastDay, currency);
	this.last7Days = new Revenue(type, last7Days, currency);
	this.last30Days = new Revenue(type, last30Days, currency);
	this.overall = new Revenue(type, overall, currency);
}

public Long getId() {
	return id;
}

public void setId(final Long id) {
	this.id = id;
}

public Revenue.Type getType() {
	return type;
}

public String getCurrency() {
	return currency;
}

public Date getDate() {
	return (Date)date.clone();
}

public Revenue getLastDay() {
	return lastDay;
}

public Revenue getLast7Days() {
	return last7Days;
}

public Revenue getLast30Days() {
	return last30Days;
}

public Revenue getOverall() {
	return overall;
}

public boolean hasRevenue() {
	return overall.getAmount() > 0 || last30Days.getAmount() > 0;
}
}
