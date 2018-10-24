package advisors.impl;

import advisors.domain.Account;
import advisors.domain.exceptions.NoSuchReferenceException;
import advisors.domain.ports.secondary.AccountRepository;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountRepositoryTestImpl implements AccountRepository {

    private HashMap<String, Account> repo = new HashMap<>();

    @Override
    public Single<List<Account>> findAll() {
        return Single.just(new ArrayList<>(repo.values()));
    }

    @Override
    public Single<Boolean> exists(String actNumber) {
        return Single.just(repo.containsKey(actNumber));
    }

    @Override
    public Single<Account> insert(Account account) {
        repo.put(account.getAccountNumber(), account);
        return Single.just(account);
    }

    @Override
    public Single<Account> findById(String actNumber) {
        if (repo.containsKey(actNumber))
            return Single.just(repo.get(actNumber));
        else
            return Single.error(new NoSuchReferenceException(String.format("Le compte [%s]", actNumber)));
    }
}
