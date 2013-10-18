package com.science_o_matic.imedjelly.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Application;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.BeachDescription;
import com.science_o_matic.imedjelly.data.Comment;
import com.science_o_matic.imedjelly.data.JellyFish;

public class MainApplication extends Application {
    
	// Embedded data.
    public static final JellyFish[] sJellyFishes = {
        new JellyFish(0, R.drawable.listado1a),
		new JellyFish(1, R.drawable.listado1b),
		new JellyFish(2, R.drawable.listado2a),
        new JellyFish(3, R.drawable.listado2b),
		new JellyFish(4, R.drawable.listado3a),
		new JellyFish(5, R.drawable.listado3b),
		new JellyFish(6, R.drawable.listado4a),
		new JellyFish(7, R.drawable.listado4b),
		new JellyFish(8, R.drawable.listado5a),
        new JellyFish(9, R.drawable.listado5b),
		new JellyFish(10, R.drawable.listado6a),
		new JellyFish(11, R.drawable.listado6b),
		new JellyFish(12, R.drawable.listado7a),
		new JellyFish(13, R.drawable.listado7b),
		new JellyFish(14, R.drawable.listado8a),
    };
    
    // Embedded link data.
    public static final String[] sJellyFishesLink = {
    	"j1a",
    	"j1b",
    	"j2a",
    	"j2b",
    	"j3a",
    	"j3b",
    	"j4a",
    	"j4b",
    	"j5a",
    	"j5b",
    	"j6a",
    	"j6b",
    	"j7a",
    	"j7b",
    	"j8a"
    };

    // Embedded data.
    public static final int[] sTreatments = {
        R.string.tt3,
		R.string.tt4,
		R.string.tt5,
        R.string.tt6,
		R.string.tt7,
		R.string.tt8,
		R.string.tt9,
		R.string.tt10,
		R.string.tt11,
        R.string.tt12,
		R.string.tt13,
		R.string.tt14,
		R.string.tt15,
    };

    // Embedded link data.
    public static final String[] sTreatmentsLink = {
    	"t1",
    	"t2",
    	"t3",
    	"t4",
    	"t5",
    	"t6",
    	"t7",
    	"t8",
    	"t9",
    	"t10",
    	"t11",
    	"t12",
    	"t13",
    };
    
    // Embedded jellyfish name.
    public static final String[] sJellyFishName = {
    	"nj1a",
    	"nj1b",
    	"nj2a",
    	"nj2b",
    	"nj3a",
    	"nj3b",
    	"nj4a",
    	"nj4b",
    	"nj5a",
    	"nj5b",
    	"nj6a",
    	"nj6b",
    	"nj7a",
    	"nj7b",
    	"nj8a",
    };

    private final static DateFormat format =
    	new SimpleDateFormat(BeachDescription.DateFormat, Locale.ENGLISH);
    
    // Dummy comments.
    public static final Comment sComments[] = {
    	new Comment("testUser1",
    		"testComment1: a very and really interesting comment related with all those thins so interesting that bother me.",
    		format, "2012-08-07 11:50:10"),
    	new Comment("testUser2",
   			"testComment2: a very and really interesting comment related with all those thins so interesting that bother me.",
    		format, "2012-08-07 11:50:11"),
    	new Comment("testUser3",
    		"testComment3: a very and really interesting comment related with all those thins so interesting that bother me.",
    		format, "2012-08-07 11:50:12"),
    	new Comment("testUser4",
   			"testComment4: a very and really interesting comment related with all those thins so interesting that bother me.",
    		format, "2012-08-07 11:50:13")
    };
}
