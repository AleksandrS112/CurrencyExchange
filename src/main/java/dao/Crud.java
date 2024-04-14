package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Crud<K, E> {
    List<E> findAll();
    Optional<E> findById(K id);
    boolean update(E entity);
    boolean delete(K id);
    E save(E entity) throws SQLException;


}
