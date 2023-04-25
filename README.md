In Repository:

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
                            
        
         UpdateResult updateResult = template.updateMulti(q, updateOps, Document.class, "reviews");
         ...}

Q1: Why do we need to convert the values to String/Json/Primitive Data Type in .set()?

Ans: 
MongoDB stores data as BSON documents, which support a limited set of data types. The Update class builds up a set of these field/value pairs as a BSON document, which is then used to update the matching documents in the MongoDB collection.

When updating a BSON document in the database using the MongoDB Java driver, the set values should generally be of primitive data types or simple objects that can be automatically serialized to BSON by the driver. This includes the following data types:

Primitive data types (int, long, double, etc.)
Strings
Dates (java.util.Date, java.time.LocalDateTime, etc.)
Lists and arrays of primitive data types or simple objects
Maps and POJOs (Plain Old Java Objects) that can be mapped to BSON using a registered codec
In general, it's best to stick to these simple data types when working with the MongoDB Java driver, as more complex objects may require custom serialization or deserialization logic.

Q2:
UpdateResult updateResult = template.updateMulti(q, updateOps, Review.class, "reviews");

We can also use Review.class instead of Document.class

Ans:
In the case of the mongoTemplate.update method, the Review.class parameter is used to specify the type of documents stored in the "reviews" collection. This tells Spring Data MongoDB to map the results of the update operation to Review objects.

When a query is executed, Spring Data MongoDB maps the document to a Java object based on the structure of the document and the fields defined in the Review class. If the document does not match the expected structure of the Review class, the mapping may fail or produce unexpected results.

So for the mapping to be successful, field names in Review.class must be the same as that in the Documents.