package main.Services;

import main.Models.dao.AccessoryDao;
import main.Models.dao.GlassDao;
import main.Models.entities.AccessoryEntity;
import main.Models.entities.GlassEntity;
import main.Models.entities.PriceEntity;

import java.util.List;

public class GlassService {

    private GlassDao glassDao = new GlassDao();

    public List<GlassEntity> getAllGlassProducts(){

        List<GlassEntity> listAlum = glassDao.getAll();

        return listAlum;

    }

    public boolean addProduct( GlassEntity alum, PriceEntity defaultPrice){

        GlassEntity newAlum = glassDao.save( alum , defaultPrice);

        return newAlum != null ;
    }


    public GlassEntity saveProduct( GlassEntity alum ){

        GlassEntity updatedAlum = glassDao.updateEntity( alum );

        return updatedAlum ;
    }

    public boolean updateProduct( GlassEntity alum ){

        boolean isSaved = glassDao.update( alum );

        return isSaved ;
    }

    public GlassEntity getGlass( int id ){

        GlassEntity newAlum = glassDao.get( id );

        return newAlum;
    }
}
