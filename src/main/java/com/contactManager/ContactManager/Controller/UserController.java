package com.contactManager.ContactManager.Controller;

import com.contactManager.ContactManager.Entity.Contact;
import com.contactManager.ContactManager.Entity.User;
import com.contactManager.ContactManager.Repository.ContactRepo;
import com.contactManager.ContactManager.Repository.UserRepo;
import com.contactManager.ContactManager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ContactRepo contactRepo;

    //method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model,Principal principal){

        String userName = principal.getName();
        System.out.println("USERNAME"+userName);

        //get the using userName(Email)

        User user = userRepo.getUserByUserName(userName);
        System.out.println("USER"+user);

        model.addAttribute("user",user);
    }

    //dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        model.addAttribute("title","User DashBoard");


        return "user/user_dashboard";
    }

    //open add form handler

    @GetMapping("/add-contact")
    public String AddContactForm(Model model){

     model.addAttribute("title","Add Contact");
     model.addAttribute("contact",new Contact());

        return "user/add_contact_form";
    }

    //processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal, HttpSession session){

        try {
        String name = principal.getName();
        User user = this.userRepo.getUserByUserName(name);

        //processing and uploading file
            if (file.isEmpty()){
                //if the file is empty then try our message

                System.out.println("File is empty");
                contact.setImage("contacts.png");

            }else {
                //file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

               File saveFile = new ClassPathResource("static/image").getFile();

              Path path =  Paths.get(saveFile.getAbsoluteFile()+File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");
            }

        contact.setUser(user);

        user.getContacts().add(contact);

        this.userRepo.save(user);

        user.getContacts().add(contact);

        System.out.println("DATA"+contact);

        System.out.println("Added to the data base");

        //message success
            session.setAttribute("message",new Message("Your contact is added !! Add More","success"));


    } catch (Exception e){
            System.out.println("Error"+e.getMessage());
            e.printStackTrace();

            //message error
            session.setAttribute("message", new Message("Something went wrong !! Try Again","danger"));
        }

        return "user/add_contact_form";
    }

    // show contact handler
    //per page= 5[n]
    //current page = 0[page]
    @GetMapping("/show-contact/{page}")
    public String showContact(@PathVariable("page") Integer page, Model model,Principal principal){

        model.addAttribute("title","Show User Contacts");

        //contact list show the screen

        String userName = principal.getName();
        User user = this.userRepo.getUserByUserName(userName);

        //current page
        //Contact per page - 5
        Pageable pageable = PageRequest.of(page,3);

         Page<Contact> contacts = this.contactRepo.findContactByUser(user.getId(),pageable);

         model.addAttribute("contacts",contacts);
         model.addAttribute("currentPage",page);
         model.addAttribute("totalPages",contacts.getTotalPages());

        return "user/show_contact";
    }

    //showing particular contact details..

    @GetMapping("/contact/{cId}")
    public String showContactDetails(@PathVariable("cId") Integer cId,Model model,Principal principal){

        System.out.println("CID"+cId);

        Optional<Contact> contactOptional = this.contactRepo.findById(cId);
        Contact contact = contactOptional.get();

        //
        String userName = principal.getName();
        User user = this.userRepo.getUserByUserName(userName);

        if (user.getId()==contact.getUser().getId()){
            model.addAttribute("contact",contact);
            model.addAttribute("title",contact.getFirstName());
        }

        return "user/contact_details";
    }

    //delete contact handler

    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Model model, HttpSession session,
                                Principal principal ){
      System.out.println("CID"+cId);

      Contact contact = this.contactRepo.findById(cId).get();

      User user = this.userRepo.getUserByUserName(principal.getName());

      user.getContacts().remove(contact);
      this.userRepo.save(user);


        System.out.println("DELETED");
        session.setAttribute("message",new Message("Contact deleted successfully..","success"));
        return "redirect:/user/show-contact/0";
    }

    //update handler

    @PostMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId, Model model){

        model.addAttribute("title","Update Contact");

        Contact contact = this.contactRepo.findById(cId).get();

        model.addAttribute("contact",contact);
        return "user/update_form";
    }

    //update contact handler

    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
                                Model model,Principal principal,HttpSession session){

        try {
            //old contacts details
            Contact oldContactDetail = this.contactRepo.findById(contact.getcId()).get();

            //image
            if (!file.isEmpty()){

                //file work..
                //rewrite

                //delete old photo

                File deleteFile = new ClassPathResource("static/image").getFile();
                File file1 = new File(deleteFile,oldContactDetail.getImage());
                file1.delete();

                //update new photo

                File saveFile = new ClassPathResource("static/image").getFile();

                Path path =  Paths.get(saveFile.getAbsolutePath() + File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());

            } else {
                contact.setImage(oldContactDetail.getImage());
            }
            User user = this.userRepo.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepo.save(contact);

            session.setAttribute("message",new Message("your contact is updated..","success"));

        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("CONTACT FIRSTNAME"+contact.getFirstName());
        System.out.println("CONTACT LASTNAME"+contact.getLastName());
        System.out.println("CONTACT ID" +contact.getcId());

        return "redirect:/user/contact/"+contact.getcId();
    }

    // Your profile handler
    @GetMapping("/profile")
    public  String yourProfile(Model model){
        model.addAttribute("title","Profile_page");
        return "user/profile";
    }
}

