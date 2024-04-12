package dao;

import java.util.List;
import java.util.Optional;

public interface CRUD<K, E> {
    List<E> findAll();
    Optional<E> findById(K id);
    void update(E entity);
    boolean delete(K id);
    E save(E entity);


}
