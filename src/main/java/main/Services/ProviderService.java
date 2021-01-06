package main.Services;

import main.Models.dao.ClientDao;
import main.Models.dao.ProviderDao;
import main.Models.entities.ClientEntity;
import main.Models.entities.ProviderEntity;

import java.util.List;

public class ProviderService {

    private ProviderDao providerDao = new ProviderDao();

    public List<ProviderEntity> getAll(){

        List<ProviderEntity> listAlum = providerDao.getAll();

        return listAlum;

    }

    public boolean add(ProviderEntity entity){

        ProviderEntity newClient = providerDao.save( entity);

        return newClient != null ;
    }


    public ProviderEntity save( ProviderEntity entity ){

        ProviderEntity updatedCleint = providerDao.updateEntity( entity );

        return updatedCleint ;
    }

    public boolean update( ProviderEntity entity ){

        boolean isSaved = providerDao.update( entity );

        return isSaved ;
    }

    public ProviderEntity get( int id ){

        ProviderEntity client = providerDao.get( id );

        return client;
    }
}
