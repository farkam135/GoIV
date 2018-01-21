package com.kamron.pogoiv.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardResultMode;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;

import java.util.EnumSet;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.PERFECT_IV_RESULT;
import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.GENERAL_RESULT;
import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.SINGLE_RESULT;

public class ClipboardModifierActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.pagerTabStrip)
    PagerTabStrip pagerTabStrip;
    @BindView(R.id.clipboardDescription)
    TextView clipboardDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard_modifier);
        ButterKnife.bind(this);

        ClipboardResultMode[] resultModesEnabled = getResultModesEnabled();
        ModePagerAdapter pagerAdapter = new ModePagerAdapter(getSupportFragmentManager(), resultModesEnabled);
        viewPager.setAdapter(pagerAdapter);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.clipboard_activity_title);
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (resultModesEnabled.length > 1) {
                actionBar.setElevation(0);
                pagerTabStrip.setTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else {
                pagerTabStrip.setVisibility(View.GONE);
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
                onBackPressed();
                return true;
            case R.id.save:
                for (Fragment f : getSupportFragmentManager().getFragments()) {
                    if (f instanceof ClipboardModifierFragment) {
                        ((ClipboardModifierFragment) f).saveConfiguration();
                    }
                }
                Toast.makeText(this, R.string.configuration_saved, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onBackPressed() {
        // Check for unsaved changes
        boolean unsavedChanges = false;
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f instanceof ClipboardModifierFragment) {
                unsavedChanges |= ((ClipboardModifierFragment) f).hasUnsavedChanges();
            }
        }
        if (unsavedChanges) {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.discard_unsaved_changes)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                            ClipboardModifierActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private ClipboardResultMode[] getResultModesEnabled() {
        GoIVSettings settings = GoIVSettings.getInstance(this);
        EnumSet<ClipboardResultMode> clipboardResultModes = EnumSet.of(GENERAL_RESULT);

        if (settings.shouldCopyToClipboardSingle()) {
            clipboardResultModes.add(SINGLE_RESULT);
        }
        if (settings.shouldCopyToClipboardPerfectIV()) {
            clipboardResultModes.add(PERFECT_IV_RESULT);
        }

        return clipboardResultModes.toArray(new ClipboardResultMode[clipboardResultModes.size()]);
    }

    /**
     * Sets the description for the selected token.
     */
    public void updateTokenDescription(ClipboardToken selectedToken) {
        if (selectedToken == null) {
            clipboardDescription.setText(R.string.no_token_selected);
        } else if (selectedToken.maxEv) {
            clipboardDescription.setText(getResources().getString(R.string.token_max_evolution,
                    selectedToken.getLongDescription(this)));
        } else { //selectedtoken not max ev
            clipboardDescription.setText(selectedToken.getLongDescription(this));
        }
    }

    private static class ModePagerAdapter extends FragmentPagerAdapter {

        final ClipboardResultMode[] resultModesEnabled;

        ModePagerAdapter(FragmentManager fm, ClipboardResultMode[] resultModesEnabled) {
            super(fm);
            this.resultModesEnabled = resultModesEnabled;
        }

        @Override
        public int getCount() {
            return resultModesEnabled.length;
        }

        @Override
        public Fragment getItem(int position) {
            return ClipboardModifierFragment.newInstance(resultModesEnabled[position]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (resultModesEnabled[position]) {
                case GENERAL_RESULT:
                    return "Multiple results";
                case SINGLE_RESULT:
                    return "Single result";
                case PERFECT_IV_RESULT:
                    return "Perfect IV result";
                default:
                    return null;
            }
        }
    }

}
