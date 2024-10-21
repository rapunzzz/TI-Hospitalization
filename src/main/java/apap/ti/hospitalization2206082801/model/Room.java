package apap.ti.hospitalization2206082801.model;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room")
@SQLDelete(sql = "UPDATE room SET deleted_at = NOW() WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class Room {
    @Id
    private String id;

    @NotNull
    @Size(max = 30)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Deskripsi room tidak boleh kosong")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull
    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @NotNull
    @Column(name = "price_per_day", nullable = false)
    private double pricePerDay;

    @OneToMany(mappedBy = "room")
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
