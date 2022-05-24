package com.example.cbk30ce_java;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

public class QueryDryRunner {
    private static final String TAG = "CBL";

    public static void buildQuery(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property("type").equalTo(Expression.string("hotel")))
                .limit(Expression.intValue(10));
    }
    public static void executeQuery(Query query) throws CouchbaseLiteException {
        ResultSet rs = query.execute();
    }
    public static void itereateResultSet(ResultSet rs) throws CouchbaseLiteException {
        for (Result result : rs) {
            // 何らかの処理を行う
        }
    }
    public static void createQuery(Database database) throws CouchbaseLiteException {
        Query query = database.createQuery("SELECT * FROM database");
    }
    public static void startLiveQuery(Database database) throws CouchbaseLiteException {
        // Queryオブジェクトの作成
        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database));

        // リスナー追加によるライブクエリのアクティブ化
        ListenerToken token = query.addChangeListener(change -> {
            // コールバック関数定義
            for (Result result : change.getResults()) {
                /*  変更を反映するための、例えばUIを更新するためのコードを記述します */
            }
        });

        // クエリの実行
        query.execute();
    }
    public static void stopLiveQuery(Query query, ListenerToken token) throws CouchbaseLiteException {
        query.removeChangeListener(token);
    }
}
