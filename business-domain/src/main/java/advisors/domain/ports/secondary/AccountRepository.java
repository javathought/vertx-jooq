package advisors.domain.ports.secondary;

import advisors.domain.Account;
import io.reactivex.Single;

import java.util.List;

public interface AccountRepository {
    Single<List<Account>> findAll();

    Single<Boolean> exists(String actNumber);

    Single<Account> insert(Account account);

    Single<Account> findById(String actNumber);
}
