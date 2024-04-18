package dao;

import exception.RespException;
import model.CurrencyEntity;
import model.ExchangeRatesEntity;
import util.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao implements Crud<Integer, ExchangeRatesEntity> {

    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

    private static final String UNIQUE_STATE = "23505";
    private static final String CHECK_STATE = "23514";
    private static final String NOT_NULL_STATE = "23502";

    private static final String FIND_ALL_SQL = """
            SELECT er.id, er.rate,
                   bc.id, bc.code, bc.full_name, bc.sign,
                   tc.id, tc.code, tc.full_name, tc.sign
              FROM exchange_rates AS er
              JOIN currencies AS bc on base_currency_id = bc.id
              JOIN currencies AS tc on target_currency_id = tc.id
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE er.id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM exchange_rates
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES(?, ?, ?)
            """;
    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRatesEntity> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            List<ExchangeRatesEntity> listExchangeRatesEntity = new ArrayList<>();
            while (resultSet.next()) {
                listExchangeRatesEntity.add(buildExchangeRates(resultSet));
            }
            return listExchangeRatesEntity;
        } catch (SQLException sqlException) {
            throw new RespException(sqlException, 500, "База данных недоступна");
        }
    }

    @Override
    public Optional<ExchangeRatesEntity> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);
            var resultSet = prepareStatement.executeQuery();
            ExchangeRatesEntity exchangeRatesEntity = null;
            if(resultSet.next()) {
                exchangeRatesEntity = buildExchangeRates(resultSet);
            }
            return Optional.ofNullable(exchangeRatesEntity);
        } catch (SQLException throwables) {
            throw new RespException(throwables, 500, "База данных недоступна");
        }
    }

    @Override
    public boolean update(ExchangeRatesEntity exchangeRatesEntityxchangeRatesEntity) {

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setInt(1, id);
            return prepareStatement.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throw new RespException(throwables, 500, "База данных недоступна");
        }
    }

    @Override
    public ExchangeRatesEntity save(ExchangeRatesEntity exchangeRatesEntity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setInt(1, exchangeRatesEntity.getBaseCurrencyEntity().getId());
            prepareStatement.setInt(2, exchangeRatesEntity.getTargetCurrencyEntity().getId());
            prepareStatement.setBigDecimal(3, exchangeRatesEntity.getRate());
            prepareStatement.executeUpdate();
            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRatesEntity.setId(generatedKeys.getInt("id"));
            }
            return exchangeRatesEntity;
        } catch (SQLException sqlException) {
            String sqlState = sqlException.getSQLState();
            String message = sqlException.getMessage();
            if (sqlState.equals(NOT_NULL_STATE)) {
                if (message.contains("\"base_currency_id\"")) {
                    throw new RespException(sqlException, 400, "Не указана базовая валюта");
                } else if (message.contains("\"target_currency_id\"")) {
                    throw new RespException(sqlException, 400, "Не указана целевая валюта");
                } else if (message.contains("\"rate\"")) {
                    throw new RespException(sqlException, 400, "Не указан курс обмена");
                }
            } else if (sqlState.equals(UNIQUE_STATE)) {
                if (message.contains("exchange_rates_base_and_target_currency_id_key")) {
                    throw new RespException(sqlException, 409, "Валютная пара с таким кодом ("
                            + exchangeRatesEntity.getBaseCurrencyEntity().getCode() + "|"
                            + exchangeRatesEntity.getTargetCurrencyEntity().getCode()
                            + ") уже существует");
                }
            } else if (sqlState.equals(CHECK_STATE)) {
                if (message.contains("exchange_rates_base_equals_target_check")) {
                    throw new RespException(sqlException, 400, "Указана одна и та же валютная пара");
                }
            }
            throw new RespException(sqlException, 500, "База данных недоступна");
        }
    }

    // почему-то JDBC не видит колонки по названию,
    // через алиасы работает, но получается некрасивый запрос, поэтому сделано по номеру колонок
    private ExchangeRatesEntity buildExchangeRates(ResultSet resultSet) throws SQLException {
        CurrencyEntity baseCurrencyEntity = new CurrencyEntity(
                resultSet.getInt(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6));
        CurrencyEntity targetCurrencyEntity = new CurrencyEntity(
                resultSet.getInt(7),
                resultSet.getString(8),
                resultSet.getString(9),
                resultSet.getString(10));
        return new ExchangeRatesEntity(
                resultSet.getInt(1),
                baseCurrencyEntity,
                targetCurrencyEntity,
                resultSet.getBigDecimal(2)
        );
    }

    private ExchangeRatesDao() {
    }
}
