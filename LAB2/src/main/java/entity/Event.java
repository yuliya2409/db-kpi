package entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "event")
@IdClass(EventId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Event {

    @Id
    private String name;

    @Id
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "entrance_fee")
    private int entranceFee;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

}


