package com.example.cbk30ce_java;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DocumentFlag;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.ReplicatedDocument;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorActivityLevel;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.ReplicatorType;
import com.couchbase.lite.SessionAuthenticator;
import com.couchbase.lite.URLEndpoint;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

public class ReplicationDryRunner {

    private static final String TAG = "CBL";

    public static void dryRun(Database database) throws CouchbaseLiteException {
        try {
            // ローカルDBと、リモートDBのエンドポイントを指定
            final ReplicatorConfiguration config = new ReplicatorConfiguration(
                    database,
                    new URLEndpoint(new URI("wss://10.0.2.2:4984/travel-sample")));

            // レプリケーションのタイプを指定
            config.setType(ReplicatorType.PUSH_AND_PULL);

            // ベーシック認証情報を指定
            final BasicAuthenticator auth
                    = new BasicAuthenticator("Username", "Password".toCharArray());
            config.setAuthenticator(auth);

            // 継続的レプリケーションに設定
            config.setContinuous(true);

            // チャネル設定
            String[] channels = {"channel1", "channel2", "channel3"};
            List<String> channelList = Arrays.asList(channels);
            config.setChannels(channelList);

            // カスタムヘッダー設定
            Map<String, String> headers = new HashMap<>();
            headers.put("CustomHeaderName", "Value");
            config.setHeaders(headers);

            // プッシュフィルター
            config.setPushFilter((document, flags) -> flags.contains(DocumentFlag.DELETED));
            // プルフィルター
            config.setPullFilter((document, flags) -> "draft".equals(document.getString("type")));

            // 自動パージ無効化
            config.setAutoPurgeEnabled(false);

            // ハートビートパルス間隔(秒)を設定
            config.setHeartbeat(150);
            // 最大再試行回数を設定
            config.setMaxAttempts(20);
            // 再施行最大待機時間(秒)を設定
            config.setMaxAttemptWaitTime(600);

            // レプリケーター作成
            final Replicator replicator = new Replicator(config);

            // レプリケーター開始
            boolean resetCheckpointRequired = false;
            if (resetCheckpointRequired) {
                replicator.start(true);
            } else {
                replicator.start();
            }

            Log.i(TAG, "The Replicator is currently " + replicator.getStatus().getActivityLevel());

            if (replicator.getStatus().getActivityLevel() == ReplicatorActivityLevel.BUSY) {
                Log.i(TAG, "Replication Processing");
            }

            replicator.addChangeListener(new ReplicatorChangeListener() {
                @Override
                public void changed(ReplicatorChange change) {

                    if (change.getReplicator().getStatus().getActivityLevel().equals(ReplicatorActivityLevel.IDLE)) {

                        Log.e("Replication Comp Log", "Schedular Completed");
                    }
                    if (change.getReplicator().getStatus().getActivityLevel()
                            .equals(ReplicatorActivityLevel.STOPPED) || change.getReplicator().getStatus().getActivityLevel()
                            .equals(ReplicatorActivityLevel.OFFLINE)) {
                        Log.e("Rep schedular  Log", "ReplicationTag Stopped");
                    }
                }
            });

            ListenerToken token = replicator.addChangeListener(change -> {
                final CouchbaseLiteException err = change.getStatus().getError();
                if (err != null) {
                    Log.i(TAG, "Error code ::  " + err.getCode(), err);
                }
            });


            replicator.removeChangeListener(token);

            replicator.addDocumentReplicationListener(replication -> {

                // レプリケーションタイプをログ出力
                Log.i(TAG, "Replication type: " + (replication.isPush() ? "Push" : "Pull"));
                for (ReplicatedDocument doc : replication.getDocuments()) {

                    // 送受信されたドキュメントのドキュメントIDをログ出力
                    Log.i(TAG, "Doc ID: " + doc.getID());

                    CouchbaseLiteException err = doc.getError();
                    if (err != null) {
                        // エラー発生
                        Log.e(TAG, "Error replicating document: ", err);
                        return;
                    }
                    // ドキュメント削除のケース
                    if (doc.getFlags().contains(DocumentFlag.DELETED)) {
                        Log.i(TAG, "Successfully replicated a deleted document");
                    }
                    // アクセス権削除のケース
                    if (doc.getFlags().contains(DocumentFlag.ACCESS_REMOVED)) {
                        Log.i(TAG, "Access removed");
                    }
                }
            });

            final Set<String> pendingDocs = replicator.getPendingDocumentIds();

            for (Iterator<String> itr = pendingDocs.iterator(); itr.hasNext(); ) {
                final String docId = itr.next();

                if (replicator.isDocumentPending(docId)) {
                    Log.i(TAG, "Doc ID " + docId + " is pending");
                } else {
                    Log.i(TAG, "Doc ID " + docId + " is not pending");
                }
            }

            replicator.stop();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setSessionAuthenticator(Database database) throws CouchbaseLiteException {
        // ローカルDBと、リモートDBのエンドポイントを指定
        try {
        final ReplicatorConfiguration config = new ReplicatorConfiguration(
                    database,
                    new URLEndpoint(new URI("wss://10.0.2.2:4984/travel-sample")));

            // セッションID指定による認証
            config.setAuthenticator(new SessionAuthenticator("904ac010862f37c8dd99015a33ab5a3565fd8447"));

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setReplicatorTypePUSH(Database database) throws CouchbaseLiteException {
        // ローカルDBと、リモートDBのエンドポイントを指定
        try {
            final ReplicatorConfiguration config = new ReplicatorConfiguration(
                    database,
                    new URLEndpoint(new URI("wss://10.0.2.2:4984/travel-sample")));

            // レプリケーションのタイプを指定
            config.setType(ReplicatorType.PUSH);

            config.setContinuous(true);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
