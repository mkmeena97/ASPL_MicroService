package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.PanaltyService;

@RestController
@RequestMapping("panalty")
public class PanaltyController {

	@Autowired
	private PanaltyService panaltyService;
	
    @GetMapping("/checkPenalties/{userId}")
    public ResponseEntity<Double> checkPenalties(@PathVariable String userId) {
        return panaltyService.checkPenalties(userId);
    }
}
