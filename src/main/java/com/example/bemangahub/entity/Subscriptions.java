package com.example.bemangahub.entity;

import com.example.bemangahub.dto.res.SubscriptionsRes;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscriptions")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "SubscriptionsResponseByAccountIdMappingQuery",
                query = "SELECT s.id AS id, " +
                        "s.name AS name, " +
                        "s.image AS image, " +
                        "s.id_comic AS idComic FROM subscriptions s " +
                        "WHERE s.account_id = :accountId",
                resultSetMapping = "SubscriptionsResponseMappingResult"
        )
})
@SqlResultSetMapping(
        name = "SubscriptionsResponseMappingResult",
        classes = @ConstructorResult(
                targetClass = SubscriptionsRes.class,
                columns = {
                        @ColumnResult(name = "id", type = Integer.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "image", type = String.class),
                        @ColumnResult(name = "idComic", type = String.class),
                }
        )
)
public class Subscriptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "id_comic")
    private String idComic;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
