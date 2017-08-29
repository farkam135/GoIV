package com.kamron.pogoiv.clipboard;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.kamron.pogoiv.clipboard.adapters.decorators.MarginItemDecorator;
import com.kamron.pogoiv.clipboard.layoutmanagers.TokenGridLayoutManager;
import com.kamron.pogoiv.clipboard.tokens.CustomSeparatorToken;
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;


public class ClipboardModifierFragment
        extends Fragment
        implements ClipboardToken.OnTokenSelectedListener {

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
    private ClipboardTokenHandler cth;


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
        cth = new ClipboardTokenHandler(getContext());
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
        tokenPreviewAdapter = new TokensPreviewAdapter();
        tokenPreviewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            private void updateMaxLength() {
                clipboardMaxLength.setText(String.format(Locale.getDefault(),
                        getString(R.string.token_max_characters), tokenPreviewAdapter.getMaxLength()));
            }

            @Override public void onChanged() {
                updateMaxLength();
            }

            @Override public void onItemRangeInserted(int positionStart, int itemCount) {
                updateMaxLength();
            }

            @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateMaxLength();
            }
        });
        tokenPreviewAdapter.setData(cth.getTokens(singleResultMode));
        tokenPreviewRecyclerView.setHasFixedSize(false);
        tokenPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
        tokenPreviewRecyclerView.addItemDecoration(new MarginItemDecorator(2, 0, 2, 0));
        tokenPreviewRecyclerView.setAdapter(tokenPreviewAdapter);
        new ItemTouchHelper(new TokensPreviewAdapter.TokenTouchCallback(tokenPreviewAdapter))
                .attachToRecyclerView(tokenPreviewRecyclerView);

        // Populate the token showcase RecyclerView with all possible tokens. The TokenListAdapter will put them in
        // their respective category while TokenGridLayoutManager will arrange them in a grid with category headers
        // that span the entire RecyclerView width.
        tokenShowcaseAdapter = new TokensShowcaseAdapter(getContext(), maxEvolutionVariant, this);
        tokenShowcaseRecyclerView.setHasFixedSize(false);
        tokenShowcaseRecyclerView.setLayoutManager(new TokenGridLayoutManager(getContext(), tokenShowcaseAdapter));
        tokenShowcaseRecyclerView.addItemDecoration(new MarginItemDecorator(2, 4, 2, 4));
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
                    Toast.makeText(getContext(), R.string.token_show_max_evo_variant, Toast.LENGTH_SHORT).show();
                } else {
                    btnMaxEvolution.setImageDrawable(
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_star_border_white_18dp));
                    Toast.makeText(getContext(), R.string.token_show_standard, Toast.LENGTH_SHORT).show();
                }
                tokenShowcaseAdapter.setEvolvedVariant(maxEvolutionVariant);
            }
        });
    }

    public void saveConfiguration() {
        cth.setTokenList(tokenPreviewAdapter.getData(), singleResultMode);
    }

    /**
     * Check if the user has edited the list of tokens without saving.
     *
     * @return true if there are unsaved changes
     */
    public boolean hasUnsavedChanges() {
        return !cth.savedConfigurationEquals(tokenPreviewAdapter.getData(), singleResultMode);
    }

    /**
     * Select a token to show its description.
     *
     * @param token Which token to show.
     */
    @Override public void onTokenSelected(ClipboardToken token, int adapterPosition) {
        selectedToken = token;

        final Activity activity = getActivity();
        if (activity instanceof ClipboardModifierActivity) {
            ((ClipboardModifierActivity) activity).updateTokenDescription(selectedToken);
        }
    }

    /**
     * Add a token to the user settings.
     */
    public void addToken() {
        if (selectedToken != null) {
            if (selectedToken instanceof CustomSeparatorToken) {
                buildCustomSeparatorToken();
            } else {
                tokenPreviewAdapter.addItem(selectedToken);
                tokenPreviewRecyclerView.smoothScrollToPosition(tokenPreviewAdapter.getItemCount() - 1);
            }
        } else {
            Toast.makeText(getContext(), R.string.clipboard_no_token_selected, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Builds a Dialog to create a custom string token to be added to the user settings.
     */
    public void buildCustomSeparatorToken() {
        // The custom separator will be written in this EditText
        @SuppressLint("InflateParams")
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.edittext_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);

        // This dialog will implement the user interaction
        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setMessage(R.string.token_input_custom_separator)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        String separator = editText.getText().toString().trim();
                        if (Strings.isNullOrEmpty(separator)) {
                            Toast.makeText(getContext(),
                                    R.string.token_fill_custom_separator, Toast.LENGTH_LONG).show();
                        } else if (separator.contains(".")) {
                            Toast.makeText(getContext(),
                                    R.string.token_not_dot_separator, Toast.LENGTH_LONG).show();
                        } else {
                            selectedToken = new SeparatorToken(separator);
                            addToken();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

}
