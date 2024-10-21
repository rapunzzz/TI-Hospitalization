package apap.ti.hospitalization2206082801.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patient")
@SQLDelete(sql = "UPDATE patient SET is_deleted = NOW() WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class Patient {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String NIK;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Date birthDate;

    @Column(nullable = false)
    private boolean gender;

    @OneToMany(mappedBy = "patient")
    @JsonBackReference
    private List<Reservation> reservations;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;
}