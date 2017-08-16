package com.kamron.pogoiv.clipboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClipboardModifierActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.pagerTabStrip)
    PagerTabStrip pagerTabStrip;
    @BindView(R.id.clipboardDescription)
    TextView clipboardDescription;

    private ClipboardTokenHandler cth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard_modifier);
        ButterKnife.bind(this);

        cth = new ClipboardTokenHandler(this);

        boolean singleModeEnabled = isSingleModeEnabled();
        ModePagerAdapter pagerAdapter = new ModePagerAdapter(getSupportFragmentManager(), singleModeEnabled);
        viewPager.setAdapter(pagerAdapter);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.clipboard_activity_title);
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (!singleModeEnabled) {
                pagerTabStrip.setVisibility(View.GONE);
            } else {
                actionBar.setElevation(0);
                pagerTabStrip.setTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clipboard_menu, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isSingleModeEnabled() {
        GoIVSettings settings = GoIVSettings.getInstance(this);
        return settings.shouldCopyToClipboardSingle();
    }

    public ClipboardTokenHandler getClipboardTokenHandler() {
        return cth;
    }

    @Override public void finish() {
        super.finish();
        Toast.makeText(this, "Configuration saved!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets the description for the selected token.
     */
    public void updateTokenDescription(ClipboardToken selectedToken) {
        if (selectedToken == null) {
            clipboardDescription.setText("No token selected...");
        } else if (selectedToken.maxEv) {
            clipboardDescription.setText(selectedToken.getLongDescription(this) + "\n\nThis token is a max evolution "
                    + "variant, meaning that it will return a result as if your monster was already fully evolved, "
                    + "which might be more interesting in a lot of cases.");
        } else { //selectedtoken not max ev
            clipboardDescription.setText(selectedToken.getLongDescription(this));
        }
    }

    private static class ModePagerAdapter extends FragmentPagerAdapter {

        final boolean singleResultModeEnabled;

        public ModePagerAdapter(FragmentManager fm, boolean singleResultModeEnabled) {
            super(fm);
            this.singleResultModeEnabled = singleResultModeEnabled;
        }

        @Override
        public int getCount() {
            return singleResultModeEnabled ? 2 : 1;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ClipboardModifierFragment.newInstance(false);
                case 1:
                    return ClipboardModifierFragment.newInstance(true);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Multiple result";
                case 1:
                    return "Single result";
                default:
                    return null;
            }
        }
    }

}
