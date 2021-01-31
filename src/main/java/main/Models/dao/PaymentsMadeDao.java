package main.Models.dao;

import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PaymentsMadeDao {


    /**
     * Get all Users
     * @return
     */
    @SuppressWarnings("unchecked")
    public void getAll() {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            session.createSQLQuery("DELETE FROM `payements_made` WHERE payements_made.order_id IS NULL").executeUpdate();

            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            //e.printStackTrace();
        }
    }

}
