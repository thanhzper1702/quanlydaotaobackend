package com.academic;

import com.university.core.config.EnvironmentLoader;
import com.university.core.model.*;
import com.university.core.model.enums.*;
import com.university.core.util.JwtUtil;
import com.university.core.util.PasswordUtil;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.PostgreSQLDialect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MetamodelTest {

    @Test
    public void testPostgreSQLMetadataBuilding() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", PostgreSQLDialect.class.getName())
                .applySetting("hibernate.temp.use_jdbc_metadata_defaults", "false")
                .build();

        try {
            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(BaseEntity.class);
            sources.addAnnotatedClass(NguoiDung.class);
            sources.addAnnotatedClass(SinhVien.class);
            sources.addAnnotatedClass(SinhVienUuTien.class);
            sources.addAnnotatedClass(NhanVien.class);
            sources.addAnnotatedClass(TroLyDaoTao.class);
            sources.addAnnotatedClass(TruongPhongDaoTao.class);
            sources.addAnnotatedClass(GiangVien.class);
            sources.addAnnotatedClass(HocKy.class);
            sources.addAnnotatedClass(HocPhan.class);
            sources.addAnnotatedClass(LopHocPhan.class);
            sources.addAnnotatedClass(HoSoHocPhan.class);
            sources.addAnnotatedClass(YeuCauMoLop.class);
            sources.addAnnotatedClass(LichHoc.class);
            sources.addAnnotatedClass(DiemDanh.class);
            sources.addAnnotatedClass(ThongBao.class);
            sources.addAnnotatedClass(KetQuaConverter.class);

            Metadata metadata = sources.buildMetadata();
            assertNotNull(metadata, "Metadata should not be null");
            assertTrue(metadata.getEntityBindings().size() >= 14, "Expected at least 14 entity bindings");

            System.out.println("PostgreSQL Hibernate metadata verified successfully! Entity bindings: " + metadata.getEntityBindings().size());
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    public void testJwtUtil() {
        String token = JwtUtil.generateAccessToken("079203001234", "SV2021001", "SINHVIEN", "test@university.edu.vn");
        assertNotNull(token);
        assertTrue(JwtUtil.validateToken(token));
        assertEquals("079203001234", JwtUtil.extractCccd(token));
        assertEquals("SV2021001", JwtUtil.extractUserCode(token));
        assertEquals("SINHVIEN", JwtUtil.extractRole(token));
    }

    @Test
    public void testPasswordUtil() {
        String plain = "SecretPassword123!";
        String hashed = PasswordUtil.hashPassword(plain);
        assertNotNull(hashed);
        assertTrue(PasswordUtil.checkPassword(plain, hashed));
        assertFalse(PasswordUtil.checkPassword("WrongPassword", hashed));
    }

    @Test
    public void testEnvironmentLoaderDefault() {
        String dbDriver = EnvironmentLoader.get("DB_DRIVER", "org.postgresql.Driver");
        assertNotNull(dbDriver);
        assertEquals("org.postgresql.Driver", dbDriver);
    }
}
