package com.becoder.controller;

import com.becoder.entity.Notes;
import com.becoder.repository.UserRepository;
import com.becoder.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.becoder.entity.User;
import com.becoder.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String index() {

		return "index";
	}


	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, HttpSession session) {

		boolean f = userService.existEmailCheck(user.getEmail());

		if (f) {
			session.setAttribute("msg", "Email already exist");
		} else {
			User saveUser = userService.saveUser(user);
			if (saveUser != null) {
				session.setAttribute("msg", "Register success");
			} else {
				session.setAttribute("msg", "Something wrong on server");
			}
		}

		return "redirect:/register";
	}


//	replace this signin with login
	@GetMapping("/signin")
	public String login() {

		return "login";
	}

	@GetMapping("/viewNotes")
	public String viewNotes(){
		return "view_notes";
	}

	@GetMapping("/editNotes")
	public String editNotes(){
		return "edit_notes";
	}




}









