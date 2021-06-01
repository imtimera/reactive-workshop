package com.bonitasoft.reactiveworkshop.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"artist"}, allowSetters = true)
public class Comment {

    @NonNull
    String artist;

    @NonNull
    String userName;

    @NonNull
    String comment;

}