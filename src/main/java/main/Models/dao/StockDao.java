package main.Models.dao;

import main.Models.entities.ArticleEntity;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;
import main.Models.entities.queryContainers.*;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;
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

            listOfEntities = session.createQuery("from main.Models.entities.StockEntity").getResultList();

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

            //" from main.Models.entities.ArticleEntity A WHERE A.id=:id")
            List<Object[]> rows = session.createQuery("SELECT A , " +
                    "(SELECT COALESCE( SUM( ST.quantity ) , 0 )  FROM main.Models.entities.StockItemsEntity as ST WHERE ST.article=A) as inProducts ," +
                    "(SELECT COALESCE( sum( IO.quantity ) , 0 ) FROM main.Models.entities.OrderItemsEntity IO WHERE IO.article=A and IO.order IS NOT NULL) as outProducts " +
                    " from main.Models.entities.ArticleEntity A")
            //        .setParameter("id",3)
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
            return new ArrayList<StockItemStatus>() ;
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
                            " WHERE oi.article_id = :id and oi.order_id is NOT null and oi.order_id=o.id" +
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
                    "select "+
                            "( SELECT ROUND( ifnull( sum(oi.quantity*oi.price) , 0 ) , 2 ) FROM orders as o , order_items as oi " +
                            "    WHERE EXTRACT(YEAR_MONTH FROM o.orderDate ) = EXTRACT(YEAR_MONTH FROM Now() ) and oi.order_id=o.id ) as vende_month , " +
                            "( SELECT ROUND( ifnull( sum( si.priceOfBuy * si.quantity ) , 0 ) , 2) FROM stock as s , stock_items as si " +
                            "    WHERE EXTRACT(YEAR_MONTH FROM s.importedAt ) = EXTRACT(YEAR_MONTH FROM Now() ) and si.stock_Id=s.Id ) as total_achat , " +
                            "( SELECT ROUND( ifnull( sum(oi.quantity*oi.price) , 0 ) , 2 ) FROM orders as o , order_items as oi " +
                            "    WHERE oi.order_id=o.id and year(o.orderDate) = year( now() ) ) as global_vende , " +
                            "( SELECT ROUND( ifnull( sum( si.priceOfBuy * si.quantity ) , 0 ) , 2) FROM stock as s , stock_items as si " +
                            "    WHERE si.stock_Id=s.Id AND year( s.importedAt ) = year( now() ) ) as global_achat , " +
                            "( SELECT round( ifnull( sum(pm.amountPaid) , 0 ) , 2 ) FROM payements_made as pm, orders as o " +
                            "    WHERE EXTRACT(YEAR_MONTH FROM o.orderDate ) = EXTRACT(YEAR_MONTH FROM Now() ) and pm.order_id=o.id ) as paid_month , " +
                            "(select vende_month - paid_month ) as creadit_month , " +
                            "( SELECT round( ifnull( sum(pm.amountPaid) , 0 ) , 2 ) FROM payements_made as pm " +
                            "    WHERE year(pm.paymentDate)=year(now()) and `order_id` is not null ) as global_paid , " +
                            "( select global_vende - global_paid ) global_creadit").list().get(0);

            MoneyStatus moneyStatus = new MoneyStatus();

            moneyStatus.setSalesOfMonth( (double) row[0] );
            moneyStatus.setPurchaseOfMonth( (double) row[1] );
            moneyStatus.setSalesGlobal( (double) row[2] );
            moneyStatus.setPurchaseGlobal( (double) row[3] );
            moneyStatus.setPaymentsOfMonth( (double) row[4] );
            moneyStatus.setCreditOfMonth( (double) row[5] );
            moneyStatus.setPaymentsGlobal( (double) row[6] );
            moneyStatus.setCreditGlobal( (double) row[7] );


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

    public List<ProductEnter> getProductsEnter(int month) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

//            List<Object[]> rows = session.createSQLQuery(
//                    " SELECT " +
//                            "    round( si.priceOfBuy , 2 ) as priceOfBuy , " +
//                            "    round( si.quantity , 2 ) as quantity  , " +
//                            "    s.importedAt as date_importation , " +
//                            "    s.name as facture_label ,  " +
//                            "    p.name as product_name " +
//                            " FROM  " +
//                            "    stock_items as si , " +
//                            "    stock as s , " +
//                            "    providers as p " +
//                            " WHERE " +
//                            "    year(s.importedAt) = year(now()) and month(s.importedAt) = 2 and " +
//                            "    si.stock_Id=s.Id and " +
//                            "    s.provider_id = p.id "
//                            )
//                    .list();

            List<Object[]> rows = session.createSQLQuery(
                    " SELECT " +
                            "    a.name as product_name ," +
                            "    round( si.priceOfBuy , 2 ) as priceOfBuy , " +
                            "    round( si.quantity , 2 ) as quantity  , " +
                            "    s.importedAt as date_importation , " +
                            "    s.name as facture_label ,  " +
                            "    p.name as product_name " +
                            " FROM  " +
                            "    stock_items as si , " +
                            "    stock as s , " +
                            "    providers as p , " +
                            "    articles as a " +
                            " WHERE " +
                            "    year(s.importedAt) = year(now()) and month(s.importedAt) = 2 and " +
                            "    si.stock_Id=s.Id and " +
                            "    s.provider_id = p.id and " +
                            "    si.article_id=a.id")
                    .list();


            List<ProductEnter> listProductEnter = new ArrayList<>();

            for (Object[] row : rows) {
                ProductEnter productEnter = new ProductEnter();
                productEnter.setProductName( (String) row[0] );
                productEnter.setPriceOfBuy( (double) row[1] );
                productEnter.setQuantity( (double) row[2] );
                productEnter.setDateImportation( ((Timestamp) row[3]).toInstant() );
                productEnter.setFactureLabel( (String) row[4]);
                productEnter.setArticleName( (String) row[5]);
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
}
