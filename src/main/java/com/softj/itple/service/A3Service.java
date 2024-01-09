package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.A3StudentDTO;
import com.softj.itple.domain.A4ResourceDTO;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.*;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class A3Service{

    private final JPAQueryFactory jpaQueryFactory;

    private final UserRepo userRepo;
    private final AttendanceRepo attendanceRepo;

    private final AcademyClassRepo academyClassRepo;
    private final StudentTaskRepo studentTaskRepo;

    private final StudentTaskFileRepo studentTaskFileRepo;
    private final AttendanceHistoryRepo attendanceHistoryRepo;
    private final BoardRepo boardRepo;

    private final BoardLogRepo boardLogRepo;

    private final BoardFileRepo boardFileRepo;

    private final BoardCommentRepo boardCommentRepo;

    private final BoardStarRepo boardStarRepo;
    private final BookRentalRepo bookRentalRepo;
    private final CoinHistoryRepo coinHistoryRepo;
    private final PortfolioRepo portfolioRepo;

    private final PortfolioFileRepo portfolioFileRepo;

    private final PaymentRepo paymentRepo;
    private final StudentRepo studentRepo;

    private final TaskRepo taskRepo;

    private final TaskFileRepo taskFileRepo;

    final private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public Page<Student> getStudentList(SearchVO params, Pageable pageable){

        int studentListTotal = studentRepo.getStudentListTotal(params.getStudentStatus() != null ? params.getStudentStatus().getCode() : null, params.getAcademyType() != null ? params.getAcademyType().getCode() : null, params.getGrade() != null ? params.getGrade().getCode() : null, params.getClassId(), params.getSearchType(), params.getSearchValue());

        List<Student> studentList = studentRepo.getStudentList(params.getStudentStatus() != null ? params.getStudentStatus().getCode() : null, params.getAcademyType() != null ? params.getAcademyType().getCode() : null, params.getGrade() != null ? params.getGrade().getCode() : null, params.getClassId(), params.getEdOrder() != null ? params.getEdOrder() : null, params.getSearchType(), params.getSearchValue(), pageable);

        return new PageImpl<Student>(studentList, pageable, studentListTotal);
    }

    public List<A3StudentDTO> getStudentCodingRankingList(){
        return studentRepo.getStudentCodingRankingList();
    }

    public List<A3StudentDTO> getStudentEnglishRankingList(){
        return studentRepo.getStudentEnglishRankingList();
    }

    public Student getStudent(SearchVO params){
        return studentRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public HashMap<String, Object> updateStudent(SearchVO params){
        Student save = studentRepo.findById(params.getStudentId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        Student attendStudent = studentRepo.findByAttendanceNo(params.getAttendanceNo()).orElse(Student.builder().build());
        HashMap<String, Object> resultMap = new HashMap<>();
        List<Long> bfStudentTaskList = new ArrayList<>();
        List<Long> bfTaskList = new ArrayList<>();

        if(!LongUtils.isEmpty(attendStudent.getId()) && save.getId() != attendStudent.getId()){
            throw new ApiException("동일한 출결번호가 존재합니다. 출결번호를 변경해주세요.", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        save.getUser().setUserName(params.getUserName());
        save.getUser().setApproved(Boolean.parseBoolean(params.getApproved()));

        userRepo.save(save.getUser());

        long prevCoin = save.getCoin();
        long reqCoin = params.getCoin();
        save.setStudentStatus(params.getStudentStatus());
        if(LongUtils.noneEmpty(params.getClassId())) {
            AcademyClass academyClass = academyClassRepo.findById(params.getClassId()).orElseThrow(() -> new ApiException("반 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
            if(save.getAcademyClass() != null && save.getAcademyClass().getId() != params.getClassId()){
                List<Task> taskList = taskRepo.findByAcademyClass(save.getAcademyClass());
                List<Task> afterTaskList = taskRepo.findByAcademyClass(academyClass);
                boolean orgFlag = false;
                for(Task task : taskList){
                    List<StudentTask> studentTaskList = task.getStudentTasks();

                    for(StudentTask studentTask : studentTaskList){
                        if(studentTask.getStudent().getId() == save.getId()){

                            Task newTask = Task.builder().build();
                            boolean flag = false;

                            if(afterTaskList != null) {
                                for (Task afterTask : afterTaskList) {
                                    if (afterTask.getId() == task.getRootId()) {
                                        flag = true;
                                        newTask = afterTask;
                                    }
                                }
                            }

                            if(!flag) {
                                newTask.setAcademyClass(academyClass);
                                newTask.setTaskType(task.getTaskType());
                                newTask.setCoin(task.getCoin());
                                newTask.setContents(task.getContents());
                                newTask.setStartDate(task.getStartDate());
                                newTask.setEndDate(task.getEndDate());
                                newTask.setSubject(task.getSubject());
                                newTask.setTeacher(academyClass.getUser().getUserName());
                                newTask.setRootId(task.getRootId());
                                newTask.setTaskMap(task.getTaskMap());
                                newTask.setTaskMapName(task.getTaskMapName());
                                taskRepo.save(newTask);

                                for(TaskFile taskFile : task.getTaskFileList()){
                                    taskFile.setTask(newTask);
                                    taskFileRepo.save(taskFile);
                                }
                            }

                            StudentTask newStudentTask = StudentTask.builder().build();
                            newStudentTask.setStudent(save);
                            newStudentTask.setTask(newTask);
                            newStudentTask.setCoinComp(studentTask.getCoinComp());
                            newStudentTask.setContents(studentTask.getContents());
                            newStudentTask.setStatus(studentTask.getStatus());
                            newStudentTask.setReturnMessage(studentTask.getReturnMessage());
                            newStudentTask.setCompDate(studentTask.getCompDate());
                            StudentTask newStudentTaskId = studentTaskRepo.save(newStudentTask);

                            for(StudentTaskFile studentTaskFile : studentTask.getStudentTaskFileList()){
                                studentTaskFile.setStudentTask(newStudentTaskId);
                                studentTaskFileRepo.save(studentTaskFile);
                            }
                            bfStudentTaskList.add(studentTask.getId());

                        }else{
                            orgFlag = true;
                        }
                    }

                    if(!orgFlag){
                        bfTaskList.add(task.getId());
                    }
                }
            }

            save.setAcademyClass(academyClass);
        }else{
            save.setAcademyClass(null);
        }
        save.setCoin(reqCoin);
        save.setEnterDate(params.getEnterDate());
        save.setAttendanceNo(params.getAttendanceNo());
        save.setBirth(params.getBirth());
        save.setGrade(params.getGrade());
        save.setSchool(params.getSchool());
        save.setEmail(params.getEmail());
        save.setZonecode(params.getZonecode());
        save.setRoadAddress(params.getRoadAddress());
        save.setDetailAddress(params.getDetailAddress());
        save.setParentName(params.getParentName());
        save.setParentTel(params.getParentTel());
        save.setPaymentDay(params.getPaymentDay());
        save.setPrice(params.getPrice());
        save.setMemo(params.getMemo());
        save.setTelNo(params.getTelNo());

        if(!params.getStudentStatus().equals(Types.StudentStatus.STUDENT)){
            save.setOutDate(params.getOutDate());
        }

        studentRepo.save(save);

        if(prevCoin != reqCoin){
            long diffCoin = reqCoin - prevCoin;
            String coinMemo = params.getCoinMemo();
            String coinEtc = params.getCoinEtc();
            if(coinMemo.equals("기타")){
                coinMemo = coinEtc;
            }

            coinHistoryRepo.save(CoinHistory.builder()
                    .coinStatus(diffCoin > 0L ? Types.CoinStatus.PLUS : Types.CoinStatus.MINUS)
                    .user(save.getUser())
                    .coin(diffCoin)
                    .memo(coinMemo)
                    .build());
        }

        attendanceRepo.deleteAllByUser(save.getUser());
        attendanceRepo.flush();

        for(int i=0; i < params.getDayOfWeekList().length; i++){
            attendanceRepo.save(Attendance.builder()
                    .user(save.getUser())
                    .attendanceAt(LocalTime.of(params.getHourList()[i], params.getMinList()[i]))
                    .leaveAt(LocalTime.of(params.getLeaveHourList()[i], params.getLeaveMinList()[i]))
                    .attendanceDay(params.getDayOfWeekList()[i])
                    .build());
        }

        resultMap.put("studentTaskList", bfStudentTaskList);
        resultMap.put("taskList", bfTaskList);

        return resultMap;
    }

    @Transactional
    public void saveTaskFileList(SearchVO params) {

        String[] studentTaskIdList = params.getStudentTaskIdList();
        String[] orgFileNameList = params.getOrgFileNameList();
        String[] uploadFileNameList = params.getUploadFileNameList();

        if (studentTaskIdList.length > 0) {
            for(int i=0; i<studentTaskIdList.length; i++){
                StudentTask newStudentTask = studentTaskRepo.findById(Long.parseLong(studentTaskIdList[i])).orElseThrow(() -> new ApiException("과제 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
                StudentTaskFile newStudentTaskFile = StudentTaskFile.builder().build();
                newStudentTaskFile.setStudentTask(newStudentTask);
                newStudentTaskFile.setOrgFileName(orgFileNameList[i]);
                newStudentTaskFile.setUploadFileName(uploadFileNameList[i]);
                studentTaskFileRepo.save(newStudentTaskFile);
            }
        }
    }

    @Transactional
    public void deleteFkStudent(SearchVO params){
        for(long id : params.getIdList()){
            Student delete1 = studentRepo.findById(id).orElseThrow(() -> new ApiException("학생 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
            delete1.setAcademyClass(null);
            studentRepo.save(delete1);

            List<StudentTask> delete2 = studentTaskRepo.findByStudent(delete1);
            for(StudentTask studentTask : delete2){
                studentTaskRepo.delete(studentTask);
                List<StudentTaskFile> studentTaskFileList = studentTaskFileRepo.findByStudentTask(studentTask);
                for(StudentTaskFile studentTaskFile : studentTaskFileList){
                    studentTaskFileRepo.delete(studentTaskFile);
                }
            }

            List<Payment> delete3 = paymentRepo.findByStudent(delete1);
            for(Payment payment : delete3){
                paymentRepo.delete(payment);
            }

            List<AttendanceHistory> delete4 = attendanceHistoryRepo.findByUser(delete1.getUser());
            for(AttendanceHistory attendanceHistory : delete4){
                attendanceHistoryRepo.delete(attendanceHistory);
            }

            List<Board> delete5 = boardRepo.findByUser(delete1.getUser());
            for(Board board : delete5){
                boardRepo.delete(board);
                List<BoardFile> boardFileList = boardFileRepo.findByBoard(board);
                for(BoardFile boardFile : boardFileList){
                    boardFileRepo.delete(boardFile);
                }
            }

            List<BoardComment> delete9 = boardCommentRepo.findByUser(delete1.getUser());
            for(BoardComment boardComment : delete9){
                boardCommentRepo.delete(boardComment);
            }

            List<BoardStar> delete10 = boardStarRepo.findByUser(delete1.getUser());
            for(BoardStar boardStar : delete10){
                boardStarRepo.delete(boardStar);
            }

            List<BoardLog> delete11 = boardLogRepo.findByUser(delete1.getUser());
            for(BoardLog boardLog : delete11){
                boardLogRepo.delete(boardLog);
            }

            List<BookRental> delete6 = bookRentalRepo.findByUserId(delete1.getUser().getId());
            for(BookRental bookRental : delete6){
                bookRentalRepo.delete(bookRental);
            }

            List<CoinHistory> delete7 = coinHistoryRepo.findByUser(delete1.getUser());
            for(CoinHistory coinHistory : delete7){
                coinHistoryRepo.delete(coinHistory);
            }

            List<Portfolio> delete8 = portfolioRepo.findByUser(delete1.getUser());
            for(Portfolio portfolio : delete8){
                portfolioRepo.delete(portfolio);
                List<PortfolioFile> portfolioFileList = portfolioFileRepo.findByPortfolio(portfolio);
                for(PortfolioFile portfolioFile : portfolioFileList){
                    portfolioFileRepo.delete(portfolioFile);
                }
            }

            studentRepo.deleteById(id);
        }
    }

    @Transactional
    public void deleteStudent(SearchVO params){
        for(long id : params.getIdList()) {
            studentRepo.deleteById(id);
        }
    }

    @Transactional
    public void updateStudentDelete(SearchVO params){
        QStudent qStudent = QStudent.student;
        QUser qUser = QUser.user;
        for(long id : params.getIdList()) {
            Student save  = studentRepo.findById(id).get();

            BooleanBuilder where1 = new BooleanBuilder().and(qStudent.isDeleted.eq(false).and(qStudent.id.eq(id)));
            jpaQueryFactory.update(qStudent)
                    .set(qStudent.isDeleted, true)
                    .where(where1)
                    .execute();

            BooleanBuilder where2 = new BooleanBuilder().and(qUser.isDeleted.eq(false).and(qUser.id.eq(save.getUser().getId())));
            jpaQueryFactory.update(qUser)
                    .set(qUser.isDeleted, true)
                    .where(where2)
                    .execute();

        }
    }

    @Transactional
    public void deleteStudentTask(SearchVO params){
        String[] studentTaskIdList = params.getStudentTaskIdList();
        StudentTask studentTask  = null;
        if(studentTaskIdList != null) {
            for (String studentTaskId : studentTaskIdList) {
                studentTask = studentTaskRepo.getById(Long.parseLong(studentTaskId));
                if (studentTask != null) {
                    studentTaskRepo.delete(studentTask);
                }
            }
        }
    }

    @Transactional
    public void deleteTask(SearchVO params){
        String[] taskIdList = params.getTaskIdList();
        Task task  = null;
        if(taskIdList != null) {
            for (String taskId : taskIdList) {
                task = taskRepo.getById(Long.parseLong(taskId));
                if (task != null) {
                    taskRepo.delete(task);
                }
            }
        }
    }

    /* 코인 히스토리 */
    public Page<CoinHistory> getCoinHistoryList(SearchVO params, Pageable pageable){
        QCoinHistory qCoinHistory = QCoinHistory.coinHistory;
        Student student = studentRepo.findById(params.getId()).orElseThrow(() -> new ApiException("학생 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
        BooleanBuilder where = new BooleanBuilder().and(qCoinHistory.isDeleted.eq(false).and(qCoinHistory.user.eq(student.getUser())));
        return coinHistoryRepo.findAll(where, pageable);
    }
}
