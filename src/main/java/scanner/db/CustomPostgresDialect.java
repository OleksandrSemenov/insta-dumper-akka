package scanner.db;

import org.hibernate.dialect.PostgreSQL9Dialect;

public class CustomPostgresDialect extends PostgreSQL9Dialect {
    public CustomPostgresDialect() {
        registerFunction("fts", new PostgreSQLFullTextSearchFunction());
    }
}
