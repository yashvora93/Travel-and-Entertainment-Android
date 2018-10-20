package com.example.yashvora93.placessearch;

public class Reviews {
    private String author_url;
    private String profile_photo_url;
    private String author_name;
    private Integer ratings;
    private Long reviewDate;
    private String reviewText;

    public Reviews(String author_url, String profile_photo_url, String author_name, Integer ratings, Long reviewDate, String reviewText) {
        this.author_url = author_url;
        this.profile_photo_url = profile_photo_url;
        this.author_name = author_name;
        this.ratings = ratings;
        this.reviewDate = reviewDate;
        this.reviewText = reviewText;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public Integer getRatings() {
        return ratings;
    }

    public void setRatings(Integer ratings) {
        this.ratings = ratings;
    }

    public long getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(long reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public int compareToRatings(Reviews review, int order) {
        int compareQuantity = review.getRatings();
        //ascending order
        if(order == 2)
            return this.getRatings() - compareQuantity;
        else
            return  compareQuantity - this.getRatings();

    }

    public Long compareToDate(Reviews review, int order) {
        Long compareQuantity = review.getReviewDate();
        //ascending order
        if(order == 4)
            return this.getReviewDate() - compareQuantity;
        else
            return compareQuantity - this.getReviewDate();

    }
}
