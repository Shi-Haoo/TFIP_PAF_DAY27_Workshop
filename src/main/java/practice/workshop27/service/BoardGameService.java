package practice.workshop27.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import practice.workshop27.exception.GameNotFoundException;
import practice.workshop27.model.EditedComment;
import practice.workshop27.model.Game;
import practice.workshop27.model.Review;
import practice.workshop27.repository.BoardGameRepository;

@Service
public class BoardGameService {
    
    //methods from workshop26
    @Autowired
    BoardGameRepository repo;

    public List<Game> getGamesByLimitAndOffset(int limit, int offset){

        return repo.getGamesByLimitAndOffset(limit, offset);


    }

    public List<Game> getGamesByRank(int limit, int offset, Direction dir, String fieldToSort){

        return repo.getGamesByRank(limit, offset, dir, fieldToSort);


    }

    public Game getGamesByObjectId(String objectId){

        if(repo.getGamesByObjectId(objectId).get().isEmpty()){
            return null;
        }

        return Game.convertFromDocument(repo.getGamesByObjectId(objectId).get());
    }

    
    public Optional<Game> getGamesById(String id){
        return repo.getGamesById(id);
    }


    //methods from workshop 27

    public Review insertReview(Review r) throws GameNotFoundException{

        Game g = repo.getGameByGid(r.getGid());

        if(g == null){

            System.out.println("game not found !");
            throw new GameNotFoundException("Game not found!!");
        }

        r.setPostedDate(LocalDateTime.now());
        r.setName(g.getName());

        repo.insertReview(r);

        return r;
    }


    public long updateReview(EditedComment comment, String reviewId){

        Optional<Document> doc = repo.getReviewById(reviewId);

        //No document with specified cid found. So no update will take place. 
        //Return 0 number of documents modified
        if(doc.isEmpty()){
            return 0;
        }

        Review r = Review.convertFromDocument(doc.get());

        List<EditedComment> pastComments = new ArrayList<>();

        for(EditedComment ec : r.getEdited()){
            pastComments.add(ec);
        }

        //retrieve existing comment, rating and posted from Review object, 
        //create EditedComment object with those values and add it into pastComments

        EditedComment lastUpdatedComment = new EditedComment(r.getC_text(), r.getRating(), r.getPostedDate());
        
        pastComments.add(lastUpdatedComment);

        r.setC_text(comment.getC_text());
        r.setRating(comment.getRating());
        r.setPostedDate(r.getPostedDate());
        r.setEdited(pastComments);

        return repo.updateReview(r);
        
    }

    public Optional<Review> getReviewByCid(String reviewId){
        Optional<Document> doc = repo.getReviewById(reviewId);

        if(doc.isEmpty()){
            return Optional.empty();

        }

        Review r = Review.convertFromDocument(doc.get());

        return Optional.of(r);
        
    }

}