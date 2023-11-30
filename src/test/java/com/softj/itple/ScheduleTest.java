package com.softj.itple;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.Book;
import com.softj.itple.entity.BookRental;
import com.softj.itple.repo.BookRentalRepo;
import com.softj.itple.repo.BookRepo;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith(SpringExtension.class)
@Slf4j
class ScheduleTest {
    @Autowired
    BookRentalRepo bookRentalRepo;
    BookRepo bookRepo;

    @Test
    void schedule(){
        LocalDateTime now = LocalDateTime.now();
        List<BookRental> list = bookRentalRepo.findAllByEndDateAndRentalStatus(now.toLocalDate().plusDays(0), Types.BookRentalStatus.LOAN);
        list.forEach(e -> {
            e.setRentalStatus(Types.BookRentalStatus.DELINQUENT);
            bookRentalRepo.save(e);

            Book book = e.getBook();
            book.setBookStatus(Types.BookRentalStatus.DELINQUENT);
            bookRepo.save(book);
        });
        bookRentalRepo.saveAll(list);
    }
}