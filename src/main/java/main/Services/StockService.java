package main.Services;

import main.Models.dao.StockDao;
import main.Models.dao.StockItemsDao;
import main.Models.entities.StockEntity;
import main.Models.entities.queryContainers.MoneyStatus;
import main.Models.entities.queryContainers.MovementArticle;
import main.Models.entities.queryContainers.StockItemStatus;
import main.Models.entities.queryContainers.TurnoverByMonth;

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

    public List<MovementArticle> getMovementProductInStock(int articleId) {
        List<MovementArticle> movementArticles = stockDao.getMovementProductInStock(articleId);
        return movementArticles ;
    }

    public MoneyStatus getMoneyStatus(){
        MoneyStatus moneyStatus = stockDao.getMoneyStatus();
        return moneyStatus ;
    }

    public List<TurnoverByMonth> getTurnoverByMonth() {
        List<TurnoverByMonth> getTurnoverByMonth = stockDao.getTurnoverByMonth();
        return getTurnoverByMonth ;
    }
}
