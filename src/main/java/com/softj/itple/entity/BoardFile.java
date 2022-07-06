package com.softj.itple.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_board_file")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BoardFile extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    private String orgFileName;
    private String uploadFileName;
}
