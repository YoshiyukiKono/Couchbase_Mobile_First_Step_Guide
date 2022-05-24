package com.example.cbk30ce_java;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.LogDomain;
import com.couchbase.lite.LogFileConfiguration;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.Logger;

import android.content.Context;

import java.io.File;


public class LogDryRunner {
    private static final String TAG = "CBL";

    public static void setFileLog(Context context) throws CouchbaseLiteException {
        final File path = context.getCacheDir();
        // ログファイルディレクトリを設定
        LogFileConfiguration LogCfg = new LogFileConfiguration(path.toString());
        // ログファイルの最大サイズ（バイト）を設定
        LogCfg.setMaxSize(10240);
        // 最大回転数を5に変更
        LogCfg.setMaxRotateCount(5);
        // フォーマットをプレインテキストに変更
        LogCfg.setUsePlaintext(true);

        Database.log.getFile().setConfig(LogCfg);

        // ログ出力レベルをデフォルト(WARN)から「INFO」に変更
        Database.log.getFile().setLevel(LogLevel.INFO);
    }

    public static void setConsoleLog() throws CouchbaseLiteException {
        Database.log.getConsole().setDomains(LogDomain.DATABASE);
        Database.log.getConsole().setLevel(LogLevel.VERBOSE);
    }

    static class LogTestLogger implements Logger {

        private final LogLevel level;

        public LogTestLogger(LogLevel level) { this.level = level; }

        @Override
        public LogLevel getLevel() { return level; }

        @Override
        public void log(LogLevel level, LogDomain domain, String message) {
            //カスタムロギング処理の実装
        }
    }

    public static void setCustomeLog() throws CouchbaseLiteException {
        Database.log.setCustom(new LogTestLogger(LogLevel.WARNING));
    }

}
