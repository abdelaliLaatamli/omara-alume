package main.Services;

import main.Models.dao.RepositoryDao;
import main.Models.entities.AluminumEntity;

import java.util.List;


public class AluminumService {

    private RepositoryDao<AluminumEntity> aluminumDoa = new RepositoryDao<AluminumEntity>();

    public List<AluminumEntity> getAllAlumunuimProducts(){

        List<AluminumEntity> listAlum = aluminumDoa.getAll("main.Models.entities.AluminumEntity");

        return listAlum;

    }

    public boolean addProductAlum( AluminumEntity alum ){

        AluminumEntity newAlum = aluminumDoa.save( alum , "main.Models.entities.AluminumEntity" );

        return newAlum != null ;
    }


    public boolean saveProduct( AluminumEntity alum ){

        return false;
    }

    public AluminumEntity getAlumenuim( int id ){



        return null;
    }


}
