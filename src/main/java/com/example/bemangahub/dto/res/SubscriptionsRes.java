package com.example.bemangahub.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionsRes {
    private Integer id;
    private String name;
    private String image;
    private String idComic;
}
