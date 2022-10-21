package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_board")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Board extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Convert(converter = Types.AcademyType.Converter.class)
    private Types.AcademyType boardType;
    private String boardCategory;
    private String subject;
    private String thumbnail;
    private String contents;
    private long viewCount;
    private Boolean isPopup;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<BoardFile> boardFileList;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<BoardComment> boardCommentList;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<BoardStar> boardStarList;

    @Transient
    private long commentCount;
    @Transient
    private long starCount;

    @Builder
    public Board(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, User user, Types.AcademyType boardType, String boardCategory, String subject, String thumbnail, String contents, long viewCount, List<BoardFile> boardFileList, List<BoardComment> boardCommentList, long commentCount, long starCount, Boolean isPopup) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.user = user;
        this.boardType = boardType;
        this.boardCategory = boardCategory;
        this.subject = subject;
        this.thumbnail = thumbnail;
        this.contents = contents;
        this.viewCount = viewCount;
        this.boardFileList = boardFileList;
        this.boardCommentList = boardCommentList;
        this.commentCount = commentCount;
        this.starCount = starCount;
        this.isPopup = isPopup;
    }
}
