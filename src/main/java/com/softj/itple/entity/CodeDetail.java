package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_code_detail")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CodeDetail extends Auditing{
    private long masterId;
    private String codeName;
    private String codeValue;
    @Convert(converter = Types.RoleType.Converter.class)
    private Types.RoleType roleType;
    private int sort;

}
