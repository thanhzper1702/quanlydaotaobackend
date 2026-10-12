-- Dữ liệu kiểm thử bộ lọc khoa sinh viên (idempotent).
-- Chỉ thêm 2 sinh viên cho mỗi khoa chuẩn; không sửa hoặc xóa dữ liệu hiện có.

WITH seed_students (cccd, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, dia_chi) AS (
    VALUES
        ('990000000001', 'Nguyễn Minh Anh', DATE '2004-01-15', 'Nữ', 'svcntt01@example.edu.vn', '0900000001', 'Hà Nội'),
        ('990000000002', 'Trần Quốc Huy', DATE '2004-03-21', 'Nam', 'svcntt02@example.edu.vn', '0900000002', 'Hà Nội'),
        ('990000000003', 'Lê Thu Hà', DATE '2004-05-09', 'Nữ', 'svkt01@example.edu.vn', '0900000003', 'Hà Nội'),
        ('990000000004', 'Phạm Đức Long', DATE '2004-07-18', 'Nam', 'svkt02@example.edu.vn', '0900000004', 'Hà Nội'),
        ('990000000005', 'Vũ Khánh Linh', DATE '2004-09-12', 'Nữ', 'svddt01@example.edu.vn', '0900000005', 'Hà Nội'),
        ('990000000006', 'Đỗ Minh Quân', DATE '2004-11-25', 'Nam', 'svddt02@example.edu.vn', '0900000006', 'Hà Nội'),
        ('990000000007', 'Hoàng Ngọc Mai', DATE '2004-02-28', 'Nữ', 'svckot01@example.edu.vn', '0900000007', 'Hà Nội'),
        ('990000000008', 'Bùi Gia Bảo', DATE '2004-06-14', 'Nam', 'svckot02@example.edu.vn', '0900000008', 'Hà Nội')
)
INSERT INTO nguoidung (cccd, hoten, ngaysinh, gioitinh, email, sodienthoai, diachi, matkhauhashed, deleted)
SELECT cccd, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, dia_chi,
       '$2a$10$.Zr2b6rdoEN/tSXMVO9XUu2f7OuxSkglAsNqkrRLNuRqpe8A4AKDq', FALSE
FROM seed_students
ON CONFLICT (cccd) DO NOTHING;

WITH seed_students (masv, cccd, khoa, chuyen_nganh) AS (
    VALUES
        ('SVFCT001', '990000000001', 'Công nghệ thông tin', 'Kỹ thuật phần mềm'),
        ('SVFCT002', '990000000002', 'Công nghệ thông tin', 'Hệ thống thông tin'),
        ('SVFKE001', '990000000003', 'Kinh tế', 'Quản trị kinh doanh'),
        ('SVFKE002', '990000000004', 'Kinh tế', 'Tài chính - Ngân hàng'),
        ('SVFDT001', '990000000005', 'Điện - Điện tử', 'Kỹ thuật điện tử'),
        ('SVFDT002', '990000000006', 'Điện - Điện tử', 'Kỹ thuật viễn thông'),
        ('SVFCO001', '990000000007', 'Cơ khí Ô tô', 'Công nghệ kỹ thuật ô tô'),
        ('SVFCO002', '990000000008', 'Cơ khí Ô tô', 'Kỹ thuật cơ khí')
)
INSERT INTO sinhvien (masv, cccd, nienkhoa, khoa, chuyennganh, deleted)
SELECT masv, cccd, '2022-2026', khoa, chuyen_nganh, FALSE
FROM seed_students
WHERE EXISTS (SELECT 1 FROM nguoidung u WHERE u.cccd = seed_students.cccd)
ON CONFLICT (masv) DO NOTHING;
