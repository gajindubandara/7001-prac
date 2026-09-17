package com.iwfc.repository;

import com.iwfc.exceptions.DuplicateDataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Repository<T> {

    private final Map<String, T> items;
    private final Function<T, String> idExtractor;

    public Repository(Function<T, String> idExtractor) {
        this.idExtractor = Objects.requireNonNull(idExtractor, "idExtractor must not be null");
        this.items = new LinkedHashMap<>();
    }

    public void add(T item) throws DuplicateDataException {
        Objects.requireNonNull(item, "item must not be null");
        String id = idExtractor.apply(item);
        if (items.containsKey(id)) {
            throw new DuplicateDataException("Duplicate id: " + id);
        }
        items.put(id, item);
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }

    public boolean existsById(String id) {
        return items.containsKey(id);
    }

    public boolean removeById(String id) {
        return items.remove(id) != null;
    }

    public int count() {
        return items.size();
    }
}
