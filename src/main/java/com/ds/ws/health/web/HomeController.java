package com.ds.ws.health.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
final class HomeController {
	
	@RequestMapping("/")
	private String redirectHome() {
		return "redirect:html/index.html";
	}

}
