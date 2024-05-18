package bankingapp.dto;

public record TransferFundDto(Long formAccountId,
		Long toAccountId,
		double amount){

}
