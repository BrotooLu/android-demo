package com.bro2.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by bro2 on 17-5-3.
 */

public class ContactsFetcher {
    private static final String TAG = "ContactsFetcher";

    public static void queryNumberByName1(Context ctx, String target) {
        Log.d(TAG, "queryNumberByName1 " + target);
        ContentResolver cr = ctx.getContentResolver();
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.Data.MIMETYPE + "=? AND " +
                ContactsContract.Contacts.DISPLAY_NAME + "=?";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, target
        };

        Cursor contact = cr.query(uri, projection, selection, selectionArgs, null);
        int count;
        if (contact != null && (count = contact.getCount()) > 0) {
            String[] numbers = new String[count];
            int numberIndex = contact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            for (int i = 0; i < count; ++i) {
                contact.moveToNext();
                numbers[i] = contact.getString(numberIndex);
            }
            Log.d(TAG, "queryNumberByName1 result" + Arrays.asList(numbers));
        }
    }

    public static void queryNumberByName(Context ctx, String name) {
        ContentResolver cr = ctx.getContentResolver();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};
        String selection = ContactsContract.Contacts.DISPLAY_NAME + "=?";
        String[] selectionArgs = {
                name
        };

        Cursor contacts = cr.query(uri, projection, selection, selectionArgs, null);
        if (contacts == null) {
            Log.e(TAG, "query failed");
            return;
        }

        int count = contacts.getCount();
        if (count <= 0) {
            contacts.close();
            Log.e(TAG, "query no result");
            return;
        }

        Log.e(TAG, "query count: " + count);
        String[] contactId = new String[count];
        int idIndex = contacts.getColumnIndex(ContactsContract.Contacts._ID);
        int nameIndex = contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        for (int i = 0; i < count; ++i) {
            contacts.moveToNext();
            contactId[i] = contacts.getString(idIndex);
            Log.d(TAG, "queryNumberByName " + i + ": " + contactId[i] + " "
                    + contacts.getString(nameIndex));
            queryByContactId(ctx, contactId[i]);
        }
        contacts.close();
    }

    private static void queryByContactId(Context ctx, String id) {
        Log.d(TAG, "queryByContactId ");
        ContentResolver cr = ctx.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
        String[] selectionArgs = {
                id
        };

        Cursor contacts = cr.query(uri, projection, selection, selectionArgs, null);
        if (contacts == null) {
            Log.e(TAG, "query failed");
            return;
        }

        int count = contacts.getCount();
        if (count <= 0) {
            contacts.close();
            Log.e(TAG, "query no result");
            return;
        }

        Log.e(TAG, "query count: " + count);
        String[] numbers = new String[count];
        int numberIndex = contacts.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER);
        for (int i = 0; i < count; ++i) {
            contacts.moveToNext();
            numbers[i] = contacts.getString(numberIndex);
        }
        contacts.close();
        Log.d(TAG, "queryByContactId result" + Arrays.asList(numbers));
    }

}
