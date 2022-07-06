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
import com.softj.itple.repo.BoardFileRepo;
import com.softj.itple.repo.BoardRepo;
import com.softj.itple.repo.BoardStarRepo;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.transaction.Transactional;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class C1Service {
    private final JPAQueryFactory jpaQueryFactory;
    final private BoardRepo boardRepo;
    final private BoardCommentRepo boardCommentRepo;
    final private BoardStarRepo boardStarRepo;
    final private BoardFileRepo boardFileRepo;
    final private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

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

    @Transactional
    public List<BoardFile> boardFileUpload(MultipartHttpServletRequest request) throws Exception{
        List<BoardFile> result = new ArrayList<>();

        List<MultipartFile> imgList = request.getFiles("files");
        for(MultipartFile img : imgList) {
            if (!Objects.isNull(img) && StringUtils.noneEmpty(img.getOriginalFilename())) {
                String imagePath = null;
                String realFileName = img.getOriginalFilename();
                String ext = realFileName.substring(realFileName.lastIndexOf(".") + 1);
                String systemFileName = UUID.randomUUID().toString().toUpperCase() + "." + ext;

                String targetPath = FILE_PATH + "/" + sdf.format(new Date()) + "/";
                File targetDir = new File(targetPath);

                //폴더 생성
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }

                FileUtils.writeByteArrayToFile(new File(targetPath + systemFileName), img.getBytes());

                imagePath = targetPath + systemFileName;
                imagePath = imagePath.replace("/", "_");
                result.add(boardFileRepo.save(BoardFile.builder()
                        .board(null)
                        .orgFileName(realFileName)
                        .uploadFileName(imagePath)
                        .build()));
            }
        }

        return result;
    }

    @Transactional
    public void deleteBoardFile(SearchVO params){
        for(long id:params.getIdList()){
            boardFileRepo.deleteById(id);
        }
    }

    @Transactional
    public void saveBoard(SearchVO params){
        Board save = Board.builder().build();
        if(LongUtils.noneEmpty(params.getId())){
            save = boardRepo.findById(params.getId()).get();
        }
        save.setSubject(params.getSubject());
        save.setBoardCategory(params.getBoardCategory());
        save.setBoardType(params.getBoardType());
        save.setContents(params.getContents());
        save.setUser(User.builder().id(AuthUtil.getUserId()).build());
        int idxStart = save.getContents().indexOf("src=\"");
        int idxEnd = save.getContents().indexOf("\">");
        if(idxStart != -1){
            String thumbnail = save.getContents().substring(idxStart+5, idxEnd);
            save.setThumbnail(thumbnail);
        }
        Board finalSave = boardRepo.save(save);

        List<BoardFile> fileList = boardFileRepo.findAllById(Arrays.asList(params.getIdList()));
        for(BoardFile f:fileList){
            f.setBoard(finalSave);
        }
        boardFileRepo.saveAll(fileList);
    }
}
