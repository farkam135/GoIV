package com.kamron.pogoiv.widgets.recyclerviews.adapters;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.ViewGroup;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.viewholders.TokenViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TokensPreviewAdapter
        extends RecyclerView.Adapter<TokenViewHolder>
        implements ClipboardToken.OnTokenDeleteListener {

    private ArrayList<ClipboardToken> tokenList;


    public TokensPreviewAdapter() {
        this.tokenList = new ArrayList<>();
        setHasStableIds(false);
        setData(tokenList);
    }

    public ArrayList<ClipboardToken> getData() {
        return new ArrayList<>(tokenList);
    }

    public void setData(List<ClipboardToken> tokenList) {
        this.tokenList.clear();
        this.tokenList.addAll(tokenList);
        notifyDataSetChanged();
    }

    public void addItem(ClipboardToken token) {
        tokenList.add(token);
        notifyItemInserted(tokenList.size() - 1);
    }

    private void deleteItem(int position) {
        tokenList.remove(position);
        notifyItemRemoved(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(tokenList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(tokenList, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override public TokenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TokenViewHolder(parent, null, this, false, true);
    }

    @Override public void onBindViewHolder(TokenViewHolder holder, int position) {
        holder.bind(tokenList.get(position));
    }

    @Override public int getItemCount() {
        return tokenList.size();
    }

    @Override public void onTokenDeleted(int adapterPosition) {
        deleteItem(adapterPosition);
    }

    /**
     * Get the maximum possible length of the string produced by the current token settings.
     *
     * @return An integer which represents the maximum possible size of the token inputs.
     */
    public int getMaxLength() {
        int sum = 0;
        for (ClipboardToken token : tokenList) {
            sum += token.getMaxLength();
        }
        return sum;
    }

    /**
     * This class handles the events recognised by the ItemTouchHelper instance attached to the RecyclerView.
     */
    public static class TokenTouchCallback extends ItemTouchHelper.SimpleCallback {

        private TokensPreviewAdapter adapter;

        public TokenTouchCallback(TokensPreviewAdapter adapter) {
            super(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            adapter.moveItem(fromPosition, toPosition); // Item dragged
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            adapter.deleteItem(viewHolder.getAdapterPosition()); // Item swiped
        }
    }

}
