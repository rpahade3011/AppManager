// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.appman.appmanager.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class TextView_Light extends TextView
{

    public TextView_Light(Context context)
    {
        super(context);
        a();
    }

    public TextView_Light(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        a();
    }

    public TextView_Light(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        a();
    }

    private void a()
    {
        while (isInEditMode() || isInEditMode()) 
        {
            return;
        }
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/clan-book-webfont.ttf"));
    }
}
