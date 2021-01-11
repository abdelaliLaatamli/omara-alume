package main.Services;

import main.Models.dao.OrderDao;
import main.Models.dao.RepositoryDao;
import main.Models.entities.OrderEntity;
import main.Models.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class OrderService {

    private OrderDao commandDao = new OrderDao();

    public List<OrderEntity> getAllCommands(){

        List<OrderEntity> listCommands = commandDao.getAll();

        return listCommands;

    }

    //public boolean addProduct( CommandEntity command, PriceEntity defaultPrice){
    public boolean addCommand( OrderEntity command ){

        OrderEntity newAlum = commandDao.save( command );

        return newAlum != null ;
    }


    public OrderEntity saveCommand(OrderEntity command ){

        OrderEntity updatedCommand = commandDao.updateEntity( command );

        return updatedCommand ;
    }

    public boolean updateOrder(OrderEntity command ){

        boolean isSaved = commandDao.update( command );

        return isSaved ;
    }

    public OrderEntity getCommand(int id ){

        OrderEntity newAlum = commandDao.get( id );

        return newAlum;
    }
    private RepositoryDao<Object> repositoryDao= new RepositoryDao<Object>();

    public List<Object> getOrder(int i) {
        List<Object>  aa = null ;
        aa = repositoryDao.getOrder( i );
        System.out.println(aa);
        return aa ;
    }
}
