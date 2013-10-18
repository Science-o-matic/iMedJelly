package com.science_o_matic.imedjelly.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import com.science_o_matic.imedjelly.data.Beach;

public class Util {

	public static int getResourceId(Context context, String name, String type) {
		Resources res = context.getResources();
		String packageName = context.getPackageName();
		return res.getIdentifier(name, type, packageName);
	}
	
	public static Bundle beachToBundle(Beach beach) {
		Bundle bundle = new Bundle();
		bundle.putLong("id", beach.getId());
		bundle.putString("name", beach.getName());
		bundle.putLong("api_id", beach.getApi_Id());
		bundle.putDouble("latitude", beach.getLatitude());
		bundle.putDouble("longitude", beach.getLongitude());
		bundle.putString("jellyfish_status", beach.getJellyfish_Status());
		bundle.putString("municipality_name", beach.getMunicipality_Name());
		return bundle;
	}
	
	public static Beach bundleToBeach(Bundle bundle) {
		Beach beach = new Beach();
		beach.setId(bundle.getLong("id"));
		beach.setName(bundle.getString("name"));
		beach.setApi_Id(bundle.getLong("api_id"));
		beach.setLatitude(bundle.getDouble("latitude"));
		beach.setLongitude(bundle.getDouble("longitude"));
		beach.setJellyfish_Status(bundle.getString("jellyfish_status"));
		beach.setMunicipality_Name(bundle.getString("municipality_name"));
		return beach;
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
