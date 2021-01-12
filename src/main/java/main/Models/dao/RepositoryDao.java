package main.Models.dao;

import main.Models.entities.Invoice;
import main.Models.enums.PaymentStatus;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositoryDao<T> {


    /**
     * Save User
     * @param
     */
    public void save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
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
    }

    public T save(T entity , String className) {
        Transaction transaction = null;
        T newRntity = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the student object
            //session.save(entity);
            int idEntity = (int) session.save(entity);
            // commit transaction
            transaction.commit();

            newRntity = this.get(idEntity , className );

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return newRntity ;
    }

    /**
     * Update User
     * @param
     */
    public void update( T entity ) {
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
    }


    /**
     * Delete User
     * @param id
     */
    public void delete(int id , String className) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            // Delete a user object
            T entity = (T) session.get(className, id);
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
    public T get(int id , String className ) {

        Transaction transaction = null;
        T entity = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object
            entity = (T) session.get(className, id);
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
    public List< T > getAll( String className ) {

        Transaction transaction = null;
        List < T > listOfEntities = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            listOfEntities = session.createQuery("from "+className).getResultList();

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

    public List<Object> getOrder(int orderId){

        List <Object> listOfEntities = null;

        String query =
                "SELECT " +
                    "OI.id AS order_items_id , " +
                    "OI.name AS order_items_name , " +
                    "OI.quantity AS order_items_quantity , " +
                    "OI.price AS order_items_price , " +
                    "O.orderDate AS orders_orderDate , " +
                    "O.paymentStatus AS orders_paymentStatus , " +
                    "C.name AS clients_name , " +
                    "( SELECT COALESCE( sum(OI.price * OI.quantity ) , 0 )  from OI WHERE OI.order = O ) as total , " +
                    "( SELECT COALESCE( SUM( P.amountPaid ) , 0 ) FROM main.Models.entities.PaymentsMadeEntity as P WHERE P.order = O ) as paid " +
                " FROM " +
                    " main.Models.entities.OrderItemsEntity as OI " +
                    " INNER JOIN main.Models.entities.OrderEntity as O ON OI.order = O " +
                    " INNER JOIN main.Models.entities.ClientEntity C ON O.client = C " +
                " WHERE " +
                    "O.id = " +orderId;


        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

           //listOfEntities = (List<Invoice>) session.createQuery(query).getResultList();
            listOfEntities = session.createQuery(query).getResultList();



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
