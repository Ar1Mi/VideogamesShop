package domain.videogamesshop.service;

import domain.videogamesshop.model.BankCard;
import domain.videogamesshop.model.User;
import domain.videogamesshop.repository.BankCardRepository;
import domain.videogamesshop.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankCardService {

    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public List<BankCard> findByUser(User user) {
        return bankCardRepository.findByUser(user);
    }

    public Optional<BankCard> findById(Long id) {
        return bankCardRepository.findById(id);
    }

    public void save(BankCard bankCard) {
        bankCardRepository.save(bankCard);
    }

    public void deleteById(Long id) {
        // Проверить, есть ли связанные транзакции
        boolean hasTransactions = transactionRepository.existsByBankCardId(id);
        if (hasTransactions) {
            throw new IllegalStateException("Cannot delete a card with existing transactions.");
        }

        // Удалить карту
        bankCardRepository.deleteById(id);
    }

}
