package practice.workshop27.repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import practice.workshop27.model.Game;
import practice.workshop27.model.Review;

@Repository
public class BoardGameRepository {
    
    @Autowired
    MongoTemplate template;

    //methods from workshop26

    public List<Game> getGamesByLimitAndOffset(int limit, int offset){
        
        Query q = new Query().skip(offset).limit(limit);

        return template.find(q, Document.class, "games").stream()
                                                         .map(d -> Game.convertFromDocument(d))
                                                         .toList();
                                                    
    }

    public List<Game> getGamesByRank(int limit, int offset, Direction dir, String fieldToSort){
        
        Query q = new Query().skip(offset).limit(limit);

        q.with(Sort.by(dir , fieldToSort));

        return template.find(q, Document.class, "games").stream()
                                                         .map(d -> Game.convertFromDocument(d))
                                                         .toList();
                                                    
    }

    public Optional<Document> getGamesByObjectId(String id){
        ObjectId objectId = new ObjectId(id);

        return Optional.ofNullable(template.findById(objectId, Document.class, "games"));
    }

    public Optional<Game> getGamesById(String id){
        
        Optional<Document> doc = null;
        
        //check whether string id is a valid 24-character hexadecimal string which represents a
        // valid ObjectId used in MongoDB. It does not mean the ObjectId exist in the database.

        if(ObjectId.isValid(id)){
            
            try{
               doc = Optional.ofNullable(template.findById(id, Document.class, "games"));
               return Optional.of(Game.convertFromDocument(doc.get()));
            }catch(NoSuchElementException e){
                return Optional.empty();
            }            
        }
        
        else{
            //If not valid object id, search by gid
            Query q = new Query();
            q.addCriteria(Criteria.where("gid").is(Integer.parseInt(id)));

            try{
            //template.find().stream().findFirst() returns a stream of results,  
            //and then returns the first element of the stream as an Optional.
                doc = (template.find(q, Document.class, "games")).stream().findFirst();
                return Optional.of(Game.convertFromDocument(doc.get()));
            }catch(NoSuchElementException e){
                return Optional.empty();
            }        
        }
    }

    //methods for workshop27 

    public Game getGameByGid(int gid){
        Query q = new Query();
        q.addCriteria(Criteria.where("gid").is(gid));

        //Since we want Game.class, When a query is executed, Spring Data MongoDB maps the document to a Java object
        // based on the structure of the document and the fields defined in the Game class. So the fields in Game.java
        //must have the same name as the attributes in Document.if not all the fields will be null

        Game game = template.findOne(q, Game.class, "games");

        return game;
    }

    
    public Optional<Document> getReviewById(String reviewId){
        Query q = new Query();
        q.addCriteria(Criteria.where("cid").is(Integer.parseInt(reviewId)));

        return template.find(q, Document.class, "reviews").stream().findFirst();
    }


    public ObjectId insertReview(Review r){

        Document doc = new Document();
        doc = Document.parse(r.toJsonInsert().toString());
        Document newDoc = template.insert(doc, "reviews");
        ObjectId oId = newDoc.getObjectId("_id");

        return oId;
    }

    public Long updateReview(Review r){
        
        Query q = new Query();
        q.addCriteria(Criteria.where("cid").is( r.getCid()));

        Update updateOps= new Update()
                            .set("comment", r.getC_text())
                            .set("rating", r.getRating())
                            .set("posted", Review.convertDateToString(r.getPostedDate()))
                            .set("edited", r.getEdited().stream()
                                                            .map(e->e.toJsonUpdate().toString())
                                                            .toList());
                            
        //Can also use Review.class instead of Document.class. 
         UpdateResult updateResult = template.updateMulti(q, updateOps, Document.class, "reviews");

         long modifiedCount = updateResult.getModifiedCount();

         return modifiedCount;
    }

}
