package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.repo.*;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.CodeUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class C1Service {
    private final JPAQueryFactory jpaQueryFactory;
    final private BoardRepo boardRepo;
    final private BoardCommentRepo boardCommentRepo;
    final private BoardStarRepo boardStarRepo;
    final private BoardFileRepo boardFileRepo;
    final private CodeDetailRepo codeDetailRepo;

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

                //?????? ??????
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
        Pattern p = Pattern.compile("src=\"([^\"]+)\".*>");
        Matcher m = p.matcher(save.getContents());
        if(m.find()){
            String thumbnail = m.group(1);
            save.setThumbnail(thumbnail);
        }
        Board finalSave = boardRepo.save(save);

        List<BoardFile> fileList = boardFileRepo.findAllById(Arrays.asList(params.getIdList()));
        for(BoardFile f:fileList){
            f.setBoard(finalSave);
        }
        boardFileRepo.saveAll(fileList);
    }

    @Transactional
    public List<CodeDetail> selectBoardCategoryList(SearchVO params){
        CodeUtil cu = new CodeUtil(codeDetailRepo);
        List<CodeDetail> list = cu.getCodeList(params.getMasterId());
        return list;
    }

    public CodeDetail selectBoardWriteConfig(){
        CodeUtil cu = new CodeUtil(codeDetailRepo);
        CodeDetail codeDetail = cu.getCode(2,"01");
        return codeDetail;
    }

    @Transactional
    public void saveBoardCategory(SearchVO params, HttpServletRequest request){
        // delete
        HttpSession session = request.getSession();
        LocalDateTime now = LocalDateTime.now();

        if(params.getRemoveIdList() != null) {
            for (long id : params.getRemoveIdList()) {
                CodeDetail save = codeDetailRepo.findById(id).get();
                save.setUpdatedAt(now);
                save.setUpdatedId(session.getAttribute("userId").toString());
                save.setDeleted(true);
                codeDetailRepo.save(save);
            }
        }

        // update
        if(params.getUpdateIdList() != null){
            int i=0;
            String[] codeNameList = params.getCodeNameList();

            for(long id: params.getUpdateIdList()){
                CodeDetail save = codeDetailRepo.findById(id).get();
                save.setCodeName(codeNameList[i]);
                save.setUpdatedAt(now);
                save.setUpdatedId(session.getAttribute("userId").toString());
                codeDetailRepo.save(save);
                i++;
            }
        }

        // insert
        if(params.getNewCodeNameList() != null){

            long masterId = 1;
            CodeDetail getTopCodeDetailOrderBySort = codeDetailRepo.findTopByMasterIdOrderBySortDesc(masterId).orElse(CodeDetail.builder().build());
            CodeDetail getTopCodeDetailOrderByCodeValue = codeDetailRepo.findTopByMasterIdOrderByCodeValueDesc(masterId).orElse(CodeDetail.builder().build());


            int startSort = getTopCodeDetailOrderBySort.getSort();
            int startCodeValue = Integer.parseInt(getTopCodeDetailOrderByCodeValue.getCodeValue());
            for(String codeName : params.getNewCodeNameList()){
                CodeDetail save = codeDetailRepo.findByCodeName(codeName).orElse(CodeDetail.builder().build());

                if(save.getId() != 0){
                    save.setDeleted(false);
                    save.setUpdatedAt(now);
                    save.setUpdatedId(session.getAttribute("userId").toString());
                    codeDetailRepo.save(save);
                }else {
                    String codeValue = "";
                    startCodeValue = startCodeValue + 1;

                    if (startCodeValue < 10) {
                        codeValue = "0" + startCodeValue;
                    } else {
                        codeValue = Integer.toString(startCodeValue);
                    }
                    startSort += 1;

                    save.setMasterId(1);
                    save.setCodeName(codeName);
                    save.setCodeValue(codeValue);
                    save.setRoleType(Types.RoleType.STUDENT);
                    save.setSort(startSort);
                    codeDetailRepo.save(save);
                }
            }
        }

        CodeDetail save = codeDetailRepo.findByMasterId(2).orElse(CodeDetail.builder().build());

        if(params.getRoleType() != save.getRoleType()) {
            save.setUpdatedAt(now);
            save.setUpdatedId(session.getAttribute("userId").toString());
            save.setRoleType(params.getRoleType());
            codeDetailRepo.save(save);
        }

        CodeUtil cu = new CodeUtil(codeDetailRepo);
        cu.refresh();
    }
}
