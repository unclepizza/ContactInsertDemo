package example.gk.com.contactinsertdemo;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.provider.ContactsContract.Contacts.Data.MIMETYPE;
import static android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID;

public class MainActivity extends AppCompatActivity {
    private String name = "周杰伦";
    private String[] numbers = {"123456789", "987654321", "6666666", "8888888"};
    private TextView tvContatMen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        tvContatMen = (TextView) findViewById(R.id.tv_contact_man);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void sync(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                update("智行火车票", "123456");
//                syncContactMen();
            }
        }).start();
    }

    public void del(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteZX();
            }
        }).start();
    }

    public void insert(View v) {
        final ArrayList<ContactMan> contactMen = new ArrayList<>();
        ContactMan c = new ContactMan();
        c.setName("周杰伦");
        c.setNumbers("123456789,987654321,6666666,8888888");
        ContactMan c1 = new ContactMan();
        c1.setName("薛之谦");
        c1.setNumbers("");
        contactMen.add(c);
        contactMen.add(c1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentProviderHelper.insertContactMen(MainActivity.this, contactMen);
//                insertZXPhoneNumbers();
            }
        }).start();
    }

    private void insertZXPhoneNumbers() {
        ContentValues values = new ContentValues();
        //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        //往data表入姓名数据
        values.clear();
        values.put(RAW_CONTACT_ID, rawContactId);
        values.put(MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

        //往data表入电话数据
        values.clear();
        for (String number : numbers) {
            values.put(RAW_CONTACT_ID, rawContactId);
            values.put(MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(Phone.NUMBER, number);
            values.put(Phone.TYPE, Phone.TYPE_MOBILE);
            try {
                getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
            } catch (Exception e) {
                Toast.makeText(this, "Insert error!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Insert successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncContactMen() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        Cursor c = contentResolver.query(uri, null, null, null, null);
        final StringBuilder sb = new StringBuilder();
        while (c.moveToNext()) {
            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            sb.append(name).append("\t");
            Cursor phoneC = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                    contactId, null, null);
            while (phoneC.moveToNext()) {
                String phoneNumber = phoneC.getString(phoneC.getColumnIndex("data1"));
                sb.append(phoneNumber).append("\n");
            }
            phoneC.close();
        }
        c.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContatMen.setText(sb.toString());
                Toast.makeText(MainActivity.this, "Sync successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteZX() {
        Cursor cursor = getContentResolver().query(android.provider.ContactsContract.Data.CONTENT_URI, new String[]{RAW_CONTACT_ID}, ContactsContract.Contacts.DISPLAY_NAME +
                "=?", new String[]{name}, null);
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        if (cursor.moveToFirst()) {
            do {
                long Id = cursor.getLong(cursor.getColumnIndex(RAW_CONTACT_ID));
                ops.add(ContentProviderOperation.newDelete(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, Id)).build());
                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    Toast.makeText(this, "Delete error!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Delete successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update(String name, String number) {
        Cursor cursor = getContentResolver().query(Data.CONTENT_URI, new String[]{Data.RAW_CONTACT_ID}, ContactsContract.Contacts.DISPLAY_NAME + "=?", new String[]{"周杰伦"}, null);
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex(Data.RAW_CONTACT_ID));
        cursor.close();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

//        //更新名字
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + " = " +
                "?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}).withValue(ContactsContract.CommonDataKinds
                .StructuredName.DISPLAY_NAME, name).build());
        //更新电话
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + " = " +
                "?" + " AND " + Phone.TYPE + "=?", new String[]{String.valueOf(id), Phone.CONTENT_ITEM_TYPE, String.valueOf(Phone.TYPE_HOME)}).withValue(Phone.NUMBER, number)
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
        }
    }
}
