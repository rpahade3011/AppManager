package com.appman.appmanager.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.appman.appmanager.R;
import com.appman.appmanager.adapter.SMSRestoreAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Rudraksh on 19-Jan-16.
 */
public class MyFileManager extends AppCompatActivity{
    String path;
    private File currentDir;
    private List<String> listOfFiles;
    private ListView lvSMSRestoreFiles;
    private TextView tvPath;
    private SMSRestoreAdapter smsRestoreAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_file_manager);
        path = getIntent().getStringExtra("path");

        lvSMSRestoreFiles = (ListView) findViewById (R.id.lvSMSRestoreFiles);
        tvPath = (TextView) findViewById (R.id.tvPath);
        tvPath.setText(path);
        showFiles();
    }

    private void showFiles() {
        currentDir = new File(path);

        if (currentDir.exists()){
            File[] dirs = currentDir.listFiles();
            if (dirs != null){
                this.listOfFiles = new ArrayList<String>();
                List asList = Arrays.asList(dirs);
                try{
                    Collections.sort(asList, new MyComparator());
                }catch (Exception e){
                    e.getMessage().toString();
                }
                for (int i = 0; i < dirs.length; i++){
                    File file = (File) asList.get(i);
                    if (file.isFile() || file.getName().endsWith(".xml") || file.getName().endsWith(".vcf")){
                        this.listOfFiles.add(file.toString());
                    }
                }
                smsRestoreAdapter = new SMSRestoreAdapter(MyFileManager.this,listOfFiles, path);
                lvSMSRestoreFiles.setAdapter(smsRestoreAdapter);
            }
        }

    }

    private class MyComparator implements Comparator<File> {


        @Override
        public int compare(File lhs, File rhs) {
            File file = (File) lhs;
            File file2 = (File) rhs;
            return (file.isDirectory() && file2.isFile()) ? -1 : (file.isFile() && file2.isDirectory()) ? 1 : (file.isDirectory() && file2.isDirectory()) ? file.getName().compareToIgnoreCase(file2.getName()) : file.lastModified() <= file2.lastModified() ? file.lastModified() > file2.lastModified() ? 1 : 0 : -1;
        }
    }
}
