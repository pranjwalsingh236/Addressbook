package com.urbancompany.addressBook.repository;

import com.urbancompany.addressBook.model.Contact;
import com.urbancompany.addressBook.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ContactRepository {

    @Autowired
    private InMemoryStorage storage;

    public Contact save(Contact contact) {
        return storage.save(contact);
    }

    public List<Contact> saveAll(List<Contact> contacts) {
        return contacts.stream()
                .map(storage::save)
                .collect(Collectors.toList());
    }

    public Optional<Contact> findById(String id) {
        return storage.findById(id);
    }

    public boolean deleteById(String id) {
        return storage.deleteById(id);
    }

    public int deleteByIds(List<String> ids) {
        return (int) ids.stream()
                .mapToLong(id -> storage.deleteById(id) ? 1 : 0)
                .sum();
    }

    public Set<Contact> searchByQuery(String query) {
        return storage.searchByQuery(query);
    }

    public Collection<Contact> findAll() {
        return storage.findAll();
    }

    public int count() {
        return storage.size();
    }
}
