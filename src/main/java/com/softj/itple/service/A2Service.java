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

import javax.transaction.Transactional;
import java.io.File;
import java.sql.Time;
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
    private final StudentTaskFileRepo studentTaskFileRepo;
    private final StudentTaskRepo studentTaskRepo;
    private final StudentRepo studentRepo;
    private final CoinHistoryRepo coinHistoryRepo;
    private final TaskFileRepo taskFileRepo;

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


    public List<Integer> getStudentTaskNotSubmit(Page<Task> list){

        List<Integer> notSubmitList = new ArrayList<>();

        for(Task task : list){
            int notSubmit = studentTaskRepo.findByTaskAndStatus(task, Types.TaskStatus.NOT_SUBMIT).size();
            notSubmitList.add(notSubmit);
        }
        return notSubmitList;
    }

    public List<StudentTask> getStudentTaskListNotSubmit(Page<Task> list){

        List<StudentTask> notSubmitList = new ArrayList<>();

        for(Task task : list){
            notSubmitList = studentTaskRepo.findByTaskAndStatus(task, Types.TaskStatus.NOT_SUBMIT);
        }
        return notSubmitList;
    }

    public List<Integer> getStudentTaskSubmit(Page<Task> list){

        List<Integer> submitList = new ArrayList<>();
        for(Task task : list){
            int submit = studentTaskRepo.findByTaskAndStatus(task, Types.TaskStatus.SUBMIT).size();
            submitList.add(submit);
        }
        return submitList;
    }

    public List<StudentTask> getStudentTaskListSubmit(Page<Task> list){

        List<StudentTask> submitList = new ArrayList<>();

        for(Task task : list){
            submitList = studentTaskRepo.findByTaskAndStatus(task, Types.TaskStatus.SUBMIT);
        }
        return submitList;
    }

    public List<Integer> getStudentTaskComplete(Page<Task> list){

        List<Integer> completeList = new ArrayList<>();
        for(Task task : list){
            int complete = studentTaskRepo.findByTaskAndStatus(task, Types.TaskStatus.COMPLETE).size();
            completeList.add(complete);
        }

        return completeList;
    }

    public List<StudentTask> getStudentTaskListComplete(Page<Task> list){

        List<StudentTask> completeList = new ArrayList<>();

        for(Task task : list){
            completeList = studentTaskRepo.findByTaskAndStatus(task, Types.TaskStatus.COMPLETE);
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

    public List<StudentTask> getStudentTaskListTotal(Page<Task> list){

        List<StudentTask> totalList = new ArrayList<>();

        for(Task task : list){
            totalList = studentTaskRepo.findByTask(task);
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
        find.setStatus(Types.TaskStatus.COMPLETE);
        find.setReturnMessage(null);
        find.setCompDate(LocalDate.now());
        studentTaskRepo.save(find);
    }

    public void rejectStudentTask(SearchVO params){
        StudentTask find = studentTaskRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        find.setStatus(Types.TaskStatus.NOT_SUBMIT);
        find.setReturnMessage(params.getReturnMessage());
        studentTaskRepo.save(find);
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
                .memo("과제/독후감 확인 포인트 지급")
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
                .coin(task.getCoin())
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
