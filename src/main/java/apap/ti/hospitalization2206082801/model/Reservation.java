package apap.ti.hospitalization2206082801.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
@SQLDelete(sql = "UPDATE reservation SET is_deleted = NOW() WHERE id=?")
public class Reservation {
    @Id
    private String id;

    @Column(nullable = false)
    private Date dateIn;

    @Column(nullable = false)
    private Date dateOut;

    @Column(nullable = false)
    private double totalFee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonManagedReference
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_nurse_id", nullable = false)
    @JsonManagedReference
    private Nurse assignedNurse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonManagedReference
    private Room room;

    @ManyToMany
    @JoinTable(
        name = "reservation_facility",
        joinColumns = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    @JsonManagedReference
    private List<Facility> facilities;

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