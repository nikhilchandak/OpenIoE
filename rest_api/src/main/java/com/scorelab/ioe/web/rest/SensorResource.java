package com.scorelab.ioe.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.scorelab.ioe.domain.Sensor;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Sensor.
 */
@RestController
@RequestMapping("/api")
public class SensorResource {

    private final Logger log = LoggerFactory.getLogger(SensorResource.class);
        
    @Inject
    private SensorRepository sensorRepository;
    
    /**
     * POST  /sensors : Create a new sensor.
     *
     * @param sensor the sensor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sensor, or with status 400 (Bad Request) if the sensor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensors",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sensor> createSensor(@Valid @RequestBody Sensor sensor) throws URISyntaxException {
        log.debug("REST request to save Sensor : {}", sensor);
        if (sensor.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sensor", "idexists", "A new sensor cannot already have an ID")).body(null);
        }
        Sensor result = sensorRepository.save(sensor);
        return ResponseEntity.created(new URI("/api/sensors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sensor", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sensors : Updates an existing sensor.
     *
     * @param sensor the sensor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sensor,
     * or with status 400 (Bad Request) if the sensor is not valid,
     * or with status 500 (Internal Server Error) if the sensor couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sensor> updateSensor(@Valid @RequestBody Sensor sensor) throws URISyntaxException {
        log.debug("REST request to update Sensor : {}", sensor);
        if (sensor.getId() == null) {
            return createSensor(sensor);
        }
        Sensor result = sensorRepository.save(sensor);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sensor", sensor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sensors : get all the sensors.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of sensors in body
     */
    @RequestMapping(value = "/sensors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Sensor> getAllSensors() {
        log.debug("REST request to get all Sensors");
        List<Sensor> sensors = sensorRepository.findAll();
        return sensors;
    }

    /**
     * GET  /sensors/:id : get the "id" sensor.
     *
     * @param id the id of the sensor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sensor, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sensors/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sensor> getSensor(@PathVariable Long id) {
        log.debug("REST request to get Sensor : {}", id);
        Sensor sensor = sensorRepository.findOne(id);
        return Optional.ofNullable(sensor)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sensors/:id : delete the "id" sensor.
     *
     * @param id the id of the sensor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sensors/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        log.debug("REST request to delete Sensor : {}", id);
        sensorRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sensor", id.toString())).build();
    }

}
