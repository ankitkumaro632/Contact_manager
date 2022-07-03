package com.contactManager.ContactManager.Controller;

import com.contactManager.ContactManager.Entity.User;
import com.contactManager.ContactManager.Repository.UserRepo;
import com.contactManager.ContactManager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    //this handler from home page

    @RequestMapping("/")
  public String home(Model m){
        m.addAttribute("title","Home- Contact Management");
      return "home";
  }

  // this handler from about page

    @RequestMapping("/about")
    public String about(Model m){
        m.addAttribute("title","About- Contact Management");
        return "about";
    }

    // this handler from signup page

    @RequestMapping("/signup")
    public String signup(Model m){
        m.addAttribute("title","Sign-Up Page");
        m.addAttribute("user",new User());
        return "signup";
    }

    // this handler from registering user
    @RequestMapping(value = "/do_register",method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
                               @RequestParam(value="agreement",defaultValue="false") boolean agreement,
                               Model m, HttpSession session){



      try {
          if (!agreement){
              System.out.println("Please agreed the terms & conditions ");
              throw new Exception("Please agreed the terms & conditions ");
          }

          if (bindingResult.hasErrors()){
              System.out.println("ERROR"+bindingResult.toString());
              m.addAttribute("user",user);
              return "signup";
          }

          user.setRole("ROLE_USER");
          user.setEnabled(true);
          user.setImageUrl("default.png");
          user.setPassword(passwordEncoder.encode(user.getPassword()));

          System.out.println("Agreement "+agreement);
          System.out.println("USER"+user);

          User result = this.userRepo.save(user);


          m.addAttribute("user",new User());

          session.setAttribute("message",new Message(" Successfully Registered !!","alert-success"));

          return "signup";

      } catch (Exception e){
          e.printStackTrace();
          m.addAttribute("user",user);
          session.setAttribute("message",new Message("Something Went Wrong !!"+e.getMessage(),"alert-danger"));
          return "signup";
      }

    }
    // handler for login page
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("title","Login page");
        return "login";
    }
}
