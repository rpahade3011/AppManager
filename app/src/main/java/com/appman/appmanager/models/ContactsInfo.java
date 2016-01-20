package com.appman.appmanager.models;

import android.graphics.Bitmap;

/**
 * Created by rudhraksh.pahade on 20-01-2016.
 */
public class ContactsInfo {
    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public Bitmap getContactImage() {
        return contactImage;
    }

    public void setContactImage(Bitmap contactImage) {
        this.contactImage = contactImage;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    long contactId;
    Bitmap contactImage;
    String contactName;
    String contactNumber;
}
