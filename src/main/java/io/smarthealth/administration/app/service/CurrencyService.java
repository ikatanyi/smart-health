package io.smarthealth.administration.app.service;

import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.administration.app.domain.CurrencyRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Transactional
    public Currency createCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }
    
    @Transactional
    public Currency updateCurrency(Long id, Currency currency) {
        Currency savedcurrency = getCurrency(id).orElseThrow(() -> APIException.notFound("Address with id {0} not found", id));
        savedcurrency.setActive(currency.isActive());
        savedcurrency.setCode(currency.getCode());
        savedcurrency.setDecimalPlaces(currency.getDecimalPlaces());
        savedcurrency.setFormat(currency.getFormat());
        savedcurrency.setName(currency.getName());
        savedcurrency.setSymbol(currency.getSymbol());
        return currencyRepository.save(savedcurrency);
    }

    public Optional<Currency> getCurrency(Long id) {
        return currencyRepository.findById(id);
    }
     public Optional<Currency> getCurrencyByCode(String code) {
        return currencyRepository.findByCode(code);
    }

    public Optional<Currency> getCurrencyByName(String term) {
        return currencyRepository.findByName(term);
    }
    
    public List<Currency> getCurrencyByNameOrCode(String term) {
        return currencyRepository.findByNameContainingIgnoreCaseOrSymbolContainingIgnoreCase(term,term);
    }

    public Page<Currency> getCurrency(Pageable page, boolean includeClosed) {

        if (includeClosed) {
            return currencyRepository.findAll(page);
        }
        return currencyRepository.findByActiveTrue(page);
    }
    public List<Currency> getCurrencies(){
        return currencyRepository.findAll();
    }
}
