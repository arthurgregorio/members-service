package br.eti.arthurgregorio.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "members")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Member extends PersistentEntity {

    @Getter
    @Setter
    @Column(name = "name", length = 150, nullable = false)
    private String name;
    @Getter
    @Setter
    @Column(name = "salary", nullable = false)
    private BigDecimal salary;
    @Getter
    @Setter
    @Column(name = "contract_duration", length = 3)
    private Integer contractDuration;
    @Getter
    @Setter
    @Column(name = "role", length = 90)
    private String role;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false)
    private Type type;

    @Getter
    @Setter
    @ElementCollection
    @Column(name = "tags")
    private List<String> tags;

    @Getter
    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_country", nullable = false)
    private Country country;

    public void updateValues(Member updated) {
        this.name = updated.getName();
        this.salary = updated.getSalary();
        this.contractDuration = updated.getContractDuration();
        this.role = updated.getRole();
        this.tags = updated.getTags();
        this.country = updated.getCountry();
    }

    public enum Type {
        CONTRACTOR,
        EMPLOYEE
    }
}
