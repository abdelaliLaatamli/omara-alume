package main.Models.dao;

import main.Models.entities.ArticleEntity;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;
import main.Models.entities.queryContainers.MovementArticle;
import main.Models.entities.queryContainers.StockItemStatus;
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


    /**
     * Save User
     * @param entity
     * @return
     */
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

    /**
     * Update User
     * @param entity
     * @return
     */
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

    /**
     * Delete User
     * @param id
     */
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

    /**
     * Get User By ID
     * @param id
     * @return
     */
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

    /**
     * Get all Users
     * @return
     */
    @SuppressWarnings("unchecked")
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
}
