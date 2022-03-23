package com.example.cbk30ce_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Context;
import android.util.Log;
import com.couchbase.lite.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CBL";
    private static final String DB_NAME = "cbl";
    private Context cntx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Couchbase Lite 初期化
        CouchbaseLite.init(cntx);
        Log.i(TAG,"Couchbase Lite 初期化完了");

        // データベース作成
        Log.i(TAG, "データベース作成開始");
        DatabaseConfiguration cfg = new DatabaseConfiguration();
        Database database = null;
        try {
            database = new Database(DB_NAME, cfg);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        // ドキュメント作成
        MutableDocument mutableDoc =
                new MutableDocument().setString("type", "user").setString("last-name", "佐藤");

        // ドキュメント保存
        try {
            database.save(mutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        // ドキュメント取得、変更、保存
        mutableDoc =
                database.getDocument(mutableDoc.getId())
                        .toMutable()
                        .setString("first-name", "太郎");
        try {
            database.save(mutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        // ドキュメント取得、変更結果確認
        Document document = database.getDocument(mutableDoc.getId());
        Log.i(TAG, String.format("Document ID : %s", document.getId()));
        Log.i(TAG, String.format("名前: %s %s", document.getString("last-name"), document.getString("first-name")));

        // クエリ実行
        try {
            ResultSet rs =
                    QueryBuilder.select(SelectResult.all())
                            .from(DataSource.database(database))
                            .where(Expression.property("type").equalTo(Expression.string("user")))
                            .execute();
            Log.i(TAG,
                    String.format("件数: %d", rs.allResults().size()));

            for (Result result : rs) {
                Dictionary userProps = result.getDictionary(0);
                String firstName = userProps.getString("first-name");
                String lastName = userProps.getString("last-name");
                Log.i(TAG, String.format("名前: %s %s", firstName, lastName));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        // データベースクローズ
        try {
            database.close();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
