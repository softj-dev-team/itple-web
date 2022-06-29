package com.softj.itple.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_role")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Role extends Auditing{
    private String roleName;

    @Convert(converter = Types.RoleType.Converter.class)
    Types.RoleType roleType;

    @ManyToOne
    private User user;
}
