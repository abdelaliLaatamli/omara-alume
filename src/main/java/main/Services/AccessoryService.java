package main.Services;

import main.Models.dao.AccessoryDao;
import main.Models.entities.AccessoryEntity;
import main.Models.entities.AluminumEntity;
import main.Models.entities.PriceEntity;

import java.util.List;

public class AccessoryService {

    private AccessoryDao accessoryDao = new AccessoryDao();

    public List<AccessoryEntity> getAllAccessoryProducts(){

        List<AccessoryEntity> listAlum = accessoryDao.getAll();

        return listAlum;

    }

    public boolean addProduct( AccessoryEntity alum, PriceEntity defaultPrice){

        AccessoryEntity newAlum = accessoryDao.save( alum , defaultPrice);

        return newAlum != null ;
    }


    public AccessoryEntity saveProduct( AccessoryEntity alum ){

        AccessoryEntity updatedAlum = accessoryDao.updateEntity( alum );

        return updatedAlum ;
    }

    public boolean updateProduct( AccessoryEntity alum ){

        boolean isSaved = accessoryDao.update( alum );

        return isSaved ;
    }

    public AccessoryEntity getAlumenuim( int id ){

        AccessoryEntity newAlum = accessoryDao.get( id );

        return newAlum;
    }
}
