package com.softj.itple.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_board_comment")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BoardComment extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    private String contents;
}
