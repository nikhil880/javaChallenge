package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void createAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    Account account = accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  public void createDuplicateAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNegativeBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void getAccount() throws Exception {
    String uniqueAccountId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
    this.accountsService.createAccount(account);
    this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId))
      .andExpect(status().isOk())
      .andExpect(
        content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
  }
  
  @Test
  public void transferFunds_WithAvailableBalanceGreaterThanAmount() throws Exception {
	   String uniqueFromAccountId = "Id-269";
	    Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("200.00"));
		   String uniqueToAccountId = "Id-875" ;
	    Account toAccount = new Account(uniqueToAccountId, new BigDecimal("300.00"));
	    this.accountsService.createAccount(fromAccount);
	    this.accountsService.createAccount(toAccount);
	    BigDecimal amount = new BigDecimal("80.00");
	    this.mockMvc.perform(put("/v1/accounts").content("{\"debitAccountId\":\"Id-269\",\"creditAccountId\":\"Id-875\",\"amount\":10}").contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andExpect(jsonPath("$.creditedAccount.balance", is(310.0))).andExpect(jsonPath("$.debitedAccount.balance", is(190.0))).
	    andExpect(jsonPath("$.message", is("Transaction Successful")));

  }
  
  @Test
  public void transferFunds_WithAvailableBalanceLessThanAmount() throws Exception {
	   String uniqueFromAccountId = "Id-547" ;
	    Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("30"));
		   String uniqueToAccountId = "Id-567";
	    Account toAccount = new Account(uniqueToAccountId, new BigDecimal("20"));
	    this.accountsService.createAccount(fromAccount);
	    this.accountsService.createAccount(toAccount);
	    this.mockMvc.perform(put("/v1/accounts").content("{\"debitAccountId\":\"Id-547\",\"creditAccountId\":\"Id-567\",\"amount\":80}").contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotAcceptable());

  }
  @Test
  public void transferFunds_WithInvalidDebitAccountId() throws Exception {
	   String uniqueFromAccountId = "Id-854";
	    Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("30"));
		   String uniqueToAccountId = "Id-361";
	    Account toAccount = new Account(uniqueToAccountId, new BigDecimal("20"));
	    this.accountsService.createAccount(fromAccount);
	    this.accountsService.createAccount(toAccount);
	    this.mockMvc.perform(put("/v1/accounts").content("{\"debitAccountId\":\"Id-129\",\"creditAccountId\":\"Id-361\",\"amount\":80}").contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotAcceptable());

  }
  
  @Test
  public void transferFunds_WithInvalidCreditAccountId() throws Exception {
	   String uniqueFromAccountId = "Id-304" ;
	    Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("30"));
		   String uniqueToAccountId = "Id-903";
	    Account toAccount = new Account(uniqueToAccountId, new BigDecimal("20"));
	    this.accountsService.createAccount(fromAccount);
	    this.accountsService.createAccount(toAccount);
	    this.mockMvc.perform(put("/v1/accounts").content("{\"debitAccountId\":\"Id-304\",\"creditAccountId\":\"Id-12674\",\"amount\":80}").contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotAcceptable());

  }
  
  @Test
  public void transferFunds_WithEmptyDebitAccountId() throws Exception {
	   String uniqueFromAccountId = "Id-304" ;
	    Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("30"));
		   String uniqueToAccountId = "Id-903";
	    Account toAccount = new Account(uniqueToAccountId, new BigDecimal("20"));
	    this.accountsService.createAccount(fromAccount);
	    this.accountsService.createAccount(toAccount);
	    this.mockMvc.perform(put("/v1/accounts").content("{\"debitAccountId\":\"\",\"creditAccountId\":\"Id-12674\",\"amount\":80}").contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

  }
  
  @Test
  public void transferFunds_WithEmptyCreditAccountId() throws Exception {
	   String uniqueFromAccountId = "Id-304" ;
	    Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("30"));
		   String uniqueToAccountId = "Id-903";
	    Account toAccount = new Account(uniqueToAccountId, new BigDecimal("20"));
	    this.accountsService.createAccount(fromAccount);
	    this.accountsService.createAccount(toAccount);
	    this.mockMvc.perform(put("/v1/accounts").content("{\"debitAccountId\":\"Id-304\",\"creditAccountId\":\"\",\"amount\":80}").contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

  }
  
  @Test
  public void transferFunds_WithNegativeAmount() throws Exception {
	   String uniqueFromAccountId = "Id-304" ;
	    Account fromAccount = new Account(uniqueFromAccountId, new BigDecimal("30"));
		   String uniqueToAccountId = "Id-903";
	    Account toAccount = new Account(uniqueToAccountId, new BigDecimal("20"));
	    this.accountsService.createAccount(fromAccount);
	    this.accountsService.createAccount(toAccount);
	    this.mockMvc.perform(put("/v1/accounts").content("{\"debitAccountId\":\"Id-304\",\"creditAccountId\":\"\",\"amount\":-80}").contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

  }
}
