package practice.workshop27.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class Games {

    private int offset;
    private int limit;
    private LocalDateTime timestamp;
    private List<Game> gamesList;
    
    public Games() {
    }

    public Games(int limit, int offset, LocalDateTime timestamp, List<Game> gamesList) {
        this.offset = offset;
        this.limit = limit;
        this.timestamp = timestamp;
        this.gamesList = gamesList;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<Game> getGamesList() {
        return gamesList;
    }

    public void setGamesList(List<Game> gamesList) {
        this.gamesList = gamesList;
    }

    @Override
    public String toString() {
        return "Games [offset=" + offset + ", limit=" + limit + ", timestamp=" + timestamp + ", gamesList=" + gamesList
                + "]";
    }

    public JsonObject toJson(){

        JsonArrayBuilder jab = Json.createArrayBuilder();
        
        //Game Object has fields all set with values but we build JsonObject 
        //with only the fields required for task a
        this.getGamesList().forEach(j -> jab.add(j.toJsonByLimitAndOffset()));

        //need convert timestamp to String aka Stringify because JsonObjectBuilder only 
        //accepts JsonObject/JsonObjectBuilder/Primitive Data Types
        return Json.createObjectBuilder()
                .add("games", jab)
                .add("offset", this.getOffset())
                .add("limit", this.getLimit())
                .add("total", this.getGamesList().size())
                .add("timestamp", this.getTimestamp().toString())
                .build();
    }




}
