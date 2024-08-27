package com.prj.furni_shop.modules.authentication.entity;

import com.prj.furni_shop.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "token")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int tokenId;

    String token;

    @Enumerated(EnumType.ORDINAL)
    TokenType tokenType;

    @Column(name = "user_id")
    int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    User user;

}
