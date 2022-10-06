package com.monitoring.Springbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "website_stats")
public class WebsiteStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registeredWebsites_seq")
    @SequenceGenerator(name="registeredWebsites_seq", sequenceName="registeredWebsites_seq", allocationSize=1)
    private long id;

    @Column(name = "monitor_id")
    private long monitorId;

    @Column(name = "status")
    private String status; //up or down - true or false

    @Column(name = "website_url",nullable = false)
    private String websiteUrl;

    @Column(name = "uptime",nullable = false)
    private Timestamp uptime ;

    @Column(name = "downtime",nullable = false)
    private Timestamp downtime ;

    @Column(name = "response_time")
    private double responseTme;
}
