# ContactInsertDemo
android通讯录操作，包括添加、删除、同步联系人。

添加联系人支持一个联系人下多个电话号码，批量操作。

![这里写图片描述](http://img.blog.csdn.net/20170105200913432?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjcyNTg3OTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

添加联系人有两种方式：

## 传统

```java
 getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
```
这种方式，如果要插入大量联系方式，效率很低，因为要一个一个插入

## 批处理

使用 `ContentProviderOperation` 把所有电话都 `add`进去以后，统一：

```java
 context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
```

这种方式效率极高，并且是事务处理方式，如果出错，全部回滚。
