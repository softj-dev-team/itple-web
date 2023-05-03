package com.softj.itple.service;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Admin;
import com.softj.itple.entity.Student;
import com.softj.itple.repo.*;
import com.softj.itple.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommonService {
    final private AcademyClassRepo academyClassRepo;
    final private StudentRepo studentRepo;

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public List<String> comFileUpload(MultipartHttpServletRequest request) throws Exception{
        List<String> result = new ArrayList<>();

        List<MultipartFile> imgList = request.getFiles("files");
        for(MultipartFile img : imgList) {
            if (!Objects.isNull(img)) {
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
                result.add(imagePath);
            }
        }

        return result;
    }

    public List<AcademyClass> getClassList(SearchVO params){
        return academyClassRepo.getAllAcademyClassList(false, Objects.nonNull(params.getAcademyType()) ? params.getAcademyType().getCode() : null);
    }

    public List<AcademyClass> getTeacherClassList(SearchVO params){
        return academyClassRepo.getTeacherAcademyClassList(false, Objects.nonNull(params.getAcademyType()) ? params.getAcademyType().getCode() : null, params.getTeacherId());
    }

    public List<AcademyClass> getOtherClassList(SearchVO params){
        return academyClassRepo.getOtherAcademyClassList(false, Objects.nonNull(params.getAcademyType()) ? params.getAcademyType().getCode() : null, params.getTeacherId());
    }

    public List<AcademyClass> getClassListByType(SearchVO params){
        return academyClassRepo.findByAcademyType(params.getAcademyType());
    }

    public List<Student> getClassStudentList(SearchVO params){
        return studentRepo.findAllWithUserByAcademyClassAndStudentStatus(AcademyClass.builder().id(params.getId()).build(), Types.StudentStatus.STUDENT);
    }
}
