package co.edu.hotel.configuracionservice.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;


    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;


    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = true;


    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.active = true;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(this.name);
    }

    public boolean isCustomer() {
        return "CUSTOMER".equals(this.name);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}