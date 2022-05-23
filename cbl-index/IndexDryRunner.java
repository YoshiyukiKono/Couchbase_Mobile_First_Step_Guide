package com.example.cbk30ce_java;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.IndexBuilder;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.SelectResult;
import com.couchbase.lite.ValueIndexItem;

public class IndexDryRunner {

    private static final String TAG = "CBL";

    public static void createIndex(Database database) throws CouchbaseLiteException {
        database.createIndex(
                "TypeNameIndex",
                IndexBuilder.valueIndex(
                        ValueIndexItem.property("type"),
                        ValueIndexItem.property("name")));
    }

    public static void deleteIndex(Database database) throws CouchbaseLiteException {
        database.deleteIndex("TypeNameIndex");
    }

    public static void explainIndexNotApplicable(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Function.lower(Expression.property("name")).equalTo(Expression.string("apple")));
        Log.i(TAG, query.explain());
    }
}
