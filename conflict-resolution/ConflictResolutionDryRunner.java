package com.example.cbk30ce_java;

import android.util.Log;
import android.content.Context;

import com.couchbase.lite.Conflict;
import com.couchbase.lite.ConflictResolver;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


public class ConflictResolutionDryRunner {

    private static final String TAG = "CBL";
    private static final String DB_NAME = "db";

    private static void saveDocWithCustomConflictResolusion(Database database) {
        // ドキュメント作成
        MutableDocument mutableDocument = new MutableDocument();

        // ドキュメント保存
        try {
            database.save(
                    mutableDocument,
                    (newDoc, curDoc) -> {
                        if (curDoc == null) { return false; }
                        Map<String, Object> dataMap = curDoc.toMap();
                        dataMap.putAll(newDoc.toMap());
                        newDoc.setData(dataMap);
                        return true;
                    });
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static class MergeConflictResolver implements ConflictResolver {
        public Document resolve(Conflict conflict) {
            Map<String, Object> merge = conflict.getLocalDocument().toMap();
            merge.putAll(conflict.getRemoteDocument().toMap());
            return new MutableDocument(conflict.getDocumentId(), merge);
        }
    }

    static class RemoteWinConflictResolver implements ConflictResolver {
        public Document resolve(Conflict conflict) {
            return conflict.getRemoteDocument();
        }
    }

    static class LocalWinConflictResolver implements ConflictResolver {
        public Document resolve(Conflict conflict) {
            return conflict.getLocalDocument();
        }
    }

    private static void startReplicatorWithCustomConflictResolusion(Database database) {
        URLEndpoint target = null;
        try {
            target = new URLEndpoint(new URI("wss://10.0.2.2:4984/travel-sample"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        ReplicatorConfiguration config = new ReplicatorConfiguration(database, target);
        config.setConflictResolver(new LocalWinConflictResolver());

        Replicator replication = new Replicator(config);
        replication.start();
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

        saveDocWithCustomConflictResolusion(database);
        startReplicatorWithCustomConflictResolusion(database);

        // データベースクローズ
        try {
            database.close();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }



}
