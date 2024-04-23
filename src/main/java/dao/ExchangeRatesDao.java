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

import static dao.PSQLState.*;

public class ExchangeRatesDao implements Crud<Integer, ExchangeRatesEntity> {

    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

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
    private static final String FIND_BY_CODES_SQL = FIND_ALL_SQL + """
            WHERE bc.code = ? AND tc.code = ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM exchange_rates
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES(?, ?, ?)
            """;
    private ExchangeRatesDao() {
    }

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
            throw new RespException(500, "База данных недоступна");
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
        } catch (SQLException sqlException) {
            throw new RespException(500, "База данных недоступна");
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
        } catch (SQLException sqlException) {
            throw new RespException(500, "База данных недоступна");
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
            попробовать вытянуть курс и проверить правильно ли вставился
            return exchangeRatesEntity;
        } catch (SQLException sqlException) {
            String sqlState = sqlException.getSQLState();
            String message = sqlException.getMessage();
            if (sqlState.equals(NOT_NULL.getState())) {
                if (message.contains("\"base_currency_id\"")) {
                    throw new RespException(400, "Не указана базовая валюта");
                } else if (message.contains("\"target_currency_id\"")) {
                    throw new RespException(400, "Не указана целевая валюта");
                } else if (message.contains("\"rate\"")) {
                    throw new RespException(400, "Не указан курс обмена");
                }
            }
            if (sqlState.equals(FOREIGN_KEY.getState())) {
                if (message.contains("exchange_rates_base_currency_id_fkey")) {
                    throw new RespException(404, "Базовая валюта с кодом "
                            + exchangeRatesEntity.getBaseCurrencyEntity().getCode() + " отсутствует");
                } else if (message.contains("exchange_rates_target_currency_id_fkey")) {
                    throw new RespException(404, "Целевая валюта с кодом "
                            + exchangeRatesEntity.getTargetCurrencyEntity().getCode() + " отсутствует");
                }
            }
            if (sqlState.equals(UNIQUE.getState())) {
                if (message.contains("exchange_rates_base_and_target_currency_id_key")) {
                    throw new RespException(409, "Валютная пара с таким кодом ("
                            + exchangeRatesEntity.getBaseCurrencyEntity().getCode() + "|"
                            + exchangeRatesEntity.getTargetCurrencyEntity().getCode()
                            + ") уже существует");
                }
            }
            if (sqlState.equals(CHECK.getState())) {
                if (message.contains("exchange_rates_base_equals_target_check")) {
                    throw new RespException(400, "Указана одна и та же валютная пара");
                }
            }
            throw new RespException(500, "База данных недоступна");
        }
    }

    public Optional<ExchangeRatesEntity> findByCodes(String baseCurrency, String targetCurrency) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODES_SQL)){
            prepareStatement.setString(1, baseCurrency);
            prepareStatement.setString(2, targetCurrency);
            var resultSet = prepareStatement.executeQuery();
            ExchangeRatesEntity exchangeRatesEntity = null;
            if(resultSet.next())
                exchangeRatesEntity = buildExchangeRates(resultSet);
            return Optional.ofNullable(exchangeRatesEntity);
        } catch (SQLException sqlException) {
            throw new RespException(500, "База данных недоступна");
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
}
