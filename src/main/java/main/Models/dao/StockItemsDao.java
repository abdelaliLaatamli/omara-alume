package main.Models.dao;

import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class StockItemsDao {


    /**
     * Get all Users
     * @return
     */
    @SuppressWarnings("unchecked")
    public void deleteAllNull() {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            session.createSQLQuery("DELETE FROM stock_items WHERE  stock_items.stock_Id IS NULL").executeUpdate();

            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
