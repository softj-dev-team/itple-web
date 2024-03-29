package com.softj.itple.domain;

import com.softj.itple.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SearchVO {
	//공통
	private long id;
	private Long[] idList = new Long[0];
	private String isDeleted;
	private String searchType;
	private String searchValue;
	private int page;
	private int bfpage;
	private String bfPageStr;

	//회원가입
	private String email;
	private String userName;
	private String userId;
	private String userPw;
	private String school;
	private String zonecode;
	private String roadAddress;
	private String detailAddress;
	private String parentName;
	private String parentTel;
	private Types.Grade grade;
	private Long classId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birth;
	private Types.DayOfWeek[] dayOfWeekList = new Types.DayOfWeek[0];
	private Integer[] hourList = new Integer[0];
	private Integer[] minList = new Integer[0];
	private Integer[] leaveHourList = new Integer[0];
	private Integer[] leaveMinList = new Integer[0];
	private String telNo;

	//게시판
	private Types.AcademyType boardType;
	private Types.RoleType roleType;
	private Boolean isPopup;
	private String subject;
	private String contents;
	private Long upperId;
	private Long commentId;
	private String boardCategory;
	private String commentOrder;
	private long masterId;
	private CodeDetail codeDetail;
	private long[] removeIdList;
	private long[] updateIdList;
	private String[] codeNameList;
	private String[] newCodeNameList;
	private int[] codeOrderList;
	private List<CodeDetail> boardCategoryList;


	//수업연계
	private String classTaskName;

	private Types.TaskStatus status;
	private List<Types.TaskType> taskTypeList;
	private Types.TaskType taskType;
	private AcademyClass academyClass;
	private Task task;
	private String teacher;

	private Long studentId;
	private List<Long> studentIdList;
	private List<String> studentIdStrList1;
	private List<String> studentIdStrList2;
	private long coin;
	private String returnMessage;
	private int endTimeHour;
	private int endTimeMin;
	private Types.TaskStatus taskStatus;
	private List<Long> classIdList;
	private List<String> classNameList;
	private long taskMapId;

	//관리자-출판서적
	private long bookId;
	private String bookNo;
	private String thumbnail;
	private Types.BookRentalStatus bookStatus;
	private String writer;
	private String rentalName;
	private String bookCategory;

	//관리자-대여
	private String evBookRental; // 대여/반납 이벤트구분
	private Types.BookRentalStatus rentalStatus;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate returnDate;

	//관리자-학생관리
	private Types.StudentStatus studentStatus;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate enterDate;
	private Integer paymentDay;
	private Long price;
	private String memo;
	private String coinMemo;
	private String coinEtc;
	private String approved;
	private String edOrder;
	private String searchCateogry;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate outDate;
	//private List<StudentTaskFile> studentTaskFileList;
	private String[] studentTaskIdList;
	private String[] taskIdList;
	private String[] orgFileNameList;
	private String[] uploadFileNameList;

	//관리자-출결
	private String attendanceNo;
	private Types.AcademyType attendanceType;
	private Types.AttendanceStatus attendanceStatus;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate attendanceDate;

	//관리자-포트폴리오
	private Types.VisibleStatus visibleStatus;
	private String summary;
	private Types.PortfolioType portfolioType;
	private int[] sortList;

	private int sort;

	//관리자-수납관리
	private Long[] paymentIdList = new Long[0];
	private Long[] priceList = new Long[0];
	private Integer[] paymentDayList = new Integer[0];
	private Integer year;
	private Integer month;
	private Long cost;
	private List<AcademyClass> academyClassList;
	private Types.AcademyType academyClassType;
	private Types.PaymentType paymentType;
	private Types.PaymentType searchPaymentType;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate paymentDate;
	private Types.AcademyType academyType;
	private Types.PaymentStatus paymentStatus;
	private Student totalYear;
	private Student totalMonth;

	//SMS관리
	private int pageOffset;
	private int pageNum;
	private int pageSize;
	private int limitDay;
	private int mid;
	private String type;
	private String[] receiverList;
	private String message;

	//관리자-반관리
	private String className;
	private Boolean isInvisible;
	private Long teacherId;

	//관리자-관리자관리
	private Long adminId;
	private boolean menu1;
	private boolean menu2;
	private boolean menu3;
	private boolean menu4;
	private boolean menu5;
	private boolean menu6;
	private boolean menu7;
	private boolean menu8;
	private boolean menu9;
}




