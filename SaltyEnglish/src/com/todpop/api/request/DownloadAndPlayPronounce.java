package com.todpop.api.request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.todpop.api.FileManager;
import com.todpop.saltyenglish.db.PronounceDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


public class DownloadAndPlayPronounce {
	Context context;
	
	PronounceDBHelper pHelper;
	SQLiteDatabase pDB;
	
	int category;
	
	public DownloadAndPlayPronounce(Context c){
		context = c;
		
		pHelper = new PronounceDBHelper(c);
	}
	public void readItForMe(String word, String version, int category){
		this.category = category;

		pDB = pHelper.getWritableDatabase();
		Cursor find = pDB.rawQuery("SELECT word, version FROM pronounce WHERE word=\'" + word + "\'", null);

		if(find.getCount() > 0){
			find.moveToFirst();
			if(new FileManager().pronounceFileCheck(word)){
				Log.i("STEVEN", "find.getString(1) is : " + find.getString(1) + "  version is : " + version);
				if(!find.getString(1).equals(version)){
					String name = Environment.getExternalStorageDirectory().getAbsolutePath() 
							+ "/Android/data/com.todpop.saltyenglish/pronounce/" + find.getString(0) + ".data";
					File file = new File(name);

					file.delete();

					pDB.delete("pronounce", "word='" + word + "'", null);
					new DownloadTask().execute(word, version);
				}
				else{
					pronouncePlay(word);
				}
			}
			else{
				pDB.delete("pronounce", "word='" + word + "'", null);
				new DownloadTask().execute(word, version);
			}
		}
		else{
			new DownloadTask().execute(word, version);
		}
		find.close();
		pDB.close();
	}	
	public void pronouncePlay(String word){
		SoundPool mSoundPool =  new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		mSoundPool.setOnLoadCompleteListener (new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
				soundPool.play(soundId, 100, 100, 1, 0, 1.0f); 
			}
		});
		mSoundPool.load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.todpop.saltyenglish/pronounce/" + word + ".data", 1);
	}
	private class DownloadTask extends AsyncTask<String, String, Boolean> {
		String word;
		String version;

		@Override
		protected Boolean doInBackground(String... param) {
			try {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.todpop.saltyenglish/pronounce/";
				File saltEng = new File(path);
				if(!saltEng.exists())
					saltEng.mkdirs();

				word = param[0];
				version = param[1];
				InputStream input = null;
				FileOutputStream fileOutput = null;
				HttpURLConnection connection = null;
				try {
					//TODO testing
					URL url = new URL("http://www.todpop.co.kr/uploads/voice/" + word + ".mp3");
					connection = (HttpURLConnection) url.openConnection();
					connection.connect();

					// expect HTTP 200 OK, so we don't mistakenly save error report 
					// instead of the file
					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
						return false;

					// download the file
					String finalPath = path + word + ".data";

					File file = new File(finalPath);
					file.createNewFile();

					input = connection.getInputStream();
					fileOutput = new FileOutputStream(finalPath);
					//output = openFileOutput(word, Context.MODE_PRIVATE);

					byte data[] = new byte[1024];
					int count;
					while ((count = input.read(data)) != -1) {
						fileOutput.write(data, 0, count);
					}
				} catch (Exception e) {
					return false;
				} finally {
					try {
						if (fileOutput != null){
							fileOutput.flush();
							fileOutput.close();
						}
						if (input != null)
							input.close();
					} 
					catch (IOException ignored) { }

					if (connection != null)
						connection.disconnect();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return true;
		}    
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result){
				//do nothing
			}
			else{
				pDB = pHelper.getWritableDatabase();

				ContentValues row = new ContentValues();
				Log.i("STEVEN", "before save");
				row.put("word", word);
				row.put("version", version);
				row.put("category", category);

				pDB.insert("pronounce", null, row);
				pDB.close();

				Log.i("STEVEN", "saved version is " + version);
				pronouncePlay(word);
			}
		}
	}
}