package advisors.impl;

import advisors.domain.Account;
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
}
