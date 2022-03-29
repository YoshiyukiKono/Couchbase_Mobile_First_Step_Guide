package com.example.cbl3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Context
import android.util.Log
import com.couchbase.lite.*


class MainActivity : AppCompatActivity() {

    private var TAG = "CBL"
    private var DB_NAME = "cbla"
    private var cntx: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Couchbase Lite 初期化
        CouchbaseLite.init(cntx)
        Log.i(TAG, "Couchbase Lite 初期化完了")

        // データベース作成
        Log.i(TAG, "データベース作成開始")
        val cfg = DatabaseConfigurationFactory.create()
        val database = Database(DB_NAME, cfg)

        // ドキュメント作成
        var mutableDoc = MutableDocument().setString("type", "user").setString("last-name", "佐藤")

        // ドキュメント保存
        database.save(mutableDoc)

        // ドキュメント取得、変更、保存
        mutableDoc = database.getDocument(mutableDoc.id)!!.toMutable().setString("first-name", "太郎")
        database.save(mutableDoc)


        // ドキュメント取得、ログ出力
        val document = database.getDocument(mutableDoc.id)!!
        Log.i(TAG, "ドキュメントID: ${document.id}")
        Log.i(TAG, "名前: ${document.getString("last-name")} ${document.getString("first-name")}")

        // クエリ
        val rs = QueryBuilder.select(SelectResult.all())
            .from(DataSource.database(database))
            .where(Expression.property("type").equalTo(Expression.string("user")))
            .execute()

        for (result in rs) {
            // get the k-v pairs from the 'hotel' key's value into a dictionary
            val userProps = result.getDictionary(0)
            val firstName = userProps!!.getString("first-name")
            val lastName = userProps.getString("last-name")
            Log.i(TAG, "名前: ${lastName} ${firstName}")
        }

        // データベースクローズ
        database.close()
    }
}
