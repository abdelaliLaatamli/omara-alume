package main.Services;

import main.Models.dao.CommandDao;
import main.Models.entities.CommandEntity;

import java.util.List;

public class CommandService {

    private CommandDao commandDao = new CommandDao();

    public List<CommandEntity> getAllCommands(){

        List<CommandEntity> listCommands = commandDao.getAll();

        return listCommands;

    }

    //public boolean addProduct( CommandEntity command, PriceEntity defaultPrice){
    public boolean addCommand( CommandEntity command ){

        CommandEntity newAlum = commandDao.save( command );

        return newAlum != null ;
    }


    public CommandEntity saveCommand( CommandEntity command ){

        CommandEntity updatedCommand = commandDao.updateEntity( command );

        return updatedCommand ;
    }

    public boolean updateOrder(CommandEntity command ){

        boolean isSaved = commandDao.update( command );

        return isSaved ;
    }

    public CommandEntity getCommand( int id ){

        CommandEntity newAlum = commandDao.get( id );

        return newAlum;
    }


}
