package com.example.cbk30ce_java;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.couchbase.lite.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class QueryBuilderDryRunner {

    private static final String TAG = "CBL";

    public static void selectAll(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database));

        for (Result result : query.execute().allResults()) {

            Dictionary props = result.getDictionary(0);
            String id = props.getString("id");
            String name = props.getString("name");
            String city = props.getString("city");
        }
    }

    public static void selectProperties(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(
                SelectResult.property("id"),
                SelectResult.property("type"),
                SelectResult.property("name"))
                .from(DataSource.database(database));
        for (Result result : query.execute().allResults()) {

            String name = result.getString("name");
            String city = result.getString("city");
        }
    }

    public static void selectDocumentIds(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id).as("id"))
                .from(DataSource.database(database));
    }

    public static void selectCount(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(
                SelectResult.expression(Function.count(Expression.string("*"))).as("count"))
                .from(DataSource.database(database));
    }

    public static void useEqualsTo(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property("type").equalTo(Expression.string("hotel")));
    }

    public static void useInOperator(Database database) throws CouchbaseLiteException {
        MutableDocument mutableDoc =
                new MutableDocument().setString("name", "Baz");
        database.save(mutableDoc);

        Expression[] values = new Expression[] {
                Expression.string("Foo"),
                Expression.string("Bar"),
                Expression.string("Baz")
        };

        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property("name").in(values));

        ResultSet rs = query.execute();

        for (Result result : rs) {
            Log.i(TAG, String.format("JSON: %s", result.toJSON()));
        }
    }

    public static void useInOperatorConversely(Database database) throws CouchbaseLiteException {
        MutableDocument mutableDoc =
                new MutableDocument().setString("first", "Cameron");
        database.save(mutableDoc);

        mutableDoc =
                new MutableDocument().setString("last", "Cameron");
        database.save(mutableDoc);

        mutableDoc =
                new MutableDocument().setString("username", "Cameron");
        database.save(mutableDoc);

        Expression[] properties = new Expression[] {
                Expression.property("first"),
                Expression.property("last"),
                Expression.property("username")
        };

        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.string("Cameron").in(properties));

        ResultSet rs = query.execute();

        for (Result result : rs) {
            Log.i(TAG, String.format("JSON: %s", result.toJSON()));
        }
    }

    public static void useLikeWildCard(Database database) throws CouchbaseLiteException{
        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property("type").equalTo(Expression.string("landmark"))
                        .and(Function.lower(Expression.property("name")).like(Expression.string("eng%e%"))));
    }

    public static void useLikeCharacterMatch(Database database) throws CouchbaseLiteException{
        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property("type").equalTo(Expression.string("landmark"))
                        .and(Function.lower(Expression.property("name")).like(Expression.string("eng____r"))));
    }

    public static void useIsValued(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder
                .select(SelectResult.expression(Expression.property("email")))
                .from(DataSource.database(database))
                .where(Expression.property("email").isValued());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void useMetaData(Database database) throws CouchbaseLiteException {
        Instant fiveMinutesFromNow = Instant.now().plus(5, ChronoUnit.MINUTES);

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database))
                .where(Meta.expiration.lessThan(
                        Expression.doubleValue(fiveMinutesFromNow.toEpochMilli())));
    }

    public static void useJoin(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(
                SelectResult.expression(Expression.property("name").from("airline")),
                SelectResult.expression(Expression.property("callsign").from("airline")),
                SelectResult.expression(Expression.property("destinationairport").from("route")),
                SelectResult.expression(Expression.property("stops").from("route")),
                SelectResult.expression(Expression.property("airline").from("route")))
                .from(DataSource.database(database).as("airline"))
                .join(Join.join(DataSource.database(database).as("route"))
                        .on(Meta.id.from("airline").equalTo(Expression.property("airlineid").from("route"))))
                .where(Expression.property("type").from("route").equalTo(Expression.string("route"))
                        .and(Expression.property("type").from("airline").equalTo(Expression.string("airline")))
                        .and(Expression.property("sourceairport")
                                .from("route").equalTo(Expression.string("RIX"))));
    }

    public static void useGroupBy(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(
                SelectResult.expression(Function.count(Expression.string("*"))),
                SelectResult.property("country"),
                SelectResult.property("tz"))
                .from(DataSource.database(database))
                .where(Expression.property("type").equalTo(Expression.string("airport"))
                        .and(Expression.property("geo.alt").greaterThanOrEqualTo(Expression.intValue(300))))
                .groupBy(Expression.property("country"), Expression.property("tz"));
    }

    public static void useOrderBy(Database database) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.property("name"))
                .from(DataSource.database(database))
                .where(Expression.property("type").equalTo(Expression.string("hotel")))
                .orderBy(Ordering.property("name").ascending());
    }

    public static void useLimit(Database database) throws CouchbaseLiteException {
        int offset = 0;
        int limit = 20;

        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .limit(Expression.intValue(limit), Expression.intValue(offset));
    }

    public static void useDateMethods(Database database) throws CouchbaseLiteException {
        Expression eStM = Function.stringToMillis(Expression.property("date_time_str"));
        Expression eStU = Function.stringToUTC(Expression.property("date_time_str"));
        Expression eMtS = Function.millisToString(Expression.property("date_time_millis"));
        Expression eMtU = Function.millisToUTC(Expression.property("date_time_millis"));
    }
}
