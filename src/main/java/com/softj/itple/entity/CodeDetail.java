package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_code_detail")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CodeDetail extends Auditing{
    private long masterId;
    private String codeName;
    private String codeValue;
    @Convert(converter = Types.RoleType.Converter.class)
    private Types.RoleType roleType;

    @Convert(converter = Types.AcademyType.Converter.class)
    private Types.AcademyType academyType;
    private int sort;

    @Builder
    public CodeDetail(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, long masterId, String codeName, String codeValue, Types.RoleType roleType, int sort, Types.AcademyType academyType) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.masterId = masterId;
        this.codeName = codeName;
        this.codeValue = codeValue;
        this.roleType = roleType;
        this.sort = sort;
        this.academyType = academyType;
    }
}
