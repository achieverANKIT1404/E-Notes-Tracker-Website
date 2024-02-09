package com.enotetracker.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.enotetracker.model.Notes;
import com.enotetracker.model.User;
import com.enotetracker.repository.UserRepository;
import com.enotetracker.service.NoteService;
import com.enotetracker.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private NoteService noteService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@ModelAttribute
	public User getUser(Principal p, Model m) { //used for getting dynamic user name in navbar
		
		String email = p.getName();
		
		User user = userRepo.findByEmail(email);
		
		m.addAttribute("user", user);
		
		return user;
	}
	
	@GetMapping("/add_notes")
	public String addnotes() {
		
		return "add_notes.html";
	}
	
	@GetMapping("/view_notes")
	public String viewnotes(Model m, Principal p,@RequestParam(defaultValue = "0") Integer pageno) {
		
		User user = getUser(p, m);
		
		Page<Notes>  notes = noteService.getNotesByUser(user,pageno);
		
		m.addAttribute("currentPage", pageno);
		
//		m.addAttribute("totalElements", notes.getTotalElements());
		
		m.addAttribute("totalPages", notes.getTotalPages());
		
		m.addAttribute("notesList", notes.getContent());
		
		return "view_notes.html";
	}
	
	@GetMapping("/edit_notes/{id}")
	public String editnotes(@PathVariable int id, Model m) {
		
		Notes notes = noteService.getNotesById(id);
		
		m.addAttribute("n", notes);
		
		return "edit_notes.html";
	}
	
	@GetMapping("/changpass")
    public String loadChangePassword() {
   	 
   	 return "change_password.html";
    }
	
	@PostMapping("/updatepass")
	public String changepassword(Principal p, @RequestParam("oldpass") String oldpass,
			@RequestParam("newpass") String newpass, HttpSession session) {
		
		String email = p.getName();
		
		User userlogin = userRepo.findByEmail(email);
		
		boolean f = passwordEncoder.matches(oldpass, userlogin.getPassword());
		
	if(f) {
			
			userlogin.setPassword(passwordEncoder.encode(newpass));
			
			User updatePassUser = userRepo.save(userlogin); //new updated password save in database
			
			if(updatePassUser!=null) {
				
				session.setAttribute("succMsg", "Password Changed...");
			}
			else {
				
				session.setAttribute("errorMsg", "Something Wrong!");
			}
		}
		else {
			
			session.setAttribute("errorMsg", "Incorrect Old Password!");
		}
		
		return "redirect:/user/changpass";
	}
	
	@PostMapping("/saveNotes")
	public String savenotes(@ModelAttribute Notes notes, HttpSession session, Principal p, Model m) {
		
		notes.setDate(LocalDate.now());
		
		notes.setUser(getUser(p, m));
		
		Notes saveNotes = noteService.saveNotes(notes);
		
		  if(saveNotes!=null) {
			   
			   session.setAttribute("msg", "Notes Added Succesfully");
		   }
		   else {
			   
			   session.setAttribute("war", "Something went wrong!");
		   }
		return "redirect:/user/add_notes";
	}
	
	@PostMapping("/updateNotes")
	public String updatenotes(@ModelAttribute Notes notes, HttpSession session, Principal p, Model m) {
		
		notes.setDate(LocalDate.now());
		
		notes.setUser(getUser(p, m));
		
		Notes saveNotes = noteService.saveNotes(notes);
		
		  if(saveNotes!=null) {
			   
			   session.setAttribute("msg", "Notes Updated Succesfully");
		   }
		   else {
			   
			   session.setAttribute("war", "Something went wrong!");
		   }
		return "redirect:/user/view_notes";
	}
	
	@GetMapping("/delete_notes/{id}")
	public String deletenotes(@PathVariable int id, Model m, HttpSession session) {
		
		boolean f = noteService.deleteNotes(id);
		
		if(f) {
			   
			   session.setAttribute("msg", "Notes Deleted Succesfully");
		   }
		   else {
			   
			   session.setAttribute("war", "Something went wrong!");
		   }
				
		return "redirect:/user/view_notes";
	}
	
	@GetMapping("/view_profile")
	public String viewprofile(Model m, Principal p) {
		
		String email = p.getName();
				
		User users = userRepo.findByEmail(email);
		
		m.addAttribute("p", users);
		
		return "view_profile.html";
	}
	
	@GetMapping("/edit_profile/{id}")
	public String editprofile(@PathVariable int id, Model m) {
		
		User user = userService.getUserById(id);
		
		m.addAttribute("us", user);
		
		return "edit_profile.html";
	}
	
	@PostMapping("/updateProfile")
	public String updateprofile(@ModelAttribute User users, HttpSession session, Principal p, Model m) {
				
          User profile = userService.saveUser(users);		
		  if(profile!=null) {
			   
			   session.setAttribute("msg", "Profile Updated Succesfully");
		   }
		   else {
			   
			   session.setAttribute("war", "Something went wrong!");
		   }
		return "redirect:/user/view_profile";
	}
}
