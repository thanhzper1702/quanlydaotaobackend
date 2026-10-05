package com.university.modules.lecturer;
import com.university.modules.lecturer.requestDTO.*;
import com.university.modules.lecturer.responseDTO.*;


public class LecturerServiceImpl implements LecturerService {

    @Override
    public LecturerTeachingClassResponseDTO getTeachingClasses(String maGV, String maHocKy) {
        return null;
    }

    @Override
    public LecturerClassGradeResponseDTO getClassGrades(String maGV, String maLHP) {
        return null;
    }

    @Override
    public UpdateGradesResponseDTO updateClassGrades(String maGV, String maLHP, UpdateGradesRequestDTO request) {
        return null;
    }

    @Override
    public byte[] exportGradesExcel(String maGV, String maLHP) {
        return null;
    }

    @Override
    public LecturerTeachingScheduleResponseDTO getTeachingSchedule(String maGV, String maHocKy) {
        return null;
    }

    @Override
    public LecturerAttendanceResponseDTO getAttendanceList(String maGV, String maLichHoc) {
        return null;
    }

    @Override
    public UpdateAttendanceResponseDTO updateAttendance(String maGV, String maLichHoc, UpdateAttendanceRequestDTO request) {
        return null;
    }
}
