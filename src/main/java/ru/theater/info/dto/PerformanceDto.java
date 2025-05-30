package ru.theater.info.dto;

import ru.theater.info.enumModel.*;

import java.time.LocalDateTime;

public class PerformanceDto {
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private int duration;
    private Genre genre;
    private AgeRating ageRating;

    public PerformanceDto(){}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public LocalDateTime getDateTime() {return dateTime;}
    public void setDateTime(LocalDateTime dateTime) {this.dateTime = dateTime;}

    public int getDuration() {return duration;}
    public void setDuration(int duration) {this.duration = duration;}

    public Genre getGenre() {return genre;}
    public void setGenre(Genre genre) {this.genre = genre;}

    public AgeRating getAgeRating() {return ageRating;}
    public void setAgeRating(AgeRating ageRating) {this.ageRating = ageRating;}
}
