package dao;

import dto.CurrencyFilter;
import exception.RespException;
import model.CurrencyEntity;
import util.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dao.PSQLState.*;
import static java.util.stream.Collectors.joining;

public class CurrencyDao implements Crud<Integer, CurrencyEntity> {

    private static final String DELETE_SQL = """
            DELETE FROM currencies
             WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO currencies (code, full_name, sign)
            VALUES (?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE currencies
               SET code = ?,
                   full_name = ?,
                   sign = ?
             WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id,
                   code,
                   full_name,
                   sign
              FROM currencies
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String FIND_BY_CODE = FIND_ALL_SQL + """
            WHERE code = ?
            """;

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    public List<CurrencyEntity> findAll(CurrencyFilter currencyFilter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (currencyFilter.code() != null) {
            whereSql.add("code = ?");
            parameters.add(currencyFilter.code());
        }
        if (currencyFilter.full_name() != null) {
            whereSql.add("full_name LIKE ?");
            parameters.add("%" + currencyFilter.full_name() + "%");
        }
        if (currencyFilter.sign() != null) {
            whereSql.add("sign = ?");
            parameters.add(currencyFilter.sign());
        }
        String whereFlag = whereSql.isEmpty() ? "" : " WHERE ";
        parameters.add(currencyFilter.limit());
        parameters.add(currencyFilter.offset());
        var where = whereSql.stream().collect(joining(" AND ", whereFlag, " LIMIT ? OFFSET ? "));
        String sql = FIND_ALL_SQL + where;
        System.out.println(sql);
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                prepareStatement.setObject(i+1, parameters.get(i));
            }
            System.out.println(prepareStatement);
            var resultSet = prepareStatement.executeQuery();
            List<CurrencyEntity> listCurrencyEntities = new ArrayList<>();
            while (resultSet.next()) {
                listCurrencyEntities.add(bildCurrencyEntity(resultSet));
            }
            return listCurrencyEntities;
        } catch (SQLException throwables) {
            throw new RespException(500, "База данных недоступна");
        }
    }

    public List<CurrencyEntity> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            List<CurrencyEntity> listCurrencyEntities = new ArrayList<>();
            while (resultSet.next()) {
                listCurrencyEntities.add(bildCurrencyEntity(resultSet));
            }
            return listCurrencyEntities;
        } catch (SQLException throwables) {
            throw new RespException(500, "База данных недоступна");
        }
    }

    public Optional<CurrencyEntity> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);
            var resultSet = prepareStatement.executeQuery();
            CurrencyEntity currencyEntity = null;
            if(resultSet.next()) {
                currencyEntity = bildCurrencyEntity(resultSet);
            }
            return Optional.ofNullable(currencyEntity);
        } catch (SQLException sqlException) {
            throw new RespException(500, "База данных недоступна");
        }
    }

    public boolean update(CurrencyEntity currencyEntity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setString(1, currencyEntity.getCode());
            prepareStatement.setString(2, currencyEntity.getFullName());
            prepareStatement.setString(3, currencyEntity.getSign());
            prepareStatement.setInt(4, currencyEntity.getId());
            return prepareStatement.executeUpdate() == 1;
        } catch (SQLException sqlException) {
            throw createDaoException(sqlException);
        }
    }
    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setLong(1, id);
            return prepareStatement.executeUpdate() == 1;
        } catch (SQLException sqlException) {
            throw new RespException(500, "База данных недоступна");
        }
    }

    public CurrencyEntity save(CurrencyEntity currencyEntity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, currencyEntity.getCode());
            prepareStatement.setString(2, currencyEntity.getFullName());
            prepareStatement.setString(3, currencyEntity.getSign());
            prepareStatement.executeUpdate();
            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next())
                currencyEntity.setId(generatedKeys.getInt("id"));
            return currencyEntity;
        } catch (SQLException sqlException) {
            throw createDaoException(sqlException);
        }
    }

    public Optional<CurrencyEntity> findByCode(String code) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE)) {
            prepareStatement.setString(1, code);
            var resultSet = prepareStatement.executeQuery();
            CurrencyEntity currencyEntity = null;
            if(resultSet.next()) {
                currencyEntity = bildCurrencyEntity(resultSet);
            }
            return Optional.ofNullable(currencyEntity);
        } catch (SQLException sqlException) {
            throw new RespException(500, "База данных недоступна");
        }
    }

    private static CurrencyEntity bildCurrencyEntity(ResultSet resultSet) throws SQLException {
        return new CurrencyEntity(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign"));
    }

    private static RespException createDaoException(SQLException sqlException) {
        String sqlState = sqlException.getSQLState();
        String message = sqlException.getMessage();
        if (sqlState.equals(NOT_NULL.getState())) {
            if (message.contains("\"code\"")) {
                throw new RespException(400, "Не указано значение кода валюты");
            } else if (message.contains("\"full_name\"")) {
                throw new RespException(400, "Не указано полное имя валюты");
            } else if (message.contains("\"sign\"")) {
                throw new RespException(400, "Не указан знак валюты");
            }
        }
        if (sqlState.equals(UNIQUE.getState())) {
            if (message.contains("currencies_code_pk")) {
                throw new RespException(409, "Валюта с таким кодом уже существует");
            }
        }
        // SQL кидает var(3) без указания поля, если у двух полей ограничение var(3) непонятно на какое ругается
        if (sqlState.equals(VARCHAR_LENGTH.getState())) {
            if (message.contains("varying(3)")) {
                throw new RespException(400, "Код валюты больше 3 букв");
            } else if (message.contains("varying(255)")) {
                throw new RespException(400, "Название валюты превышает 255 символов");
            }
        }
        if (sqlState.equals(CHECK.getState())) {
            if (message.contains("currencies_code_regular_check")) {
                throw new RespException(400, "Код валюты состоит не из 3 заглавных латинских букв");
            } else if (message.contains("currencies_full_name_length_check")) {
                throw new RespException(400, "Название валюты превышает 255 символов");
            } else if (message.contains("currencies_sign_length_check")) {
                throw new RespException(400, "Знак валюты превышает 3 символа");
            }
        }
        throw new RespException(500, "База данных недоступна");
    }

}
