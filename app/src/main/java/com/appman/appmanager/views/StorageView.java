// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.appman.appmanager.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StorageView extends View
{

    private Paint a;
    private boolean b;
    private float c;
    private float d;
    private float e;
    private float f;
    private float g;
    private float h;
    private float i;
    private float j;
    private float k;
    private float l;
    private float m;

    public StorageView(Context context)
    {
        super(context);
        a = new Paint(1);
        a();
    }

    public StorageView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        a = new Paint(1);
        a();
    }

    public StorageView(Context context, AttributeSet attributeset, int i1)
    {
        super(context, attributeset, i1);
        a = new Paint(1);
        a();
    }

    private void a()
    {
        a.setStyle(Paint.Style.FILL_AND_STROKE);
        a.setTextSize(40F);
        b = true;
    }

    private void a(float f1)
    {
        k = m;
        m = k + l * f1;
    }

    public void a(long l1, long l2, long l3, long l4, long l5, long l6, long l7)
    {
        b = false;
        d = l1;
        e = l2;
        g = l3;
        f = l4;
        h = l5;
        i = l6;
        c = l7;
        j = l1 + l2 + l3 + l4 + l7 + l5 + l6;
        l = (float)getMeasuredWidth() / j;
        invalidate();
    }

    protected void onDraw(Canvas canvas)
    {
        if (!b)
        {
            a(d);
            a.setColor(0xff0099cc);
            a.setStrokeWidth(10F);
            canvas.drawLine(k, 8F, m, 8F, a);
            a(e);
            a.setColor(0xff9933cc);
            a.setStrokeWidth(10F);
            canvas.drawLine(k, 8F, m, 8F, a);
            a(g);
            a.setColor(0xff669900);
            a.setStrokeWidth(10F);
            canvas.drawLine(k, 8F, m, 8F, a);
            a(f);
            a.setColor(-30720);
            a.setStrokeWidth(10F);
            canvas.drawLine(k, 8F, m, 8F, a);
            a(h);
            a.setColor(0xff0b7e69);
            a.setStrokeWidth(10F);
            canvas.drawLine(k, 8F, m, 8F, a);
            a(i);
            a.setColor(-10240);
            a.setStrokeWidth(10F);
            canvas.drawLine(k, 8F, m, 8F, a);
            a(c);
            a.setColor(0xffcc0000);
            a.setStrokeWidth(10F);
            canvas.drawLine(k, 8F, m, 8F, a);
        }
    }

    protected void onMeasure(int i1, int j1)
    {
        setMeasuredDimension(MeasureSpec.getSize(i1), MeasureSpec.getSize(j1));
    }
}
