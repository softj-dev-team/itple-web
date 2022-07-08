package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_class")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AcademyClass extends Auditing{
    @Convert(converter = Types.AcademyType.Converter.class)
    private Types.AcademyType academyType;
    private String className;
}
