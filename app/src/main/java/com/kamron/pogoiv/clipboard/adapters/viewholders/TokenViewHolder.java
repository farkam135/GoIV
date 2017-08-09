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
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;


public class TokenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ClipboardToken.OnTokenSelectedListener onTokenSelectedListener;
    private ClipboardToken.OnTokenDeleteListener onTokenDeleteListener;
    private ImageView evolvedVariantImageView;
    private TextView textView1;
    private TextView textView2;
    private ImageButton deleteButton;
    private boolean showPreview;

    public TokenViewHolder(ViewGroup parent,
                           ClipboardToken.OnTokenSelectedListener onTokenSelectedListener,
                           ClipboardToken.OnTokenDeleteListener onTokenDeleteListener,
                           boolean widthMatchParent,
                           boolean showPreview) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_token_preview, parent, false));

        this.onTokenSelectedListener = onTokenSelectedListener;
        this.onTokenDeleteListener = onTokenDeleteListener;
        this.showPreview = showPreview;

        evolvedVariantImageView = (ImageView) itemView.findViewById(R.id.evolvedVariantImageView);
        textView1 = (TextView) itemView.findViewById(android.R.id.text1);
        textView2 = (TextView) itemView.findViewById(android.R.id.text2);
        deleteButton = (ImageButton) itemView.findViewById(R.id.btnDelete);

        if (!showPreview) {
            textView2.setVisibility(View.GONE);
        }
        if (onTokenDeleteListener != null) {
            deleteButton.setOnClickListener(this);
        } else {
            deleteButton.setVisibility(View.GONE);
        }
        if (onTokenSelectedListener != null) {
            itemView.setOnClickListener(this);
        }
        if (widthMatchParent) {
            itemView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    public void bind(ClipboardToken token) {
        itemView.setTag(token);
        itemView.setActivated(token.maxEv);
        evolvedVariantImageView.setVisibility(token.maxEv ? View.VISIBLE : View.GONE);
        if (showPreview && token instanceof SeparatorToken) {
            textView1.setText(null);
        } else {
            textView1.setText(token.getTokenName(itemView.getContext()));
        }
        if (showPreview) {
            textView2.setText(token.getPreview());
        }
    }

    @Override public void onClick(View v) {
        if (v.getId() == R.id.btnDelete) {
            if (onTokenDeleteListener != null) {
                onTokenDeleteListener.onTokenDeleted(getAdapterPosition());
            }
        } else if (onTokenSelectedListener != null) {
            onTokenSelectedListener.onTokenSelected(((ClipboardToken) v.getTag()), getAdapterPosition());
        }
    }
}
