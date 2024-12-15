package com.tsad.web.backend.repository.webservicedb.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "master_level")
@Data
public class MasterLevelJpaEntity {

    @Id
    @Column(name = "priority")
    private Integer priority;

    @Column(name = "name")
    private String name;
}
