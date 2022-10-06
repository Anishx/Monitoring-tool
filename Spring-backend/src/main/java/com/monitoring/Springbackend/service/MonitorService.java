package com.monitoring.Springbackend.service;

import com.monitoring.Springbackend.model.MonitorModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MonitorService {

    public ResponseEntity<?> saveChecks(MonitorModel newMonitorCheck);

    public ResponseEntity<List<MonitorModel>> getAllChecks();

    public ResponseEntity<?> getChecksByName(String websiteName);
    public ResponseEntity<?> getChecksByFrequency(long frequency);

    public ResponseEntity<?> updateChecks(Long id, MonitorModel monitorModel);

}
