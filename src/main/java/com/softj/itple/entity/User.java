package com.softj.itple.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_user")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class User extends Auditing{
    private String userId;
    private String userPw;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Role> roleList;

    @Builder
    public User(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, String userId, String userPw, List<Role> roleList) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.userId = userId;
        this.userPw = userPw;
        this.roleList = roleList;
    }
}
