package advisors.domain.ports.primary;

import advisors.domain.Account;
import advisors.domain.exceptions.AlreadyExistsException;
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

    public Single<Account> create(String actNumber) {
        return repo.exists(actNumber).flatMap(b -> {
                    if (b) return Single.error(new AlreadyExistsException(String.format("Le compte [%s]", actNumber)));
                    else return repo.insert(new Account(actNumber));
                }
        );
    }

    public Single<Account> findById(String actNumber) {
        return repo.findById(actNumber);
    }
}
