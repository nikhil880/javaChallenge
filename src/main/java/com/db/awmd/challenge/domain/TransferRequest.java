package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TransferRequest {

	@NotNull
	@NotEmpty
	private final String debitAccountId;

	@NotNull
	@NotEmpty
	private final String creditAccountId;

	@NotNull
	@Min(value = 0, message = "Amount should be positive")
	private BigDecimal amount;
	
	@JsonCreator
	  public TransferRequest(@JsonProperty("debitAccountId") String debitAccountId,@JsonProperty("creditAccountId") String creditAccountId,
	    @JsonProperty("amount") BigDecimal amount) {
		super();
		this.debitAccountId = debitAccountId;
		this.creditAccountId = creditAccountId;
		this.amount = amount;
	  }

	
}
