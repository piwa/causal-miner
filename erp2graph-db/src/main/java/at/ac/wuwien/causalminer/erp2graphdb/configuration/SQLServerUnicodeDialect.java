package at.ac.wuwien.causalminer.erp2graphdb.configuration;

import java.sql.Types;

public class SQLServerUnicodeDialect extends org.hibernate.dialect.SQLServer2012Dialect {
    public SQLServerUnicodeDialect() {
        super();
//        registerColumnType(Types.CHAR, "nchar(1)");
//        registerColumnType(Types.LONGVARCHAR, "nvarchar(max)" );
//        registerColumnType(Types.VARCHAR, 4000, "nvarchar($l)");
//        registerColumnType(Types.VARCHAR, "nvarchar(max)");
//        registerColumnType(Types.CLOB, "nvarchar(max)" );
//
//        registerColumnType(Types.NCHAR, "nchar(1)");
//        registerColumnType(Types.LONGNVARCHAR, "nvarchar(max)");
//        registerColumnType(Types.NVARCHAR, 4000, "nvarchar($l)");
//        registerColumnType(Types.NVARCHAR, "nvarchar(max)");
//        registerColumnType(Types.NCLOB, "nvarchar(max)");
//
//        registerHibernateType(Types.NCHAR, StandardBasicTypes.CHARACTER.getName());
//        registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.TEXT.getName());
//        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
//        registerHibernateType(Types.NCLOB, StandardBasicTypes.CLOB.getName() );

        registerColumnType(Types.VARCHAR, 4000, "nvarchar($l)");
        registerColumnType(Types.CLOB, "nvarchar(max)");

//        registerColumnType(Types.NUMERIC, 19, "smallint");

    }
}