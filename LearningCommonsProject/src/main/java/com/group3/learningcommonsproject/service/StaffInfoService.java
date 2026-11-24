package com.group3.learningcommonsproject.service;

import com.group3.learningcommonsproject.dto.StaffInfoDto;
import com.group3.learningcommonsproject.errorResponses.BadRequest;
import com.group3.learningcommonsproject.errorResponses.ResourceNotFoundException;
import com.group3.learningcommonsproject.mapper.StaffInfoMapper;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
            throw new BadRequest("Make sure all fields except position 2 and supervisor 2 are filled in!");
        }
        //endregion

        staffInfoRepository.save(staffInfo);
        return staffInfoMapper.toDto(staffInfo);
    }

    @Transactional
    public StaffInfoDto updateStaffInfo(Long id, StaffInfoDto data) {
        if (data == null) throw new BadRequest("Nothing Exists!");

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

    @Transactional
    public List<StaffInfoDto> filterStaffByProgram(Long programId) {
        var employees = programId == null ? staffInfoRepository.findAll()
                : staffInfoRepository.findByPrograms_Id(programId);
        return employees.stream().map(staffInfoMapper::toDto).toList();
    }

    public List<StaffInfoDto> getAllStaffInfo(String sortBy) {
        return staffInfoRepository.findAll(Sort.by(sortBy))
                .stream()
                .map(staffInfoMapper::toDto)
                .toList();
    }
}
