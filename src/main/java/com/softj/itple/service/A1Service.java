package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
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
import org.springframework.util.ObjectUtils;
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
        return bookRepo.findAll(where,pageable);
    }
    public Book getBook(SearchVO params){
        return bookRepo.findById(params.getId()).orElse(Book.builder().build());
    }

    @Transactional
    public void deleteBook(SearchVO params) {
        for(long id : params.getIdList()){
            bookRepo.deleteById(id);
        }
    }

    @Transactional
    public long saveBook(SearchVO params) {

      Book save = bookRepo.findById(params.getId()).orElse(Book.builder().build());

      String startDateStr = params.getStartDate();
      String endDateStr = params.getEndDate();

      LocalDate startDate = null;
      LocalDate endDate = null;

      if(!StringUtils.isEmpty(startDateStr)){
          int startDateYear = Integer.parseInt(startDateStr.substring(0,startDateStr.indexOf("년")));
          int startDateMonth = Integer.parseInt(startDateStr.substring(6,startDateStr.indexOf("월")));
          int startDateDay = Integer.parseInt(startDateStr.substring(10,startDateStr.indexOf("일")));

          startDate = LocalDate.of(startDateYear,startDateMonth,startDateDay);
      }

      if(!StringUtils.isEmpty(endDateStr)){
          int endDateYear = Integer.parseInt(endDateStr.substring(0,endDateStr.indexOf("년")));
          int endDateMonth = Integer.parseInt(endDateStr.substring(6,endDateStr.indexOf("월")));
          int endDateDay = Integer.parseInt(endDateStr.substring(10,endDateStr.indexOf("일")));

          endDate = LocalDate.of(endDateYear,endDateMonth,endDateDay);
      }

      save.setSubject(params.getSubject());
      save.setWriter(params.getWriter());
      save.setBookNo(params.getBookNo());
      save.setContents(params.getContents());
      save.setThumbnail(params.getThumbnail());
      save.setBookStatus(params.getBookStatus());
      save.setStartDate(startDate);
      save.setEndDate(endDate);
      save.setRentalName(params.getRentalName());


      if(ObjectUtils.isEmpty(params.getBookStatus())){
            save.setBookStatus(Types.BookRentalStatus.AVAILABLE);
      }

        return bookRepo.save(Book.builder()
                .id(save.getId())
                .subject(save.getSubject())
                .writer(save.getWriter())
                .bookNo(save.getBookNo())
                .contents(save.getContents())
                .thumbnail(save.getThumbnail())
                .bookStatus(save.getBookStatus())
                .rentalName(save.getRentalName())
                .startDate(save.getStartDate())
                .endDate(save.getEndDate())
                .build()).getId();
    }

    public BookRental getBookRental(SearchVO params){
        Book book = bookRepo.findById(params.getBookId()).orElse(Book.builder().build());
        BookRental bookRental = BookRental.builder().build();

        if(book.getBookStatus().equals(Types.BookRentalStatus.LOAN)) {
            bookRental = bookRentalRepo.findTopByBookOrderByCreatedAtDesc(book).orElse(BookRental.builder().build());
        }

        bookRental.setBook(book);

        return bookRental;
    }

    @Transactional
    public long saveBookRental(SearchVO params) throws ApiException {
        BookRental save = bookRentalRepo.findById(params.getId()).orElse(BookRental.builder().build());

        Book book = bookRepo.findById(params.getBookId()).orElseThrow(() -> new ApiException("도서 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        Student student = studentRepo.findByAttendanceNo(params.getAttendanceNo()).orElseThrow(() -> new ApiException("학생 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        Types.BookRentalStatus status = null;
        LocalDate returnDate = null;

        if(StringUtils.equals(params.getEvBookRental(),"LOAN")){
            if(StringUtils.equals(book.getBookStatus(),"LOAN"))
                throw new ApiException("이미 대여중인 도서입니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            if(!StringUtils.equals(student.getUser().getUserName(),params.getUserName()))
                throw new ApiException("대여하는 학생의 출결번호가 대여자와 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            status = Types.BookRentalStatus.LOAN;
        }else{
            if(!StringUtils.equals(save.getUser().getUserName(),params.getUserName()))
                throw new ApiException("대여한 대여자와 반납하는 대여자가 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            if(save.getUser().getUserId() != student.getUser().getUserId())
                throw new ApiException("대여한 출결번호와 반납하는 출결번호가 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);

            status = Types.BookRentalStatus.AVAILABLE;
            returnDate = LocalDate.now();
        }
        String startDateStr = params.getStartDate();
        String endDateStr = params.getEndDate();

        int startDateYear = Integer.parseInt(startDateStr.substring(0,startDateStr.indexOf("년")));
        int startDateMonth = Integer.parseInt(startDateStr.substring(6,startDateStr.indexOf("월")));
        int startDateDay = Integer.parseInt(startDateStr.substring(10,startDateStr.indexOf("일")));

        int endDateYear = Integer.parseInt(endDateStr.substring(0,endDateStr.indexOf("년")));
        int endDateMonth = Integer.parseInt(endDateStr.substring(6,endDateStr.indexOf("월")));
        int endDateDay = Integer.parseInt(endDateStr.substring(10,endDateStr.indexOf("일")));

        LocalDate startDate = LocalDate.of(startDateYear,startDateMonth,startDateDay);
        LocalDate endDate = LocalDate.of(endDateYear,endDateMonth,endDateDay);

        save.setRentalStatus(status);
        save.setStartDate(startDate);
        save.setEndDate(endDate);
        save.setReturnDate(returnDate);
        save.setBook(book);
        save.setUser(student.getUser());

        if(StringUtils.equals(params.getEvBookRental(),"LOAN")) {
            book.setBookStatus(save.getRentalStatus());
            book.setStartDate(save.getStartDate());
            book.setEndDate(save.getEndDate());
            book.setRentalName(save.getUser().getUserName());
        }else{
            book.setBookStatus(save.getRentalStatus());
            book.setStartDate(null);
            book.setEndDate(null);
            book.setRentalName("");
        }

        bookRepo.save(Book.builder()
                .id(book.getId())
                .subject(book.getSubject())
                .thumbnail(book.getThumbnail())
                .contents(book.getContents())
                .bookNo(book.getBookNo())
                .writer(book.getWriter())
                .bookStatus(book.getBookStatus())
                .startDate(book.getStartDate())
                .endDate(book.getEndDate())
                .rentalName(book.getRentalName())
                .build());

        return bookRentalRepo.save(BookRental.builder()
                .id(save.getId())
                .user(save.getUser())
                .book(save.getBook())
                .rentalStatus(save.getRentalStatus())
                .startDate(save.getStartDate())
                .endDate(save.getEndDate())
                .returnDate(save.getReturnDate())
                .build()).getId();
    }
}