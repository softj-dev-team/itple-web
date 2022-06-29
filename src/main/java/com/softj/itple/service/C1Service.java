package com.softj.itple.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class C1Service {
    final private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//    public Page<Board> getBoardList(SearchVO params,Pageable pageable){
//        QBoard qBoard = QBoard.board;
//        QSort sort = new QSort(qBoard.seq.desc());
//        BooleanBuilder query = new BooleanBuilder().and(qBoard.delAt.eq(false));
//        if(!AuthUtil.isRole12()){
//            Dept all = Dept.builder().build();
//            all.setSeq(9999);
//            Dept loginDept = AuthUtil.getLoginVO().getDept();
//            Dept[] whereDept = {all,loginDept};
//            query.and(qBoard.accessDept.in(whereDept));
//        }
//        if(!StringUtils.isEmpty(params.getStartDate())){
//            query.and(qBoard.createdAt.goe(LocalDateTime.of(LocalDate.parse(params.getStartDate(), df), LocalTime.MIN)));
//        }
//        if(!StringUtils.isEmpty(params.getEndDate())){
//            query.and(qBoard.createdAt.loe(LocalDateTime.of(LocalDate.parse(params.getEndDate(), df), LocalTime.MAX)));
//        }
//        if(!StringUtils.isEmpty(params.getAccessDeptSeqS())){
//            query.and(qBoard.accessDept.eq(deptRepo.findOneBySeq(Long.parseLong(params.getAccessDeptSeqS()))));
//        }
//        if(!StringUtils.isEmpty(params.getContent())){
//            query.and(qBoard.content.contains(params.getContent()));
//        }
//        return boardRepo.findAll(query, PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),sort));
//    }
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
