package main.Models.dao;

import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

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

        List < Object > listOfEntities = null;

        String query = "SELECT " +
                "OI.id AS order_items_id , " +
                "OI.name AS order_items_name , " +
                "OI.price AS order_items_price , " +
                "OI.priceOfArticle_id AS order_items_priceOfArticle_id " +
//                "O.`orderDate` AS orders_orderDate, " +
//                "O.`paymentStatus` AS orders_paymentStatus, " +
//                "C.`name` AS clients_name, " +
                " FROM " +
                " main.Models.entities.OrderItemsEntity as OI ";// +
//                "INNER JOIN main.Models.entities.OrderEntity as O ON OI.`order_id` = O.`id` " +
//                "INNER JOIN ClientEntity C ON O.`client_id` = C.`id` " +
//                " WHERE " +
//                "O.`id` = " +orderId;

//        String query = "SELECT " +
//                            "OI.`id` AS order_items_id, " +
//                            "OI.`name` AS order_items_name, " +
//                            "OI.`price` AS order_items_price, " +
//                            "OI.`quantity` AS order_items_quantity, " +
//                            "OI.`priceOfArticle_id` AS order_items_priceOfArticle_id, " +
//                            "O.`orderDate` AS orders_orderDate, " +
//                            "O.`paymentStatus` AS orders_paymentStatus, " +
//                            "C.`name` AS clients_name, " +
//                            "( SELECT sum(OI.`price` * OI.`quantity`) from OI WHERE OI.`order_id` = O.`id` ) as total , " +
//                            "IFNULL(( SELECT SUM( P.`amountPaid`) FROM main.Models.entities.PaymentsMadeEntity as P WHERE P.`order_id` = O.`id` ), 0 )as paid " +
//                        "FROM " +
//                             "main.Models.entities.OrderItemsEntity as OI " +
//                            "INNER JOIN main.Models.entities.OrderEntity as O ON OI.`order_id` = O.`id` " +
//                            "INNER JOIN ClientEntity C ON O.`client_id` = C.`id` " +
//                        "WHERE " +
//                            "O.`id` = " +orderId;

//        String query = "SELECT order_items.`id` AS order_items_id, " +
//                "order_items.`name` AS order_items_name, " +
//                "order_items.`price` AS order_items_price, " +
//                "order_items.`quantity` AS order_items_quantity, " +
//                "order_items.`priceOfArticle_id` AS order_items_priceOfArticle_id, " +
//                "orders.`orderDate` AS orders_orderDate, " +
//                "orders.`paymentStatus` AS orders_paymentStatus, " +
//                "clients.`name` AS clients_name, " +
//                "( SELECT sum(order_items.`price` * order_items.`quantity`) from `order_items` order_items WHERE order_items.`order_id` = orders.`id` ) as total , " +
//                "IFNULL(( SELECT SUM( `payements_made`.`amountPaid`) FROM `payements_made` WHERE `order_id` = orders.`id` ), 0 )as paid " +
//                "FROM " +
//                "`order_items` order_items " +
//                "INNER JOIN `orders` orders ON order_items.`order_id` = orders.`id` " +
//                "INNER JOIN `clients` clients ON orders.`client_id` = clients.`id` " +
//                "WHERE " +
//                "orders.`id` = " +orderId;

//        SELECT
//        order_items.`id` AS order_items_id,
//        order_items.`name` AS order_items_name,
//        order_items.`price` AS order_items_price,
//        order_items.`quantity` AS order_items_quantity,
//        order_items.`priceOfArticle_id` AS order_items_priceOfArticle_id,
//        orders.`orderDate` AS orders_orderDate,
//        orders.`paymentStatus` AS orders_paymentStatus,
//        clients.`name` AS clients_name,
//        ( SELECT ifnull( sum(order_items.`price` * order_items.`quantity`) , 0 ) from  `order_items` order_items WHERE  order_items.`order_id` = orders.`id` ) as total ,
//        ( SELECT ifnull( SUM( `payements_made`.`amountPaid`) , 0 )FROM `payements_made` WHERE `order_id` = orders.`id` ) as paid
//        FROM
//     `order_items` order_items
//        INNER JOIN `orders` orders ON order_items.`order_id` = orders.`id`
//        INNER JOIN `clients` clients ON orders.`client_id` = clients.`id`
//        WHERE
//        orders.`id` = 2


        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

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
