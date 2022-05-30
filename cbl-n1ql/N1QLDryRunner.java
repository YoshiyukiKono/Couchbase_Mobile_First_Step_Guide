package com.example.cbk30ce_java;

import android.os.Bundle;
import android.util.Log;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.Parameters;

import android.util.Log;
import android.content.Context;

public class N1QLDryRunner {

    private static final String TAG = "CBL";
    private static final String DB_NAME = "db";

    public static void runQueries(Database database) {
        query("SELECT name FROM _",database);
        query("SELECT store.name FROM _ AS store",database);
        query("SELECT store.name FROM _ store",database);
        query("SELECT * FROM db",database);
        query("SELECT name FROM _ WHERE department = 'engineer' AND division = 'mobile'",database);
        query("SELECT one.prop1, other.prop2 FROM _ AS one JOIN _ AS other ON one.key = other.key",database);
        query("SELECT name, score FROM _ ORDER BY name ASC, score DESC",database);
        query("SELECT name FROM _ ORDER BY name LIMIT 10 ",database);
        query("SELECT name FROM _ ORDER BY name OFFSET 10",database);
        query("SELECT name FROM _ ORDER BY name LIMIT 10 OFFSET 10",database);

        query("SELECT * FROM _ WHERE number = 10",database);
        query("SELECT * FROM _ WHERE number = 0",database);
        query("SELECT * FROM _ WHERE number = -10",database);
        query("SELECT * FROM _ WHERE number = 10.25",database);
        query("SELECT * FROM _ WHERE number = 10.25e2",database);
        query("SELECT * FROM _ WHERE number = 10.25E2",database);
        query("SELECT * FROM _ WHERE number = 10.25E+2",database);
        query("SELECT * FROM _ WHERE number = 10.25E-2",database);

        query("SELECT * FROM _ WHERE middleName = \"Fitzgerald\"",database);
        query("SELECT * FROM _ WHERE middleName = 'Fitzgerald'",database);

        query("SELECT * FROM _ WHERE value = true",database);
        query("SELECT * FROM _ WHERE value = false",database);

        query("SELECT firstName, lastName FROM _ WHERE middleName IS NULL",database);
        query("SELECT firstName, lastName FROM _ WHERE middleName IS MISSING",database);

        query("SELECT firstName, lastName FROM _ WHERE middleName IS VALUED",database);
        query("SELECT firstName, lastName FROM _ WHERE middleName IS NOT VALUED",database);

        query("SELECT [\"a\", \"b\", \"c\"] FROM _",database);
        query("SELECT [ property1, property2, property3] FROM _",database);
        query("SELECT { 'name': 'James', 'department': 10, 'phones': ['650-100-1000', '650-100-2000'] } FROM _",database);
        query("SELECT `first-name` FROM _",database);
        query("SELECT contact.address.city, contact.phones[0] FROM _",database);
        query("SELECT directory.* FROM _ AS directory",database);

        query("SELECT name FROM _ WHERE ANY v IN contacts SATISFIES v.city = 'San Mateo' END",database);

        queryWithParamter(database);

        query("SELECT (n1 + n3) * r AS n FROM _ WHERE (n1 = n2) AND (n3 = n4) ",database);
        query("SELECT name FROM _ WHERE department IN ('engineering', 'sales')",database);

        query("SELECT name FROM _ WHERE name LIKE 'art%'",database);
        query("SELECT name FROM _ WHERE name LIKE 'a__'",database);
      
        query("SELECT * FROM _ WHERE n BETWEEN 10 and 100",database);
        query("SELECT * FROM _ WHERE n >= 10 AND n <= 100",database);
      
        query("SELECT * FROM _ WHERE p IS NULL",database);
        query("SELECT * FROM _ WHERE p IS NOT NULL",database);

        query("SELECT * FROM _ WHERE city = \"Paris\" AND state = \"Texas\"",database);
        query("SELECT * FROM _ WHERE city = \"San Francisco\" OR city = \"Santa Clara\"",database);
      
        query("SELECT firstName || lastName AS fullName FROM _",database);
      
        query("SELECT * FROM _ WHERE n1 >= -10 AND n1 <= 10",database);
        query("SELECT * FROM _ WHERE name NOT IN (\"James\",\"Jane\")",database);

        query("SELECT name FROM _ ORDER BY name COLLATE UNICODE",database);

        query("SELECT CASE state WHEN 'CA' THEN 'Local' ELSE 'Non-Local' END FROM _",database);
        query("SELECT CASE WHEN shippedOn IS NOT NULL THEN 'Shipped' ELSE 'Not-Shipped' END FROM _",database);
      
        query("SELECT META().id, META().deleted FROM _",database);

        query("SELECT p.name, r.rating FROM _ as p INNER JOIN _ AS r ON META(r).id = p.reviewID WHERE META(p).id = \"product123\"",database);

    }


    public static void query(String query, Database database) {
        Log.i(TAG, String.format("クエリ: %s", query));
        try {

            Query thisQuery = database.createQuery(query);
            ResultSet rs = thisQuery.execute();

            Log.i(TAG, String.format("件数: %d", rs.allResults().size()));
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public static void queryWithParamter(Database database) {
        Log.i(TAG, String.format("クエリ: SELECT * FROM _ WHERE type = $type"));
        try {

            Query query = database.createQuery("SELECT * FROM _ WHERE type = $type");
            query.setParameters(new Parameters().setString("type", "hotel"));
            ResultSet rs = query.execute();

            Log.i(TAG, String.format("件数: %d", rs.allResults().size()));
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    public static void dryRun(Context cntx) {

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

        prepareDocument(database);

        runQueries(database);


        // データベースクローズ
        try {
            database.close();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private static void prepareDocument(Database database) {
        // ドキュメント作成
        MutableDocument mutableDoc =
                new MutableDocument();
        // ドキュメントへの値の設定

        // ドキュメント保存
        try {
            database.save(mutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
