package com.kamron.pogoiv.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardResultMode;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;

import java.util.EnumSet;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.GENERAL_RESULT;
import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.PERFECT_IV_RESULT;
import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.SINGLE_RESULT;

public class ClipboardModifierParentFragment extends Fragment {

    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.pagerTabStrip)
    PagerTabStrip pagerTabStrip;
    @BindView(R.id.clipboardDescription)
    public TextView clipboardDescription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clipboard_modifier_parent, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ClipboardResultMode[] resultModesEnabled = getResultModesEnabled();
        ModePagerAdapter pagerAdapter = new ModePagerAdapter(getChildFragmentManager(), resultModesEnabled);
        viewPager.setAdapter(pagerAdapter);

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setTitle(R.string.clipboard_activity_title);

            if (resultModesEnabled.length > 1) {
                actionBar.setElevation(0);
                pagerTabStrip.setTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else {
                pagerTabStrip.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.clipboard_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                AppCompatActivity activity = ((AppCompatActivity) getActivity());
                for (Fragment f : getChildFragmentManager().getFragments()) {
                    if (f instanceof ClipboardModifierChildFragment) {
                        ((ClipboardModifierChildFragment) f).saveConfiguration();
                    }
                }
                if (GoIVSettings.getInstance(activity).shouldCopyToClipboard()) {
                    Toast.makeText(activity, R.string.configuration_saved, Toast.LENGTH_LONG).show();
                } else {
                    // User saved clipboard configuration but the feature is disabled
                    String text = getString(R.string.configuration_saved_feature_disabled,
                            getString(R.string.copy_to_clipboard_setting_title));
                    Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets the description for the selected token.
     */
    public void updateTokenDescription(ClipboardToken selectedToken) {


        if (selectedToken == null) {
            clipboardDescription.setText(R.string.no_token_selected);
        } else if (selectedToken.maxEv) {
            clipboardDescription.setText(getResources().getString(R.string.token_max_evolution,
                    selectedToken.getLongDescription(getContext())));
        } else { // Selected token not max evolution
            clipboardDescription.setText(selectedToken.getLongDescription(getContext()));
        }
    }

    private ClipboardResultMode[] getResultModesEnabled() {
        GoIVSettings settings = GoIVSettings.getInstance(getContext());
        EnumSet<ClipboardResultMode> clipboardResultModes = EnumSet.of(GENERAL_RESULT);

        if (settings.shouldCopyToClipboardSingle()) {
            clipboardResultModes.add(SINGLE_RESULT);
        }
        if (settings.shouldCopyToClipboardPerfectIV()) {
            clipboardResultModes.add(PERFECT_IV_RESULT);
        }

        return clipboardResultModes.toArray(new ClipboardResultMode[clipboardResultModes.size()]);
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
            return ClipboardModifierChildFragment.newInstance(resultModesEnabled[position]);
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
