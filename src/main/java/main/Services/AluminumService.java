package main.Services;

import main.Models.dao.AluminumDao;
import main.Models.entities.AluminumEntity;
import main.Models.entities.PriceEntity;

import java.util.List;


public class AluminumService {

    private AluminumDao aluminumDao = new AluminumDao();

    public List<AluminumEntity> getAllAlumunuimProducts(){

        List<AluminumEntity> listAlum = aluminumDao.getAll();

        return listAlum;

    }

    public boolean addProductAlum(AluminumEntity alum, PriceEntity defaultPrice){

        AluminumEntity newAlum = aluminumDao.save( alum , defaultPrice);

        return newAlum != null ;
    }


    public AluminumEntity saveProduct( AluminumEntity alum ){

        AluminumEntity updatedAlum = aluminumDao.update( alum );

        return updatedAlum ;
    }

    public AluminumEntity getAlumenuim( int id ){

        AluminumEntity newAlum = aluminumDao.get( id );

        return newAlum;
    }


}
