package com.kamron.pogoiv.clipboard.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.ClipboardToken;


public class TokenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ClipboardToken.OnTokenSelectedListener onTokenSelectedListener;
    private ImageView evolvedVariantImageView;
    private TextView textView;
    private ImageButton deleteButton;

    public TokenViewHolder(ViewGroup parent, ClipboardToken.OnTokenSelectedListener onTokenSelectedListener,
                           boolean deleteActionVisible, boolean widthMatchParent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_token_preview, parent, false));

        this.onTokenSelectedListener = onTokenSelectedListener;

        evolvedVariantImageView = (ImageView) itemView.findViewById(R.id.evolvedVariantImageView);
        textView = (TextView) itemView.findViewById(android.R.id.text1);
        deleteButton = (ImageButton) itemView.findViewById(R.id.btnDelete);

        if (!deleteActionVisible) {
            deleteButton.setVisibility(View.GONE);
            if (onTokenSelectedListener != null) {
                itemView.setOnClickListener(this);
            }
        }
        if (widthMatchParent) {
            itemView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    public void bind(ClipboardToken token) {
        itemView.setTag(token);
        itemView.setActivated(token.maxEv);
        evolvedVariantImageView.setVisibility(token.maxEv ? View.VISIBLE : View.GONE);
        textView.setText(token.getTokenName(itemView.getContext()));
    }

    @Override public void onClick(View v) {
        onTokenSelectedListener.onTokenSelected(((ClipboardToken) v.getTag()), getAdapterPosition());
    }
}
