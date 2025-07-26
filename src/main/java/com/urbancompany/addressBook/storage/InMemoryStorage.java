package com.urbancompany.addressBook.storage;

import com.urbancompany.addressBook.model.Contact;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStorage {
    private final Map<String, Contact> contactsById = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> nameIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> phoneIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> emailIndex = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> normalizedNameIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> normalizedEmailIndex = new ConcurrentHashMap<>();

    public Contact save(Contact contact) {
        String id = contact.getId();
        Contact existingContact = contactsById.get(id);

        if (existingContact != null) {
            removeFromIndexes(existingContact);
        }

        contactsById.put(id, contact);

        addToIndexes(contact);

        return contact;
    }

    public Optional<Contact> findById(String id) {
        return Optional.ofNullable(contactsById.get(id));
    }

    public boolean deleteById(String id) {
        Contact contact = contactsById.remove(id);
        if (contact != null) {
            removeFromIndexes(contact);
            return true;
        }
        return false;
    }

    public Set<Contact> searchByQuery(String query) {
        Set<Contact> results = new HashSet<>();
        String normalizedQuery = normalizeString(query);

        if (contactsById.containsKey(query)) {
            results.add(contactsById.get(query));
        }

        Set<String> phoneMatches = phoneIndex.get(query);
        if (phoneMatches != null) {
            phoneMatches.forEach(id -> results.add(contactsById.get(id)));
        }

        searchInIndex(normalizedNameIndex, normalizedQuery, results);

        searchInIndex(normalizedEmailIndex, normalizedQuery, results);

        return results;
    }

    public Collection<Contact> findAll() {
        return contactsById.values();
    }

    public int size() {
        return contactsById.size();
    }

    private void removeFromIndexes(Contact contact) {
        String id = contact.getId();

        if (contact.getName() != null) {
            removeFromIndex(nameIndex, contact.getName(), id);
            removeFromIndex(normalizedNameIndex, normalizeString(contact.getName()), id);
        }

        if (contact.getPhone() != null) {
            removeFromIndex(phoneIndex, contact.getPhone(), id);
        }

        if (contact.getEmail() != null) {
            removeFromIndex(emailIndex, contact.getEmail(), id);
            removeFromIndex(normalizedEmailIndex, normalizeString(contact.getEmail()), id);
        }
    }

    private void addToIndexes(Contact contact) {
        String id = contact.getId();

        if (contact.getName() != null) {
            addToIndex(nameIndex, contact.getName(), id);
            addToIndex(normalizedNameIndex, normalizeString(contact.getName()), id);
        }

        if (contact.getPhone() != null) {
            addToIndex(phoneIndex, contact.getPhone(), id);
        }

        if (contact.getEmail() != null) {
            addToIndex(emailIndex, contact.getEmail(), id);
            addToIndex(normalizedEmailIndex, normalizeString(contact.getEmail()), id);
        }
    }

    private void addToIndex(Map<String, Set<String>> index, String key, String contactId) {
        index.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(contactId);
    }

    private void removeFromIndex(Map<String, Set<String>> index, String key, String contactId) {
        Set<String> ids = index.get(key);
        if (ids != null) {
            ids.remove(contactId);
            if (ids.isEmpty()) {
                index.remove(key);
            }
        }
    }

    private void searchInIndex(Map<String, Set<String>> index, String query, Set<Contact> results) {
        index.entrySet().stream()
                .filter(entry -> entry.getKey().contains(query))
                .flatMap(entry -> entry.getValue().stream())
                .forEach(id -> {
                    Contact contact = contactsById.get(id);
                    if (contact != null) {
                        results.add(contact);
                    }
                });
    }

    private String normalizeString(String str) {
        return str == null ? "" : str.toLowerCase().trim();
    }
}