// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.appman.appmanager.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StorageSimpleView extends View
{

    private float a;
    private float b;
    private Paint c;
    private boolean d;

    public StorageSimpleView(Context context)
    {
        super(context);
        c = new Paint(1);
        a();
    }

    public StorageSimpleView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        c = new Paint(1);
        a();
    }

    public StorageSimpleView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        c = new Paint(1);
        a();
    }

    private void a()
    {
        c.setStyle(Paint.Style.FILL_AND_STROKE);
        c.setTextSize(40F);
        d = true;
    }

    public void a(long l, long l1)
    {
        d = false;
        a = l;
        b = l1;
        invalidate();
    }

    protected void onDraw(Canvas canvas)
    {
        if (!d)
        {
            c.setColor(0xffeeeeee);
            c.setStrokeWidth(8F);
            canvas.drawLine(0.0F, 8F, getMeasuredWidth(), 8F, c);
            c.setColor(0xff25a599);
            c.setStrokeWidth(8F);
            canvas.drawLine(0.0F, 8F, ((float)getMeasuredWidth() * a) / b, 8F, c);
        }
    }

    protected void onMeasure(int i, int j)
    {
        setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(j));
    }
}
