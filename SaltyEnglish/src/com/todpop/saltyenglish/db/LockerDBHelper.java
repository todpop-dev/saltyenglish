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
public class LockerDBHelper extends SQLiteOpenHelper {
	public LockerDBHelper(Context context) {
		super(context, "LockScreen.db", null, 1);
	}
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE latest ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
	"category INTEGER, id INTEGER, type INTEGER, image TEXT, target_url TEXT, reward INTEGER, point INTEGER);");
		db.execSQL("CREATE TABLE history ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
	"category_id INTEGER, reward INTEGER, point INTEGER);");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS latest");
		db.execSQL("DROP TABLE IF EXISTS history");
		onCreate(db);
	}
}