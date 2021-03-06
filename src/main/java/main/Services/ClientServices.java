package main.Services;

import main.Models.dao.AluminumDao;
import main.Models.dao.ClientDao;
import main.Models.entities.AluminumEntity;
import main.Models.entities.ClientEntity;
import main.Models.entities.PriceEntity;
import main.Models.entities.queryContainers.ClientCredit;

import java.util.List;

public class ClientServices {

    private ClientDao clientDao = new ClientDao();

    public List<ClientEntity> getAll(){

        List<ClientEntity> listAlum = clientDao.getAll();

        return listAlum;

    }

    public boolean addClient(ClientEntity entity){

        boolean saved = clientDao.save( entity);

        return saved ;
    }


    public ClientEntity saveProduct( ClientEntity entity ){

        ClientEntity updatedCleint = clientDao.updateEntity( entity );

        return updatedCleint ;
    }

    public boolean updateClient( ClientEntity entity ){

        boolean isSaved = clientDao.update( entity );

        return isSaved ;
    }

    public ClientEntity getClient( int id ){

        ClientEntity client = clientDao.get( id );

        return client;
    }



    public List<ClientCredit> getAllWithCredit() {

        List<ClientCredit>  clients = clientDao.getAllWithCredit(  );

        return clients;
    }

}
