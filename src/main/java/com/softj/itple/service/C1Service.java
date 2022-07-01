package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.Board;
import com.softj.itple.entity.QBoard;
import com.softj.itple.entity.QBoardComment;
import com.softj.itple.entity.QBoardStar;
import com.softj.itple.repo.BoardRepo;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class C1Service {
    private final JPAQueryFactory jpaQueryFactory;
    final private BoardRepo boardRepo;
    final private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<Board> getBoardList(SearchVO params, Pageable pageable){
        QBoard qBoard = QBoard.board;
        QBoardComment qBoardComment = QBoardComment.boardComment;
        QBoardStar qBoardStar = QBoardStar.boardStar;
        BooleanBuilder where = new BooleanBuilder().and(qBoard.isDeleted.eq(false).and(qBoard.boardType.eq(params.getBoardType())));
        if(StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "subject":
                    where.and(qBoard.subject.contains(params.getSearchValue()));
                    break;
                case "contents":
                    where.and(qBoard.contents.contains(params.getSearchValue()));
                    break;
                case "user":
                    where.and(qBoard.user.userName.contains(params.getSearchValue()));
                    break;
            }
        }
        JPAQuery<Board> query = jpaQueryFactory.select(Projections.fields(Board.class,
                qBoard.id,
                qBoard.thumbnail,
                qBoard.createdAt,
                qBoard.subject,
                qBoard.boardCategory,
                qBoard.viewCount,
                qBoard.user,
                ExpressionUtils.as(
                        JPAExpressions.select(qBoardComment.count())
                                .from(qBoardComment)
                                .where(qBoardComment.board.eq(qBoard)),"commentCount"),
                ExpressionUtils.as(
                        JPAExpressions.select(qBoardStar.count())
                                .from(qBoardStar)
                                .where(qBoardStar.board.eq(qBoard)),"starCount"))
        )
        .from(qBoard)
        .where(where)
        .orderBy(qBoard.id.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset());

        return new PageImpl<Board>(query.fetch(), pageable, query.fetchCount());
    }
//
//    public Board getBoard(SearchVO params){
//        return boardRepo.findOneBySeq(params.getSeq());
//    }
//
//    @Transactional
//    public Board saveBoard(BoardDTO params){
//        Dept accessDept = Dept.builder().build();
//        accessDept.setSeq(params.getAccessDeptSeq());
//        Board save = Board.builder()
//                .content(params.getContent())
//                .boardType("NOTICE")
//                .title("공지사항")
//                .accessDept(accessDept)
//                .answer(AuthUtil.getLoginVO())
//                .build();
//        save.setSeq(params.getSeq());
//        save = boardRepo.save(save);
//
//        List<Long> adminSeqList = null;
//        adminSeqList = accessDept.getSeq() == 9999L ? deptRepo.findAdminSeq() : deptRepo.findAdminSeqByDept(accessDept);
//        for(long adminSeq : adminSeqList) {
//            alarmRepo.save(Alarm.builder()
//                    .content(save.getContent())
//                    .adminSeq(adminSeq)
//                    .readYn(false)
//                    .refSeq(save.getSeq())
//                    .type("B") //공지사항
//                    .build());
//        }
//        return save;
//    }
//
//    @Transactional
//    public void deleteBoard(SearchVO params){
//        for(long seq:params.getSeqList()){
//            Board board = boardRepo.findOneBySeq(seq);
//            board.setDelAt(true);
//            boardRepo.save(board);
//        }
//    }
}
