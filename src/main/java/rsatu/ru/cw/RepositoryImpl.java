package rsatu.ru.cw;

import java.util.*;

class RepositoryImpl<T> implements Repository<T> {
    private final Map<Integer, T> storage = new HashMap<>();
    private int nextId = 1;

    @Override
    public void save(T entity) {
        storage.put(nextId++, entity);
    }

    @Override
    public Optional<T> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }
}

