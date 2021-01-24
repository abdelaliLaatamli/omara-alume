package main.Services;

import main.Models.dao.OrderDao;
import main.Models.entities.OrderEntity;

import java.util.List;

public class OrderService {

    private OrderDao orderDao = new OrderDao();

    public List<OrderEntity> getAllCommands(){

        List<OrderEntity> listCommands = orderDao.getAll();

        return listCommands;

    }

    public boolean addOrder(OrderEntity command ){

        OrderEntity newAlum = orderDao.save( command );

        return newAlum != null ;
    }


    public OrderEntity saveOrder(OrderEntity command ){

        OrderEntity updatedOrder = orderDao.updateEntity( command );

        return updatedOrder ;
    }

    public boolean updateOrder(OrderEntity command ){

        boolean isSaved = orderDao.update( command );

        return isSaved ;
    }

    public OrderEntity getCommand(int id ){

        OrderEntity newAlum = orderDao.get( id );

        return newAlum;
    }

}
