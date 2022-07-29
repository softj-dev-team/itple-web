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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class A1Service {

    private final BookRepo bookRepo;
    private final BookRentalRepo bookRentalRepo;
    private final StudentRepo studentRepo;

    public Page<Book> getBookList(SearchVO params, Pageable pageable){
        QBook qBook = QBook.book;
        BooleanBuilder where = new BooleanBuilder().and(qBook.isDeleted.eq(false));
        if(com.softj.itple.util.StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "subject":
                    where.and(qBook.subject.contains(params.getSearchValue()));
                    break;
                case "rentalName":
                    where.and(qBook.rentalName.contains(params.getSearchValue()));
                    break;
            }
        }
        return bookRepo.findAll(where,pageable);
    }
    public Book getBook(SearchVO params){
        return bookRepo.findById(params.getBookId()).orElseThrow(() -> new ApiException("도서 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional
    public void deleteBook(SearchVO params) {
        for(long id : params.getIdList()){
            bookRepo.deleteById(id);
        }
    }

    @Transactional
    public void saveBook(SearchVO params) {

      Book save = bookRepo.findById(params.getId()).orElse(Book.builder().build());

      LocalDate startDate = params.getStartDate();
      LocalDate endDate = params.getEndDate();

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

      bookRepo.save(save);
    }

    public BookRental getBookRental(SearchVO params){
        Book book = bookRepo.findById(params.getBookId()).orElse(Book.builder().build());
        BookRental bookRental = BookRental.builder().build();

        if(book.getBookStatus().equals(Types.BookRentalStatus.LOAN) || book.getBookStatus().equals(Types.BookRentalStatus.DELINQUENT)) {
            bookRental = bookRentalRepo.findTopByBookOrderByCreatedAtDesc(book).orElse(BookRental.builder().build());
        }

        bookRental.setBook(book);

        return bookRental;
    }

    public BookRental getBookReturn(SearchVO params){
        Book book = bookRepo.findById(params.getBookId()).orElse(Book.builder().build());
        BookRental bookRental = bookRentalRepo.findTopByBookOrderByCreatedAtDesc(book).orElse(BookRental.builder().build());

        return bookRental;
    }

    @Transactional
    public void saveBookRental(SearchVO params) throws ApiException {
        BookRental save = bookRentalRepo.findById(params.getId()).orElse(BookRental.builder().build());

        Book book = bookRepo.findById(params.getBookId()).orElseThrow(() -> new ApiException("도서 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        Student student = studentRepo.findByAttendanceNo(params.getAttendanceNo()).orElseThrow(() -> new ApiException("학생 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        Types.BookRentalStatus status;
        LocalDate returnDate = null;
        LocalDate now = LocalDate.now();
        LocalDate startDate = params.getStartDate();
        LocalDate endDate = params.getEndDate();

        if(StringUtils.equals(params.getEvBookRental(),"LOAN")){
            if(StringUtils.equals(book.getBookStatus(),"LOAN"))
                throw new ApiException("이미 대여중인 도서입니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            if(!StringUtils.equals(student.getUser().getUserName(),params.getUserName()))
                throw new ApiException("대여하는 학생의 출결번호가 대여자와 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            if(endDate.isBefore(now)){
                status = Types.BookRentalStatus.DELINQUENT;
            }else {
                status = Types.BookRentalStatus.LOAN;
            }
        }else{
            if(!StringUtils.equals(save.getUser().getUserName(),params.getUserName()))
                throw new ApiException("대여한 대여자와 반납하는 대여자가 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            if(save.getUser().getId() != student.getUser().getId())
                throw new ApiException("대여한 출결번호와 반납하는 출결번호가 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);

            status = Types.BookRentalStatus.RETURN;
            returnDate = LocalDate.now();
        }

        save.setRentalStatus(status);
        save.setStartDate(startDate);
        save.setEndDate(endDate);
        save.setReturnDate(returnDate);
        save.setBook(book);
        save.setUser(student.getUser());

        if(status.equals(Types.BookRentalStatus.RETURN))
            status = Types.BookRentalStatus.AVAILABLE;

        if(StringUtils.equals(params.getEvBookRental(),"LOAN")) {
            book.setBookStatus(status);
            book.setStartDate(save.getStartDate());
            book.setEndDate(save.getEndDate());
            book.setRentalName(save.getUser().getUserName());
        }else{
            book.setBookStatus(status);
            book.setStartDate(null);
            book.setEndDate(null);
            book.setRentalName("");
        }

        bookRepo.save(book);
        bookRentalRepo.save(save);
    }


    @Transactional
    public long saveBookReturn(SearchVO params) throws ApiException {

        Book book = bookRepo.findById(params.getBookId()).orElseThrow(() -> new ApiException("도서 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        BookRental save = bookRentalRepo.findTopByBookOrderByCreatedAtDesc(book).orElse(BookRental.builder().build());

        if(StringUtils.equals(book.getBookStatus(),"AVAILABLE"))
            throw new ApiException("이미 반납된 도서입니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        
        Types.BookRentalStatus status = Types.BookRentalStatus.AVAILABLE;
        LocalDate returnDate = LocalDate.now();

        save.setRentalStatus(status);
        save.setReturnDate(returnDate);

        book.setBookStatus(save.getRentalStatus());
        book.setStartDate(null);
        book.setEndDate(null);
        book.setRentalName("");

        bookRepo.save(book);
        bookRentalRepo.save(save);

        return bookRepo.save(book).getId();
    }
}