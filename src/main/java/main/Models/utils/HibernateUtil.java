package main.Models.utils;

import main.Models.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() throws IOException {

        FileHandler handler = new FileHandler("default.log", true);
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        Logger logger = Logger.getLogger("main");
        logger.addHandler(handler);

        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();
//                settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
//                settings.put(Environment.URL, "jdbc:mysql://localhost:3306/omar_alum");
//                settings.put(Environment.USER, "root");
//                settings.put(Environment.PASS, "");

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

            } catch (IllegalStateException e ){
                //e.printStackTrace();

                //e.getStackTrace().toString()
                logger.warning( e.getMessage() );
                //System.out.println("a------------------------------");
                logger.log( Level.WARNING , "aaaa" , Arrays.stream(e.getStackTrace())
                        .map(s->s.toString())
                        .collect(Collectors.joining("\n")) /* e.getStackTrace().toString() */);
            } catch (Exception e) {
                //e.printStackTrace();
                logger.severe( e.getMessage() );
                logger.log( Level.WARNING , "aaaa" , Arrays.stream(e.getStackTrace())
                        .map(s->s.toString())
                        .collect(Collectors.joining("\n")) /* e.getStackTrace().toString() */);
                //logger.warning( );
            }
        }
        return sessionFactory;
    }
}
