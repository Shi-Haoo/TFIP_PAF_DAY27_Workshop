package practice.workshop27.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class Review {
    
    private String cid;
    private String user;
    private int rating;
    private String c_text;
    private int gid;
    private LocalDateTime postedDate;
    private String name;
    private List<EditedComment> edited;

    public Review() {
    }


    public Review(String cid, String user, int rating, String c_text, int gid, LocalDateTime postedDate, String name,
            List<EditedComment> edited) {
        this.cid = cid;
        this.user = user;
        this.rating = rating;
        this.c_text = c_text;
        this.gid = gid;
        this.postedDate = postedDate;
        this.name = name;
        this.edited = edited;
    }

    

    public String getCid() {
        return cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getC_text() {
        return c_text;
    }
    public void setC_text(String c_text) {
        this.c_text = c_text;
    }
    public int getGid() {
        return gid;
    }
    public void setGid(int gid) {
        this.gid = gid;
    }
    public LocalDateTime getPostedDate() {
        return postedDate;
    }
    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<EditedComment> getEdited() {
        return edited;
    }
    public void setEdited(List<EditedComment> edited) {
        this.edited = edited;
    }


    @Override
    public String toString() {
        return "Review [cid=" + cid + ", user=" + user + ", rating=" + rating + ", c_text=" + c_text + ", gid=" + gid
                + ", postedDate=" + postedDate + ", name=" + name + ", edited=" + edited + "]";
    }


    public JsonObject toJsonInsert(){

    //To convert LocalDateTime object into String of desired pattern
        String formattedDateTime = convertDateToString(this.getPostedDate());
        
        return Json.createObjectBuilder()
                .add("user", this.getUser())
                .add("rating", this.getRating())
                .add("c_text", this.getC_text())
                .add("gid", this.getGid())

                //instead of using formatter, we can actually use.toString() to convert date to String.
                //But toString() only can convert to default format uuuu-MM-ddTHH:mm:ss.SSS
                //Using formatter allows us to convert to whatever format we desire
                .add("posted", formattedDateTime)
                .add("name", this.getName())
                .add("c_id", this.getCid())
                .build();

    }

    public JsonObject toJsonUpdate(){

        JsonArrayBuilder jab = Json.createArrayBuilder();
        List<JsonObject> listOfPastComments = this.getEdited().stream()
                                            .map(e->e.toJsonUpdate())
                                            .toList();

                                        
        for(JsonObject jOb : listOfPastComments){
             jab.add(jOb);

        }

        String formattedDateTime = convertDateToString(this.getPostedDate());

        return Json.createObjectBuilder()
        .add("user", this.getUser())
        .add("rating", this.getRating())
        .add("c_text", this.getC_text())
        .add("gid", this.getGid())

        //instead of using formatter, we can actually use.toString() to convert date to String.
        //But toString() only can convert to default format uuuu-MM-ddTHH:mm:ss.SSS
        //Using formatter allows us to convert to whatever format we desire
        .add("posted", formattedDateTime)
        .add("name", this.getName())
        .add("edited", jab)
        .build();
    }

    //toJson() for task c
    public JsonObject toJson(){

        String formattedDateTime = convertDateToString(this.getPostedDate());

        return Json.createObjectBuilder()
        .add("user", this.getUser())
        .add("rating", this.getRating())
        .add("c_text", this.getC_text())
        .add("gid", this.getGid())
        .add("posted", formattedDateTime)
        .add("name", this.getName())
        .add("edited", this.getEdited().isEmpty()?false:true)
        .add("timestamp", LocalDateTime.now().toString())
        .build();
    }

    //convert to Json for task d
    public JsonObject toJsonHistory(){

        JsonArrayBuilder jab = Json.createArrayBuilder();
        List<JsonObject> editedList = this.getEdited().stream()
                                                    .map(e->e.toJsonUpdate())
                                                    .toList();
        
        for(JsonObject j : editedList){
            jab.add(j);
        }

        String formattedDateTime = convertDateToString(this.getPostedDate());

        return Json.createObjectBuilder()
                .add("user", this.getUser())
                .add("rating", this.getRating())
                .add("c_text", this.getC_text())
                .add("gid", this.getGid())
                .add("posted", formattedDateTime)
                .add("name", this.getName())
                .add("edited", jab)
                .add("timestamp", LocalDateTime.now().toString())
                .build();
    }

    public static Review convertFromDocument(Document d){

        Review r = new Review();

        r.setUser(d.getString("user"));
        r.setRating(d.getInteger("rating"));
        r.setC_text(d.getString("c_text"));
        r.setGid(d.getInteger("gid"));
        r.setPostedDate(convertStringToDate(d.getString("posted")));
        r.setName(d.getString("name"));
        //retrieve embedded doc of attribute name "edited" and convert it to List<EditedComment> before setting it to Review Object
        List<Document> editedDocs = (List<Document>) d.get("edited");
        r.setEdited(EditedComment.convertFromEmbeddedDoc(editedDocs));
        r.setCid(d.getString("c_id"));

        return r;
        
    }
    
    public static String convertDateToString(LocalDateTime postedDate){
        
        //To convert LocalDateTime object into String of desired pattern
        LocalDateTime posted = postedDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDateTime = posted.format(formatter);
        return formattedDateTime;
    }

    public static LocalDateTime convertStringToDate(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return dateTime;
    }


}
