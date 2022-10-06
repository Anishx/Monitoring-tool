package com.monitoring.Springbackend.repository;

import com.monitoring.Springbackend.model.MonitorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MonitorRepository extends JpaRepository<MonitorModel, Long> {

    @Query("SELECT m FROM MonitorModel m WHERE m.websiteName=?1 ")
    List<MonitorModel> findByWebsiteName(String websiteName);

    @Query("SELECT r FROM MonitorModel r WHERE r.frequency=?1 ")
    List<MonitorModel> findByFrequency(Long frequency);

    @Query("SELECT r FROM MonitorModel r WHERE r.active=true")
    List<MonitorModel> findByActive();

}
