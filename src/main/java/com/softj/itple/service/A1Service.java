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
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.BookRentalRepo;
import com.softj.itple.repo.BookRepo;
import com.softj.itple.repo.CodeDetailRepo;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.util.CodeUtil;
import com.softj.itple.util.CustomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class A1Service {

    private final BookRepo bookRepo;
    private final BookRentalRepo bookRentalRepo;
    private final StudentRepo studentRepo;

    private final CodeDetailRepo codeDetailRepo;

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Book> getBookList(SearchVO params, Pageable pageable){

        QBook qBook = QBook.book;
        QBookRental qBookRental = QBookRental.bookRental;
        QUser qUser = QUser.user;
        QCodeDetail qCodeDetail = QCodeDetail.codeDetail;

        BooleanBuilder where = new BooleanBuilder().and(qBook.isDeleted.eq(false));

        if(com.softj.itple.util.StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "subject":
                    where.and(qBook.subject.contains(params.getSearchValue()));
                    break;
                case "rentalName":
                    where.and(qBookRental.user.userName.contains(params.getSearchValue()));
                    break;
            }
        }

        if(com.softj.itple.util.StringUtils.noneEmpty(params.getBookCategory())){
            where.and(qBook.bookCategory.eq(params.getBookCategory()));
        }

        if(com.softj.itple.util.StringUtils.noneEmpty(params.getRentalStatus())){
            if(Objects.equals(params.getRentalStatus(),Types.BookRentalStatus.AVAILABLE)){
                where.and(qBookRental.rentalStatus.eq(Types.BookRentalStatus.AVAILABLE).or(qBookRental.rentalStatus.eq(Types.BookRentalStatus.RETURN)).or(qBookRental.isNull()));
            }else{
                where.and(qBookRental.rentalStatus.eq(params.getRentalStatus()));
            }
        }

        JPAQuery<Book> query = jpaQueryFactory.select(Projections.fields(Book.class,
                        qBook.id,
                        qBook.thumbnail,
                        qBook.createdAt,
                        qBook.subject,
                        qBook.writer,
                        qBook.bookNo,
                        qBook.contents,
                        qBook.bookCategory,
                        qBookRental
                ))
                .from(qBook)
                .join(qCodeDetail).on(qCodeDetail.codeValue.eq(qBook.bookCategory).and(qCodeDetail.masterId.eq(4L)).and(qCodeDetail.academyType.eq(params.getAcademyType())))
                .leftJoin(qBookRental).on(qBookRental.book.eq(qBook).and(qBookRental.isDeleted.eq(false))
                        .and(qBookRental.id.eq(JPAExpressions.select(qBookRental.id.max())
                                .from(qBookRental)
                                .where(qBookRental.book.eq(qBook).and(qBookRental.isDeleted.eq(false))))))
                .where(where)
                .orderBy(qBookRental.rentalStatus.desc().nullsLast(), qBook.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());

        return new PageImpl<Book>(query.fetch(), pageable, query.fetchCount());
    }
    public Book getBook(SearchVO params) {
        return bookRepo.findById(params.getId()).orElseThrow(() -> new ApiException("도서 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional
    public void deleteBook(SearchVO params) {
        for(long id : params.getIdList()){

            Book book = bookRepo.findById(id).orElse(Book.builder().build());
            List<BookRental> bookRental = bookRentalRepo.findAllByBook(book);

            for(BookRental deleteRental : bookRental){
                bookRentalRepo.deleteById(deleteRental.getId());
            }

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
        save.setBookCategory(params.getBookCategory());


        if(ObjectUtils.isEmpty(params.getBookStatus())){
            save.setBookStatus(Types.BookRentalStatus.AVAILABLE);
        }

        bookRepo.save(save);
    }

    public BookRental getBookRental(SearchVO params){

        Book book = bookRepo.findById(params.getBookId()).orElse(Book.builder().build());
        BookRental bookRental = bookRentalRepo.findTopByBookOrderByCreatedAtDesc(book).orElse(BookRental.builder().build());

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
            if(StringUtils.equals(book.getBookStatus(),"RETURN") || StringUtils.equals(book.getBookStatus(),"AVAILABLE"))
                throw new ApiException("대여되지 않은 도서입니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            if(StringUtils.equals(params.getEvBookRental(),"RELOAN") && !StringUtils.equals(save.getUser().getUserName(),params.getUserName())){
                throw new ApiException("연장하는 대여자와 기존 대여자가 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }else if (!StringUtils.equals(save.getUser().getUserName(),params.getUserName())) {
                throw new ApiException("대여한 대여자와 반납하는 대여자가 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }
            if(save.getUser().getId() != student.getUser().getId())
                throw new ApiException("대여한 출결번호와 반납하는 출결번호가 일치하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);

            status = Types.BookRentalStatus.RETURN;
            if(StringUtils.equals(params.getEvBookRental(),"RELOAN")) {
                status = Types.BookRentalStatus.LOAN;
            }
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

        book.setBookStatus(status);

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

        bookRepo.save(book);
        bookRentalRepo.save(save);

        return bookRepo.save(book).getId();
    }

    @Transactional
    public List<CodeDetail> selectBookCategoryList(SearchVO params){
        CodeUtil cu = new CodeUtil(codeDetailRepo);
        List<CodeDetail> list = cu.getBookCodeList(params.getMasterId(), params.getAcademyType().name());
        return list;
    }

    @Transactional
    public void saveBookCategory(SearchVO params){
        // delete

        if(params.getRemoveIdList() != null) {
            for (long id : params.getRemoveIdList()) {
                CodeDetail save = codeDetailRepo.findById(id).get();
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
                codeDetailRepo.save(save);
                i++;
            }
        }

        // insert
        if(params.getNewCodeNameList() != null){

            long masterId = 4;
            CodeDetail getTopCodeDetailOrderBySort = codeDetailRepo.findTopByMasterIdOrderBySortDesc(masterId).orElse(CodeDetail.builder().build());
            CodeDetail getTopCodeDetailOrderByCodeValue = codeDetailRepo.findTopByMasterIdOrderByCodeValueDesc(masterId).orElse(CodeDetail.builder().build());

            int startSort = getTopCodeDetailOrderBySort.getSort();
            int startCodeValue = 0;

            if(!StringUtils.isEmpty(getTopCodeDetailOrderByCodeValue.getCodeValue())){
                startCodeValue = Integer.parseInt(getTopCodeDetailOrderByCodeValue.getCodeValue());
            }

            for(String codeName : params.getNewCodeNameList()){
                CodeDetail save = codeDetailRepo.findByMasterIdAndCodeName(4L, codeName).orElse(CodeDetail.builder().build());

                if(save.getId() != 0){
                    save.setDeleted(false);
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

                    save.setMasterId(masterId);
                    save.setCodeName(codeName);
                    save.setCodeValue(codeValue);
                    save.setAcademyType(params.getAcademyType());
                    save.setRoleType(Types.RoleType.STUDENT);
                    save.setSort(startSort);
                    codeDetailRepo.save(save);
                }
            }
        }

        CodeUtil cu = new CodeUtil(codeDetailRepo);
        cu.refresh();
    }
}