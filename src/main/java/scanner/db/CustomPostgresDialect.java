package scanner.db;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.dialect.PostgreSQLDialect;

public class CustomPostgresDialect extends PostgreSQLDialect {
    public CustomPostgresDialect() {
        registerFunction("fts", new PostgreSQLFullTextSearchFunction());
    }
}
