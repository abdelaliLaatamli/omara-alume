package main.Services;

import main.Models.dao.OrderDao;
import main.Models.entities.OrderEntity;

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

}
