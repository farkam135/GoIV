package com.kamron.pogoiv.clipboard.layoutmanagers;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

import com.kamron.pogoiv.clipboard.adapters.TokenListAdapter;


public class TokenGridLayoutManager extends GridLayoutManager {

    private static int SPAN_COUNT = 3;

    public TokenGridLayoutManager(Context context, final TokenListAdapter tokenListAdapter) {
        super(context, SPAN_COUNT);
        setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (tokenListAdapter.getItemViewType(position)) {
                    case TokenListAdapter.VIEW_TYPE_HEADER:
                        return SPAN_COUNT;
                    case TokenListAdapter.VIEW_TYPE_TOKEN:
                        return 1;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        });
    }
}
