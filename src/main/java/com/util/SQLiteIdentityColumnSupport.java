package com.util;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.GetGeneratedKeysDelegate;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.hibernate.id.PostInsertIdentityPersister;

public class SQLiteIdentityColumnSupport extends IdentityColumnSupportImpl {

    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) throws MappingException {
        return "select last_insert_rowid()";
    }

    @Override
    public String getIdentityColumnString(int type) throws MappingException {
        return "integer";
    }

    @Override
    public GetGeneratedKeysDelegate buildGetGeneratedKeysDelegate(
            PostInsertIdentityPersister persister,
            Dialect dialect) {
        return new GetGeneratedKeysDelegate(persister, dialect);
    }
}
