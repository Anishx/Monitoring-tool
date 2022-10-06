package com.monitoring.Springbackend.controller;

import com.monitoring.Springbackend.model.MonitorModel;
import com.monitoring.Springbackend.repository.MonitorRepository;
import com.monitoring.Springbackend.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monitors")
public class MonitorController {

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    MonitorService monitorService;

    @PostMapping
    public ResponseEntity<?> saveChecks(@RequestBody MonitorModel newMonitorCheck){
        return monitorService.saveChecks(newMonitorCheck);
        //new ResponseEntity<>("Added to the database",HttpStatus.OK)
    }

    @GetMapping
    public ResponseEntity<List<MonitorModel>> getAllChecks(){
        return monitorService.getAllChecks();
    }

    @GetMapping("/name/{websiteName}")
    public ResponseEntity<?> getChecksByName(@PathVariable String websiteName){
        return monitorService.getChecksByName(websiteName);
    }
    @GetMapping("/frequency/{frequency}")
    public ResponseEntity<?> getChecksByFrequency(@PathVariable long frequency){
        return monitorService.getChecksByFrequency(frequency);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChecks(@PathVariable Long id, @RequestBody MonitorModel monitorModel){
       return monitorService.updateChecks(id, monitorModel);
    }
}
