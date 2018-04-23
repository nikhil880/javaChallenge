package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferResponse;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.OverdraftNotSupportedException;
import com.db.awmd.challenge.service.NotificationService;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	@Autowired
	private NotificationService notificationService;
	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public synchronized Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public TransferResponse transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
		Account fromAccount = null;
		Account toAccount = null;
		synchronized (this) {
			fromAccount = accounts.get(fromAccountId);
			toAccount = accounts.get(toAccountId);
			if (amount.compareTo(fromAccount.getBalance()) == 1) {
				throw new OverdraftNotSupportedException();
			}
			fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
			toAccount.setBalance(toAccount.getBalance().add(amount));
		}
		this.notificationService.notifyAboutTransfer(fromAccount,
				"Amount: " + amount + " was debited from account: " + fromAccountId);
		this.notificationService.notifyAboutTransfer(toAccount,
				"Amount: " + amount + " was credited to account: " + toAccountId);
		return new TransferResponse(fromAccount, toAccount, "Transaction Successful");
	}

}
