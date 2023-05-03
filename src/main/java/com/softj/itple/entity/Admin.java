package com.softj.itple.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_admin")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Admin extends Auditing{
    @OneToOne(cascade= CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean menu1;
    private boolean menu2;
    private boolean menu3;
    private boolean menu4;
    private boolean menu5;
    private boolean menu6;
    private boolean menu7;
    private boolean menu8;
    private boolean menu9;

    @Builder
    public Admin(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, User user, boolean menu1, boolean menu2, boolean menu3, boolean menu4, boolean menu5, boolean menu6, boolean menu7, boolean menu8, boolean menu9) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.user = user;
        this.menu1 = menu1;
        this.menu2 = menu2;
        this.menu3 = menu3;
        this.menu4 = menu4;
        this.menu5 = menu5;
        this.menu6 = menu6;
        this.menu7 = menu7;
        this.menu8 = menu8;
        this.menu9 = menu9;
    }

}
