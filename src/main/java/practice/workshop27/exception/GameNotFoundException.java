package practice.workshop27.exception;

public class GameNotFoundException extends Exception{
    
    public GameNotFoundException(){
        
        super();

    }

    public GameNotFoundException (String message){
        super(message);
    }
    
}
