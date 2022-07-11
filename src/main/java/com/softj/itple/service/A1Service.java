package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.BookRentalRepo;
import com.softj.itple.repo.BookRepo;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
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
        QBook qBook = QBook.book;
        BooleanBuilder where = new BooleanBuilder().and(qBook.isDeleted.eq(false));
        if(com.softj.itple.util.StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "subject":
                    where.and(qBook.subject.contains(params.getSearchValue()));
                    break;
                case "writer":
                    where.and(qBook.writer.contains(params.getSearchValue()));
                    break;
            }
        }
//        List<Book> bookList = (List<Book>) bookRepo.findAll(where);
//        return new PageImpl<Book>(bookList, pageable, bookRepo.findAll(where, pageable).getTotalElements());
        return bookRepo.findAll(where,pageable);
    }
    public Book getBook(SearchVO params){
        return bookRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public long saveBookRental(SearchVO params) {
        Book book = bookRepo.findById(params.getId()).orElseThrow(() -> new ApiException("책 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        String attendanceNo = params.getAttendanceNo();
        Student student = studentRepo.findByAttendanceNo(attendanceNo).orElseThrow(() -> new ApiException("학생 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        LocalDate currentLocalDate = LocalDate.now();
        LocalDate endLocalDate = currentLocalDate.plusWeeks(1);

        String status = null;

        if(StringUtils.equals(params.getNStatus(),"1")){
            status = "2";
        }else{
            status = "1";
        }

        bookRepo.updateStatus(status,book.getId());

        return bookRentalRepo.save(BookRental.builder()
                .user(student.getUser())
                .book(book)
                .startDate(currentLocalDate)
                .endDate(endLocalDate)
                .build()).getId();
    }

    @Transactional
    public void deleteBook(SearchVO params) {
        for(long id:params.getIdList()){
            bookRepo.deleteById(id);
        }
    }
    @Transactional
    public long saveBook(SearchVO params) {
      Book save = Book.builder().build();
      if(LongUtils.noneEmpty(params.getId())){
          save = bookRepo.findById(params.getId()).get();
      }

      save.setSubject(params.getSubject());
      save.setWriter(params.getSubject());
      save.setBookNo(params.getSubject());
      save.setContents(params.getSubject());
      save.setThumbnail(params.getSubject());

      if(StringUtils.isEmpty(params.getRentalStatus().getCode())){
            save.setBookStatus(params.getRentalStatus());
      }

        return bookRepo.save(Book.builder()
                .subject(save.getSubject())
                .writer(save.getWriter())
                .bookNo(save.getBookNo())
                .contents(save.getContents())
                .thumbnail(save.getThumbnail())
                .bookStatus(save.getBookStatus())
                .build()).getId();
    }
}