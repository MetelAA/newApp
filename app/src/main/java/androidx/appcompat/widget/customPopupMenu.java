package androidx.appcompat.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

public class customPopupMenu  extends  PopupMenu{
    @SuppressLint("RestrictedApi")
    public customPopupMenu(Context context, View anchor){
        super(context, anchor);
        mPopup.setForceShowIcon(true);
    }
}
