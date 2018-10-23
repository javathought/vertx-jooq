package advisors.infra.ports.secondary;

import advisors.dao.tables.daos.AccountsDao;
import advisors.domain.Account;
import advisors.domain.ports.secondary.AccountRepository;
import advisors.infra.mapping.AccountMapper;
import io.reactivex.Single;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class AccountRepositoryImpl implements AccountRepository {

    AccountsDao delegate;

    public AccountRepositoryImpl(AsyncSQLClient sqlClient) {
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.MYSQL);
        this.delegate = new AccountsDao(configuration, sqlClient);
    }

    @Override
    public Single<List<Account>> findAll() {
        return delegate.findAll()
                .map(accounts -> {
                            System.out.println(String.format("accounts = %s", accounts));
                            List<Account> accountList = accounts.stream().map(account -> {
                                System.out.println(String.format("dao = %s", account));
                                Account accountD = AccountMapper.INSTANCE.accountsToAccount(account);
                                    System.out.println(String.format("domain = %s", accountD));
                                    return accountD;
                                    }
                            )
                                    .collect(Collectors.toList());
                            System.out.println(String.format("accounts list = %s", accounts));
                            return accountList;
                        }
                );
    }
}
