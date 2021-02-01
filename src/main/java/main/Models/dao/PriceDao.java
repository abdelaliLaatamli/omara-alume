package main.Models.dao;

import main.Models.entities.PriceEntity;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class PriceDao {

    public boolean save(PriceEntity entity) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            session.save(entity);
            // commit transaction

            transaction.commit();

            return true ;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        }
    }



}
