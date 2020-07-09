package com.github.andlyticsproject.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.github.andlyticsproject.R;

public class AboutActivity
    extends AppCompatActivity implements ActionBar.TabListener {
  private static final String BUNDLE_KEY_TABINDEX = "tabindex";

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.about_navigation);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    Tab tab1 = getSupportActionBar().newTab();
    tab1.setText(R.string.about_credits);
    tab1.setTabListener(this);

    Tab tab2 = getSupportActionBar().newTab();
    tab2.setText(R.string.changelog_title);
    tab2.setTabListener(this);

    getSupportActionBar().addTab(tab1);
    getSupportActionBar().addTab(tab2);
  }

  @Override
  public void onSaveInstanceState(final Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt(
        BUNDLE_KEY_TABINDEX,
        getSupportActionBar().getSelectedTab().getPosition());
  }

  @Override
  public void onRestoreInstanceState(final Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    getSupportActionBar().setSelectedNavigationItem(
        savedInstanceState.getInt(BUNDLE_KEY_TABINDEX));
  }

  @Override
  public void onTabReselected(final Tab tab,
                              final FragmentTransaction transaction) {
    Log.i("Tab Reselected", tab.getText().toString());
  }

  @Override
  public void onTabSelected(final Tab tab,
                            final FragmentTransaction transaction) {
    if (0 == tab.getPosition()) {
      AboutFragment fragment = new AboutFragment();
      transaction.replace(android.R.id.content, fragment);
    } else {
      ChangelogFragment fragment = new ChangelogFragment();
      transaction.replace(android.R.id.content, fragment);
    }
  }

  @Override
  public void onTabUnselected(final Tab tab,
                              final FragmentTransaction transaction) {
    Log.i("Tab Unselected", tab.getText().toString());
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      finish();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  public void onOpenGoogleplusClick(final View view) {
    openBrowser(this.getString(R.string.googleplus_url));
  }

  public void onOpenGithubClick(final View view) {
    openBrowser(this.getString(R.string.github_url));
  }

  public void onOpenFeedbackClick(final View view) {
    openBrowser(this.getString(R.string.github_issues_url));
  }

  public void onOpenFacebookClick(final View view) {
    openBrowser(this.getString(R.string.facebook_url));
  }

  public void onOpenTwitterClick(final View view) {
    openBrowser(this.getString(R.string.twitter_url));
  }

  private void openBrowser(final String url) {
    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
  }
}
