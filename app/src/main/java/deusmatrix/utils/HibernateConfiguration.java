package deusmatrix.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;

public class HibernateConfiguration {
    private static EntityManagerFactory entityManagerFactory;
    private static ApplicationConfigReader configReader;

    private static String lastUsedConnectionURL;

    private static final String DB_URL_PREFIX = "jdbc:";
    private static final String PERSISTENCE_PROVIDER_NAME = "org.hibernate.jpa.HibernatePersistenceProvider";
    private static final String PERSISTENCE_UNIT_NAME = "DeusMatrix";
    private static final String SQLITE_DB_TYPE = "sqlite";

    private static final String DB_URL_ADDRESS_SEPARATOR = ":";

    private HibernateConfiguration() {}

    private static final List<Class<?>> ENTITY_CLASSES = new ArrayList<>();

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static void shutdown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    public static void build(ApplicationConfigReader inputConfigReader) {
        shutdown();
        configReader = inputConfigReader;

        lastUsedConnectionURL = buildDatabaseUrl(inputConfigReader);

        HashMap<String, Object> settings = new HashMap<>();
        settings.put(AvailableSettings.DRIVER, configReader.getDbDriver());
        settings.put(AvailableSettings.URL, lastUsedConnectionURL);
        settings.put(AvailableSettings.DIALECT, CustomSQLiteDialect.class);
        settings.put(AvailableSettings.SHOW_SQL, false);
        settings.put(AvailableSettings.QUERY_STARTUP_CHECKING, false);
        settings.put(AvailableSettings.USER, configReader.getDbUser());
        settings.put(AvailableSettings.PASS, configReader.getDbPassword());
        settings.put(AvailableSettings.GENERATE_STATISTICS, false);
        settings.put(AvailableSettings.USE_REFLECTION_OPTIMIZER, false);
        settings.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, false);
        settings.put(AvailableSettings.USE_QUERY_CACHE, false);
        settings.put(AvailableSettings.USE_STRUCTURED_CACHE, false);
        settings.put(AvailableSettings.STATEMENT_BATCH_SIZE, 20);
        settings.put(AvailableSettings.HBM2DDL_AUTO, configReader.getDbUsingType());

        entityManagerFactory = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(persistenceUnitInfo(inputConfigReader), settings);
    }

    private static String buildDatabaseUrl(ApplicationConfigReader inputConfigReader) {
        if (!SQLITE_DB_TYPE.equals(inputConfigReader.getDbType())) {
            throw new IllegalArgumentException("Only SQLite is supported");
        }

        return DB_URL_PREFIX + SQLITE_DB_TYPE + DB_URL_ADDRESS_SEPARATOR + inputConfigReader.getDbAddress();
    }

    private static PersistenceUnitInfo persistenceUnitInfo(ApplicationConfigReader inputConfigReader) {
        return new HibernatePersistenceUnitInfo(inputConfigReader);
    }

    private static Properties hibernateProperties(ApplicationConfigReader inputConfigReader) {
        final Properties properties = new Properties();

        properties.put(AvailableSettings.HBM2DDL_AUTO, configReader.getDbUsingType());
        properties.put(AvailableSettings.SHOW_SQL, true);
        properties.put(AvailableSettings.DRIVER, configReader.getDbDriver());
        properties.put(AvailableSettings.URL, buildDatabaseUrl(inputConfigReader));
        properties.put(AvailableSettings.DIALECT, CustomSQLiteDialect.class);

        return properties;
    }

    private static List<String> entityClassNames() {
        return ENTITY_CLASSES.stream().map(Class::getName).collect(Collectors.toList());
    }

    public static void addEntity(Class<?> entityClass) {
        if (!ENTITY_CLASSES.contains(entityClass)) {
            ENTITY_CLASSES.add(entityClass);
        }
    }

    public static String getLastUsedConnectionURL() {
        return lastUsedConnectionURL;
    }

    public static String getDbUrlPrefix() {
        return DB_URL_PREFIX + SQLITE_DB_TYPE + DB_URL_ADDRESS_SEPARATOR;
    }

    private static class HibernatePersistenceUnitInfo implements PersistenceUnitInfo {
        private ApplicationConfigReader configReader;

        HibernatePersistenceUnitInfo(ApplicationConfigReader inputConfigReader) {
            configReader = inputConfigReader;
        }

        @Override
        public String getPersistenceUnitName() {
            return PERSISTENCE_UNIT_NAME;
        }

        @Override
        public String getPersistenceProviderClassName() {
            return PERSISTENCE_PROVIDER_NAME;
        }

        @Override
        public PersistenceUnitTransactionType getTransactionType() {
            return PersistenceUnitTransactionType.RESOURCE_LOCAL;
        }

        @Override
        public DataSource getJtaDataSource() {
            return null;
        }

        @Override
        public DataSource getNonJtaDataSource() {
            return null;
        }

        @Override
        public List<String> getMappingFileNames() {
            return Collections.emptyList();
        }

        @Override
        public List<URL> getJarFileUrls() {
            try {
                return Collections.list(this.getClass().getClassLoader().getResources(""));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public URL getPersistenceUnitRootUrl() {
            return null;
        }

        @Override
        public List<String> getManagedClassNames() {
            return entityClassNames();
        }

        @Override
        public boolean excludeUnlistedClasses() {
            return false;
        }

        @Override
        public SharedCacheMode getSharedCacheMode() {
            return null;
        }

        @Override
        public ValidationMode getValidationMode() {
            return null;
        }

        @Override
        public Properties getProperties() {
            return hibernateProperties(configReader);
        }

        @Override
        public String getPersistenceXMLSchemaVersion() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }

        @Override
        public void addTransformer(ClassTransformer transformer) {}

        @Override
        public ClassLoader getNewTempClassLoader() {
            return null;
        }
    }
}
