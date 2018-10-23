package advisors.infra.mapping;

import advisors.dao.tables.pojos.Accounts;
import advisors.domain.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper( AccountMapper.class );

    Account accountsToAccount(Accounts account);

}
