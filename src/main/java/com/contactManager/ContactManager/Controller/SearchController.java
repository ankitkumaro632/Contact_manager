/*
package com.contactManager.ContactManager.Controller;

import com.contactManager.ContactManager.Entity.Contact;
import com.contactManager.ContactManager.Entity.User;
import com.contactManager.ContactManager.Repository.ContactRepo;
import com.contactManager.ContactManager.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ContactRepo contactRepo;

    */
/*search handler*//*

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){

        System.out.println(query);

        User user =  this.userRepo.getUserByUserName(principal.getName());

        List<Contact> contacts = this.contactRepo.findByNameContainingAndUser(query,user);

        return ResponseEntity.ok(contacts);
    }
}
*/
