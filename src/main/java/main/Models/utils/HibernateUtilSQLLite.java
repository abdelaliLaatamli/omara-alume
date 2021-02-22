package main.Models.utils;

import main.Logging.FileHandlerLogger;
import main.Models.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class HibernateUtilSQLLite {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() throws IOException {

        Logger logger = FileHandlerLogger.getHandlerFile( HibernateUtil.class.getName() );


        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();

                settings.put(Environment.DRIVER , DBConnection.dbDRIVER );
                settings.put(Environment.URL    , DBConnection.dbURL    );
                settings.put(Environment.USER   , DBConnection.dbUSER   );
                settings.put(Environment.PASS   , DBConnection.dbPASS   );

                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");

                settings.put( Environment.SHOW_SQL , "true" );

                settings.put( Environment.CURRENT_SESSION_CONTEXT_CLASS , "thread" );
                //settings.put(Environment.HBM2DDL_AUTO, "create-drop");
                settings.put( Environment.HBM2DDL_AUTO , "update" );

                configuration.setProperties(settings);


                configuration.addAnnotatedClass(UserEntity.class);
                configuration.addAnnotatedClass(ClientEntity.class);
                configuration.addAnnotatedClass(ArticleEntity.class);
                configuration.addAnnotatedClass(GlassEntity.class);
                configuration.addAnnotatedClass(AluminumEntity.class);
                configuration.addAnnotatedClass(AccessoryEntity.class);
                configuration.addAnnotatedClass(OrderItemsEntity.class);
                configuration.addAnnotatedClass(OrderEntity.class);
                configuration.addAnnotatedClass(PaymentsMadeEntity.class);
                configuration.addAnnotatedClass(PriceEntity.class);
                configuration.addAnnotatedClass(StockEntity.class);
                configuration.addAnnotatedClass(StockItemsEntity.class);
                configuration.addAnnotatedClass( ProviderEntity.class );



                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                System.out.println("Hibernate Java Config serviceRegistry created");
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                return sessionFactory;

            } catch ( Exception e  ){
                logger.warning( e.getMessage() );
            }

        }
        return sessionFactory;
    }
}
