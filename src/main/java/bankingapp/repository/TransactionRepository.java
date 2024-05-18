package bankingapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bankingapp.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);
	
}
