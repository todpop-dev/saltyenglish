package com.todpop.saltyenglish.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright 2014 TODPOP Corp. All rights reserved.
 * 
 * @author steven@todpop.co.kr
 * @version 1.0
 * 
 */	//------- Database Operation ------------------
public class PronounceDBHelper extends SQLiteOpenHelper {
	public PronounceDBHelper(Context context) {
		super(context, "Pronounce.db", null, 1);
	}
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE pronounce ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"word TEXT NOT NULL UNIQUE, version TEXT, category INTEGER);");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS pronounce");
		onCreate(db);
	}
}