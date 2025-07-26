package com.urbancompany.addressBook.service;

import com.urbancompany.addressBook.model.Contact;
import com.urbancompany.addressBook.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public List<Contact> createContacts(List<Map<String, String>> contactRequests) {
        List<Contact> contacts = contactRequests.stream()
                .map(contactData -> new Contact(
                        contactData.get("name"),
                        contactData.get("phone"),
                        contactData.get("email")
                ))
                .collect(Collectors.toList());

        return contactRepository.saveAll(contacts);
    }

    public List<Contact> updateContacts(List<Map<String, String>> updateRequests) {
        return updateRequests.stream()
                .map(this::updateContact)
                .filter(contact -> contact != null)
                .collect(Collectors.toList());
    }

    private Contact updateContact(Map<String, String> updateData) {
        String id = updateData.get("id");
        if (id == null) {
            return null;
        }

        return contactRepository.findById(id)
                .map(existingContact -> {
                    String name = updateData.get("name");
                    String phone = updateData.get("phone");
                    String email = updateData.get("email");

                    if (name != null) {
                        existingContact.setName(name);
                    }
                    if (phone != null) {
                        existingContact.setPhone(phone);
                    }
                    if (email != null) {
                        existingContact.setEmail(email);
                    }
                    return contactRepository.save(existingContact);
                })
                .orElse(null);
    }

    public Map<String, Integer> deleteContacts(List<String> contactIds) {
        int deletedCount = contactRepository.deleteByIds(contactIds);
        Map<String, Integer> response = new HashMap<>();
        response.put("deleted", deletedCount);
        return response;
    }

    public List<Contact> searchContacts(Map<String, String> searchRequest) {
        String query = searchRequest.get("query");
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        Set<Contact> results = contactRepository.searchByQuery(query);
        return results.stream().collect(Collectors.toList());
    }
}
