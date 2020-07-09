package com.github.andlyticsproject;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.andlyticsproject.cache.AppIconInMemoryCache;
import com.github.andlyticsproject.db.AndlyticsDb;
import com.github.andlyticsproject.dialog.AddEditLinkDialog;
import com.github.andlyticsproject.dialog.LongTextDialog;
import com.github.andlyticsproject.model.AppInfo;
import com.github.andlyticsproject.model.Link;
import com.github.andlyticsproject.util.DetachableAsyncTask;
import com.github.andlyticsproject.util.Utils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class AppInfoActivity extends AppCompatActivity implements
    AddEditLinkDialog.OnFinishAddEditLinkDialogListener, OnItemLongClickListener {

    public static final String TAG = Main.class.getSimpleName();

    private LinksListAdapter linksListAdapter;

    private LoadLinksDb loadLinksDb;

    private AppInfo appInfo;
    private List<Link> links;

    private ListView list;
    private View linksListEmpty;

    private AndlyticsDb db;

    private String packageName;
    private String iconFilePath;

    private ActionMode currentActionMode;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appinfo);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            packageName = b.getString(BaseActivity.EXTRA_PACKAGE_NAME);
            iconFilePath = b.getString(BaseActivity.EXTRA_ICON_FILE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String appName = getDbAdapter().getAppName(packageName);
        if (appName != null) {
            getSupportActionBar().setSubtitle(appName);
        }

        LayoutInflater layoutInflater = getLayoutInflater();

        list = (ListView) findViewById(R.id.appinfo_links_list);

        list.addHeaderView(layoutInflater.inflate(R.layout.appinfo_header, null), null, false);

        list.addFooterView(layoutInflater.inflate(R.layout.appinfo_links_list_empty, null), null,
                           false);

        links = new ArrayList<Link>();

        linksListAdapter = new LinksListAdapter(this);
        list.setAdapter(linksListAdapter);
        list.setOnItemLongClickListener(this);

        linksListAdapter.setLinks(links);
        linksListAdapter.notifyDataSetChanged();

        View playStoreButton = findViewById(R.id.appinfo_playstore);

        playStoreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="
                                         + packageName));
                startActivity(intent);
            }
        });

        db = AndlyticsDb.getInstance(this);

        loadLinksDb = new LoadLinksDb(this);
        Utils.execute(loadLinksDb);

        registerForContextMenu(list);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (currentActionMode != null) {
            return false;
        }

        // skip header
        if (position == 0) {
            return false;
        }

        currentActionMode = startActionMode(new ContextCallback(position));
        list.setItemChecked(position, true);

        return true;
    }

    @SuppressLint("NewApi")
    class ContextCallback implements ActionMode.Callback {

        private int position;

        ContextCallback(final int position) {
            this.position = position;
        }

        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.links_context_menu, menu);
            return true;
        }

        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {

            if (menuItem.getItemId() == R.id.itemLinksmenuEdit) {
                int pos = position - 1; // Subtract one for the header
                Link link = links.get(pos);

                showAddEditLinkDialog(link);
                actionMode.finish();
                return true;
            } else if (menuItem.getItemId() == R.id.itemLinksmenuDelete) {
                int pos = position - 1; // Subtract one for the header
                Link link = links.get(pos);

                db.deleteLink(link.getId().longValue());

                loadLinksDb = new LoadLinksDb(AppInfoActivity.this);
                Utils.execute(loadLinksDb);
                actionMode.finish();
                return true;
            }

            return false;
        }

        public void onDestroyActionMode(final ActionMode actionMode) {
            list.setItemChecked(position, false);
            currentActionMode = null;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.links_menu, menu);

        return true;
    }

    /**
     * Called if item in option menu is selected.
     *
     * @param item
     * The chosen menu item
     * @return boolean true/false
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            overridePendingTransition(R.anim.activity_prev_in, R.anim.activity_prev_out);
            return true;
        case R.id.itemLinksmenuAdd:
            showAddEditLinkDialog(null);
            return true;
        default:
            return (super.onOptionsItemSelected(item));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_prev_in, R.anim.activity_prev_out);
    }

    public ContentAdapter getDbAdapter() {
        return getAndlyticsApplication().getDbAdapter();
    }

    public AndlyticsApp getAndlyticsApplication() {
        return (AndlyticsApp) getApplication();
    }

    private static class LoadLinksDb extends DetachableAsyncTask<Void, Void, Void, AppInfoActivity> {

        LoadLinksDb(final AppInfoActivity activity) {
            super(activity);
        }

        @Override
        protected Void doInBackground(final Void... params) {
            if (activity == null) {
                return null;
            }

            activity.getLinksFromDb();

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            if (activity == null) {
                return;
            }

            activity.refreshLinks();
        }

    }

    private void getLinksFromDb() {
        appInfo = db.findAppByPackageName(packageName);
        links = appInfo == null ? new ArrayList<Link>() : appInfo.getDetails().getLinks();
    }

    private void refreshLinks() {
        linksListAdapter.setLinks(links);
        linksListAdapter.notifyDataSetChanged();

        linksListEmpty = findViewById(R.id.appinfo_links_list_empty);

        if (links.size() == 0) {
            linksListEmpty.setVisibility(View.VISIBLE);
        } else {
            linksListEmpty.setVisibility(View.GONE);
        }

        ImageView iconView = (ImageView) findViewById(R.id.appinfo_app_icon);
        String packageName = appInfo.getPackageName();
        // XXX hack? should be cached on main screen, so don't bother trying to load
        //              if (inMemoryCache.contains(packageName)) {
        iconView.setImageBitmap(AppIconInMemoryCache.getInstance().get(packageName));
        iconView.clearAnimation();

        TextView appNameView = (TextView) findViewById(R.id.appinfo_app_name);
        appNameView.setText(appInfo.getName());

        TextView packageNameView = (TextView) findViewById(R.id.appinfo_package_name);
        packageNameView.setText(packageName);

        TextView developerView = (TextView) findViewById(R.id.appinfo_developer);
        developerView.setText(String.format("%s / %s", appInfo.getDeveloperName(),
                                            appInfo.getDeveloperId()));

        TextView versionNameView = (TextView) findViewById(R.id.appinfo_version_name);
        versionNameView.setText(appInfo.getVersionName());

        TextView lastStoreUpdateView = (TextView) findViewById(R.id.appinfo_last_store_update);
        lastStoreUpdateView.setText(DateFormat.getDateInstance().format(
                                        appInfo.getDetails().getLastStoreUpdate()));

        TextView descriptionView = (TextView) findViewById(R.id.appinfo_description);
        final String description = appInfo.getDetails().getDescription().replace("\n", "<br/>");
        descriptionView.setText(Html.fromHtml(description));
        descriptionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                showLongTextDialog(R.string.appinfo_description_label, description);
            }
        });

        TextView changelogView = (TextView) findViewById(R.id.appinfo_changelog);
        final String changelog = appInfo.getDetails().getChangelog().replace("\n", "<br/>");
        changelogView.setText(Html.fromHtml(changelog));
        changelogView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                showLongTextDialog(R.string.appinfo_changelog_label, changelog);
            }
        });
    }

    @Override
    public void onFinishAddEditLink(final String url, final String name, final Long id) {
        if (id == null) {
            db.addLink(appInfo.getDetails(), url, name);
        } else {
            db.editLink(id, url, name);
        }

        loadLinksDb = new LoadLinksDb(this);
        Utils.execute(loadLinksDb);
    }

    private void showAddEditLinkDialog(final Link link) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("fragment_addedit_link");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        AddEditLinkDialog addEditLinkDialog = new AddEditLinkDialog();

        Bundle arguments = new Bundle();
        if (link != null) {
            arguments.putLong("id", link.getId().longValue());
            arguments.putString("name", link.getName());
            arguments.putString("url", link.getURL());
        }

        addEditLinkDialog.setArguments(arguments);

        addEditLinkDialog.setOnFinishAddEditLinkDialogListener(this);

        addEditLinkDialog.show(ft, "fragment_addedit_link");
    }

    private void showLongTextDialog(final int title, final String longText) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("fragment_longtext");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        LongTextDialog longTextDialog = new LongTextDialog();

        Bundle arguments = new Bundle();
        arguments.putInt("title", title);
        arguments.putString("longText", longText);

        longTextDialog.setArguments(arguments);

        longTextDialog.show(ft, "fragment_longtext");
    }
}
