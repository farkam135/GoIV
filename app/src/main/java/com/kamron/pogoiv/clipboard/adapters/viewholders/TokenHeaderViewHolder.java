package com.kamron.pogoiv.clipboard.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;


public class TokenHeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;

    public TokenHeaderViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_token_header, parent, false));

        textView = (TextView) itemView.findViewById(android.R.id.text1);
    }

    public void bind(ClipboardToken.Category category) {
        textView.setText(category.toString());
    }

}
