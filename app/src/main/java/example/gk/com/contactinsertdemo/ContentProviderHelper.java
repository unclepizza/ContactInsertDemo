package example.gk.com.contactinsertdemo;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaok on 2016/12/16.
 */

public class ContentProviderHelper {

    public static void insertContactMen(Context context, List<ContactMan> contactManList) {
        if (contactManList == null) {
            return;
        }
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = 0;

        for (ContactMan contactMan : contactManList) {
            //添加姓名
            String name = contactMan.getName();
            String numbers = contactMan.getNumbers();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(numbers)) {
                continue;
            }
            rawContactInsertIndex = ops.size();
            //必不可少
            ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true).build());
            //添加名字
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex).withValue(Data.MIMETYPE,
                    StructuredName.CONTENT_ITEM_TYPE).withValue(StructuredName.DISPLAY_NAME, name).withYieldAllowed(true).build());
            //添加号码,支持多个号码
            String[] numberArr = numbers.split(",");
            for (String number : numberArr) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex).withValue(Data.MIMETYPE, Phone
                        .CONTENT_ITEM_TYPE).withValue(Phone.NUMBER, number).withValue(Phone.TYPE, Phone.TYPE_MOBILE).withValue(Phone.LABEL, "").withYieldAllowed(true).build());
            }
        }
        //开始导入
        if (ops.size() != 0) {
            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
    }
}
