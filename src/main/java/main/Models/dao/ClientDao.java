package main.Models.dao;

import main.Models.entities.ArticleEntity;
import main.Models.entities.ClientEntity;
import main.Models.entities.GlassEntity;
import main.Models.entities.PriceEntity;
import main.Models.entities.queryContainers.ClientCredit;
import main.Models.entities.queryContainers.StockItemStatus;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ClientDao {


    /**
     * Save User
     * @param entity
     * @return
     */
    public ClientEntity saveClient(ClientEntity entity) {
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
            return null ;
        }
        return entity;
    }

    public boolean save(ClientEntity entity) {
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
            return false ;
        }
        return true;
    }

    /**
     * Update User
     * @param entity
     * @return
     */
    public ClientEntity updateEntity(ClientEntity entity) {
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

    public boolean update(ClientEntity entity) {
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
            ClientEntity entity = session.get(ClientEntity.class, id);
            if (entity != null) {
                session.delete(entity);
                System.out.println("Client is deleted");
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
    public ClientEntity get(int id) {

        Transaction transaction = null;
        ClientEntity entity = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object
            entity = session.get(ClientEntity.class, id);
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
    public List< ClientEntity > getAll() {

        Transaction transaction = null;
        List < ClientEntity > listOfEntities = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            listOfEntities = session.createQuery("from main.Models.entities.ClientEntity").getResultList();

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


    public List<ClientCredit> getAllWithCredit() {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object



//            listOfEntities = session.createQuery("from main.Models.entities.ClientEntity").getResultList();
            List<Object[]> rows = session.createQuery(" SELECT C ,  ( " +
                    " SELECT ROUND( COALESCE( sum( PM.amountPaid ) , 0 ) + C.oldCredit , 2 )" +
                    " FROM main.Models.entities.PaymentsMadeEntity as PM WHERE PM.order in (SELECT O.id from main.Models.entities.OrderEntity as O WHERE O.client = C ) ) as creaditClient " +
                    " From main.Models.entities.ClientEntity as C").list();

            List<ClientCredit> listOfEntities = new ArrayList<>();

            for (Object[] row : rows) {
                ClientCredit clientCredit = new ClientCredit();
                clientCredit.setClient( (ClientEntity) row[0] );
                clientCredit.setCredit((Double) row[1]);
                listOfEntities.add( clientCredit ) ;
            }

            // commit transaction
            transaction.commit();


            return listOfEntities ;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>();
        }

    }




}
