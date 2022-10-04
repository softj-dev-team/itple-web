package com.softj.itple.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_board_log")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BoardLog extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    @Builder
    public BoardLog(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, Board board, User user) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.board = board;
        this.user = user;
    }
}
