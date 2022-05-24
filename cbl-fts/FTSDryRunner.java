package com.example.cbk30ce_java;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.IndexBuilder;
import com.couchbase.lite.*;

public class FTSDryRunner {

    private static final String TAG = "CBL";

    public static void createFTSIndex(Database database) throws CouchbaseLiteException {
        FullTextIndexConfiguration config = new FullTextIndexConfiguration("overview").ignoreAccents(false);

        database.createIndex("overviewFTSIndex", config);
    }
    public static void createFTSQueryByQueryBuilder(Database database) throws CouchbaseLiteException {
        Expression whereClause = FullTextFunction.match("overviewFTSIndex", "michigan");

        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(whereClause);
    }
    public static void createFTSQueryByN1QL(Database database) throws CouchbaseLiteException {
        Query query = database.createQuery(
                "SELECT * FROM _ WHERE MATCH(overviewFTSIndex, 'michigan')");
    }
    public static void usePatternMatchingFormat(Database database) throws CouchbaseLiteException {
        Expression whereClausePrefix = FullTextFunction.match("overviewFTSIndex", "lin*");
        Expression whereClauseProperty = FullTextFunction.match("overviewFTSIndex", "'title: linux problems'");
        Expression whereClausePhrase = FullTextFunction.match("overviewFTSIndex", "\"linux applications\"");
        Expression whereClauseNEAR = FullTextFunction.match("overviewFTSIndex", "database NEAR/2 replication");
        Expression whereClauseAND = FullTextFunction.match("overviewFTSIndex", "couchbase AND database");
        Expression whereClauseMulti = FullTextFunction.match("overviewFTSIndex", "(\"couchbase database\" OR \"sqlite library\") AND \"linux\"");
    }
    public static void rankByQueryBuilder(Database database) throws CouchbaseLiteException {
        Expression whereClause = FullTextFunction.match("overviewFTSIndex", "'michigan'");

        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(whereClause)
                .orderBy(Ordering.expression(FullTextFunction.rank("overviewFTSIndex")).descending());
    }
    public static void rankByQueryN1QL(Database database) throws CouchbaseLiteException {
        Query query = database.createQuery(
                "SELECT * FROM _ WHERE MATCH(overviewFTSIndex, 'michigan') ORDER BY RANK(overviewFTSIndex)");
    }
}
