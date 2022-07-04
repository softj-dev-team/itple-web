package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.*;
import com.softj.itple.repo.BoardRepo;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class A1Service {
    private final JPAQueryFactory jpaQueryFactory;
    final private BoardRepo boardRepo;

    final private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<Book> getBookList(SearchVO params, Pageable pageable){
        QBook qBook = QBook.book;

        BooleanBuilder where = new BooleanBuilder().and(qBook.isDeleted.eq(false));
        if(StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "subject":
                    where.and(qBook.subject.contains(params.getSearchValue()));
                    break;
                case "writer":
                    where.and(qBook.writer.contains(params.getSearchValue()));
                    break;
            }
        }
        JPAQuery<Book> query = jpaQueryFactory.select(Projections.fields(Book.class,
                        qBook.id,
                        qBook.thumbnail,
                        qBook.createdAt,
                        qBook.subject,
                        qBook.book_no,
                        qBook.contents,
                        qBook.status,
                        qBook.writer)
        )
        .from(qBook)
        .where(where)
        .orderBy(qBook.id.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset());

        return new PageImpl<Book>(query.fetch(), pageable, query.fetchCount());
    }

    public Book getBook(SearchVO params){
        QBook qBook = QBook.book;
        BooleanBuilder where = new BooleanBuilder().and(qBook.isDeleted.eq(false));

        JPAQuery<Book> query = jpaQueryFactory.select(qBook)
        .from(qBook)
        .where(where);

        return query.fetchOne();
    }
}
