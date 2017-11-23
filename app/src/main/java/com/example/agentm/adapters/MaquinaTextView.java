package com.example.agentm.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by carlo on 08/05/2017.
 */

public class MaquinaTextView extends android.support.v7.widget.AppCompatTextView {
    public MaquinaTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/maqui.ttf"));
    }
}
