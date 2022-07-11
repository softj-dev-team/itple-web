package com.softj.itple.service;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.BookRentalRepo;
import com.softj.itple.repo.BookRepo;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class A1Service {

    private final BookRepo bookRepo;
    private final BookRentalRepo bookRentalRepo;
    private final StudentRepo studentRepo;

    final private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public Page<Book> getBookList(SearchVO params, Pageable pageable){

        if(StringUtils.equals(params.getSearchType(),"subject")){
            return bookRepo.findBySubject(params.getSearchValue(), pageable);
        }else if(StringUtils.equals(params.getSearchType(),"writer")){
            return bookRepo.findByWriter(params.getSearchValue(), pageable);
        }else{
            return bookRepo.findAll(pageable);
        }
    }
    public Book getBook(SearchVO params){
        return bookRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));

    }

    @Transactional
    public void saveBookRental(SearchVO params) {
        BookRental save = new BookRental();
        if(LongUtils.noneEmpty(params.getId())){
            save = bookRentalRepo.findById(params.getId()).get();
        }

        String attendanceNo = params.getAttendanceNo();
        Optional<Student> student = studentRepo.findByAttendanceNo(attendanceNo);

        LocalDate currentLocalDate = LocalDate.now();
        LocalDate endLocalDate = currentLocalDate.plusWeeks(1);

        String startDate = currentLocalDate.format(df);
        String endDate = endLocalDate.format(df);

        String status = null;

        if(StringUtils.equals(params.getNStatus(),"1")){
            status = "2";
        }else{
            status = "1";
        }

        save.setBookId(params.getId());
        save.setUserId(student.get().getUser().getId());
        save.setStartDate(startDate);
        save.setEndDate(endDate);
        save.setStatus(status);

        bookRentalRepo.save(save);
        bookRepo.updateStatus(status,save.getBookId());
    }

    @Transactional
    public void deleteBook(SearchVO params) {
        for(long id:params.getIdList()){
            bookRepo.deleteById(id);
        }
    }
    @Transactional
    public void saveBook(SearchVO params) {
        Book save = new Book();

        String status = params.getStatus();
        if(StringUtils.isEmpty(status)){
            status = "1";
        }

        save.setSubject(params.getSubject());
        save.setWriter(params.getWriter());
        save.setStatus(status);
        save.setBook_no(params.getBook_no());
        save.setContents(params.getContents());

        bookRepo.save(save);
    }
    @Transactional
    public String bookFileUpload(MultipartHttpServletRequest request) throws Exception{

        MultipartFile img = request.getFile("file");
        String imagePath = null;
        if (!Objects.isNull(img) && com.softj.itple.util.StringUtils.noneEmpty(img.getOriginalFilename())) {

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
        }

        return imagePath;
    }
}