package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping({ "/", "/index" })
	public String index() {
		return "index";
	}

	@GetMapping("/account")
	public String account() {
		return "account";
	}

	@GetMapping("/account/liked")
	public String likedRecipes() {
		return "account-liked";
	}

	@GetMapping("/account/tagged")
	public String taggedRecipes() {
		return "account-tagged";
	}
}
