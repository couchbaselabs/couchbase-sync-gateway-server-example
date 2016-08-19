package com.couchbaselabs;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.json.JsonObject;
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
import java.io.IOException;


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
    public Object getUserById(@PathVariable("todoId") String todoId) {
        return Database.getById(bucket(), todoId);
    }

    @RequestMapping(value="/todo", method= RequestMethod.GET)
    public Object getUsers() {
        return Database.getAll(bucket());
    }


   /* @RequestMapping(value="/user/create", method= RequestMethod.POST)
    public Object createUser(@RequestBody String json) {
        JsonObject jsonData = JsonObject.fromJson(json);
        if(!jsonData.containsKey("username")) {
            return new ResponseEntity<String>(JsonObject.create().put("error", 400).put("message", "A username must exist").toString(), HttpStatus.BAD_REQUEST);
        } else if(!jsonData.containsKey("password")) {
            return new ResponseEntity<String>(JsonObject.create().put("error", 400).put("message", "A password must exist").toString(), HttpStatus.BAD_REQUEST);
        }
        return Database.createUser(bucket(), jsonData, jsonData.getString("username"), jsonData.getString("password"));
    }*/

}