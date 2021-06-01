package com.bonitasoft.reactiveworkshop.external;


import lombok.Data;
import lombok.NonNull;

@Data
public class Comment {

    @NonNull
    String artist;
    @NonNull
    String userName;
    @NonNull
    String comment;

}
