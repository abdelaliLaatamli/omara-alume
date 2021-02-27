package main.Models.dao;

import lombok.SneakyThrows;
import main.Logging.FileHandlerLogger;
import main.Models.entities.*;
import main.Models.enums.PaymentStatus;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDao {


    Logger logger = FileHandlerLogger.getHandlerFile( this.getClass().getName() );

    public OrderEntity save(OrderEntity entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            Set<PaymentsMadeEntity> savedPayment = new HashSet<>();

            for (PaymentsMadeEntity paymentsMadeEntity: entity.getPaymentsMades() ) {
                if( paymentsMadeEntity.getAmountPaid() != 0 ){
                    session.save( paymentsMadeEntity );
                    savedPayment.add( paymentsMadeEntity );
                }
            }

            float sumMount = savedPayment.stream().map( c -> c.getAmountPaid() ).reduce( 0f , ( subSum , elem ) -> subSum + elem );
            if( sumMount == 0 ) entity.setPaymentStatus( PaymentStatus.CRÉDIT );

            Set<OrderItemsEntity> savedArticleOrders = new HashSet<>();

            for ( OrderItemsEntity orderItemsEntity : entity.getArticleOrders() ){
                session.save(orderItemsEntity);
                savedArticleOrders.add(orderItemsEntity);
            }

            entity.setPaymentsMades( savedPayment );
            entity.setArticleOrders( savedArticleOrders );

            session.save(entity);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.log (Level.SEVERE, e.getMessage() );
        }
        return entity;
    }

    public OrderEntity updateEntity(OrderEntity entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the student object

            // session.update();

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

    public boolean update(OrderEntity entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            Set<PaymentsMadeEntity> savedPayments = new HashSet<>();
            for (PaymentsMadeEntity paymentsMadeEntity: entity.getPaymentsMades() ) {
                if (paymentsMadeEntity.getAmountPaid() != 0) {
                    if( paymentsMadeEntity.getId() == 0 )
                        session.save(paymentsMadeEntity);
                    savedPayments.add(paymentsMadeEntity);
                }
            }

            float sumMount = savedPayments.stream().map( c -> c.getAmountPaid() ).reduce( 0f , ( subSum , elem ) -> subSum + elem );
            if( sumMount == 0 ) entity.setPaymentStatus( PaymentStatus.CRÉDIT );

            Set<OrderItemsEntity> savedArticleCommands = new HashSet<>();

            for ( OrderItemsEntity orderItemsEntity : entity.getArticleOrders() ){
                if( orderItemsEntity.getId() == 0 )
                    session.save(orderItemsEntity);
                else
                    session.update(orderItemsEntity);
                savedArticleCommands.add(orderItemsEntity);
            }

            entity.setPaymentsMades( savedPayments );
            entity.setArticleOrders( savedArticleCommands );


            session.update(entity);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.log (Level.SEVERE, e.getMessage() );
            return false;
        }
        return true;
        //return entity;
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
            OrderEntity entity = session.get(OrderEntity.class, id);
            if (entity != null) {
                session.delete(entity);
                System.out.println("user is deleted");
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
    public OrderEntity get(int id) {

        Transaction transaction = null;
        OrderEntity entity = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object
            entity = session.get(OrderEntity.class, id);
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
    public List<OrderEntity> getAll() {

        Transaction transaction = null;
        List <OrderEntity> listOfEntities = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            listOfEntities = session.createQuery("from main.Models.entities.OrderEntity OE WHERE OE.isCanceled = 0  ORDER BY OE.orderDate DESC").getResultList();

            // commit transaction
            transaction.commit();
        } catch (Exception e) {

            logger.severe( e.getMessage() );
            if (transaction != null) {
                transaction.rollback();
            }
            //e.printStackTrace();
            return new ArrayList<OrderEntity>();
        }
        return listOfEntities;
    }

}
