package com.kamron.pogoiv.activities;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardResultMode;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.clipboardlogic.ClipboardTokenHandler;
import com.kamron.pogoiv.clipboardlogic.tokens.CustomAppraiseSign;
import com.kamron.pogoiv.clipboardlogic.tokens.CustomNameToken;
import com.kamron.pogoiv.clipboardlogic.tokens.CustomSeparatorToken;
import com.kamron.pogoiv.clipboardlogic.tokens.HasBeenAppraisedToken;
import com.kamron.pogoiv.clipboardlogic.tokens.PokemonNameToken;
import com.kamron.pogoiv.clipboardlogic.tokens.SeparatorToken;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.TokensPreviewAdapter;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.TokensShowcaseAdapter;
import com.kamron.pogoiv.widgets.recyclerviews.decorators.MarginItemDecorator;
import com.kamron.pogoiv.widgets.recyclerviews.layoutmanagers.TokenGridLayoutManager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;


public class ClipboardModifierChildFragment
        extends Fragment
        implements ClipboardToken.OnTokenSelectedListener {

    private static final String ARG_RESULT_MODE = "a_srm";


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


    private ClipboardResultMode resultMode;
    private boolean maxEvolutionVariant = true;
    private TokensPreviewAdapter tokenPreviewAdapter;
    private TokensShowcaseAdapter tokenShowcaseAdapter;
    private ClipboardToken selectedToken = null;
    private ClipboardTokenHandler cth;


    public static ClipboardModifierChildFragment newInstance(ClipboardResultMode resultMode) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESULT_MODE, resultMode);
        ClipboardModifierChildFragment f = new ClipboardModifierChildFragment();
        f.setArguments(args);
        return f;
    }

    public ClipboardModifierChildFragment() {
        super();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultMode = (ClipboardResultMode) getArguments().get(ARG_RESULT_MODE);
        cth = new ClipboardTokenHandler(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clipboard_modifier_child, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        tokenPreviewAdapter.setData(cth.getTokens(resultMode));
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
        cth.setTokenList(tokenPreviewAdapter.getData(), resultMode);
    }

    /**
     * Check if the user has edited the list of tokens without saving.
     *
     * @return true if there are unsaved changes
     */
    public boolean hasUnsavedChanges() {
        return !cth.savedConfigurationEquals(tokenPreviewAdapter.getData(), resultMode);
    }

    /**
     * Select a token to show its description.
     *
     * @param token Which token to show.
     */
    @Override public void onTokenSelected(ClipboardToken token, int adapterPosition) {
        selectedToken = token;

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        for (Fragment f : activity.getSupportFragmentManager().getFragments()) {
            if (f instanceof ClipboardModifierParentFragment) {
                ((ClipboardModifierParentFragment) f).updateTokenDescription(selectedToken);
            }
        }
    }

    /**
     * Add a token to the user settings.
     */
    public void addToken() {
        if (selectedToken != null) {
            if (selectedToken instanceof CustomSeparatorToken) {
                buildCustomSeparatorToken();
            }else if (selectedToken instanceof CustomAppraiseSign) {
                buildCustomAppraiseToken();
            }else if (selectedToken instanceof CustomNameToken) {
                buildCustomNameToken();
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
                        String separator = editText.getText().toString();
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

    /**
     * Builds a Dialog to create a custom appraisal token to be added to the user settings.
     */
    public void buildCustomAppraiseToken() {
        // The custom separator will be written in this EditText
        @SuppressLint("InflateParams")
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.edittext_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);

        // This dialog will implement the user interaction
        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setMessage("Please input two symbols, the first representing appraised, and the second unappraised.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        String text = editText.getText().toString();
                        if (Strings.isNullOrEmpty(text)) {
                            Toast.makeText(getContext(),
                                   "Please input two symbols, the first representing appraised, and the second unappraised.", Toast.LENGTH_LONG).show();
                        } else if (text.contains(".")) {
                            Toast.makeText(getContext(),
                                    R.string.token_not_dot_separator, Toast.LENGTH_LONG).show();

                        } else if (text.length() <2) {
                            Toast.makeText(getContext(),
                                    "Please input two symbols", Toast.LENGTH_LONG).show();

                        } else {
                            selectedToken = new HasBeenAppraisedToken(maxEvolutionVariant, text.substring(0,1),text
                                    .substring(1,2));
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


    /**
     * Builds a Dialog to create a custom name token to be added to the user settings.
     */
    public void buildCustomNameToken() {
        // The custom separator will be written in this EditText
        @SuppressLint("InflateParams")
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.edittext_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);

        // This dialog will implement the user interaction
        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setMessage("Please input max allowed length of pokemon name. ")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        String text = editText.getText().toString();
                        if (Strings.isNullOrEmpty(text)) {
                            Toast.makeText(getContext(),
                                    "Please input max allowed length of pokemon name.", Toast.LENGTH_LONG).show();
                        } else if (text.contains(".")) {
                            Toast.makeText(getContext(),
                                    R.string.token_not_dot_separator, Toast.LENGTH_LONG).show();

                        }  else {
                            int input = 0;
                            try {
                                input = Integer.parseInt(text);
                                if (input < 0) { input = 0;}
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(),
                                        "Please put a normal number", Toast.LENGTH_LONG).show();
                            }
                            selectedToken = new PokemonNameToken(maxEvolutionVariant, input);
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
