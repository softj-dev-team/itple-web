package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_board")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Board extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Convert(converter = Types.BoardType.Converter.class)
    private Types.BoardType boardType;
    private String boardCategory;
    private String subject;
    private String thumbnail;
    private String contents;
    private long viewCount;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<BoardFile> boardFileList;

    @Transient
    private long commentCount;
    @Transient
    private long starCount;
}
