package main.Models.dao;

import main.Models.entities.*;
import main.Models.enums.PaymentStatus;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandDao {

    /**
     * Save User
     * @param entity
     * @return
     */
    public CommandEntity save(CommandEntity entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            Set<PaymentsMadeEntity> savedPayement = new HashSet<>();

            for (PaymentsMadeEntity paymentsMadeEntity: entity.getPaymentsMades() ) {
                if( paymentsMadeEntity.getAmountPaid() != 0 ){
                    session.save( paymentsMadeEntity );
                    savedPayement.add( paymentsMadeEntity );
                }
            }

            float sumMout = savedPayement.stream().map( c -> c.getAmountPaid() ).reduce( 0f , ( subSum , elem ) -> subSum + elem );
            if( sumMout == 0 ) entity.setPaymentStatus( PaymentStatus.CREDIT );

            Set< ArticleCommandEntity > savedArticleCommands = new HashSet<>();

            for ( ArticleCommandEntity articleCommandEntity : entity.getArticleCommands() ){
                session.save( articleCommandEntity );
                savedArticleCommands.add( articleCommandEntity );
            }

            entity.setPaymentsMades( savedPayement );
            entity.setArticleCommands( savedArticleCommands );

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

//    public CommandEntity save(CommandEntity entity) {
//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            // start a transaction
//            transaction = session.beginTransaction();
//
//            Set<PaymentsMadeEntity> savedPayement = new HashSet<>();
//
//            for (PaymentsMadeEntity paymentsMadeEntity: entity.getPaymentsMades() ) {
//                if( paymentsMadeEntity.getAmountPaid() != 0 ){
//                    session.save( paymentsMadeEntity );
//                    savedPayement.add( paymentsMadeEntity );
//                }
//            }
//
//            Set< ArticleCommandEntity > savedArticleCommands = new HashSet<>();
//
//            for ( ArticleCommandEntity articleCommandEntity : entity.getArticleCommands() ){
//                session.save( articleCommandEntity );
//                savedArticleCommands.add( articleCommandEntity );
//            }
//
//            entity.setPaymentsMades( savedPayement );
//            entity.setArticleCommands( savedArticleCommands );
//
//            // save the student object
//            session.save(entity);
//            // commit transaction
//            transaction.commit();
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//        return entity;
//    }


    /**
     * Update User
     * @param entity
     * @return
     */
    public CommandEntity updateEntity(CommandEntity entity) {
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

    public boolean update(CommandEntity entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the student object

            Set<PaymentsMadeEntity> savedPayement = new HashSet<>();
            for (PaymentsMadeEntity paymentsMadeEntity: entity.getPaymentsMades() ) {

                //if( paymentsMadeEntity.getId() == 0 || paymentsMadeEntity.getAmountPaid() == 0 ){
                if (paymentsMadeEntity.getAmountPaid() != 0) {
                    if (paymentsMadeEntity.getId() == 0)
                        session.save(paymentsMadeEntity);

                    savedPayement.add(paymentsMadeEntity);
                }
            }
            float sumMout = savedPayement.stream().map( c -> c.getAmountPaid() ).reduce( 0f , ( subSum , elem ) -> subSum + elem );
            if( sumMout == 0 ) entity.setPaymentStatus( PaymentStatus.CREDIT );

            Set< ArticleCommandEntity > savedArticleCommands = new HashSet<>();

            for ( ArticleCommandEntity articleCommandEntity : entity.getArticleCommands() ){
                session.save( articleCommandEntity );
                savedArticleCommands.add( articleCommandEntity );
            }

            entity.setPaymentsMades( savedPayement );
            entity.setArticleCommands( savedArticleCommands );


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
            CommandEntity entity = session.get(CommandEntity.class, id);
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
    public CommandEntity get(int id) {

        Transaction transaction = null;
        CommandEntity entity = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object
            entity = session.get(CommandEntity.class, id);
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
    public List< CommandEntity > getAll() {

        Transaction transaction = null;
        List < CommandEntity > listOfEntities = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // get an user object

            listOfEntities = session.createQuery("from main.Models.entities.CommandEntity C WHERE C.isCanceled = 0").getResultList();

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
