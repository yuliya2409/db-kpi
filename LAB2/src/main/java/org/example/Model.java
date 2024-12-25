package org.example;

import entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Model {
    private static SessionFactory factory;

    static
    {
        try {
            factory = new Configuration()
                    .addAnnotatedClass(Manager.class)
                    .configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private final Connection connection;

    public Model() throws SQLException, ClassNotFoundException {
        connection = connect();
    }

    public Connection connect() throws SQLException, ClassNotFoundException {
        /*Class.forName(DRIVER);

        Connection c = DriverManager.getConnection(URL, USERNAME, PASSWORD);*/
        return null;
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public void insert(String tableName, Map<String, String> columns) throws ParseException {
        Session session = factory.openSession();
        switch (tableName) {
            case "manager" -> {
                session.beginTransaction();
                session.persist(new Manager(Integer.parseInt(columns.get("manager_id")),
                        columns.get("first_name"),
                        columns.get("last_name"),
                        columns.get("email"),
                        columns.get("phone")));
                session.getTransaction().commit();
            }
            case "venue" -> {
                session.beginTransaction();
                session.persist(new Venue(Integer.parseInt(columns.get("venue_id")),
                        columns.get("name"),
                        columns.get("description"),
                        columns.get("website"),
                        Integer.parseInt(columns.get("capacity"))));
                session.getTransaction().commit();
            }
            case "event" -> {
                session.beginTransaction();
                session.persist(new Event(columns.get("name"),
                        new SimpleDateFormat("yyyy-MM-dd").parse(columns.get("date")),
                        Integer.parseInt(columns.get("entrance_fee")),
                        session.getReference(Manager.class, Integer.parseInt(columns.get("manager_id")))));

                session.getTransaction().commit();
            }
            case "events_venue" -> {
                session.beginTransaction();

                EventId eventPk = new EventId(columns.get("event_name"), new SimpleDateFormat("yyyy-MM-dd").parse(columns.get("event_date")));
                Event event = session.getReference(Event.class, eventPk);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

                session.persist(new EventsVenue(
                        event.getName(),
                        event.getDate(),
                        event,
                        session.getReference(Venue.class, Integer.parseInt(columns.get("venue_id"))),
                        new java.sql.Time(formatter.parse(columns.get("start")).getTime()),
                        new java.sql.Time(formatter.parse(columns.get("end")).getTime())
                ));

                session.getTransaction().commit();
            }
        }
    }

    public void update(String t, Map<String, String> pk, Map<String, String> c) throws ParseException {
        Session session = factory.openSession();
        switch (t) {
            case "manager" -> {
                session.beginTransaction();
                Manager manager = session.get(Manager.class, pk.get("manager_id"));
                manager.setFirstName(c.get("first_name"));
                manager.setLastName(c.get("last_name"));
                manager.setEmail(c.get("email"));
                manager.setPhone(c.get("phone"));
                session.persist(manager);
                session.getTransaction().commit();
            }
            case "venue" -> {
                session.beginTransaction();
                Venue venue = session.get(Venue.class, pk.get("venue_id"));
                venue.setCapacity(Integer.parseInt(c.get("capacity")));
                venue.setName(c.get("name"));
                venue.setDescription(c.get("description"));
                session.persist(venue);
                session.getTransaction().commit();
            }
            case "event" -> {
                session.beginTransaction();
                EventId id = new EventId(c.get("name"),  new SimpleDateFormat("yyyy-MM-dd").parse(c.get("event_date")));
                Event event = session.get(Event.class, id);
                Manager manager = session.get(Manager.class, c.get("manager_id"));
                event.setManager(manager);
                event.setEntranceFee(Integer.parseInt(c.get("entrance_fee")));
                session.getTransaction().commit();
            }
            case "events_venue" -> {
                session.beginTransaction();

                EventId eventPk = new EventId(c.get("event_name"), new SimpleDateFormat("yyyy-MM-dd").parse(c.get("event_date")));
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Venue venue = session.get(Venue.class, c.get("venue_id"));
                EventsVenue eventsVenue = session.get(EventsVenue.class, eventPk);

                eventsVenue.setVenue(venue);
                eventsVenue.setStart(new java.sql.Time(formatter.parse(c.get("start")).getTime()));
                eventsVenue.setEnd(new java.sql.Time(formatter.parse(c.get("end")).getTime()));

                session.getTransaction().commit();
            }
        }
    }

    public void delete(String t, Map<String, String> pk) throws ParseException {
        Session session = factory.openSession();
        switch (t) {
            case "manager" -> {
                session.beginTransaction();
                session.remove(session.get(Manager.class, pk.get("manager_id")));
                session.getTransaction().commit();
            }
            case "venue" -> {
                session.beginTransaction();
                session.remove(session.get(Manager.class, pk.get("venue_id")));
                session.getTransaction().commit();
            }
            case "event" -> {
                session.beginTransaction();
                EventId id = new EventId(pk.get("name"),  new SimpleDateFormat("yyyy-MM-dd")
                        .parse(pk.get("event_date")));
                Event event = session.get(Event.class, id);
                session.remove(event);
                session.getTransaction().commit();
            }
            case "events_venue" -> {
                session.beginTransaction();

                EventId eventPk = new EventId(pk.get("event_name"), new SimpleDateFormat("yyyy-MM-dd").parse(pk.get("event_date")));

                EventsVenue eventsVenue = session.get(EventsVenue.class, eventPk);
                session.remove(eventsVenue);

                session.getTransaction().commit();
            }
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
