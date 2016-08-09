package com.ds.ws.health.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	
	@RequestMapping("/")
	public String redirectHome() {
		return "redirect:html/index.html";
	}

}
