package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "events_venue")
@IdClass(EventId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventsVenue {

    @Id
    @Column(name = "event_name")
    private String name;

    @Id
    @Column(name = "event_date")
    private Date date;

    @OneToOne
    private Event event;

    @OneToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Temporal(TemporalType.TIME)
    private Time start;

    @Temporal(TemporalType.TIME)
    @Column(name = "\"end\"")
    private Time end;
}
