package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.dto.StaffInfoDto;
import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.errorResponses.BadRequest;
import com.group3.learningcommonsproject.errorResponses.ResourceNotFoundException;
import com.group3.learningcommonsproject.mapper.StaffInfoMapper;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class StaffInfoService {
    private final StaffInfoRepository staffInfoRepository;
    private final StaffInfoMapper staffInfoMapper;

    @Transactional
    public StaffInfoDto createStaffInfo(StaffInfoDto data) {
        var staffInfo = staffInfoMapper.toEntity(data);
        //region if(staffInfo == null(){throw new BadRequest}
        if (staffInfo.getFullName() == null ||
                staffInfo.getEmail() == null ||
                staffInfo.getPosition1() == null ||
                staffInfo.getSupervisor1() == null ||
                staffInfo.getCurrentPoints() == null) {
            throw new BadRequest();
        }
        //endregion

        staffInfoRepository.save(staffInfo);
        return staffInfoMapper.toDto(staffInfo);
    }

    @Transactional
    public StaffInfoDto updateStaffInfo(Long id, StaffInfoDto data) {
        if (data == null) throw new BadRequest();

        var staffInfo = staffInfoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        staffInfoMapper.update(data, staffInfo);
        staffInfoRepository.save(staffInfo);
        return staffInfoMapper.toDto(staffInfo);
    }

    @Transactional
    public void deleteStaffInfo(Long id) {
        var staffInfo = staffInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException());

        staffInfoRepository.delete(staffInfo);
    }
}
