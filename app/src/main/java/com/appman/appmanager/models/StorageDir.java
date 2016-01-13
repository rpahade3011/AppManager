package com.appman.appmanager.models;

/**
 * Created by Rudraksh on 01-Jan-16.
 */
public class StorageDir {
    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    private String directoryName;
    private String directoryPath;


}
