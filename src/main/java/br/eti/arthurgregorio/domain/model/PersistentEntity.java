package br.eti.arthurgregorio.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "externalId"})
@ToString(of = {"id", "externalId", "version"})
public abstract class PersistentEntity {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true)
    private Long id;
    @Getter
    @Setter
    @Column(name = "external_id", length = 36, updatable = false, unique = true)
    private UUID externalId;
    @Getter
    @LastModifiedDate
    @Column(name = "last_update")
    private Instant lastUpdate;

    @Version
    private short version;

    @PrePersist
    protected void onPersist() {
        if (this.externalId == null) {
            this.externalId = UUID.randomUUID();
        }
    }

    public boolean isSaved() {
        return this.id != null && this.externalId != null;
    }
}
