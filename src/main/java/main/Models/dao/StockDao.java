package main.Models.dao;

import main.Models.entities.ArticleEntity;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;
import main.Models.entities.queryContainers.StockItemStatus;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
        List < StockEntity > listOfEntities = null;
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
}
