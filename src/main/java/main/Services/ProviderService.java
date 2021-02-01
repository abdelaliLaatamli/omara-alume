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

        boolean saved = providerDao.addProvider( entity);

        return saved ;
    }


    public ProviderEntity save( ProviderEntity entity ){

        ProviderEntity updatedClient = providerDao.updateEntity( entity );

        return updatedClient ;
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
