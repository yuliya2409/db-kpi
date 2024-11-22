package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Model {
    private final String URL = "jdbc:postgresql://localhost:5432/events_managing_system",
    USERNAME = "user",
    PASSWORD = "12345abc",
    DRIVER = "org.postgresql.Driver";


    private final Connection connection;

    public Model() throws SQLException, ClassNotFoundException {
        connection = connect();
    }

    public Connection connect() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);

        Connection c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        return c;
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public void insert(String tableName, Map<String, String> columns) {
        StringBuilder q = new StringBuilder("INSERT INTO \"" + tableName + "\"(");
        StringBuilder v = new StringBuilder(" VALUES (");
        int i = 0;
        for (Map.Entry<String, String> columnData : columns.entrySet()) {
            q.append(columnData.getKey()).append(i == columns.size() - 1 ? "" : ",");
            v.append("'").append(columnData.getValue()).append("'").append(i == columns.size() - 1 ? "" : ",");
            i++;
        }
        q.append(")");
        v.append(")");
        q.append(v);
        System.out.println(q.toString());
        try {
            Statement statement = connection.createStatement();
            statement.execute(q.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String t, Map<String, String> pk, Map<String, String> c) {
        StringBuilder q = new StringBuilder("UPDATE \"" + t + "\" SET ");
        int i = 0;
        for (Map.Entry<String, String> columnData : c.entrySet()) {
            q.append(columnData.getKey())
                    .append("='").append(columnData.getValue()).append("'")
                    .append(i == c.size() - 1 ? "" : ",");
            i++;
        }
        q.append(" WHERE ");
        i = 0;
        for (Map.Entry<String, String> columnData : pk.entrySet()) {
            q.append(columnData.getKey())
                    .append("='").append(columnData.getValue()).append("'")
                    .append(i == pk.size() - 1 ? "" : ",");
            i++;
        }
        System.out.println(q);
        try {
            Statement statement = connection.createStatement();
            statement.execute(q.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String t, Map<String, String> pk) {
        StringBuilder q = new StringBuilder("DELETE FROM \"" + t + "\" WHERE ");
        int i = 0;
        for (Map.Entry<String, String> columnData : pk.entrySet()) {
            q.append(columnData.getKey())
                    .append("='").append(columnData.getValue()).append("'")
                    .append(i == pk.size() - 1 ? "" : " AND ");
            i++;
        }
        System.out.println(q.toString());
        try {
            Statement statement = connection.createStatement();
            statement.execute(q.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void generate(String table, int size) {
        String query = "";
        if(table.equals("manager")) {
            query = "insert into manager(manager_id, first_name, last_name, email, phone) \n" +
                    "select * from (select (random()*100000)::int as manager_id,\n" +
                    "    chr(trunc(65 + random() * 25)::int) || chr(trunc(65 + random() * 25)::int) as first_name,\n" +
                    "    chr(trunc(65 + random() * 25)::int) || chr(trunc(65 + random() * 25)::int) as last_name,\n" +
                    "    chr(trunc(65 + random() * 25)::int) || chr(trunc(65 + random() * 25)::int) as email,\n" +
                    "    to_char(random() * 10000000000, 'FM(000) 000-0000') as phone\n" +
                    "    from generate_series(1,100000)) as _\n" +
                    "where (manager_id not in (select manager_id from manager))\n" +
                    "limit " + size + ";";
        }
        else if(table.equals("event")) {
            query = "INSERT INTO \"event\"(name, date, entrance_fee, manager_id)" +
                    "SELECT chr(trunc(65 + random() * 25)::int) || chr(trunc(65 + random() * 25)::int) || chr(trunc(65 + random() * 25)::int) as event_name,\n" +
                    "    DATE(CURRENT_DATE + random() * interval '10 months') as date,\n" +
                    "    (random() * 100)::int as entrance_fee,\n" +
                    "    manager_id\n" +
                    "    from generate_series(1,100000)\n" +
                    "    cross join (select manager_id from manager) as m_id" +
                    "    LIMIT " + size + ";";
        }
        else throw new RuntimeException("Invalid table name");
        try {
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> search(Map<String, String> params, int queryNum) {
        List<String> rows = new ArrayList<>();
        String query = switch (queryNum) {
            case 1 -> "select manager.manager_id, first_name, last_name, email, count(*) from manager\n" +
                    "    join \"event\"\n" +
                    "    on event.manager_id = manager.manager_id\n" +
                    "    where first_name like '" + params.get("first_name") + "' \n" +
                    "    group by manager.manager_id, first_name, last_name, email";
            case 2 -> "select venue.venue_id, venue.name, venue.capacity, count(event.name) as events from venue\n" +
                    "join \"events_venue\"\n" +
                    "on venue.venue_id = events_venue.venue_id\n" +
                    "join event\n" +
                    "on events_venue.event_name = event.name\n" +
                    "group by venue.venue_id, venue.name, venue.capacity\n" +
                    "having count(event.name) = " + params.get("events");
            case 3 -> "select venue.venue_id, venue.name, venue.capacity, count(event.name) as events from venue\n" +
                    "left join \"events_venue\"\n" +
                    "on venue.venue_id = events_venue.venue_id\n" +
                    "left join event\n" +
                    "on events_venue.event_name = event.name\n" +
                    "where date between '" + params.get("first") + "' and '" + params.get("last") + "'\n" +
                    "group by venue.venue_id, venue.name";
            default -> throw new RuntimeException("Invalid option");
        };
        long start, end;
        try {
            start = System.nanoTime();
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            end = System.nanoTime();
            switch (queryNum) {
                case 1 -> {
                    rows.add(String.format("%11s | %15s | %15s | %26s | %9s", "manager_id", "first_name", "last_name", "email", "count"));
                    rows.add("------------+-----------------+-----------------+----------------------------+-----------");
                }
                case 2, 3 -> {
                    rows.add(String.format("%11s | %15s | %15s | %20s ", "venue_id", "name", "capacity", "events"));
                    rows.add("------------+-----------------+-----------------+----------------------");
                }
            }
            while (resultSet.next()) {
                switch (queryNum) {
                    case 1 -> {
                        int managerId, count;
                        String firstName, lastName, email;
                        managerId = resultSet.getInt("manager_id");
                        firstName = resultSet.getString("first_name");
                        lastName = resultSet.getString("last_name");
                        email = resultSet.getString("email");
                        count = resultSet.getInt("count");
                        rows.add(String.format("%11d | %15s | %15s | %26s | %9d", managerId, firstName, lastName, email, count));
                    }
                    case 2, 3 -> {
                        int venueId, events, capacity;
                        String name = resultSet.getString("name");
                        venueId = resultSet.getInt("venue_id");
                        events = resultSet.getInt("events");
                        capacity = resultSet.getInt("capacity");

                        rows.add(String.format("%11d | %15s | %15d | %20d ", venueId, name, capacity, events));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Time: " + (end - start) + "ns");
        return rows;
    }
}
