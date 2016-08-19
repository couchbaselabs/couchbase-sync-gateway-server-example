package com.couchbaselabs;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@RestController
@RequestMapping("/api")
public class Application implements Filter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Value("${hostname}")
    private String hostname;

    @Value("${bucket}")
    private String bucket;

    @Value("${password}")
    private String password;

    public @Bean
    Cluster cluster() {
        return CouchbaseCluster.create(hostname);
    }

    public @Bean
    Bucket bucket() {
        return cluster().openBucket(bucket, password);
    }

    @RequestMapping(value="/todo/{todoId}", method= RequestMethod.GET)
    public Object getById(@PathVariable("todoId") String todoId) {
        return Database.getById(bucket(), todoId);
    }

    @RequestMapping(value="/todo", method= RequestMethod.GET)
    public Object getAll() {
        return Database.getAll(bucket());
    }

    @RequestMapping(value="/todo", method= RequestMethod.DELETE)
    public Object deleteAll(@RequestBody String json) {
        JsonArray jsonData = JsonArray.fromJson(json);
        //List<Object> todoIdList = jsonData.toList();
        JsonArray responses = JsonArray.create();
        for(int i = 0; i < jsonData.size(); i++) {
            System.out.println(jsonData.getObject(i).getString("id"));
            responses.add(makeDeleteRequest("http://localhost:4984/" + bucket().name() + "/" + jsonData.getObject(i).getString("id") + "/" + jsonData.getObject(i).getString("rev")));
        }
        return new ResponseEntity<String>(responses.toString(), HttpStatus.OK);
    }


    @RequestMapping(value="/todo", method= RequestMethod.POST)
    public Object createTodo(@RequestBody String json) {
        JsonObject jsonData = JsonObject.fromJson(json);
        if(!jsonData.containsKey("title")) {
            return new ResponseEntity<String>(JsonObject.create().put("error", 400).put("message", "A title must exist").toString(), HttpStatus.BAD_REQUEST);
        } else if(!jsonData.containsKey("description")) {
            return new ResponseEntity<String>(JsonObject.create().put("error", 400).put("message", "A description must exist").toString(), HttpStatus.BAD_REQUEST);
        }
        JsonObject data = JsonObject.create().put("title", jsonData.get("title")).put("description", jsonData.get("description")).put("type", "todo");
        JsonObject response = makePostRequest("http://localhost:4984/" + bucket().name() + "/", data.toString());
        return new ResponseEntity<String>(response.getObject("content").toString(), HttpStatus.valueOf(response.getInt("status")));
    }

    private JsonObject makePostRequest(String url, String body) {
        JsonObject jsonResult = JsonObject.create();
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(body, ContentType.create("application/json")));
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String result = "";
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            jsonResult.put("status", response.getStatusLine().getStatusCode());
            jsonResult.put("content", JsonObject.fromJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    private JsonObject makeDeleteRequest(String url) {
        JsonObject jsonResult = JsonObject.create();
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpDelete delete = new HttpDelete(url);
            HttpResponse response = client.execute(delete);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String result = "";
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            jsonResult.put("status", response.getStatusLine().getStatusCode());
            jsonResult.put("content", JsonObject.fromJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

}