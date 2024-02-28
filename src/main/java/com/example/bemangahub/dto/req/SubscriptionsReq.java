package com.example.bemangahub.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionsReq {
    private Integer idAccount;
    private String idComic;
    private String name;
    private String image;
}
