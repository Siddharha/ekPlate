package com.ekplate.android.utils;

import android.content.Context;
import android.os.Environment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by user on 06-11-2015.
 */
public class StoreInLocal {

    private Pref _pref;
    private Context context;


    public StoreInLocal(Context context){
        this.context = context;
        this._pref = new Pref(context);
    }

    public void saveInLocalFile(JSONObject response, String fileName){
        try {
            String fileDIr = Environment.getExternalStorageDirectory()
                    + File.separator + context.getPackageName();
            File f = new File(fileDIr);
            if (!f.exists()) {
                f.mkdirs();
            }

            String filename = f.getAbsolutePath() + File.separator
                    + fileName;

            File jsonF = new File(filename);

            _pref.setSession(fileName, filename);

            if (jsonF.exists()) {
                jsonF.createNewFile();
            }

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(jsonF.getAbsolutePath());
                fos.write(response.toString().getBytes());
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertStreamToString(InputStream is) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();

        return sb.toString();

    }

    public String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        // Make sure you close all streams.
        fin.close();
        return ret;
    }
}
