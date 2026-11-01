package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.dto.StaffInfoDto;
import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.errorResponses.BadRequest;
import com.group3.learningcommonsproject.mapper.StaffInfoMapper;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class StaffInfoService {
    private final StaffInfoRepository staffInfoRepository;
    private final StaffInfoMapper staffInfoMapper;

    @Transactional
    public StaffInfoDto createStaffInfo(StaffInfoDto staffInfoDto) {

        var staffInfo = staffInfoMapper.toEntity(staffInfoDto);
        if(staffInfo.getEmail() == null) {
            throw new BadRequest();
        }

        System.out.println("StaffInfo - " + staffInfo.getEmail());
        System.out.println("StaffInfoDto - " + staffInfoDto.getEmail());
        staffInfoRepository.save(staffInfo);
        return staffInfoMapper.toDto(staffInfo);
    }
}