package com.softj.itple.entity;


import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_coin_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CoinHistory extends Auditing {
    @Convert(converter = Types.CoinStatus.Converter.class)
    private Types.CoinStatus coinStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String memo;
    private long coin;
}
