package com.kamron.pogoiv.widgets.recyclerviews.layoutmanagers;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kamron.pogoiv.widgets.recyclerviews.adapters.TokensShowcaseAdapter;


public class TokenGridLayoutManager extends GridLayoutManager {

    private static int SPAN_COUNT = 3;

    public TokenGridLayoutManager(Context context, TokensShowcaseAdapter tokensShowcaseAdapter) {
        super(context, SPAN_COUNT);
        setSpanSizeLookup(new TokenSpanSizeLookup(tokensShowcaseAdapter));
    }

    private static class TokenSpanSizeLookup extends SpanSizeLookup {
        TokensShowcaseAdapter adapter;

        TokenSpanSizeLookup(TokensShowcaseAdapter adapter) {
            this.adapter = adapter;
            setSpanIndexCacheEnabled(true);
        }

        @Override
        public int getSpanSize(int position) {
            switch (adapter.getItemViewType(position)) {
                case TokensShowcaseAdapter.VIEW_TYPE_HEADER:
                    return SPAN_COUNT;
                case TokensShowcaseAdapter.VIEW_TYPE_TOKEN:
                    return 1;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
