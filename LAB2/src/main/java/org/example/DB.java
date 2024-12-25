package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB {

    private final List<String> tables = List.of("event", "manager", "venue", "events_venue");
    private final Map<String, List<String>> columns, primaryKeys;

    public DB() {
        columns = new HashMap<>();

        columns.put("event", List.of("name", "date", "entrance_fee", "manager_id"));
        columns.put("manager", List.of("manager_id", "first_name", "last_name", "email", "phone"));
        columns.put("venue", List.of("venue_id", "name", "description", "capacity", "website"));
        columns.put("events_venue", List.of("event_name", "event_date", "venue_id", "start", "end"));

        primaryKeys = new HashMap<>();
        primaryKeys.put("event", List.of("name", "date"));
        primaryKeys.put("manager", List.of("manager_id"));
        primaryKeys.put("venue", List.of("venue_id"));
        primaryKeys.put("events_venue", List.of("event_name", "event_date"));
    }
    public List<String> tables() {
        return tables;
    }

    public Map<String, List<String>> columns() {
        return columns;
    }

    public Map<String, List<String>> primaryKeys() {
        return primaryKeys;
    }
}
