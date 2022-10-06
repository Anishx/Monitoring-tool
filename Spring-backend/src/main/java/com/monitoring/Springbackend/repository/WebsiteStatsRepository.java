package com.monitoring.Springbackend.repository;

import com.monitoring.Springbackend.model.WebsiteStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteStatsRepository extends JpaRepository<WebsiteStats, Long> {

}
