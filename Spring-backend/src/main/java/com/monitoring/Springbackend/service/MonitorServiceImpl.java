package com.monitoring.Springbackend.service;

import com.monitoring.Springbackend.exception.ResourceNotFoundException;
import com.monitoring.Springbackend.model.MonitorModel;
import com.monitoring.Springbackend.model.WebsiteStats;
import com.monitoring.Springbackend.repository.MonitorRepository;
import com.monitoring.Springbackend.repository.WebsiteStatsRepository;
import com.monitoring.Springbackend.utils.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService{

    private static final Logger LOG = LoggerFactory.getLogger(MonitorServiceImpl.class);

    @Autowired
    ScheduledExecutorService scheduledExecutorService;

    @Autowired
    WebsiteStatsRepository websiteStatsRepository;

    @Autowired
    private MonitorRepository monitorRepository;

    private Map<String, ScheduledFuture<?>> listOfScheduledJobs;

    @PostConstruct
    public void init() throws Exception{
        listOfScheduledJobs = new HashMap<String, ScheduledFuture<?>>();
        //Schedule Jobs
        List<MonitorModel> monitorModelList = monitorRepository.findByActive();
        monitorModelList.stream().forEach(monitorModel -> {
            scheduleJob(monitorModel.getId(), monitorModel.getFrequency(),monitorModel.getWebsiteUrl());
        });
    }

    @Override
    public ResponseEntity<?> saveChecks(MonitorModel newMonitorCheck) {
        newMonitorCheck.setWebsiteUrl(Utilities.formatURL(newMonitorCheck.getWebsiteUrl())); // setting common url format
        MonitorModel monitorModel = monitorRepository.save(newMonitorCheck);
        //schedule
        scheduleJob(monitorModel.getId(), monitorModel.getFrequency(),monitorModel.getWebsiteUrl());
        return new ResponseEntity<>("Added & Scheduled", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<MonitorModel>> getAllChecks() {
       return new ResponseEntity<>(monitorRepository.findAll(),HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<?> getChecksByName(String websiteName) {
        List<MonitorModel> modelsByName =  monitorRepository.findByWebsiteName(websiteName);
        if(!modelsByName.isEmpty())
            return new ResponseEntity<List<MonitorModel>>(modelsByName,HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>("We have no records for these filters",HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getChecksByFrequency(long frequency){
        List<MonitorModel> modelsByFrequency =  monitorRepository.findByFrequency(frequency);
        if(!modelsByFrequency.isEmpty())
            return new ResponseEntity<List<MonitorModel>>(modelsByFrequency,HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>("We have no records for these filters",HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> updateChecks(Long id, MonitorModel monitorModel) {
        MonitorModel fetchedModel = monitorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No records found for this search, try changing search parameters"));
        if(StringUtils.hasLength(monitorModel.getWebsiteName()))
            fetchedModel.setWebsiteName(monitorModel.getWebsiteName());
        if(StringUtils.hasLength(monitorModel.getWebsiteUrl()))
            fetchedModel.setWebsiteUrl(monitorModel.getWebsiteUrl());
        if(monitorModel.getFrequency()!=0)
            fetchedModel.setFrequency(monitorModel.getFrequency());
        fetchedModel.setActive(monitorModel.isActive());

        if(monitorModel.isActive() && !fetchedModel.isActive()){
            scheduleJob(monitorModel.getId(),monitorModel.getFrequency(),monitorModel.getWebsiteUrl());
        }else{
            cancelScheduledJob(Utilities.formatURL(monitorModel.getWebsiteUrl()));
        }
        monitorRepository.save(fetchedModel);

        return new ResponseEntity<>("Record updated successfully!",HttpStatus.OK);
    }

    public void cancelScheduledJob(String formattedUrl){
        ScheduledFuture<?> scheduledFuture = listOfScheduledJobs.get(formattedUrl);
        boolean status = scheduledFuture.cancel(true);
        if(status) listOfScheduledJobs.remove(formattedUrl);
    }

    public void scheduleJob(long id, long frequency, String url) {
        double minInHr = 60;
        TimeUnit timeUnit = TimeUnit.MINUTES; //default unit
        if(frequency>=minInHr){  //convert to hours if frequency is more than 60
            frequency = Math.round(frequency/minInHr);
            timeUnit = TimeUnit.HOURS; //Convert to Hours
        }
        ScheduledFuture<?> newJob = runJobScheduler(id, url, frequency, timeUnit);

    }

    private ScheduledFuture<?> runJobScheduler(long id, String url, long frequency, TimeUnit timeUnit){
        Runnable runner = ()->{
            WebsiteStats websiteStats = new WebsiteStats();
            websiteStats.setMonitorId(id);
            websiteStats.setWebsiteUrl(url);
            ping(websiteStats);
            websiteStatsRepository.save(websiteStats);
        };
        ScheduledFuture<?> scheduledFutureJob = scheduledExecutorService.scheduleAtFixedRate(runner, 0, frequency, timeUnit);
        return scheduledFutureJob;
    }

    public void ping(WebsiteStats websiteStats) {
        try(Socket socket = new Socket()) {
            long start = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(websiteStats.getWebsiteUrl(), 80), 100);
            long end = System.currentTimeMillis();

            websiteStats.setResponseTme(end - start);
            websiteStats.setUptime(Utilities.formatDate(end));
            websiteStats.setStatus("UP");
            LOG.info("Response received from url:"+websiteStats.getWebsiteUrl());
            // You can determine on HTTP return code received. 200 is success.
        } catch (IOException e) {
            websiteStats.setUptime(Utilities.formatDate(System.currentTimeMillis()));
            websiteStats.setStatus("DOWN");
            e.printStackTrace();
            LOG.error("URL:"+websiteStats.getWebsiteUrl()+" is not reachable");
        } finally {
            LOG.info("Ping completed");
        }
    }
}
