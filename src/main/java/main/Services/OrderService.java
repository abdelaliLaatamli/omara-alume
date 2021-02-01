package main.Services;

import main.Models.dao.OrderDao;
import main.Models.dao.PaymentsMadeDao;
import main.Models.entities.OrderEntity;

import java.util.List;

public class OrderService {

    private OrderDao orderDao = new OrderDao();
    //private Pay orderDao = new OrderDao();

    public List<OrderEntity> getAllOrders(){

        List<OrderEntity> listOrders = orderDao.getAll();

        return listOrders;

    }

    public boolean addOrder(OrderEntity order ){

        OrderEntity newOrder = orderDao.save( order );

        return newOrder != null ;
    }


    public OrderEntity saveOrder(OrderEntity command ){

        OrderEntity updatedOrder = orderDao.updateEntity( command );

        return updatedOrder ;
    }

    public boolean updateOrder(OrderEntity command ){

        boolean isSaved = orderDao.update( command );
        (new PaymentsMadeDao()).deleteAllNull();;
        return isSaved ;
    }

    public OrderEntity getCommand(int id ){

        OrderEntity newAlum = orderDao.get( id );

        return newAlum;
    }

}
