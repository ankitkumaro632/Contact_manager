package com.contactManager.ContactManager.Repository;

import com.contactManager.ContactManager.Entity.Contact;
import com.contactManager.ContactManager.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepo extends JpaRepository<Contact,Integer> {
    //pagination method...

    @Query("from Contact as c where c.user.id =:userId")
    //current page
    //Contact per page - 5
    public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);


    //searching
    /*public List<Contact> findByNameContainingAndUser(String name, User user);*/

}
