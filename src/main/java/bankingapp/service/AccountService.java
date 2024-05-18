package bankingapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import bankingapp.dto.AccountDto;
import bankingapp.dto.TransactionDto;
import bankingapp.dto.TransferFundDto;
@Service
public interface AccountService {
	
	AccountDto createAccount(AccountDto account);
	
	AccountDto getAccountById(Long id);
	
	AccountDto deposit(Long id , double amount);
	
	AccountDto withdraw(Long id , double amount);
	
	List<AccountDto> getAllAccounts();
	
	void deleteAccount(Long id);
	
	void transferFunds(TransferFundDto transferFundDto);
	List<TransactionDto> getAccountTransaction(Long accountId);

}
