package it.ddlsolution.ruota.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
public class VersioneController {

    @GetMapping(value = "/versione", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getVersion() {
        return "CIAO";
    }

}