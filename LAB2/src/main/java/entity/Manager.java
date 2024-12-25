package entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Manager {

    @Id
    @Column(name = "manager_id")
    private int managerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;


    private String phone;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Event> events = null;

    public Manager(int managerId, String firstName, String lastName, String email, String phone) {
        this(managerId, firstName, lastName, email, phone, null);
    }
}
