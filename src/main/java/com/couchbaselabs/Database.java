package com.couchbaselabs;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.*;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.*;

public class Database {

    private Database() { }

    public static List<Map<String, Object>> getById(final Bucket bucket, String todoId) {
        String queryStr = "SELECT META().id, _sync.rev, title, description, type " +
                "FROM `" + bucket.name() + "` " +
                "WHERE type = 'todo' AND META().id = $1";
        ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(queryStr, JsonArray.create().add(todoId));
        N1qlQueryResult queryResult = bucket.query(query);
        return extractResultOrThrow(queryResult);
    }

    public static List<Map<String, Object>> getAll(final Bucket bucket) {
        String queryStr = "SELECT META().id, _sync.rev, title, description, type " +
                "FROM `" + bucket.name() + "` " +
                "WHERE type = 'todo'";
        N1qlQueryResult queryResult = bucket.query(N1qlQuery.simple(queryStr));
        return extractResultOrThrow(queryResult);
    }

    private static List<Map<String, Object>> extractResultOrThrow(N1qlQueryResult result) {
        if (!result.finalSuccess()) {
            throw new DataRetrievalFailureException("Query error: " + result.errors());
        }
        List<Map<String, Object>> content = new ArrayList<Map<String, Object>>();
        for (N1qlQueryRow row : result) {
            content.add(row.value().toMap());
        }
        return content;
    }

}