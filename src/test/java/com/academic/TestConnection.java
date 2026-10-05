package com.academic;

import com.university.core.config.JpaUtil;
import jakarta.persistence.EntityManager;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Đang kiểm tra kết nối đến Neon PostgreSQL...");

        try {
            EntityManager em = JpaUtil.getEntityManager();

            // Chạy thử câu query kiểm tra phiên bản PostgreSQL
            Object version = em.createNativeQuery("SELECT version()").getSingleResult();

            System.out.println(" Kết nối thành công!");
            System.out.println("Database Version: " + version);

            em.close();
            JpaUtil.shutdown();
        } catch (Exception e) {
            System.err.println(" Kết nối thất bại. Lỗi:");
            e.printStackTrace();
        }
    }
}