package com.db.awmd.challenge.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TransferResponse {

	private Account debitedAccount;
	private Account creditedAccount;
	private String message;

	public TransferResponse(Account debitedAccount, Account creditedAccount, String message) {
		super();
		this.debitedAccount = debitedAccount;
		this.creditedAccount = creditedAccount;
		this.message = message;
	}

	public TransferResponse() {
		super();
	}
}
