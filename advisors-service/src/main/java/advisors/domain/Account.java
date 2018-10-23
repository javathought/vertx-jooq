package advisors.domain;

import io.swagger.util.Json;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

public class Account {

    private String     accountNumber;
    private BigDecimal balance;

    public Account() {
    }

    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return new EqualsBuilder()
                .append(accountNumber, account.accountNumber)
                .append(balance, account.balance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(accountNumber)
                .append(balance)
                .toHashCode();
    }

/*
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("accountNumber", accountNumber)
                .append("balance", balance)
                .toString();
    }
*/

}
