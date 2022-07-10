package com.softj.itple.service;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.QStudentTask;
import com.softj.itple.entity.StudentTask;
import com.softj.itple.entity.StudentTaskFile;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.StudentTaskFileRepo;
import com.softj.itple.repo.StudentTaskRepo;
import com.softj.itple.repo.TaskRepo;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class C3Service {
    private final JPAQueryFactory jpaQueryFactory;

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//    public Page<StudentTask> getStudentTaskList(SearchVO params, Pageable pageable){
//        QStudentTask qStudentTask = QStudentTask.studentTask;
//        BooleanBuilder where = new BooleanBuilder().and(qStudentTask.isDeleted.eq(false).and(qStudentTask.task.taskType.eq(params.getTaskType())).and(qStudentTask.student.eq(AuthUtil.getStudent())));
//        return studentTaskRepo.findAll(where, pageable);
//    }
//
//    public StudentTask getStudentTask(SearchVO params){
//        return studentTaskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
//    }
//
//    public List<StudentTask> getNotSubmitList(SearchVO params){
//        QStudentTask qStudentTask = QStudentTask.studentTask;
//        BooleanBuilder where = new BooleanBuilder().and(qStudentTask.isDeleted.eq(false).and(qStudentTask.task.taskType.eq(Types.TaskType.BOOK_REPORT)).and(qStudentTask.student.eq(AuthUtil.getStudent())).and(qStudentTask.status.eq(Types.TaskStatus.NOT_SUBMIT)));
//        return ImmutableList.copyOf(studentTaskRepo.findAll(where));
//    }

//    public Page<BoardComment> getStudentTaskCommentList(SearchVO params, Pageable pageable){
//        qStudentTaskComment qStudentTaskComment = qStudentTaskComment.boardComment;
//        BooleanBuilder where = new BooleanBuilder().and(qStudentTaskComment.board.id.eq(params.getId()).and(qStudentTaskComment.parent.isNull()));
//        return boardCommentRepo.findAll(where, pageable);
//    }
//
//    public void deleteBoard(SearchVO params){
//        boardRepo.deleteById(params.getId());
//    }
//
//    public boolean isStar(SearchVO params){
//        return !Objects.isNull(boardStarRepo.findByUserIdAndBoardId(AuthUtil.getUserId(), params.getId()));
//    }
//
//    public void toggleStar(SearchVO params){
//        BoardStar el = boardStarRepo.findByUserIdAndBoardId(AuthUtil.getUserId(), params.getId());
//        if(Objects.isNull(el)){
//            boardStarRepo.save(BoardStar.builder()
//                    .board(Board.builder().id(params.getId()).build())
//                    .user(User.builder().id(AuthUtil.getUserId()).build())
//                    .build());
//        }else{
//            boardStarRepo.delete(el);
//        }
//    }
//
//    public void deleteBoardComment(SearchVO params){
//        boardCommentRepo.deleteById(params.getId());
//    }
//
//    public void saveBoardComment(SearchVO params){
//        BoardComment save = BoardComment.builder().build();
//        BoardComment parent = null;
//        if(LongUtils.noneEmpty(params.getCommentId())){
//            save = boardCommentRepo.findById(params.getCommentId()).get();
//        }
//        save.setBoard(Board.builder().id(params.getId()).build());
//        if(LongUtils.noneEmpty(params.getUpperId())){
//            parent = boardCommentRepo.findById(params.getUpperId()).get();
//            save.setParent(parent);
//        }
//        save.setContents(params.getContents());
//        save.setUser(AuthUtil.getUser());
//        boardCommentRepo.save(save);
//    }
//
//    public void viewBoard(SearchVO params){
//        Board save = boardRepo.findById(params.getId()).get();
//        save.setViewCount(save.getViewCount()+1L);
//        boardRepo.save(save);
//    }
//
//    @Transactional
//    public List<StudentTaskFile> studentTaskFileUpload(MultipartHttpServletRequest request) throws Exception{
//        List<StudentTaskFile> result = new ArrayList<>();
//
//        List<MultipartFile> imgList = request.getFiles("files");
//        for(MultipartFile img : imgList) {
//            if (!Objects.isNull(img) && StringUtils.noneEmpty(img.getOriginalFilename())) {
//                String imagePath = null;
//                String realFileName = img.getOriginalFilename();
//                String ext = realFileName.substring(realFileName.lastIndexOf(".") + 1);
//                String systemFileName = UUID.randomUUID().toString().toUpperCase() + "." + ext;
//
//                String targetPath = FILE_PATH + "/" + sdf.format(new Date()) + "/";
//                File targetDir = new File(targetPath);
//
//                //폴더 생성
//                if (!targetDir.exists()) {
//                    targetDir.mkdirs();
//                }
//
//                FileUtils.writeByteArrayToFile(new File(targetPath + systemFileName), img.getBytes());
//
//                imagePath = targetPath + systemFileName;
//                imagePath = imagePath.replace("/", "_");
//                result.add(studentTaskFileRepo.save(StudentTaskFile.builder()
//                        .studentTask(null)
//                        .orgFileName(realFileName)
//                        .uploadFileName(imagePath)
//                        .build()));
//            }
//        }
//
//        return result;
//    }
//
//    @Transactional
//    public void deleteStudentTaskFile(SearchVO params){
//        for(long id:params.getIdList()){
//            studentTaskFileRepo.deleteById(id);
//        }
//    }
//
//    @Transactional
//    public void saveStudentTask(SearchVO params){
//        StudentTask save = studentTaskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
//        save.setContents(params.getContents());
//        save.setStatus(params.getStatus());
//        StudentTask finalSave = studentTaskRepo.save(save);
//
//        List<StudentTaskFile> fileList = studentTaskFileRepo.findAllById(Arrays.asList(params.getIdList()));
//        for(StudentTaskFile f:fileList){
//            f.setStudentTask(finalSave);
//        }
//        studentTaskFileRepo.saveAll(fileList);
//    }
}
