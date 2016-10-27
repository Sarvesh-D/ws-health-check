package com.ds.ws.health.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home Controller.
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
@Controller
final class HomeController {
	
	@RequestMapping("/")
	private String redirectHome() {
		return "redirect:html/index.html";
	}

}
