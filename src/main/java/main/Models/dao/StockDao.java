package main.Models.dao;

import main.Models.entities.PaymentsMadeEntity;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
}
