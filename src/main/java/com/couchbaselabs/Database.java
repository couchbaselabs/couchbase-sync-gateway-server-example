package com.couchbaselabs;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Database {

    private Database() { }

    public static List<Map<String, Object>> getById(final Bucket bucket, String todoId) {
        String queryStr = "SELECT META().id, _sync.rev, title, description, type " +
                "FROM `" + bucket.name() + "` " +
                "WHERE type = 'todo' AND META().id = $1 AND _sync IS NOT MISSING";
        ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(queryStr, JsonArray.create().add(todoId));
        return bucket.async().query(query)
                .flatMap(AsyncN1qlQueryResult::rows)
                .map(result -> result.value().toMap())
                .toList()
                .timeout(10, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }

    public static List<Map<String, Object>> getAll(final Bucket bucket) {
        String queryStr = "SELECT META().id, _sync.rev, title, description, type " +
                "FROM `" + bucket.name() + "` " +
                "WHERE type = 'todo' AND _sync IS NOT MISSING";
        return bucket.async().query(N1qlQuery.simple(queryStr))
                .flatMap(AsyncN1qlQueryResult::rows)
                .map(result -> result.value().toMap())
                .toList()
                .timeout(10, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }

}