package com.softj.itple.service;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.*;
import com.softj.itple.util.AligoUtil;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class A2Service {
    private final JPAQueryFactory jpaQueryFactory;
    private final AcademyClassRepo academyClassRepo;
    private final TaskRepo taskRepo;

    private final TaskFileRepo taskFileRepo;
    private final StudentTaskFileRepo studentTaskFileRepo;
    private final StudentTaskRepo studentTaskRepo;
    private final StudentRepo studentRepo;
    private final CoinHistoryRepo coinHistoryRepo;

    private final AligoUtil aligoUtil;

    final private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public Page<AcademyClass> getClassTaskList(SearchVO params, Pageable pageable){
        QAcademyClass qAcademyClass = QAcademyClass.academyClass;
        QTask qTask = QTask.task;
        QStudent qStudent = QStudent.student;
        BooleanBuilder where = new BooleanBuilder().and(qAcademyClass.isDeleted.eq(false)).and(qAcademyClass.isInvisible.eq(false));

        //반이름 검색
        if(StringUtils.noneEmpty(params.getSearchValue())){
            where.and(qAcademyClass.className.contains(params.getSearchValue()));
        }

        JPAQuery<AcademyClass> query = jpaQueryFactory.select(Projections.fields(AcademyClass.class,
                        qAcademyClass.id,
                        qAcademyClass.className,
                        qAcademyClass.academyType,
                        ExpressionUtils.as(
                                JPAExpressions.select(qStudent.count())
                                        .from(qStudent)
                                        .where(qStudent.academyClass.eq(qAcademyClass)),"studentCount"))
                )
                .from(qAcademyClass)
                .where(where)
                .orderBy(qAcademyClass.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        List<AcademyClass> list = query.fetch();
        for(AcademyClass el : list){
            el.setTaskList(
                    jpaQueryFactory.select(
                                    Projections.fields(Task.class,
                                            qTask.subject,
                                            qTask.startDate,
                                            qTask.taskType,
                                            qTask.teacher,
                                            qTask.endDate)
                            )
                            .from(qTask)
                            .where(new BooleanBuilder().and(qTask.taskType.eq(params.getTaskType()).and(qTask.academyClass.eq(el))))
                            .orderBy(qTask.id.desc())
                            .limit(2).fetch()
            );
        }

        return new PageImpl<AcademyClass>(list, pageable, query.fetchCount());
    }

    public AcademyClass getClass(SearchVO params){
        return academyClassRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    public Task getTask(SearchVO params){
        return taskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    public Page<Task> getTaskList(SearchVO params, Pageable pageable){
        QTask qTask = QTask.task;
        BooleanBuilder where = new BooleanBuilder().and(qTask.isDeleted.eq(false)).and(qTask.academyClass.eq(params.getAcademyClass())).and(qTask.taskType.eq(params.getTaskType()));
        if(StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "subject":
                    where.and(qTask.subject.contains(params.getSearchValue()));
                    break;
            }
        }
        return taskRepo.findAll(where, pageable);
    }

    public Page<StudentTask> getTaskStudentList(SearchVO params, Pageable pageable){
        QTask qTask = QTask.task;
        QStudentTask qStudentTask = QStudentTask.studentTask;
        BooleanBuilder where = new BooleanBuilder().and(qTask.isDeleted.eq(false));

        if(!Objects.isNull(params.getTaskTypeList())) {
            BooleanBuilder where2 = new BooleanBuilder();
            for(Types.TaskType taskType : params.getTaskTypeList()) {
                where2.or(qTask.taskType.eq(taskType));
            }
            where.and(where2);
        }

        if(!Objects.isNull(params.getStatus())) {
            where.and(qStudentTask.status.eq(params.getStatus()));
        }

        if(StringUtils.noneEmpty(params.getSubject())){
            where.and(qTask.subject.contains(params.getSubject()));
        }

        if(LongUtils.noneEmpty(params.getClassId())) {
            params.setId(params.getClassId());
            AcademyClass academyClass = getClass(params);
            where.and(qTask.academyClass.eq(academyClass));
        }

        if(StringUtils.noneEmpty(params.getUserName())){
            where.and(qStudentTask.student.user.userName.contains(params.getUserName()));
        }

        if(StringUtils.noneEmpty(params.getAttendanceNo())){
            where.and(qStudentTask.student.attendanceNo.contains(params.getAttendanceNo()));
        }

        JPAQuery<StudentTask> query = jpaQueryFactory.select(Projections.fields(StudentTask.class,
                        qStudentTask.id,
                        qStudentTask.status,
                        qStudentTask.student,
                        qTask
                ))
                .from(qTask)
                .leftJoin(qStudentTask).on(qStudentTask.task.eq(qTask))
                .where(where)
                .orderBy(qTask.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());

        return new PageImpl<StudentTask>(query.fetch(), pageable, query.fetchCount());
    }




    public HashMap<String, List> getStudentTaskMemberList(SearchVO params){
        HashMap<String, List> map = new HashMap<>();
        List<Long> idList = new ArrayList<>();
        List<Integer> numList = new ArrayList<>();
        List<List<StudentTask>> submitStudentList = new ArrayList<>();
        for(long taskId : params.getIdList()) {
            params.setId(taskId);
            int submitNum = studentTaskRepo.findByTaskIdAndStatus(params.getId(), Types.TaskStatus.SUBMIT).size();

            idList.add(taskId);
            numList.add(submitNum);
            submitStudentList.add(getStudentTaskListSubmit(params));


            /*getStudentTaskNotSubmit(params);
            getStudentTaskSubmit(params);
            getStudentTaskListSubmit(params);
            getStudentTaskComplete(params);
            getStudentTaskListComplete(params);
            getStudentTaskTotal(params);
            getStudentTaskListTotal(params);*/
        }
        map.put("idList", idList);
        map.put("numList", numList);
        map.put("submitStudentList", submitStudentList);
        return map;
    }

    public List<Integer> getStudentTaskNotSubmit(Page<Task> list){

        List<Integer> notSubmitList = new ArrayList<>();

        for(Task task : list){
            int notSubmit = studentTaskRepo.findByTaskIdAndStatus(task.getId(), Types.TaskStatus.NOT_SUBMIT).size();
            notSubmitList.add(notSubmit);
        }
        return notSubmitList;
    }

    public List<StudentTask> getStudentTaskListNotSubmit(SearchVO params){
        List<StudentTask> notSubmitList = studentTaskRepo.findByTaskIdAndStatus(params.getId(), Types.TaskStatus.NOT_SUBMIT);
        return notSubmitList;
    }

    public List<Integer> getStudentTaskSubmit(Page<Task> list){

        List<Integer> submitList = new ArrayList<>();
        for(Task task : list){
            int submit = studentTaskRepo.findByTaskIdAndStatus(task.getId(), Types.TaskStatus.SUBMIT).size();
            submitList.add(submit);
        }
        return submitList;
    }

    public List<StudentTask> getStudentTaskListSubmit(SearchVO params){
        List<StudentTask> submitList = studentTaskRepo.findByTaskIdAndStatus(params.getId(), Types.TaskStatus.SUBMIT);
        return submitList;
    }

    public List<Integer> getStudentTaskComplete(Page<Task> list){

        List<Integer> completeList = new ArrayList<>();
        for(Task task : list){
            int complete = studentTaskRepo.findByTaskIdAndStatus(task.getId(), Types.TaskStatus.COMPLETE).size();
            completeList.add(complete);
        }

        return completeList;
    }

    public List<List<StudentTask>> getStudentTaskListComplete(Page<Task> list){

        List<List<StudentTask>> completeList = new ArrayList<>();

        for(Task task : list){
            List<StudentTask> completeTaskList = studentTaskRepo.findByTaskIdAndStatus(task.getId(), Types.TaskStatus.COMPLETE);
            completeList.add(completeTaskList);
        }
        return completeList;
    }

    public List<Integer> getStudentTaskTotal(Page<Task> list){

        List<Integer> totalList = new ArrayList<>();
        for(Task task : list){
            int total = studentTaskRepo.findByTask(task).size();
            totalList.add(total);
        }

        return totalList;
    }

    public List<List<StudentTask>> getStudentTaskListTotal(Page<Task> list){

        List<List<StudentTask>> totalList = new ArrayList<>();

        for(Task task : list){
            List<StudentTask> totalTaskList = studentTaskRepo.findByTask(task);
            totalList.add(totalTaskList);
        }
        return totalList;
    }

    public StudentTask getStudentTask(SearchVO params){
        return studentTaskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    public List<StudentTask> getStudentList(SearchVO params){
        Task task = taskRepo.getOne(params.getId());
        List<StudentTask> studentTaskList = studentTaskRepo.findByTask(task);
        return studentTaskList;
    }

    public StudentTask getStudentTaskFetch(SearchVO params){
        return studentTaskRepo.findWithStudentById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    public Page<StudentTask> getStudentTaskList(SearchVO params, Pageable pageable){
        QStudentTask qStudentTask = QStudentTask.studentTask;
        BooleanBuilder where = new BooleanBuilder().and(qStudentTask.isDeleted.eq(false)).and(qStudentTask.task.eq(params.getTask()));
        if(StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "userName":
                    where.and(qStudentTask.student.user.userName.contains(params.getSearchValue()));
                    break;
                case "attendanceNo":
                    where.and(qStudentTask.student.attendanceNo.contains(params.getSearchValue()));
                    break;
            }
        }
        return studentTaskRepo.findAll(where, pageable);
    }

    @Transactional
    public void completeStudentTask(SearchVO params){
        StudentTask find = studentTaskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        find.setStatus(params.getTaskStatus());
        find.setReturnMessage(null);
        find.setCompDate(LocalDate.now());
        studentTaskRepo.save(find);
    }

    public void rejectStudentTask(SearchVO params){
        StudentTask find = studentTaskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        find.setStatus(Types.TaskStatus.NOT_SUBMIT);
        find.setReturnMessage(params.getReturnMessage());
        studentTaskRepo.save(find);

        String templateCode = "TK_3148";
        String message = "[" + find.getTask().getAcademyClass().getAcademyType().getMessage() + "학원 "+ find.getTask().getTaskType().getMessage() +" 반려사유 안내]\n" +
                "학생명 : "+ find.getStudent().getUser().getUserName() + "\n과제명 : " + find.getTask().getSubject() + "\n" +
                "반려사유 : " + params.getReturnMessage();
        aligoUtil.send(find.getStudent().getParentTel(), message, templateCode, "[" + find.getTask().getAcademyClass().getAcademyType().getMessage() + "학원 "+ find.getTask().getTaskType().getMessage() +" 반려사유 안내]", null);
    }


    @Transactional
    public void plusCoinStudentTask(SearchVO params){
        StudentTask find = studentTaskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));

        find.setCoinComp(true);
        studentTaskRepo.save(find); // 코인 증감 확인

        Task task = find.getTask();
        Student student = find.getStudent();
        student.setCoin(student.getCoin()+ task.getCoin());
        studentRepo.save(student);

        coinHistoryRepo.save(CoinHistory.builder()
                .user(student.getUser())
                .coinStatus(Types.CoinStatus.PLUS)
                .coin(task.getCoin())
                .memo("과제/독후감 확인")
                .build());

    }

    @Transactional
    public void minusCoinStudentTask(SearchVO params){
        StudentTask find = studentTaskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));

        find.setCoinComp(true);
        studentTaskRepo.save(find); // 코인 증감 확인

        Task task = find.getTask();
        Student student = find.getStudent();

        student.setCoin(student.getCoin() - task.getCoin());
        studentRepo.save(student);

        coinHistoryRepo.save(CoinHistory.builder()
                .user(student.getUser())
                .coinStatus(Types.CoinStatus.MINUS)
                .coin(-task.getCoin())
                .memo("과제/독후감 확인 포인트 차감")
                .build());
    }

    @Transactional
    public void saveStudentTask(SearchVO params){
        Task save = Task.builder().build();
        if(LongUtils.noneEmpty(params.getId())){
            save = taskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        }
        save.setTaskType(params.getTaskType());
        save.setAcademyClass(AcademyClass.builder().id(params.getClassId()).build());
        save.setSubject(params.getSubject());

        save.setContents(params.getContents());
        save.setTeacher(params.getTeacher());
        save.setStartDate(params.getStartDate());

        LocalDateTime endTimeDate = LocalDateTime.of(params.getEndDate(), LocalTime.of(params.getEndTimeHour(),params.getEndTimeMin()));
        save.setEndDate(endTimeDate);
        save.setCoin(params.getCoin());
        taskRepo.save(save);

        for(long studentId : params.getStudentIdList()){
            Student studentSave = studentRepo.getOne(studentId);
            StudentTask studentTaskSave = studentTaskRepo.findByTaskAndStudent(save, studentSave).orElse(StudentTask.builder().build());
            studentTaskSave.setStudent(studentSave);
            studentTaskSave.setTask(save);
            studentTaskSave.setStatus(Types.TaskStatus.NOT_SUBMIT);
            studentTaskRepo.save(studentTaskSave);
        }

        List<TaskFile> fileList = taskFileRepo.findAllById(Arrays.asList(params.getIdList()));
        for(TaskFile f:fileList){
            f.setTask(save);
        }
        taskFileRepo.saveAll(fileList);
    }

    @Transactional
    public void deleteTask(SearchVO params){
        for(long id : params.getIdList()){
            taskRepo.deleteById(id);
        }
    }

    @Transactional
    public void deleteStudentTask(SearchVO params){
        for(long id : params.getIdList()){
            studentTaskRepo.deleteById(id);
        }
    }

    @Transactional
    public List<TaskFile> taskFileUpload(MultipartHttpServletRequest request) throws Exception{
        List<TaskFile> result = new ArrayList<>();
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
                result.add(taskFileRepo.save(TaskFile.builder()
                        .task(null)
                        .orgFileName(realFileName)
                        .uploadFileName(imagePath)
                        .build()));
            }
        }

        return result;
    }

    @Transactional
    public void deleteTaskFile(SearchVO params){
        for(long id:params.getIdList()){
            studentTaskFileRepo.deleteById(id);
        }
    }



}
