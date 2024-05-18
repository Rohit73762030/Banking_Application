package bankingapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bankingapp.dto.AccountDto;
import bankingapp.dto.TransactionDto;
import bankingapp.dto.TransferFundDto;
import bankingapp.exception.AccountException;
import bankingapp.mapper.AccountMapper;
import bankingapp.model.Account;
import bankingapp.model.Transaction;
import bankingapp.repository.AccountRepository;
import bankingapp.repository.TransactionRepository;

@Service
public class AccountServiceImpl implements  AccountService{
    
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	
	@SuppressWarnings("unused")
	private static final String TRANSACTION_TYPE_DEPOSIT ="DEPOSIT";
	
	@SuppressWarnings("unused")
	private static final String TRANSACTION_TYPE_WITHDRAW ="WITHDRAW";

	@SuppressWarnings("unused")
	private static final String TRANSACTION_TYPE_TRANSFER ="TRANSFER";
	
	@Override
	public AccountDto createAccount(AccountDto accountDto) {
		Account account = AccountMapper.mapToAccount(accountDto);
		Account savesAccount =accountRepository.save(account);
		return AccountMapper.mapToAccountDto(savesAccount);
	}
	@Override
	public AccountDto getAccountById(Long id) {
	Account account = accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account does not exists"));
		return AccountMapper.mapToAccountDto(account);
	}
	@Override
	public AccountDto deposit(Long id, double amount) {
	Account  account =  accountRepository.findById(id)
		.orElseThrow(()->new AccountException("Account does not exists"));
		 double  total = account.getBalance() + amount;  
		 account.setBalance(total);
		 Account savedAccount = accountRepository.save(account);
		 Transaction transaction = new Transaction();
		 transaction.setAccountId(id);
		 transaction.setAmount(amount);
		 transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
		 transaction.setTimestamp(LocalDateTime.now());
		 transactionRepository.save(transaction);
		return AccountMapper.mapToAccountDto(savedAccount);
	}
	@Override
	public AccountDto withdraw(Long id, double amount) {
	 Account    account =  accountRepository.findById(id).orElseThrow(()->new AccountException("Account does not exists"));
	  if(account.getBalance() < amount) {
		  throw new AccountException("Insufficient amount");  
	  }
	  double total = account.getBalance()-amount;
	  account.setBalance(total);
	  Account  savedAccount =  accountRepository.save(account);
	  
	  Transaction transaction = new Transaction();
	  transaction.setAccountId(id);
	  transaction.setAmount(amount);
	  transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
	  transaction.setTimestamp(LocalDateTime.now());
	  transactionRepository.save(transaction);
	  
	  
	 return AccountMapper.mapToAccountDto(savedAccount);
	}
	@Override
	public List<AccountDto> getAllAccounts() {
	 List<Account> accounts = accountRepository.findAll();
	 return accounts.stream().map((account)->AccountMapper.mapToAccountDto(account)).collect(Collectors.toList());

		
	}
	@Override
	public void deleteAccount(Long id) {
		 accountRepository.findById(id).orElseThrow(()-> new AccountException("Account does not exists"));
		accountRepository.deleteById(id);
		
	}
	@Override
	public void transferFunds(TransferFundDto transferFundDto) {
		// Retrievee the account from which we send amount
		Account  fromAccount= accountRepository.findById(transferFundDto.formAccountId())
		 .orElseThrow(()-> new AccountException("Account does not exists"));
		// Retrive the account to send the amount
	 Account toAccount =	 accountRepository.findById(transferFundDto.toAccountId())
		 .orElseThrow(()-> new AccountException("Account does not exists"));
	 
	 if(fromAccount.getBalance() <transferFundDto.amount()) {
		 throw new RuntimeException("Insufficient Amount");
		 
	 }
	 
	 // Debit the amount from account object
	   fromAccount.setBalance(fromAccount.getBalance()-transferFundDto.amount());
	   // Credit the amount toAccount object
	   
	   toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());
	   accountRepository.save(fromAccount);
	   
	   accountRepository.save(toAccount);
	   
	   Transaction transaction = new Transaction();
	   transaction.setAccountId(transferFundDto.formAccountId());
	   transaction.setAmount(transferFundDto.amount());
	   transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
	   transaction.setTimestamp(LocalDateTime.now());
	   
	   transactionRepository.save(transaction);
	   
		
	}
	@Override
	public List<TransactionDto> getAccountTransaction(Long accountId) {
    List<Transaction>  transactions = transactionRepository
    		.findByAccountIdOrderByTimestampDesc(accountId); 
    return transactions.stream()
            .map((transaction) -> convertEntityToDto(transaction))
            .collect(Collectors.toList());
		
	}
	@SuppressWarnings("unused")
	private TransactionDto convertEntityToDto(Transaction transaction) {
	 return new TransactionDto
	(transaction.getId(),
	 transaction.getAccountId(),
	 transaction.getAmount(),
	 transaction.getTransactionType(),
	 transaction.getTimestamp()
	);
		
	}

}
