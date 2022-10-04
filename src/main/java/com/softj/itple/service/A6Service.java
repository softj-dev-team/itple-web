package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
        BooleanBuilder where = new BooleanBuilder().and(qPortfolio.isDeleted.eq(false).and(qPortfolio.user.student.id.eq(params.getId())));
        return portfolioRepo.findAll(where, pageable);
    }

    public Portfolio getPortfolio(SearchVO params){
        Portfolio el = portfolioRepo.findById(params.getId()).orElse(Portfolio.builder().build());
        return el;
    }
    public long savePortfolio(SearchVO params){
        Portfolio save = Portfolio.builder().build();
        if(LongUtils.noneEmpty(params.getId())){
            save = portfolioRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        }
        save.setVisibleStatus(params.getVisibleStatus());
        save.setSubject(params.getSubject());
        save.setSummary(params.getSummary());
        save.setContents(params.getContents());
        save.setUser(User.builder().id(Long.parseLong(params.getUserId())).build());
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

        return finalSave.getId();
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
