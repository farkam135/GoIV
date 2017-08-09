package com.kamron.pogoiv.clipboard.layoutmanagers;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

import com.kamron.pogoiv.clipboard.adapters.TokensShowcaseAdapter;


public class TokenGridLayoutManager extends GridLayoutManager {

    private static int SPAN_COUNT = 3;

    public TokenGridLayoutManager(Context context, final TokensShowcaseAdapter tokensShowcaseAdapter) {
        super(context, SPAN_COUNT);
        setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (tokensShowcaseAdapter.getItemViewType(position)) {
                    case TokensShowcaseAdapter.VIEW_TYPE_HEADER:
                        return SPAN_COUNT;
                    case TokensShowcaseAdapter.VIEW_TYPE_TOKEN:
                        return 1;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        });
    }
}
