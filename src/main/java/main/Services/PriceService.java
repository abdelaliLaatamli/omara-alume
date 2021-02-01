package main.Services;

import main.Models.dao.OrderDao;
import main.Models.dao.PaymentsMadeDao;
import main.Models.dao.PriceDao;
import main.Models.entities.OrderEntity;
import main.Models.entities.PriceEntity;

import java.util.List;

public class PriceService {

    private PriceDao priceDao = new PriceDao();

//    public List<OrderEntity> getAllCommands(){
//
//        List<OrderEntity> listCommands = orderDao.getAll();
//
//        return listCommands;
//
//    }

//    public boolean addOrder(OrderEntity command ){
//
//        OrderEntity newAlum = orderDao.save( command );
//
//        return newAlum != null ;
//    }

    public void addPrices(List<PriceEntity> prices ){

        for ( PriceEntity priceEntity  : prices)
            priceDao.save( priceEntity );


//        OrderEntity newAlum = orderDao.save( command );
//
//        return newAlum != null ;
    }


//    public OrderEntity saveOrder(OrderEntity command ){
//
//        OrderEntity updatedOrder = orderDao.updateEntity( command );
//
//        return updatedOrder ;
//    }
//
//    public boolean updateOrder(OrderEntity command ){
//
//        boolean isSaved = orderDao.update( command );
//        (new PaymentsMadeDao()).deleteAllNull();;
//        return isSaved ;
//    }
//
//    public OrderEntity getCommand(int id ){
//
//        OrderEntity newAlum = orderDao.get( id );
//
//        return newAlum;
//    }
}
