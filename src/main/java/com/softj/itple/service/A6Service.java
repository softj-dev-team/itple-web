package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
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
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.sound.sampled.Port;
import javax.transaction.Transactional;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class A6Service {
    private final JPAQueryFactory jpaQueryFactory;
    private final AttendanceHistoryRepo attendanceHistoryRepo;
    private final UserRepo userRepo;
    private final StudentRepo studentRepo;
    private final PortfolioRepo portfolioRepo;
    private final PortfolioFileRepo portfolioFileRepo;

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<Portfolio> getPortfolioList(SearchVO params, Pageable pageable){
        QPortfolio qPortfolio = QPortfolio.portfolio;
        QUser qUser = QUser.user;

        BooleanBuilder where = new BooleanBuilder().and(qPortfolio.isDeleted.eq(false).and(qUser.student.id.eq(params.getId()))
                                .and(qPortfolio.portfolioType.eq(params.getPortfolioType())));

        JPAQuery<Portfolio> query = jpaQueryFactory.select(Projections.fields(Portfolio.class,
                        qPortfolio.id,
                        qPortfolio.thumbnail,
                        qPortfolio.subject,
                        qPortfolio.sort,
                        qPortfolio.year,
                        qUser
                ))
                .from(qPortfolio)
                .join(qUser).on(qUser.eq(qPortfolio.user))
                .where(where)
                .orderBy(qPortfolio.year.desc(), qPortfolio.sort.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());

        return new PageImpl<Portfolio>(query.fetch(), pageable, query.fetchCount());
    }

    public Portfolio getPortfolio(SearchVO params){
        Portfolio el = portfolioRepo.findById(params.getId()).orElse(Portfolio.builder().build());
        return el;
    }

    public long getPortfolioSortCntByYear(SearchVO params){
        QPortfolio qPortfolio = QPortfolio.portfolio;
        QUser qUser = QUser.user;

        BooleanBuilder where = new BooleanBuilder().and(qPortfolio.isDeleted.eq(false).and(qUser.id.eq(Long.parseLong(params.getUserId())))
                .and(qPortfolio.portfolioType.eq(params.getPortfolioType())).and(qPortfolio.year.eq(params.getYear())));

        JPAQuery<Portfolio> query = jpaQueryFactory.select(Projections.fields(Portfolio.class,
                        qPortfolio.id.count().as("sortCnt")
                ))
                .from(qPortfolio)
                .join(qUser).on(qUser.eq(qPortfolio.user))
                .where(where);

        Portfolio result = query.fetch().get(0);

        return result.getSortCnt();
    }
    public Map<String,Object> savePortfolio(SearchVO params){

        Map<String,Object> result = new HashMap<>();
        Portfolio save = Portfolio.builder().build();

        if(LongUtils.noneEmpty(params.getId())){
            save = portfolioRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
            save.setSort(params.getSort());
        }else{
            Portfolio max = portfolioRepo.findTopByUserAndPortfolioTypeAndYearOrderBySortDesc(User.builder().id(Long.parseLong(params.getUserId())).build(), params.getPortfolioType(), params.getYear()).orElse(Portfolio.builder().build());
            if(ObjectUtils.isEmpty(max)){
                save.setSort(1);
            }else {
                save.setSort(max.getSort() + 1);
            }
        }
        save.setVisibleStatus(params.getVisibleStatus());
        save.setSubject(params.getSubject());
        save.setSummary(params.getSummary());
        save.setContents(params.getContents());
        save.setPortfolioType(params.getPortfolioType());
        save.setYear(params.getYear());

        User user = userRepo.findById(Long.parseLong(params.getUserId())).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        save.setUser(user);

        Pattern p = Pattern.compile("src=\"([^\"]+)\".*>");
        Matcher m = p.matcher(save.getContents());
        if(m.find()){
            String thumbnail = m.group(1);
            save.setThumbnail(thumbnail);
        }

        Portfolio finalSave = portfolioRepo.save(save);

        List<PortfolioFile> fileList = portfolioFileRepo.findAllById(Arrays.asList(params.getIdList()));
        for(PortfolioFile f:fileList){
            f.setPortfolio(finalSave);
        }
        portfolioFileRepo.saveAll(fileList);


        result.put("id", finalSave.getId());
        result.put("portfolioType", finalSave.getPortfolioType());
        result.put("academyType", params.getAcademyType());
        result.put("studentId", user.getStudent().getId());

        return result;
    }

    public Map<String,Object> savePortfolioList(SearchVO params){

        Map<String,Object> result = new HashMap<>();
        Portfolio save = Portfolio.builder().build();
        int[] sortList = params.getSortList();
        int i = 0;
        for(long id : params.getIdList()) {
            save = portfolioRepo.findById(id).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
            save.setSort(sortList[i]);
            portfolioRepo.save(save);
            i++;
        }
        result.put("academyType", params.getAcademyType());
        result.put("studentId", save.getUser().getStudent().getId());

        return result;
    }

    @Transactional
    public void deletePortfolio(SearchVO params){
        for(long id : params.getIdList()){
            portfolioRepo.deleteById(id);
        }
    }

    @Transactional
    public List<PortfolioFile> portfolioFileUpload(MultipartHttpServletRequest request) throws Exception{
        List<PortfolioFile> result = new ArrayList<>();
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
                result.add(portfolioFileRepo.save(PortfolioFile.builder()
                        .portfolio(null)
                        .orgFileName(realFileName)
                        .uploadFileName(imagePath)
                        .build()));
            }
        }

        return result;
    }

    @Transactional
    public void deletePortfolioFile(SearchVO params){
        for(long id:params.getIdList()){
            portfolioFileRepo.deleteById(id);
        }
    }
}
