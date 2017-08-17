package com.kamron.pogoiv.clipboard;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.adapters.TokensPreviewAdapter;
import com.kamron.pogoiv.clipboard.adapters.TokensShowcaseAdapter;
import com.kamron.pogoiv.clipboard.layoutmanagers.TokenGridLayoutManager;
import com.kamron.pogoiv.clipboard.tokens.CustomSeparatorToken;
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;


public class ClipboardModifierFragment
        extends Fragment
        implements ClipboardToken.OnTokenSelectedListener, ClipboardToken.OnTokenDeleteListener {

    private static final String ARG_SINGLE_RESULT_MODE = "a_srm";


    @BindView(R.id.clipboardMaxLength)
    TextView clipboardMaxLength;
    @BindView(R.id.tokenPreviewRecyclerView)
    RecyclerView tokenPreviewRecyclerView;
    @BindView(R.id.tokenShowcaseRecyclerView)
    RecyclerView tokenShowcaseRecyclerView;
    @BindView(R.id.btnAdd)
    FloatingActionButton btnAdd;
    @BindView(R.id.btnMaxEvolution)
    FloatingActionButton btnMaxEvolution;


    private boolean singleResultMode;
    private boolean maxEvolutionVariant = true;
    private TokensPreviewAdapter tokenPreviewAdapter;
    private TokensShowcaseAdapter tokenShowcaseAdapter;
    private ClipboardToken selectedToken = null;


    public static ClipboardModifierFragment newInstance(boolean singleResultMode) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_SINGLE_RESULT_MODE, singleResultMode);
        ClipboardModifierFragment f = new ClipboardModifierFragment();
        f.setArguments(args);
        return f;
    }

    public ClipboardModifierFragment() {
        super();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleResultMode = getArguments().getBoolean(ARG_SINGLE_RESULT_MODE, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clipboard_modifier, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Populate the token preview RecyclerView with all configured tokens.
        tokenPreviewAdapter = new TokensPreviewAdapter(getCurrentlyModifyingList(), this);
        tokenPreviewRecyclerView.setHasFixedSize(false);
        tokenPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
        tokenPreviewRecyclerView.setAdapter(tokenPreviewAdapter);

        // Populate the token showcase RecyclerView with all possible tokens. The TokenListAdapter will put them in
        // their respective category while TokenGridLayoutManager will arrange them in a grid with category headers
        // that span the entire RecyclerView width.
        tokenShowcaseAdapter = new TokensShowcaseAdapter(getContext(), maxEvolutionVariant, this);
        tokenShowcaseRecyclerView.setHasFixedSize(false);
        tokenShowcaseRecyclerView.setLayoutManager(new TokenGridLayoutManager(getContext(), tokenShowcaseAdapter));
        tokenShowcaseRecyclerView.setAdapter(tokenShowcaseAdapter);

        // Set the drawable here since app:srcCompat attribute in XML isn't working and android:src crashes on API 19
        btnAdd.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_white_24px));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                addToken();
            }
        });

        // Set the drawable here since app:srcCompat attribute in XML isn't working and android:src crashes on API 19
        btnMaxEvolution.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_star_white_18dp));
        btnMaxEvolution.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                maxEvolutionVariant = !maxEvolutionVariant;
                if (maxEvolutionVariant) {
                    btnMaxEvolution.setImageDrawable(
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_star_white_18dp));
                    Toast.makeText(getContext(), "Show tokens max evolution variant", Toast.LENGTH_SHORT).show();
                } else {
                    btnMaxEvolution.setImageDrawable(
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_star_border_white_18dp));
                    Toast.makeText(getContext(), "Show standard tokens", Toast.LENGTH_SHORT).show();
                }
                tokenShowcaseAdapter.setEvolvedVariant(maxEvolutionVariant);
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        updateFields();
    }

    /**
     * Select a token to show its description.
     *
     * @param token Which token to show.
     */
    @Override public void onTokenSelected(ClipboardToken token, int adapterPosition) {
        selectedToken = token;
        updateClipboardDescription();
        updateLengthIndicator();
    }

    @Override public void onTokenDeleted(int adapterPosition) {
        if (tokenPreviewAdapter.getItemCount() <= 1) {
            Toast.makeText(getContext(), "You can't delete the last token of this configuration!", Toast.LENGTH_LONG)
                    .show();
        } else {
            getClipboardTokenHandler().removeToken(adapterPosition, singleResultMode);
            updateFields();
        }
    }

    private @NonNull ClipboardTokenHandler getClipboardTokenHandler() {
        final Activity activity = getActivity();
        if (activity instanceof ClipboardModifierActivity) {
            return ((ClipboardModifierActivity) activity).getClipboardTokenHandler();
        }
        throw new IllegalStateException();
    }

    /**
     * Get a list of tokens, the default list if user is currently modifying the default list, or the single result
     * user token list if that checkbox is marked.
     *
     * @return The users token setting for either single or multiple results
     */
    private List<ClipboardToken> getCurrentlyModifyingList() {
        return getClipboardTokenHandler().getTokens(singleResultMode);
    }

    /**
     * Add a token to the user settings.
     */
    public void addToken() {
        if (selectedToken != null) {
            if (selectedToken instanceof CustomSeparatorToken) {
                buildCustomSeparatorToken();
            } else {
                getClipboardTokenHandler().addToken(selectedToken, singleResultMode);
                tokenPreviewAdapter.setData(getCurrentlyModifyingList());
                tokenPreviewRecyclerView.smoothScrollToPosition(tokenPreviewAdapter.getItemCount() - 1);
                updateLengthIndicator();
            }
        } else {
            Toast.makeText(getContext(), R.string.clipboard_no_token_selected, Toast.LENGTH_LONG).show();
        }
    }

    private void updateClipboardDescription() {
        final Activity activity = getActivity();
        if (activity instanceof ClipboardModifierActivity) {
            ((ClipboardModifierActivity) activity).updateTokenDescription(selectedToken);
        }
    }

    /**
     * Updates the preview length indicator.
     */
    @SuppressLint("DefaultLocale")
    private void updateLengthIndicator() {
        final Activity activity = getActivity();
        if (activity instanceof ClipboardModifierActivity) {
            clipboardMaxLength.setText(String.format("(%1$d/12 characters)",
                    getClipboardTokenHandler().getMaxLength(singleResultMode)));
        }
    }

    /**
     * Builds a Dialog to create a custom string token to be added to the user settings.
     */
    public void buildCustomSeparatorToken() {
        // The custom separator will be written in this EditText
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.edittext_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);

        // This dialog will implement the user interaction
        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setMessage("Please input your custom separator")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        String separator = editText.getText().toString().trim();
                        if (Strings.isNullOrEmpty(separator)) {
                            Toast.makeText(getContext(),
                                    "Please fill in your custom separator", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (separator.contains(".")) {
                            Toast.makeText(getContext(), "Custom separator can't contain ."
                                    + " because the developer is lazy", Toast.LENGTH_LONG).show();
                        }
                        selectedToken = new SeparatorToken(separator);
                        addToken();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    /**
     * Updates the description, the preview window, the highlighted single/multi text,  and the editor window.
     */
    private void updateFields() {
        // TODO updateClipboardDescription();
        updateLengthIndicator();
        tokenPreviewAdapter.setData(getCurrentlyModifyingList());
    }

}
