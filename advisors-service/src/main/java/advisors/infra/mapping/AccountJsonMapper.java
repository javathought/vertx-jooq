package advisors.infra.mapping;

import advisors.domain.Account;
import io.vertx.core.json.JsonObject;

public class AccountJsonMapper {

    public static JsonObject toJson(Account account) {
        JsonObject json = new JsonObject();
            json.put("account_number",account.getAccountNumber());
            json.put("balance",account.getBalance()==null?0.0:account.getBalance().doubleValue());
            return json;
    }
}
