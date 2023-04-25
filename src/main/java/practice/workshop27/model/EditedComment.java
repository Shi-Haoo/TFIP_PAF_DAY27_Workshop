package practice.workshop27.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class EditedComment {
    
private String c_text;
private int rating;
private LocalDateTime postedDate;

public EditedComment() {
}


public EditedComment(String c_text, int rating, LocalDateTime postedDate) {
    this.c_text = c_text;
    this.rating = rating;
    this.postedDate = postedDate;
}


public String getC_text() {
    return c_text;
}
public void setC_text(String c_text) {
    this.c_text = c_text;
}
public int getRating() {
    return rating;
}
public void setRating(int rating) {
    this.rating = rating;
}
public LocalDateTime getPostedDate() {
    return postedDate;
}
public void setPostedDate(LocalDateTime postedDate) {
    this.postedDate = postedDate;
}


@Override
public String toString() {
    return "EditedComment [c_text=" + c_text + ", rating=" + rating + ", postedDate=" + postedDate + "]";
}

public JsonObject toJsonUpdate(){
    
    String formattedDateTime = convertDateToString(this.getPostedDate());
    
    return Json.createObjectBuilder()
            .add("comment", this.getC_text())
            .add("rating", this.getRating())
            .add("posted", formattedDateTime)
            .build();
}


public static EditedComment convertFromJsonForUpdate(String json) throws IOException{

    EditedComment ec = new EditedComment();

    if(json != null){
        try(InputStream is = new ByteArrayInputStream(json.getBytes())){
            JsonReader r = Json.createReader(is);
            JsonObject jOb = r.readObject();

            ec.setC_text(jOb.getString("comment"));
            ec.setRating(jOb.getInt("rating"));
            ec.setPostedDate(LocalDateTime.now());
        }
    }

    return ec;

}


//Help to convert Document with attribute "edited" from List<Document> to List<editedComment>
public static List<EditedComment> convertFromEmbeddedDoc(List<Document> editedDocs){
    
    List<EditedComment> editedComments = new ArrayList<>();
    EditedComment ec = new EditedComment();

    //If "edited" attribute does not exist, it will throw null pointer exception
    //at the line for(Document editedDoc : editedDocs), since editedDocs would be null 
    //So we need to check whether editedDocs is null before iterating
    if(editedDocs != null){
        for(Document editedDoc : editedDocs){
            ec.setC_text(editedDoc.getString("comment"));
            ec.setRating(editedDoc.getInteger("rating"));
            ec.setPostedDate(convertStringToDate(editedDoc.getString("posted")));
            editedComments.add(ec);
        }
    }
    

    return editedComments;
}


public String convertDateToString(LocalDateTime postedDate){
            
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
