package com.kamron.pogoiv.widgets.recyclerviews.adapters.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TokenHeaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(android.R.id.text1)
    TextView textView;

    public TokenHeaderViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_token_header, parent, false));
        ButterKnife.bind(this, itemView);
    }

    public void bind(ClipboardToken.Category category) {
        textView.setText(category.toString(textView.getContext()));
    }

}
