package generators;

import org.jooq.util.JavaWriter;
import org.jooq.util.TypedElementDefinition;

import java.math.BigDecimal;

import static jdk.nashorn.internal.objects.NativeJava.isType;

public class CustomVertxGenerator extends io.github.jklingsporn.vertx.jooq.generate.VertxGenerator {
    @Override
    protected boolean handleCustomTypeFromJson(TypedElementDefinition<?> column, String setter, String columnType, String javaMemberName, JavaWriter out) {
        if(isType(columnType, BigDecimal.class)){
            out.tab(2).println("%s(generators.CustomConverter.getBigDecimalValue(json, \"%s\"));", setter, javaMemberName);
            return true;
        }
        return super.handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out);
    }

    @Override
    protected boolean handleCustomTypeToJson(TypedElementDefinition<?> column, String getter, String columnType, String javaMemberName, JavaWriter out) {
        if(isType(columnType, BigDecimal.class)){
            out.tab(2).println("json.put(\"%s\",%s()==null?0.0:%s().doubleValue());", getJsonKeyName(column),getter,getter);
            return true;
        }
        return super.handleCustomTypeToJson(column, getter, columnType, javaMemberName, out);
    }
}
