package com.todpop.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileManager {
	static final String SALTYENGPATH = "/Android/data/com.todpop.saltyenglish/";
	public boolean moveFile(Context context, String fileName, String outPath) {
		String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + outPath;
	    FileInputStream in = null;
	    OutputStream out = null;
	    
	    try {

	        //create output directory if it doesn't exist
	        File dir = new File (outputPath); 
	        if (!dir.exists())
	        {
	            dir.mkdirs();
	        }


	        in = context.openFileInput(fileName);
	        out = new FileOutputStream(outputPath + fileName + ".data");

	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;

	        // write the output file
	        out.flush();
	        out.close();
	        out = null;

	        // delete the original file
	        context.deleteFile("inputFile");
	        return true;

	    }catch (FileNotFoundException fnfe1) {
	        Log.e("tag", fnfe1.getMessage());
	        return false;
	    }catch (Exception e) {
	        Log.e("tag", e.getMessage());
	        return false;
	    }
	}
	public boolean pronounceFileCheck(String name){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ SALTYENGPATH + "pronounce/" + name + ".data";
		return fileCheck(path);
	}
	public boolean lockerFileCheck(String name){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ SALTYENGPATH + "img/locker/" + name + ".data";
		return fileCheck(path);
	}
	private boolean fileCheck(String path){
        File file = new File(path); 
        
        if(file.exists())
        	return true;
        else
        	return false;
	}
	public boolean saveImgFile(Bitmap img, String name){
		FileOutputStream out = null;
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ SALTYENGPATH + "img/locker/";
		try{
			File dir = new File (path); 
	        if (!dir.exists())
	        {
	            dir.mkdirs();
	        }
	        Log.i("STEVEN", "FILE MANAGER 88");
			out = new FileOutputStream(path + name + ".data");
			
			img.compress(Bitmap.CompressFormat.PNG, 100, out);
			
			out.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			try {
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}
	}
	public Bitmap getImgFile(String name){
		FileInputStream in = null;
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ SALTYENGPATH + "img/locker/";
		try {
			in = new FileInputStream(path + name + ".data");
			return BitmapFactory.decodeStream(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
