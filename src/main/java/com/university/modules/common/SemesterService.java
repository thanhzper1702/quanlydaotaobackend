package com.university.modules.common;

import java.util.List;

public interface SemesterService {

    SemesterDTO getCurrentSemester();

    List<SemesterDTO> getAllSemesters();

    boolean switchCurrentSemester(String maHocKy, String maNVTroLy);
}
