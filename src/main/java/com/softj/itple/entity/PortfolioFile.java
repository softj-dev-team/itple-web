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
@Table(name = "tb_portfolio_file")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PortfolioFile extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;
    private String orgFileName;
    private String uploadFileName;
}
