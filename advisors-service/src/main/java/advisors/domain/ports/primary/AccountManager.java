package advisors.domain.ports.primary;

import advisors.domain.Account;
import advisors.domain.ports.secondary.AccountRepository;
import io.reactivex.Single;

import java.util.List;

public class AccountManager {

    private final AccountRepository repo;

    public AccountManager(AccountRepository repo) {
        this.repo = repo;
    }

    public Single<List<Account>> getAll() {
        return repo.findAll();
    }
}
