
import dao.currency.CurrencyDao;
import dao.exchangeRates.ExchangeRatesDao;

public class DaoTest {
    public static void main(String[] args)  {

        System.out.println(ExchangeRatesDao.getInstance().findById(1));

        /*
        var all = ExchangeRatesDao.getInstance().findAll();

        var exchangeRatesEntity = all.get(0);

        CurrencyEntity c1 = exchangeRatesEntity.getBaseCurrency();
        CurrencyEntity c2 = CurrencyDao.getInstance().findById(7).get();
        BigDecimal bd = new BigDecimal("10");

        ExchangeRatesEntity ere = new ExchangeRatesEntity(0 , c1, c2, bd);

        ExchangeRatesDao.getInstance().save(ere);
        */

/*
        var all = CurrencyDao.getInstance().findAll();
        for(CurrencyEntity currencyEntity : all) {
            System.out.println(currencyEntity.toString());
        }
        */
/*
        CurrencyFilter cf = new CurrencyFilter(1,0, "RUB", "ru", "P");
        CurrencyDao cd = CurrencyDao.getInstance();
        System.out.println(cd.findAll(cf));
 */

    }

    private static void extracted() {
        CurrencyDao cd = CurrencyDao.getInstance();
        var currencyEntity = cd.findById(4).get();
        System.out.println(currencyEntity);
        currencyEntity.setSign("$");
        cd.update(currencyEntity);
        System.out.println(cd.findById(4).get());
    }

}
