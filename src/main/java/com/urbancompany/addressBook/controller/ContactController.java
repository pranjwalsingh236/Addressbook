package com.urbancompany.addressBook.controller;

import com.urbancompany.addressBook.model.Contact;
import com.urbancompany.addressBook.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Contact>> createContacts(@RequestBody List<Map<String, String>> contactRequests) {
        List<Contact> createdContacts = contactService.createContacts(contactRequests);
        return ResponseEntity.ok(createdContacts);
    }

    @PutMapping(value = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Contact>> updateContacts(@RequestBody List<Map<String, String>> updateRequests) {
        List<Contact> updatedContacts = contactService.updateContacts(updateRequests);
        return ResponseEntity.ok(updatedContacts);
    }

    @DeleteMapping(value = "/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Integer>> deleteContacts(@RequestBody List<String> contactIds) {
        Map<String, Integer> response = contactService.deleteContacts(contactIds);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Contact>> searchContacts(@RequestBody Map<String, String> searchRequest) {
        List<Contact> results = contactService.searchContacts(searchRequest);
        return ResponseEntity.ok(results);
    }
}
