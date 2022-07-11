package com.kamron.pogoiv.widgets.recyclerviews.adapters.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.clipboardlogic.tokens.SeparatorToken;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TokenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ClipboardToken.OnTokenSelectedListener onTokenSelectedListener;
    private ClipboardToken.OnTokenDeleteListener onTokenDeleteListener;
    @BindView(R.id.evolvedVariantImageView)
    ImageView evolvedVariantImageView;
    @BindView(android.R.id.text1)
    TextView textView1;
    @BindView(android.R.id.text2)
    TextView textView2;
    @BindView(R.id.btnDelete)
    ImageButton deleteButton;
    private boolean showPreview;

    public TokenViewHolder(ViewGroup parent,
                           ClipboardToken.OnTokenSelectedListener onTokenSelectedListener,
                           ClipboardToken.OnTokenDeleteListener onTokenDeleteListener,
                           boolean widthMatchParent,
                           boolean showPreview) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_token_preview, parent, false));
        ButterKnife.bind(this, itemView);

        this.onTokenSelectedListener = onTokenSelectedListener;
        this.onTokenDeleteListener = onTokenDeleteListener;
        this.showPreview = showPreview;

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
        } else {
            textView2.setVisibility(View.GONE);
        }
    }

    @Override public void onClick(View v) {
        if (v.getId() == R.id.btnDelete) {
            if (onTokenDeleteListener != null) {
                onTokenDeleteListener.onTokenDeleted(getBindingAdapterPosition());
            }
        } else if (onTokenSelectedListener != null) {
            onTokenSelectedListener.onTokenSelected(((ClipboardToken) v.getTag()), getBindingAdapterPosition());
        }
    }
}
