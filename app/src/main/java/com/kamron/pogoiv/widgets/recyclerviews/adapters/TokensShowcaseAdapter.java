package com.kamron.pogoiv.widgets.recyclerviews.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.clipboardlogic.ClipboardTokenCollection;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.viewholders.TokenHeaderViewHolder;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.viewholders.TokenViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class TokensShowcaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ClipboardToken.OnTokenSelectedListener {

    public static final int VIEW_TYPE_HEADER = 1;
    public static final int VIEW_TYPE_TOKEN = 2;

    private Context context;
    private boolean evolvedVariant;
    private ClipboardToken.OnTokenSelectedListener onTokenSelectedListener;
    private LinkedHashMap<ClipboardToken.Category, ArrayList<ClipboardToken>> tokenListsByCategoryMap;
    private int itemCount;
    private int selectedPosition = RecyclerView.NO_POSITION;


    public TokensShowcaseAdapter(Context context, boolean evolvedVariant,
                                 ClipboardToken.OnTokenSelectedListener onTokenSelectedListener) {
        this.context = context;
        this.evolvedVariant = evolvedVariant;
        this.onTokenSelectedListener = onTokenSelectedListener;

        rebuildDataSet();
    }

    private void rebuildDataSet() {
        final ArrayList<ClipboardToken> possibleTokens = ClipboardTokenCollection.getSamples();

        // Separate tokens into their categories. The map associates every category to its list of tokens.
        itemCount = 0;
        tokenListsByCategoryMap = new LinkedHashMap<>();
        for (ClipboardToken token : possibleTokens) {
            if (!tokenListsByCategoryMap.containsKey(token.getCategory())) {
                // Init this category list of tokens
                tokenListsByCategoryMap.put(token.getCategory(), new ArrayList<ClipboardToken>());
            }

            if (token.maxEv == evolvedVariant || !token.changesOnEvolutionMax()) {
                // Add the current token in its category list
                tokenListsByCategoryMap.get(token.getCategory()).add(token);
                itemCount++;
            }
        }
        itemCount += tokenListsByCategoryMap.size();
    }

    public void setEvolvedVariant(boolean evolvedVariant) {
        if (this.evolvedVariant != evolvedVariant) {
            this.evolvedVariant = evolvedVariant;
            rebuildDataSet();
            notifyDataSetChanged();
        }
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new TokenHeaderViewHolder(parent);
            case VIEW_TYPE_TOKEN:
                return new TokenViewHolder(parent, this, null, true, false);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TokenHeaderViewHolder) {
            ((TokenHeaderViewHolder) holder).bind(getCategory(position));
        } else if (holder instanceof TokenViewHolder) {
            ((TokenViewHolder) holder).bind(getToken(position));
        } else {
            throw new IllegalArgumentException();
        }

        holder.itemView.setSelected(position == selectedPosition);
    }

    @Override public int getItemCount() {
        return itemCount;
    }

    private ClipboardToken.Category getCategory(int position) {
        int currentCategoryStart = 0;
        for (ClipboardToken.Category category : tokenListsByCategoryMap.keySet()) {
            if (position == currentCategoryStart) {
                return category;
            }
            if (position <= currentCategoryStart + tokenListsByCategoryMap.get(category).size()) {
                throw new IllegalArgumentException();
            }
            currentCategoryStart += tokenListsByCategoryMap.get(category).size() + 1;
        }
        throw new IllegalArgumentException();
    }

    private ClipboardToken getToken(int position) {
        int currentCategoryStart = 0;
        for (ClipboardToken.Category category : tokenListsByCategoryMap.keySet()) {
            if (position == currentCategoryStart) {
                throw new IllegalArgumentException();
            }
            if (position <= currentCategoryStart + tokenListsByCategoryMap.get(category).size()) {
                return tokenListsByCategoryMap.get(category).get(position - currentCategoryStart - 1);
            }
            currentCategoryStart += tokenListsByCategoryMap.get(category).size() + 1;
        }
        throw new IllegalArgumentException();
    }

    @Override public int getItemViewType(int position) {
        int currentCategoryStart = 0;
        for (ClipboardToken.Category category : tokenListsByCategoryMap.keySet()) {
            if (position == currentCategoryStart) {
                return VIEW_TYPE_HEADER;
            }
            if (position <= currentCategoryStart + tokenListsByCategoryMap.get(category).size()) {
                return VIEW_TYPE_TOKEN;
            }
            currentCategoryStart += tokenListsByCategoryMap.get(category).size() + 1;
        }
        throw new IllegalArgumentException();
    }

    @Override public long getItemId(int position) {
        int currentCategoryStart = 0;
        for (ClipboardToken.Category category : tokenListsByCategoryMap.keySet()) {
            if (position == currentCategoryStart) {
                // This is a header
                return category.toString().hashCode();
            }
            if (position <= currentCategoryStart + tokenListsByCategoryMap.get(category).size()) {
                // This is a token
                return tokenListsByCategoryMap.get(category).get(position - currentCategoryStart - 1)
                        .getTokenName(context).hashCode();
            }
            currentCategoryStart += tokenListsByCategoryMap.get(category).size() + 1;
        }
        throw new IllegalArgumentException();
    }

    @Override public void onTokenSelected(ClipboardToken token, int adapterPosition) {
        int prevItemSelected = selectedPosition;
        selectedPosition = adapterPosition;
        notifyItemChanged(selectedPosition);
        if (prevItemSelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(prevItemSelected);
        }
        if (onTokenSelectedListener != null) {
            onTokenSelectedListener.onTokenSelected(token, adapterPosition);
        }
    }

}
