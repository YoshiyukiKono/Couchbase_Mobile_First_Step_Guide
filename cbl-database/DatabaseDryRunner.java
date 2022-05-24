package com.example.cbk30ce_java;

import com.couchbase.lite.Array;
import com.couchbase.lite.Blob;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;

import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.io.InputStream;
import java.io.IOException;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;
import android.content.res.AssetManager;

public class DatabaseDryRunner {

    private static final String TAG = "CBL";

    public static void initDatabase(Context cntx) throws CouchbaseLiteException {
        CouchbaseLite.init(cntx);
    }


    public static void newDatabase(Context context) throws CouchbaseLiteException {
        final String DB_NAME = "CBL";
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(context.getFilesDir().getAbsolutePath());
        Database database = new Database(DB_NAME, config);
    }

    public static void closeDatabase(Database database) throws CouchbaseLiteException {
        database.close();
    }

    public static void saveDocument(Database database) throws CouchbaseLiteException {
        MutableDocument newTask = new MutableDocument();
        newTask.setString("type", "task");
        newTask.setDate("createdAt", new Date());
        try {
            database.save(newTask);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void getDocument(Database database) throws CouchbaseLiteException {
        Document document = database.getDocument("xyz");
    }

    public static void changeDocument(Database database) {
        Document document = database.getDocument("xyz");
        MutableDocument mutableDocument = document.toMutable();
        mutableDocument.setString("name", "apples");
        try {
            database.save(mutableDocument);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void setDate(MutableDocument mutableDocument) throws CouchbaseLiteException {
        mutableDocument.setValue("createdAt", new Date());
    }

    public static void getDate(Document document) throws CouchbaseLiteException {
        Date date = document.getDate("createdAt");
    }

    public static void setArray(Database database) throws CouchbaseLiteException {
        // MutableArrayオブジェクト作成
        MutableArray mutableArray = new MutableArray();
        // 要素の追加
        mutableArray.addString("650-000-0000");
        mutableArray.addString("650-000-0001");
        // 新規ドキュメントのプロパティとしてMutableArrayオブジェクトを追加
        MutableDocument mutableDoc = new MutableDocument("doc1");
        mutableDoc.setArray("phones", mutableArray);
        // ドキュメント保存
        database.save(mutableDoc);
    }
    public static void getArray(Database database) throws CouchbaseLiteException {
        Document document = database.getDocument("doc1");
        // ドキュメントプロパティから配列を取得
        Array array = document.getArray("phones");
        // 配列の要素数をカウント
        int count = array.count();
        // インデックスによる配列アクセス
        for (int i = 0; i < count; i++) {
            Log.i(TAG, array.getString(i));
        }
        // ミュータブルコピーの生成
        MutableArray mutableArray = array.toMutable();
    }
    public static void setDictionary(Database database) throws CouchbaseLiteException {
        // MutableDictionaryオブジェクト作成
        MutableDictionary mutableDict = new MutableDictionary();
        // ディクショナリーへのキー/値の追加
        mutableDict.setString("street", "1 Main st.");
        mutableDict.setString("city", "San Francisco");
        // 新規ドキュメントのプロパティとしてMutableDocumentオブジェクトを追加
        MutableDocument mutableDoc = new MutableDocument("doc1");
        mutableDoc.setDictionary("address", mutableDict);
        // ドキュメント保存
        database.save(mutableDoc);
    }
    public static void getDictionary(Database database) throws CouchbaseLiteException {
        Document document = database.getDocument("doc1");
        // ドキュメントプロパティからディクショナリーを取得
        Dictionary dict = document.getDictionary("address");
        // キーによる値の取得
        String street = dict.getString("street");
        // ディクショナリーに対する走査
        for (String key : dict) {
            Log.i(TAG, key + ":" + dict.getValue(key));
        }
        // ミュータブルコピーの生成
        MutableDictionary mutableDict = dict.toMutable();
    }
    public static void checkDocument(Database database) throws CouchbaseLiteException {
        Document document = database.getDocument("doc1");
        String key = "key1";
        if (document.contains(key)) {
            Log.i(TAG, key + ":" + document.getString(key));
        }
    }
    public static void batch(Database database) throws CouchbaseLiteException {
        database.inBatch(() -> {
            for (int i = 0; i < 10; i++) {
                MutableDocument doc = new MutableDocument();
                doc.setValue("type", "user");
                doc.setValue("name", "user " + i);
                doc.setBoolean("admin", false);
                try {
                    database.save(doc);
                    Log.i(TAG, String.format("saved user document %s", doc.getString("name")));
                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }
    public static void addDocumentChangeListener(Database database, Context context) throws CouchbaseLiteException {
        database.addDocumentChangeListener(
                "doc1",
                change -> {
                    Document doc = database.getDocument(change.getDocumentID());
                    if (doc != null) {
                        Toast.makeText(context, "Status: " + doc.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static void setDocumentExpiration(Database database) throws CouchbaseLiteException {
        Instant ttl = Instant.now().plus(1, ChronoUnit.DAYS);
        database.setDocumentExpiration("doc1", new Date(ttl.toEpochMilli()));
    }
    public static void setDocumentExpirationToNull(Database database) throws CouchbaseLiteException {
        database.setDocumentExpiration("doc1", null);
    }
    public static void setBlob(Database database, MutableDocument mutableDoc, Context context)  {

        AssetManager assetManager = context.getResources().getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("image.jpg");
            Blob blob = new Blob("image/jpeg", is);
            mutableDoc.setBlob("image", blob);
            database.save(mutableDoc);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignore) { }
        }
    }
    public static void getBlob(Document doc)  {
        Blob blob = doc.getBlob("image");
        byte[] bytes = blob.getContent();
    }
    public static void toJason(Document doc)  {
        final String json = doc.toJSON();
    }
    public static void copyAsJason(Database database) throws CouchbaseLiteException {
        final String json = database.getDocument("doc1").toJSON();

        final MutableDocument document = new MutableDocument("doc2", json);

        database.save(document);
    }
    public static void newDictionaryFromJason(Database database) throws CouchbaseLiteException {
        final String JSON = "{\"name1\":\"value1\",\"name2\":\"value2\"}";
        final MutableDictionary mDict = new MutableDictionary(JSON);

        for (String key: mDict.getKeys()) {
            Log.i(TAG, key + ":" + mDict.getValue(key));
        }
    }
    public static void newArrayFromJson(Database database) throws CouchbaseLiteException {
        // JSON文字列で、配列オブジェクトを初期化
        final String JSON = "[{\"id\":\"obj1\"},{\"id\":\"obj2\"}]";
        final MutableArray mArray = new MutableArray(JSON);

        // 配列の各要素からドキュメントを作成して保存
        for (int i = 0; i < mArray.count(); i++) {
            final Dictionary dict = mArray.getDictionary(i);
            Log.i(TAG, dict.getString("id"));
            database.save(new MutableDocument(dict.getString("id"), dict.toMap()));
        }
    }
}
