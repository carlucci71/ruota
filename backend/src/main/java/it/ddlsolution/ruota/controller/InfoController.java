package it.ddlsolution.ruota.controller;

import it.ddlsolution.ruota.dto.Tabellone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class InfoController {

    @Autowired
    List<Tabellone> tabelloni;

    @GetMapping(value = "/info")
    public ResponseEntity<Map> getInfo() {
        return ResponseEntity.ok(
                Map.of("Tabelloni", tabelloni.size()
                        ,"Tabellone",tabelloni.get(3).getTitolo()
                )
        );
    }

}