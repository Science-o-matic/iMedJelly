package com.science_o_matic.imedjelly.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;

public class Util {

	public static int getResourceId(Context context, String name, String type) {
		Resources res = context.getResources();
		String packageName = context.getPackageName();
		return res.getIdentifier(name, type, packageName);
	}
	
	public static void ShowNotification(Context context, String title, String msg) {
		new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            }
         })
         .show();
	}
}
