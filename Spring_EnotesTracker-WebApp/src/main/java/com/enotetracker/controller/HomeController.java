package com.enotetracker.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.enotetracker.model.User;
import com.enotetracker.repository.UserRepository;
import com.enotetracker.service.UserService;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncode;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/")
	public String index() {
		
		return "index.html";
	}
	
	@GetMapping("/register")
	public String register() {
		
		return "register.html";
	}
	
	@PostMapping("/saveUser")
	public String saveuser(@ModelAttribute User user, HttpSession session, HttpServletRequest request) {
			
		  String url = request.getRequestURL().toString();
		  
		  url = url.replace(request.getServletPath(), "");

		   boolean f = userService.checkEmail(user.getEmail());
		   		   
		   if(f) {
				
				session.setAttribute("war", "Email already exists!");
			}
			else {
				
				   User users = userService.saveUser(user,url);

				   if(users!=null) {
					   
					   session.setAttribute("msg", "Registered Succesfully");
				   }
				   else {
					   
					   session.setAttribute("war", "Something went wrong!");
				   }
			}
		   
			return "redirect:/register";
		}
	
	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code) {
		
		if(userService.verifyAccount(code)) {
			
			return "verify_success.html";
		}
		else {
			
			return "verify_failed.html";
		}
	}
	
	@GetMapping("/signin")
	public String login() {
		
		return "login.html";
	}
	
	@GetMapping("/logout")
	public String logout() {
		
		return "login.html";
	}
	
	@GetMapping("/loadforgotpass")
	public String loadForgotPassword() {
		
		return "forgot_password.html";
	}
	
	@GetMapping("/loadresetpass/{id}") //id used for checking particular user email or mobile no
    public String loadResetPassword(@PathVariable int id, Model m) {
		
		m.addAttribute("id", id);
		
		return "reset_password.html";
	}
	
	@PostMapping("/forgotpass")
	public String forgotpassword(@RequestParam String email, @RequestParam String mobile,
			HttpSession session) {
		
		User user = userRepo.findByEmailAndMobile(email, mobile);
		
		if(user!=null) {
			
			return "redirect:/loadresetpass/"+user.getId();
		}
		else {
			
			session.setAttribute("errorMsg","Invalid email or mobile no!");

			return "forgot_password.html";
		}
   }
	
	@PostMapping("/changepass")
	public String resetpassword(@RequestParam String psw, @RequestParam Integer id,
			HttpSession session) {
		
		User user = userRepo.findById(id).get();
		
		String ecryptPsw = passwordEncode.encode(psw); //used to encrypt new password
		
		user.setPassword(ecryptPsw); //used encrypt set password
		
		User updatedPass = userRepo.save(user); //returns new updated password and save in database
		
		if(updatedPass!=null) {
			
			session.setAttribute("succMsg", "Password updated successfully..");
		}
		
		return "redirect:/loadforgotpass";
	}
}
