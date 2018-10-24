package advisors.domain;

import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.cucumberexpressions.Transformer;

import java.math.BigDecimal;
import java.util.Locale;

public class TypeRegistryConfiguration implements TypeRegistryConfigurer {

    @Override
    public Locale locale() {
        return Locale.FRENCH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        typeRegistry.defineParameterType(new ParameterType<>(
                "bigDecimal",
                "\\d+.\\d+",
                BigDecimal.class,
                (Transformer<BigDecimal>) BigDecimal::new
                )
        );

    }
}
