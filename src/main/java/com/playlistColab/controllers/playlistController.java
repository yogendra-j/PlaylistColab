package com.playlistColab.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class playlistController {
	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/sayHello")
	public String sayHello(@RequestParam(required = false) String name) {
		return String.format("Hello %s", name);
	}
}
