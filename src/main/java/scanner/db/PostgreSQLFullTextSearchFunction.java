package scanner.db;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

import java.util.List;

public class PostgreSQLFullTextSearchFunction implements SQLFunction {
    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }

    @Override
    public Type getReturnType(Type type, Mapping mapping) throws QueryException {
        return new BooleanType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String render(Type type, List args, SessionFactoryImplementor sessionFactoryImplementor) throws QueryException {
        if (args.size() != 3) {
            throw new IllegalArgumentException(
                    "The function must be passed 3 arguments");
        }

        String ftsConfig = (String) args.get(0);
        String field = (String) args.get(1);
        String value = (String) args.get(2);
        String fragment = null;
        if (ftsConfig == null) {
            fragment = "to_tsvector(" + field + ") @@ " + "to_tsquery('"
                    + value + "')";
        } else {
            fragment = "to_tsvector(" + ftsConfig + "::regconfig, " + field + ") @@ "
                    + "to_tsquery(" + ftsConfig + ", " + value + ")";
        }
        return fragment;
    }
}
