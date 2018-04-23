package com.db.awmd.challenge.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferResponse;
import com.db.awmd.challenge.exception.OverdraftNotSupportedException;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsRepositoryInMemoryTest {
	
	@Autowired
	private AccountsService accountsService;

	@Test
	public void transfer_WithDebitAccountBalanceGreaterThanCreditAccount() {
		String uniqueFromAccountId = "Id-" + "373";
		Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("30"));
		String uniqueToAccountId = "id-" + "940";
		Account toAccount = new Account(uniqueToAccountId, new BigDecimal("20"));
		this.accountsService.createAccount(fromAccount);
		this.accountsService.createAccount(toAccount);
		TransferResponse response = this.accountsService.getAccountsRepository().transfer(uniqueFromAccountId, uniqueToAccountId, new BigDecimal("10"));
		assertThat(response.getDebitedAccount().getBalance().intValue()).isEqualTo(20);
		assertThat(response.getCreditedAccount().getBalance().intValue()).isEqualTo(30);
	}

	@Test(expected = OverdraftNotSupportedException.class)
	public void transfer_FailsWithDebitAccountBalanceLessThanCreditAccount() {
		String uniqueFromAccountId = "Id-" + "134";
		Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("30"));
		String uniqueToAccountId = "id-" + "866";
		Account toAccount = new Account(uniqueToAccountId, new BigDecimal("20"));
		this.accountsService.createAccount(fromAccount);
		this.accountsService.createAccount(toAccount);
		this.accountsService.getAccountsRepository().transfer(uniqueFromAccountId, uniqueToAccountId, new BigDecimal("40"));
	}
	
}
