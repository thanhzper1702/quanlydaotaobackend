package com.university.modules.headoffice;

import com.university.core.model.HocKy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeadOfficeAcademicStatisticsTest {

    @Test
    void classifiesWeightedSemesterAveragesAndIncludesStudentsWithoutScores() {
        HeadOfficeServiceImpl service = new HeadOfficeServiceImpl(repositoryWithRows(List.of(
                row("SV01", "27", "3"),
                row("SV02", "24", "3"),
                row("SV03", "19.5", "3"),
                row("SV04", "15", "3"),
                row("SV05", "14.97", "3"),
                row("SV06", "0", "0")
        )));

        var result = service.getSemesterAcademicStatistics("2026_2027_HK1", "all", "all", "all");

        assertEquals(6, result.getTotalStudents());
        assertEquals(1, result.getAcademicStandings().get("XUAT_SAC").getCount());
        assertEquals(1, result.getAcademicStandings().get("GIOI").getCount());
        assertEquals(1, result.getAcademicStandings().get("KHA").getCount());
        assertEquals(1, result.getAcademicStandings().get("TRUNG_BINH").getCount());
        assertEquals(1, result.getAcademicStandings().get("YEU").getCount());
        assertEquals(1, result.getAcademicStandings().get("CHUA_XEP_LOAI").getCount());
        assertEquals(16.7, result.getAcademicStandings().get("XUAT_SAC").getRate());
    }

    @Test
    void appliesAcademicStandingFilterBeforeCalculatingTotalAndRate() {
        HeadOfficeServiceImpl service = new HeadOfficeServiceImpl(repositoryWithRows(List.of(
                row("SV01", "27", "3"), row("SV02", "12", "3"))));

        var result = service.getSemesterAcademicStatistics("2026_2027_HK1", "all", "all", "YEU");

        assertEquals(1, result.getTotalStudents());
        assertEquals(1, result.getAcademicStandings().get("YEU").getCount());
        assertEquals(100.0, result.getAcademicStandings().get("YEU").getRate());
        assertEquals(0, result.getAcademicStandings().get("XUAT_SAC").getCount());
    }

    @Test
    void rejectsUnknownAcademicStanding() {
        HeadOfficeServiceImpl service = new HeadOfficeServiceImpl(repositoryWithRows(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> service.getSemesterAcademicStatistics("2026_2027_HK1", "all", "all", "KEM"));
    }

    private static Object[] row(String studentId, String weightedScore, String credits) {
        return new Object[]{studentId, new BigDecimal(weightedScore), new BigDecimal(credits)};
    }

    private static HeadOfficeRepository repositoryWithRows(List<Object[]> rows) {
        return (HeadOfficeRepository) Proxy.newProxyInstance(
                HeadOfficeRepository.class.getClassLoader(),
                new Class[]{HeadOfficeRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findSemesterById" -> Optional.of(new HocKy());
                    case "findSemesterAcademicAverages" -> rows;
                    case "toString" -> "HeadOfficeRepositoryStub";
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }
}
