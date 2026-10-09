package com.academic;

import com.university.modules.headoffice.HeadOfficeResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeadOfficeLegacyResponseContractTest {

    @Test
    public void testLegacyHeadOfficeItemsExposeExpectedFields() {
        String avatarLecturer = "https://ptit.edu.vn/storage/avatars/GV001.jpg";
        HeadOfficeResponseDTO.HeadOfficeLecturerResponseDTO.LecturerItem lecturer =
                new HeadOfficeResponseDTO.HeadOfficeLecturerResponseDTO.LecturerItem(
                        "GV001",
                        "TS. Nguyễn Văn A",
                        "Tiến sĩ",
                        "CNTT",
                        "gv001@ptit.edu.vn",
                        avatarLecturer,
                        4
                );

        assertEquals(avatarLecturer, lecturer.getAnhThe());
        assertEquals(4, lecturer.getSoLopPhuTrach());

        String avatarStudent = "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg";
        HeadOfficeResponseDTO.HeadOfficeStudentResponseDTO.StudentItem student =
                new HeadOfficeResponseDTO.HeadOfficeStudentResponseDTO.StudentItem(
                        "B21DCCN001",
                        "Nguyễn Văn An",
                        "Nam",
                        "2021-2025",
                        "CNTT",
                        "KTPM",
                        avatarStudent
                );

        assertEquals(avatarStudent, student.getAnhThe());
    }
}
