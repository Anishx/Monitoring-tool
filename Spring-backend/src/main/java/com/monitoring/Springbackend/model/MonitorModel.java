package com.monitoring.Springbackend.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="registered_websites")
public class MonitorModel {

    public MonitorModel(String websiteName, String websiteUrl, long frequency) {
        this.websiteName = websiteName;
        this.websiteUrl = websiteUrl;
        this.frequency = frequency;
        this.active = active;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registeredWebsites_seq")
    @SequenceGenerator(name="registeredWebsites_seq", sequenceName="registeredWebsites_seq", allocationSize=1)
    private long id;

    @Column(name = "website_name")
    private String websiteName;

    @Column(name = "website_url",nullable = false)
    private String websiteUrl;

    @Column(name = "frequency",nullable = false)
    private long frequency ; //in seconds

    @Column(name = "active")
    private boolean active;

}
