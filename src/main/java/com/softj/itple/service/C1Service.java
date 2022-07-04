package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.*;
import com.softj.itple.repo.BoardCommentRepo;
import com.softj.itple.repo.BoardRepo;
import com.softj.itple.repo.BoardStarRepo;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class C1Service {
    private final JPAQueryFactory jpaQueryFactory;
    final private BoardRepo boardRepo;
    final private BoardCommentRepo boardCommentRepo;
    final private BoardStarRepo boardStarRepo;
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

    public Board getBoard(SearchVO params){
        Board el = boardRepo.findById(params.getId()).orElse(Board.builder().build());
        if(LongUtils.noneEmpty(el.getId())) {
            el.setCommentCount(boardCommentRepo.countByBoard(el));
            el.setStarCount(boardStarRepo.countByBoard(el));
        }
        return el;
    }

    public Page<BoardComment> getBoardCommentList(SearchVO params, Pageable pageable){
        QBoardComment qBoardComment = QBoardComment.boardComment;
        BooleanBuilder where = new BooleanBuilder().and(qBoardComment.board.id.eq(params.getId()).and(qBoardComment.parent.isNull()));
        return boardCommentRepo.findAll(where, pageable);
    }

    public void deleteBoard(SearchVO params){
        boardRepo.deleteById(params.getId());
    }

    public boolean isStar(SearchVO params){
        return !Objects.isNull(boardStarRepo.findByUserIdAndBoardId(AuthUtil.getUserId(), params.getId()));
    }

    public void toggleStar(SearchVO params){
        BoardStar el = boardStarRepo.findByUserIdAndBoardId(AuthUtil.getUserId(), params.getId());
        if(Objects.isNull(el)){
            boardStarRepo.save(BoardStar.builder()
                    .board(Board.builder().id(params.getId()).build())
                    .user(User.builder().id(AuthUtil.getUserId()).build())
                    .build());
        }else{
            boardStarRepo.delete(el);
        }
    }

    public void deleteBoardComment(SearchVO params){
        boardCommentRepo.deleteById(params.getId());
    }

    public void saveBoardComment(SearchVO params){
        BoardComment save = BoardComment.builder().build();
        BoardComment parent = null;
        if(LongUtils.noneEmpty(params.getCommentId())){
            save = boardCommentRepo.findById(params.getCommentId()).get();
        }
        save.setBoard(Board.builder().id(params.getId()).build());
        if(LongUtils.noneEmpty(params.getUpperId())){
            parent = boardCommentRepo.findById(params.getUpperId()).get();
            save.setParent(parent);
        }
        save.setContents(params.getContents());
        save.setUser(AuthUtil.getUser());
        boardCommentRepo.save(save);
    }

    public void viewBoard(SearchVO params){
        Board save = boardRepo.findById(params.getId()).get();
        save.setViewCount(save.getViewCount()+1L);
        boardRepo.save(save);
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
