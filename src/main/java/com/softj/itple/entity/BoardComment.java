package com.softj.itple.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_id")
    private BoardComment parent;

    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoardComment> children;

    @Builder
    public BoardComment(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, User user, Board board, String contents, BoardComment parent, List<BoardComment> children) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.user = user;
        this.board = board;
        this.contents = contents;
        this.parent = parent;
        this.children = children;
    }
}
