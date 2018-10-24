package advisors.domain;

import advisors.domain.ports.primary.AccountManager;
import advisors.impl.AccountRepositoryTestImpl;
import cucumber.api.java8.En;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountSteps implements En {

    private Account currentAccount;
    private List<Account> list;
    private AccountManager mgr = new AccountManager(new AccountRepositoryTestImpl());

    public AccountSteps() {

        When("^je récupère la liste des comptes$", () ->
            mgr.getAll().doOnSuccess(accounts -> list = accounts).subscribe().dispose()
        );
        Then("^la liste contient (\\d+) comptes?$", (Integer nb) ->
            // Write code here that turns the phrase above into concrete actions
            assertThat(list.size()).isEqualTo(nb)
        );
        When("^je crée le compte \"([^\"]*)\"$", (String actNumber) ->
            // Write code here that turns the phrase above into concrete actions
            currentAccount = mgr.create(actNumber).blockingGet()
        );
        And("le solde du compte {string} est {bigDecimal}", (String actNumber, BigDecimal balance) -> {
            // Write code here that turns the phrase above into concrete actions
            currentAccount = mgr.findById(actNumber).blockingGet();
            assertThat(currentAccount.getBalance()).isEqualTo(balance);
        });
    }

}
