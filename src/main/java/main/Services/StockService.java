package main.Services;

import main.Models.dao.StockDao;
import main.Models.dao.StockItemsDao;
import main.Models.entities.StockEntity;
import main.Models.entities.queryContainers.StockItemStatus;

import java.util.List;

public class StockService {

    private StockDao stockDao = new StockDao();

    public List<StockEntity> getAll(){

        List<StockEntity> listEntity = stockDao.getAll();

        return listEntity;

    }

    public boolean add(StockEntity entity){

        StockEntity newEntity = stockDao.save( entity );

        return newEntity != null ;
    }


    public StockEntity save( StockEntity entity ){

        StockEntity updatedEntity = stockDao.updateEntity( entity );

        return updatedEntity ;
    }

    public boolean update( StockEntity entity ){

        boolean isSaved = stockDao.update( entity );
        new StockItemsDao().deleteAllNull();
        return isSaved ;
    }

    public StockEntity get( int id ){

        StockEntity entities = stockDao.get( id );

        return entities;
    }

    public List<StockItemStatus> getStockProductStatus() {

        List<StockItemStatus> listStockItemStatuses = stockDao.getStockProductStatus();
        return listStockItemStatuses ;
    }
}
