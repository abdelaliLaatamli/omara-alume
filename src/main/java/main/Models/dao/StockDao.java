package main.Models.dao;

import main.Models.entities.ArticleEntity;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;
import main.Models.entities.queryContainers.*;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StockDao {

    public StockEntity save(StockEntity entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();


            Set<StockItemsEntity> savedStockItems = new HashSet<>();

            for ( StockItemsEntity stockItemsEntity : entity.getStockItems() ){
                session.save( stockItemsEntity );
                savedStockItems.add( stockItemsEntity );
            }

            entity.setStockItems( savedStockItems );

            // save the student object
            session.save(entity);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return entity;
    }


    public StockEntity updateEntity(StockEntity entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the student object

            session.update(entity);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return entity;
    }

    public boolean update(StockEntity entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            Set<StockItemsEntity> savedStockItems = new HashSet<>();

            for ( StockItemsEntity stockItemsEntity : entity.getStockItems() ){
                if( stockItemsEntity.getId() ==0 )
                    session.save( stockItemsEntity );
                savedStockItems.add( stockItemsEntity );
            }

            entity.setStockItems( savedStockItems );

            session.update(entity);
            // commit transaction
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void delete(int id) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            // Delete a user object
            StockEntity entity = session.get(StockEntity.class, id);
            if (entity != null) {
                session.delete(entity);
                System.out.println("Provider is deleted");
            }

            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public StockEntity get(int id) {

        Transaction transaction = null;
        StockEntity entity = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object
            entity = session.get(StockEntity.class, id);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return entity;
    }


    public List< StockEntity > getAll() {

        Transaction transaction = null;
        List < StockEntity > listOfEntities = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            listOfEntities = session.createQuery("from main.Models.entities.StockEntity S ORDER BY S.importedAt DESC").getResultList();

            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return listOfEntities;
    }

    public List<StockItemStatus> getStockProductStatus() {
//        https://www.journaldev.com/3422/hibernate-native-sql-query-example#hibernate-native-sql-multiple-tables
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object


            List<Object[]> rows = session.createQuery("SELECT A , " +
                    "(SELECT COALESCE( SUM( ST.quantity ) , 0 )  FROM main.Models.entities.StockItemsEntity as ST WHERE ST.article=A) as inProducts ," +
                    "(SELECT COALESCE( sum( IO.quantity ) , 0 ) FROM main.Models.entities.OrderItemsEntity IO , main.Models.entities.OrderEntity O " +
                    "       WHERE IO.article=A and IO.order IS NOT NULL  and IO.order= O and O.isCanceled = 0 ) as outProducts " +
                    " from main.Models.entities.ArticleEntity A")
                    .getResultList();


            List<StockItemStatus> listStockItemStatus = new ArrayList<>();

            for (Object[] row : rows) {
                StockItemStatus stockItemStatus = new StockItemStatus();
                stockItemStatus.setArticle( (ArticleEntity) row[0] );
                stockItemStatus.setInProducts((Double) row[1]);
                stockItemStatus.setOutProducts((Double) row[2]);
                listStockItemStatus.add( stockItemStatus ) ;
            }


            // commit transaction
            transaction.commit();
            return listStockItemStatus;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>() ;
        }
    }


    public List<StockItemCalculus> getStockItemsCalculus() {
//        https://www.journaldev.com/3422/hibernate-native-sql-query-example#hibernate-native-sql-multiple-tables
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            List<Object[]> rows = session.createSQLQuery("SELECT A.id , A.name ," +
                    "   ROUND( (SELECT COALESCE( SUM( ST.quantity ) , 0 )  FROM stock_items as ST WHERE ST.article_id=A.id) , 3 ) as inProducts , " +
                    "   ROUND( (SELECT COALESCE( sum( IO.quantity ) , 0 ) FROM order_items IO , orders as O " +
                    "       WHERE IO.article_id=A.id and IO.order_id IS NOT NULL and IO.order_id= O.id and O.isCanceled = 0 ) , 3 ) as outProducts ,  " +
                    "   ROUND( COALESCE ( (SELECT ST.priceOfBuy FROM stock_items as ST WHERE ST.article_id=A.id LIMIT 1 ) , 0 ) , 3 ) as priceOfBuy " +
                    "from articles as A")
                    .list();


            List<StockItemCalculus> listStockItemCalculus = new ArrayList<>();

            for (Object[] row : rows) {
                StockItemCalculus stockItemCalculus = new StockItemCalculus();
                stockItemCalculus.setArticleID( (Integer) row[0] );
                stockItemCalculus.setArticleName( (String) row[1] );
                stockItemCalculus.setInProducts((Double) row[2]);
                stockItemCalculus.setOutProducts((Double) row[3]);
                stockItemCalculus.setPriceOfBuy((Double) row[4]);
                listStockItemCalculus.add( stockItemCalculus ) ;
            }


            // commit transaction
            transaction.commit();
            return listStockItemCalculus;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>() ;
        }
    }

    public List<MovementArticle> getMovementProductInStock(int articleId) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            List<Object[]> rows = session.createSQLQuery(
                    "SELECT o.orderDate as movement_date , 'sortie' as type , o.id as reference , oi.quantity , oi.price as price " +
                            " FROM order_items as oi , orders as o " +
                            " WHERE oi.article_id = :id and oi.order_id is NOT null and oi.order_id=o.id and o.isCanceled=0" +
                            " UNION " +
                            " SELECT s.importedAt as movement_date , 'entrée' as type , s.name as reference , si.quantity , si.priceOfBuy as price " +
                            " FROM stock_items as si , stock as s WHERE si.article_id = :id and si.stock_Id=s.Id " +
                            " ORDER BY movement_date DESC")
                    .setParameter("id",articleId)
                    .list();


            List<MovementArticle> listMovementArticle = new ArrayList<>();

            for (Object[] row : rows) {
                MovementArticle movementArticle = new MovementArticle();
                movementArticle.setDate( ((Timestamp) row[0]).toInstant() );
                movementArticle.setType( (String) row[1] );
                movementArticle.setReference( (String) row[2] );
                movementArticle.setQuantity( (Float) row[3] );
                movementArticle.setPrice( (Float) row[4] );
                listMovementArticle.add( movementArticle ) ;
            }


            // commit transaction
            transaction.commit();
            return listMovementArticle;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<MovementArticle>() ;
        }
    }


    public MoneyStatus getMoneyStatus() {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object


            Object[] row = (Object[]) session.createSQLQuery(
                    "select " +
                            "( SELECT ROUND( ifnull( sum(oi.quantity*oi.price) , 0 ) , 2 ) FROM orders as o , order_items as oi  " +
                            "WHERE EXTRACT(YEAR_MONTH FROM o.orderDate ) = EXTRACT(YEAR_MONTH FROM Now() ) and oi.order_id=o.id and o.isCanceled=0 ) as vende_month ,  " +
                            "( SELECT ROUND( ifnull( sum( si.priceOfBuy * si.quantity ) , 0 ) , 2) FROM stock as s , stock_items as si  " +
                            "WHERE EXTRACT(YEAR_MONTH FROM s.importedAt ) = EXTRACT(YEAR_MONTH FROM Now() ) and si.stock_Id=s.Id ) as total_achat ,  " +
                            "( SELECT ROUND( ifnull( sum(oi.quantity*oi.price) , 0 ) , 2 ) FROM orders as o , order_items as oi " +
                            " WHERE oi.order_id=o.id and year(o.orderDate) = year( now() ) and o.isCanceled=0 ) as global_vende ,  " +
                            "( SELECT ROUND( ifnull( sum( si.priceOfBuy * si.quantity ) , 0 ) , 2) FROM stock as s , stock_items as si " +
                            "    WHERE si.stock_Id=s.Id AND year( s.importedAt ) = year( now() ) ) as global_achat ,  " +
                            "( SELECT round( ifnull( sum(pm.amountPaid) , 0 ) , 2 ) FROM payements_made as pm, orders as o  " +
                            "  WHERE EXTRACT(YEAR_MONTH FROM o.orderDate ) = EXTRACT(YEAR_MONTH FROM Now() ) and pm.order_id=o.id ) as paid_month ,  " +
                            "(select vende_month - paid_month ) as creadit_month , " +
                            "( SELECT round( ifnull( sum(pm.amountPaid) , 0 ) , 2 ) FROM payements_made as pm  " +
                            "WHERE year(pm.paymentDate)=year(now()) and `order_id` is not null ) as global_paid , " +
                            "( select global_vende - global_paid ) global_creadit , " +
                            "( SELECT  " +
                            "    round( sum( oi.quantity * ifnull( (SELECT si.priceOfBuy from stock_items as si WHERE " +
                            "   si.article_id=oi.article_id LIMIT 1) , oi.price ) ) , 2 ) " +
                            "FROM orders as o , order_items as oi  WHERE  " +
                            "EXTRACT(YEAR_MONTH FROM o.orderDate ) = EXTRACT(YEAR_MONTH FROM Now() ) and oi.order_id=o.id and o.isCanceled=0 ) as vente_base_price , " +
                            "(SELECT round( sum( oi.quantity * " +
                            " ifnull( (SELECT si.priceOfBuy from stock_items as si WHERE si.article_id=oi.article_id LIMIT 1) , oi.price ) ) , 2 ) " +
                            "FROM orders as o , order_items as oi " +
                            "        WHERE oi.order_id=o.id and year(o.orderDate) = year( now() ) and o.isCanceled=0 ) as vente_global_price"
                    ).list().get(0);


            MoneyStatus moneyStatus = new MoneyStatus();

            moneyStatus.setSalesOfMonth( (double) row[0] );
            moneyStatus.setPurchaseOfMonth( (double) row[1] );
            moneyStatus.setSalesGlobal( (double) row[2] );
            moneyStatus.setPurchaseGlobal( (double) row[3] );
            moneyStatus.setPaymentsOfMonth( (double) row[4] );
            moneyStatus.setCreditOfMonth( (double) row[5] );
            moneyStatus.setPaymentsGlobal( (double) row[6] );
            moneyStatus.setCreditGlobal( (double) row[7] );
            moneyStatus.setTotalBuyPriceBase( (double) row[8] );
            moneyStatus.setTotalBuyGlobalBase( (double) row[9] );

            // commit transaction
            transaction.commit();

            return  moneyStatus ;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return null ;
        }
    }


    public List<TurnoverByMonth> getTurnoverByMonth() {

        String [] months = {
                "Janvier", "Février", "Mars",
                "Avril","Mai","Juin",
                "Juillet", "Août", "Septembre",
                "Octobre",  "Novembre", "Décembre"
        };

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            List<Object[]> rows = session.createSQLQuery(
                    "SELECT month(o.orderDate) month_ofYear , round( sum(oi.price*oi.quantity) , 2 ) as chiffremonth , " +
                            " (SELECT round( ifnull( sum( st.priceOfBuy * st.quantity ) , 0 ) , 2 )   FROM stock_items as st , stock as s WHERE st.stock_Id=s.Id and " +
                            " EXTRACT(YEAR_MONTH FROM s.importedAt ) = EXTRACT(YEAR_MONTH FROM o.orderDate )) as charge_month" +
                            " from order_items as oi , orders as o WHERE oi.order_id=o.id GROUP BY month(o.orderDate)")
                    .list();


            List<TurnoverByMonth> listTurnoverByMonth = new ArrayList<>();

            for (Object[] row : rows) {
                TurnoverByMonth turnoverByMonth = new TurnoverByMonth();
                int number = (Integer) row[0] ;
                turnoverByMonth.setMonth( months[number-1] );
                turnoverByMonth.setTurnover( (Double) row[1] );
                turnoverByMonth.setCharge( (Double) row[2] );
                listTurnoverByMonth.add( turnoverByMonth ) ;
            }


            // commit transaction
            transaction.commit();
            return listTurnoverByMonth;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>() ;
        }
    }

    public List<StockArticleItems> getProductsStockStatus(int article_id) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            List<Object[]> rows = session.createQuery(
                    " SELECT " +
                            "    SI , " +
                            "    S  " +
                            " FROM  " +
                            "    main.Models.entities.StockItemsEntity as SI , " +
                            "    main.Models.entities.StockEntity as S " +
                            " WHERE " +
                            "    SI.stock=S and " +
                            "    SI.article_id= :article_id " )
                    .setParameter( "article_id" , article_id )
                    .getResultList();


            List<StockArticleItems> listProductEnter = new ArrayList<>();

            for (Object[] row : rows) {
                StockArticleItems stockArticleItems = new StockArticleItems();
                stockArticleItems.setStock( (StockEntity) row[0] );
                stockArticleItems.setStockItems( (StockItemsEntity) row[1] );
                listProductEnter.add( stockArticleItems ) ;
            }


            // commit transaction
            transaction.commit();
            return listProductEnter;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>() ;
        }
    }

    public List<ProductEnter> getProductsEnter(int month) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            List<Object[]> rows = session.createQuery(
                    " SELECT " +
                            "    A , " +
                            "    round( SI.priceOfBuy , 3 ) as priceOfBuy , " +
                            "    round( SI.quantity , 3 ) as quantity  , " +
                            "    S.importedAt , " +
                            "    S.name ,  " +
                            "    P.name " +
                            " FROM  " +
                            "    main.Models.entities.StockItemsEntity as SI , " +
                            "    main.Models.entities.StockEntity as S , " +
                            "    main.Models.entities.ProviderEntity as P , " +
                            "    main.Models.entities.ArticleEntity as A " +
                            " WHERE " +
                            "    year(S.importedAt) = year(now()) and month(S.importedAt) = :month and " +
                            "    SI.stock=S and " +
                            "    S.provider = P and " +
                            "    SI.article=A " )
                    .setParameter( "month" , month )
                    .getResultList();


            List<ProductEnter> listProductEnter = new ArrayList<>();

            for (Object[] row : rows) {
                ProductEnter productEnter = new ProductEnter();
                productEnter.setArticle( (ArticleEntity) row[0] );
                productEnter.setPriceOfBuy( (Float) row[1] );
                productEnter.setQuantity( (Float) row[2] );
                productEnter.setDateImportation( (Instant) row[3] );
                productEnter.setFactureLabel( (String) row[4]);
                productEnter.setProviderName( (String) row[5]);
                listProductEnter.add( productEnter ) ;
            }


            // commit transaction
            transaction.commit();
            return listProductEnter;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>() ;
        }
    }


    public List<ProductEnter> getProductsOut(int month) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            List<Object[]> rows = session.createQuery(
                         " SELECT " +
                            "    A , " +
                            "    round( OI.price , 2 ) as priceOfBuy , " +
                            "    round( OI.quantity , 2 ) as quantity  , " +
                            "    O.orderDate , " +
                            "    O.id , " +
                            "    C.name " +
                            " FROM " +
                            "     main.Models.entities.OrderItemsEntity as OI , " +
                            "     main.Models.entities.OrderEntity as O , " +
                            "     main.Models.entities.ClientEntity as C , " +
                            "     main.Models.entities.ArticleEntity as A " +
                            " WHERE  " +
                            "     year(O.orderDate) = year(now()) and month(O.orderDate) = :month and " +
                            "     OI.order=O and " +
                            "     O.client=C and " +
                            "     OI.article=A " )
                    .setParameter( "month" , month )
                    .getResultList();


            List<ProductEnter> listProductEnter = new ArrayList<>();

            for (Object[] row : rows) {
                ProductEnter productEnter = new ProductEnter();
                productEnter.setArticle( (ArticleEntity) row[0] );
                productEnter.setPriceOfBuy( (Float) row[1] );
                productEnter.setQuantity( (Float) row[2] );
                productEnter.setDateImportation( (Instant) row[3] );
                productEnter.setFactureLabel( (Integer) row[4] + "");
                productEnter.setProviderName( (String) row[5]);
                listProductEnter.add( productEnter ) ;
            }


            // commit transaction
            transaction.commit();
            return listProductEnter;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>() ;
        }
    }

    public List<ArticleStockStatus> getArticleStockStatus(int articleId) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            List<Object[]> rows = session.createQuery(
                    " SELECT " +
                        "   S  ," +
                        "   SI , " +
                        "   ( SELECT COALESCE(sum(OI.quantity) , 0 ) FROM main.Models.entities.OrderItemsEntity as OI WHERE " +
                        "           OI.article=SI.article and OI.stockItemId=SI.id and OI.order is not null ) as salled " +
                        " from " +
                        "   main.Models.entities.StockItemsEntity as SI , " +
                        "   main.Models.entities.StockEntity as S " +
                        " WHERE " +
                        "   SI.stock=S and" +
                        "   SI.article_id=:article_id" )
                    .setParameter( "article_id" , articleId )
                    .getResultList();


            List<ArticleStockStatus> listArticleStockStatus = new ArrayList<>();

            for (Object[] row : rows) {
                ArticleStockStatus articleStockStatus = new ArticleStockStatus();
                    articleStockStatus.setStock( (StockEntity) row[0]  );
                    articleStockStatus.setStockItems( (StockItemsEntity) row[1]  );
                    articleStockStatus.setNumberItems( (int) row[2] );
                listArticleStockStatus.add( articleStockStatus ) ;
            }


            // commit transaction
            transaction.commit();
            return listArticleStockStatus;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>() ;
        }
    }

}
