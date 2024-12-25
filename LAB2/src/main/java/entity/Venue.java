package entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "venue")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Venue {

    @Id
    @Column(name = "venue_id")
    private int venueId;

    private String name;

    private String description;

    private String website;

    private int capacity;
}
