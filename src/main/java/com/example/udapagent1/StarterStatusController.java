package com.example.udapagent1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@RestController
public class StarterStatusController {

    @Autowired(required = false)
    private DataSource dataSource;

    @GetMapping("/api/status")
    public Map<String, String> status() {
        Map<String, String> payload = new LinkedHashMap<>();
        if (dataSource == null) {
            payload.put("status", "disconnected");
            payload.put("title", "Database Not Configured");
            payload.put("detail", "No DataSource bean is available.");
            return payload;
        }
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            String product = meta.getDatabaseProductName();
            String catalog = conn.getCatalog();
            String display = (catalog != null && !catalog.isEmpty()) ? catalog : meta.getURL();
            payload.put("status", "connected");
            payload.put("title", "Database Connected");
            payload.put("detail", product + " • " + display);
        } catch (Exception e) {
            payload.put("status", "disconnected");
            payload.put("title", "Database Unreachable");
            payload.put("detail", e.getMessage());
        }
        return payload;
    }
}
