package com.softj.itple.entity;


import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_portfolio")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Portfolio extends Auditing {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String subject;
    private String summary;
    private String thumbnail;
    private String contents;
    @Convert(converter = Types.VisibleStatus.Converter.class)
    private Types.VisibleStatus visibleStatus;

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<PortfolioFile> portfolioFileList;
}
