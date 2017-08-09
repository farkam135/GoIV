package com.kamron.pogoiv.clipboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.clipboard.adapters.viewholders.TokenViewHolder;

import java.util.ArrayList;
import java.util.List;


public class TokensPreviewAdapter extends RecyclerView.Adapter<TokenViewHolder> {

    private ArrayList<ClipboardToken> tokenList;
    private ClipboardToken.OnTokenDeleteListener onTokenDeleteListener;


    public TokensPreviewAdapter(List<ClipboardToken> tokenList,
                                ClipboardToken.OnTokenDeleteListener onTokenDeleteListener) {
        this.tokenList = new ArrayList<>();
        this.onTokenDeleteListener = onTokenDeleteListener;
        setHasStableIds(false);
        setData(tokenList);
    }

    public void setData(List<ClipboardToken> tokenList) {
        this.tokenList.clear();
        this.tokenList.addAll(tokenList);
        notifyDataSetChanged();
    }

    @Override public TokenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TokenViewHolder(parent, null, onTokenDeleteListener, false, true);
    }

    @Override public void onBindViewHolder(TokenViewHolder holder, int position) {
        holder.bind(tokenList.get(position));
    }

    @Override public int getItemCount() {
        return tokenList.size();
    }

}
