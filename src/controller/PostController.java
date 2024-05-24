package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostController{
    @FXML
    private Label authorLabel;

    @FXML
    private Label tagAuthor;
    
    @FXML
    private Label cmtLabel;

    @FXML
    private Label viewLabel;
    
    @FXML
    private Label contentLabel;

    @FXML
    private ImageView image;

    @FXML
    private Label reactionLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label shareLabel;

    public void setData(Facebook post){
        authorLabel.setText(post.getAuthor());
        timeLabel.setText(timeParse(post.getPublishedAt()));
        contentLabel.setText(post.getContent().toString());
        String imgURL = post.getImgUrl();
        if(imgURL.equals("null")) {
        	image.setImage(new Image("https://i.pinimg.com/564x/eb/c9/af/ebc9afde8c2b05bbf639cfc1c56dc59a.jpg"));
        }else {
            image.setImage(new Image(imgURL));
        }

        reactionLabel.setText(post.getNumber_of_reaction());
        cmtLabel.setText(post.getNumber_of_comment());
        shareLabel.setText(post.getNumber_of_share());
    }

    // Hiển thị cho tweet
    public void setData(Tweet post){
        authorLabel.setText(post.getAuthor());
        tagAuthor.setText("@" + post.getAuthor());
        timeLabel.setText(timeParse(post.getPublishedAt()));
        
        String[] parts = post.getContent().toString().split("\\{url=");
    	String imgURL = "";
    	for (int i = 1; i < parts.length; i++) {
    		imgURL = parts[i].split(", description=")[0];
    	}
        
        contentLabel.setText(parts[0]);        
        if(!imgURL.isEmpty()) {
        	image.setImage(new Image(imgURL));
        }

        reactionLabel.setText(post.getNumber_of_liked());
        cmtLabel.setText( post.getNumber_of_comment());
        viewLabel.setText("· " + post.getNumber_of_view() + " Views");
    }
    
    private String timeParse(LocalDateTime input){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' at 'HH:mm");
        String time = input.format(formatter);
        return time;
    }

}
