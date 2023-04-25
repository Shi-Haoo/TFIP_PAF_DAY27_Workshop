package practice.workshop27.restController;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import practice.workshop27.model.EditedComment;
import practice.workshop27.model.Review;
import practice.workshop27.service.BoardGameService;

@RestController
public class BoardGameRestController {

    @Autowired
    BoardGameService svc;

    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> insertReview(@ModelAttribute Review r){
        
        try{
            svc.insertReview(r);
        }catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(r.toJsonInsert().toString());

    }

    @PutMapping(path = "/review/{reviewId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateReview(@RequestBody String json, @PathVariable String reviewId) throws IOException{

        EditedComment ec = EditedComment.convertFromJsonForUpdate(json);

        long modifiedCount = svc.updateReview(ec, reviewId);

        if(modifiedCount == 0){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error: Review not found");

        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Json.createObjectBuilder()
                                .add("Updated", "Number of modified documents is %d".formatted(modifiedCount))
                                .build()
                                .toString());

    }

    @GetMapping(path="/review/{reviewId}")
    public ResponseEntity<String> getReviewByCid(@PathVariable String reviewId){
        
        Optional<Review> reviewOpt = svc.getReviewByCid(reviewId);
        
        if(reviewOpt.isEmpty()){
            return ResponseEntity   
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error: Review not found");
        }

        Review r = reviewOpt.get();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(r.toJson().toString());
    }

    @GetMapping(path="/review/{reviewId}/history")
    public ResponseEntity<String> getHistoryByCid(@PathVariable String reviewId){
        
        Optional<Review> reviewOpt = svc.getReviewByCid(reviewId);
        
        if(reviewOpt.isEmpty()){
            return ResponseEntity   
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error: Review not found");
        }

        Review r = reviewOpt.get();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(r.toJsonHistory().toString());
    }

}
