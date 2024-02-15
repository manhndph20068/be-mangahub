package com.example.bemangahub.entity;

import com.example.bemangahub.dto.res.UserInfo;
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
@Table(name = "account")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "UserCerdentialInfoMappingQuery",
                query = "SELECT a.username AS username, " +
                        "a.email AS email, " +
                        "a.avatar AS avatar, " +
                        "a.created_at AS createdAt, " +
                        "a.updated_at AS updatedAt, " +
                        "a.refresh_token AS refreshToken, " +
                        "a.is_verified AS isVerified, " +
                        "t.name AS type, " +
                        "r.name AS role " +
                        "FROM account a " +
                        "JOIN type t ON a.type_id = t.id " +
                        "JOIN role r ON a.role_id = r.id " +
                        "WHERE t.name = 'SYSTEM' and a.email = :email",
                resultSetMapping = "UserInfoMappingResult"
        )
})

@SqlResultSetMapping(
        name = "UserInfoMappingResult",
        classes = @ConstructorResult(
                targetClass = UserInfo.class,
                columns = {
                        @ColumnResult(name = "username", type = String.class),
                        @ColumnResult(name = "email", type = String.class),
                        @ColumnResult(name = "avatar", type = String.class),
                        @ColumnResult(name = "createdAt", type = Date.class),
                        @ColumnResult(name = "updatedAt", type = Date.class),
                        @ColumnResult(name = "refreshToken", type = String.class),
                        @ColumnResult(name = "isVerified", type = Integer.class),
                        @ColumnResult(name = "type", type = String.class),
                        @ColumnResult(name = "role", type = String.class)
                }
        )
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "is_verified")
    private Integer isVerified;

    @Column(name = "status")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;

}
