-- ====================================================================
-- HỆ THỐNG QUẢN LÝ ĐÀO TẠO & ĐĂNG KÝ HỌC PHẦN (POSTGRESQL SCRIPT)
-- HỖ TRỢ XÓA MỀM (SOFT DELETE) TRÊN TOÀN BỘ CÁC BẢNG
-- ====================================================================

-- 1. XÓA BẢNG VÀ TRIGGER CŨ NẾU ĐÃ TỒN TẠI
DROP TRIGGER IF EXISTS trg_cap_nhat_vang_va_dinh_chi ON DiemDanh;
DROP FUNCTION IF EXISTS fn_cap_nhat_vang_va_dinh_chi();
DROP TRIGGER IF EXISTS trg_tinh_diem_tong_ket ON HoSoHocPhan;
DROP FUNCTION IF EXISTS fn_tinh_diem_tong_ket();

DROP TABLE IF EXISTS DiemDanh CASCADE;
DROP TABLE IF EXISTS LichHoc CASCADE;
DROP TABLE IF EXISTS YeuCauMoLop CASCADE;
DROP TABLE IF EXISTS HoSoHocPhan CASCADE;
DROP TABLE IF EXISTS LopHocPhan CASCADE;
DROP TABLE IF EXISTS HocPhan CASCADE;
DROP TABLE IF EXISTS HocKy CASCADE;
DROP TABLE IF EXISTS GiangVien CASCADE;
DROP TABLE IF EXISTS TruongPhongDaoTao CASCADE;
DROP TABLE IF EXISTS TroLyDaoTao CASCADE;
DROP TABLE IF EXISTS NhanVien CASCADE;
DROP TABLE IF EXISTS SinhVienUuTien CASCADE;
DROP TABLE IF EXISTS SinhVien CASCADE;
DROP TABLE IF EXISTS NguoiDung CASCADE;

-- ====================================================================
-- 2. PHÂN HỆ NGƯỜI DÙNG & KẾ THỪA
-- ====================================================================

-- Bảng cha Người Dùng
CREATE TABLE NguoiDung (
    CCCD VARCHAR(20) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    anhThe VARCHAR(255),
    ngaySinh DATE NOT NULL,
    gioiTinh VARCHAR(10),
    email VARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(15),
    diaChi VARCHAR(255),
    matKhauHashed VARCHAR(255) NOT NULL,
    refreshToken TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Sinh Viên kế thừa Người Dùng
CREATE TABLE SinhVien (
    maSV VARCHAR(20) PRIMARY KEY,
    CCCD VARCHAR(20) UNIQUE NOT NULL,
    nienKhoa VARCHAR(20) NOT NULL,
    khoa VARCHAR(100) NOT NULL,
    chuyenNganh VARCHAR(100) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_sv_nguoidung FOREIGN KEY (CCCD) REFERENCES NguoiDung(CCCD) ON DELETE CASCADE
);

-- Sinh Viên Ưu Tiên kế thừa Sinh Viên
CREATE TABLE SinhVienUuTien (
    maSV VARCHAR(20) PRIMARY KEY,
    dienUuTien VARCHAR(50) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT chk_dien_uu_tien CHECK (dienUuTien IN ('HONGHEO', 'CONBLS', 'KHUYETTAT')),
    CONSTRAINT fk_svut_sv FOREIGN KEY (maSV) REFERENCES SinhVien(maSV) ON DELETE CASCADE
);

-- Nhân Viên kế thừa Người Dùng
CREATE TABLE NhanVien (
    maNV VARCHAR(20) PRIMARY KEY,
    CCCD VARCHAR(20) UNIQUE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_nv_nguoidung FOREIGN KEY (CCCD) REFERENCES NguoiDung(CCCD) ON DELETE CASCADE
);

-- Trợ Lý Đào Tạo kế thừa Nhân Viên
CREATE TABLE TroLyDaoTao (
    maNV VARCHAR(20) PRIMARY KEY,
    ngayBatDau DATE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_tldt_nhanvien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV) ON DELETE CASCADE
);

-- Trưởng Phòng Đào Tạo kế thừa Nhân Viên
CREATE TABLE TruongPhongDaoTao (
    maNV VARCHAR(20) PRIMARY KEY,
    nhiemKy VARCHAR(50) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_tpdt_nhanvien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV) ON DELETE CASCADE
);

-- Giảng Viên kế thừa Nhân Viên
CREATE TABLE GiangVien (
    maNV VARCHAR(20) PRIMARY KEY,
    hocVi VARCHAR(50),
    khoa VARCHAR(100) NOT NULL,
    ngayBatDau DATE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_gv_nhanvien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV) ON DELETE CASCADE
);

-- ====================================================================
-- 3. CẤU TRÚC HỌC VỤ & LỚP HỌC PHẦN
-- ====================================================================

-- Học Kỳ
CREATE TABLE HocKy (
    maHocKy VARCHAR(20) PRIMARY KEY,
    tenHocKy VARCHAR(20) NOT NULL,
    thoiGianBD DATE NOT NULL,
    thoiGianKT DATE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT chk_ten_hocky CHECK (tenHocKy IN ('HOCKY1', 'HOCKY2', 'HOCKYHE')),
    CONSTRAINT chk_thoi_gian_hocky CHECK (thoiGianKT > thoiGianBD)
);

-- Học Phần (Môn học gốc)
CREATE TABLE HocPhan (
    maHP VARCHAR(20) PRIMARY KEY,
    tenHP VARCHAR(150) NOT NULL,
    soTinChi INT NOT NULL CHECK (soTinChi > 0),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Lớp Học Phần
CREATE TABLE LopHocPhan (
    maLHP VARCHAR(20) PRIMARY KEY,
    tenLHP VARCHAR(150) NOT NULL,
    maHP VARCHAR(20) NOT NULL,
    maHocKy VARCHAR(20) NOT NULL,
    maNV_TroLy VARCHAR(20) NOT NULL,
    maGV VARCHAR(20),
    siSoToiDa INT NOT NULL CHECK (siSoToiDa > 0),
    siSoHienTai INT NOT NULL DEFAULT 0,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'MODANGKY',
    tietBatDau INT NOT NULL,
    soTiet INT NOT NULL,
    thu INT NOT NULL,
    thoiGianBDDK TIMESTAMP WITH TIME ZONE NULL,
    thoiGianKTDK TIMESTAMP WITH TIME ZONE NULL,
    khoaDiem BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT chk_thoi_gian_dang_ky 
    CHECK (
        (thoiGianBDDK IS NULL AND thoiGianKTDK IS NULL) 
        OR 
        (thoiGianBDDK IS NOT NULL AND thoiGianKTDK IS NOT NULL AND thoiGianKTDK > thoiGianBDDK)
    ),
    CONSTRAINT fk_lhp_hocphan FOREIGN KEY (maHP) REFERENCES HocPhan(maHP) ON DELETE RESTRICT,
    CONSTRAINT fk_lhp_hocky FOREIGN KEY (maHocKy) REFERENCES HocKy(maHocKy) ON DELETE RESTRICT,
    CONSTRAINT fk_lhp_troly FOREIGN KEY (maNV_TroLy) REFERENCES TroLyDaoTao(maNV) ON DELETE RESTRICT,
    CONSTRAINT fk_lhp_giangvien FOREIGN KEY (maGV) REFERENCES GiangVien(maNV) ON DELETE SET NULL,
    CONSTRAINT chk_lhp_trangthai CHECK (trangThai IN ('MODANGKY', 'DANGHOC', 'KETTHUC')),
    CONSTRAINT chk_lhp_siso CHECK (siSoHienTai >= 0 AND siSoHienTai <= siSoToiDa),
    CONSTRAINT chk_lhp_tiet CHECK (thu BETWEEN 2 AND 8 AND tietBatDau BETWEEN 1 AND 15 AND soTiet > 0 AND (tietBatDau + soTiet - 1) <= 15)
);

-- Bảng Hồ Sơ Học Phần
CREATE TABLE HoSoHocPhan (
    maHoSo SERIAL PRIMARY KEY,
    maSV VARCHAR(20) NOT NULL,
    maLHP VARCHAR(20) NOT NULL,
    ngayDangKy TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diemQuaTrinh NUMERIC(4,2),
    diemCuoiKy NUMERIC(4,2),
    diemTongKet NUMERIC(4,2),
    ketQua VARCHAR(5),
    soBuoiVang INT NOT NULL DEFAULT 0,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'DADANGKY',
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_hshp_sinhvien FOREIGN KEY (maSV) REFERENCES SinhVien(maSV) ON DELETE RESTRICT,
    CONSTRAINT fk_hshp_lophocphan FOREIGN KEY (maLHP) REFERENCES LopHocPhan(maLHP) ON DELETE RESTRICT,
    CONSTRAINT chk_hshp_trangthai CHECK (trangThai IN ('DADANGKY', 'DANGHOC', 'HOCXONG', 'DINHCHITHI', 'DAHUY')),
    CONSTRAINT chk_hshp_ketqua CHECK (ketQua IS NULL OR ketQua IN ('A+', 'A', 'B+', 'B', 'C+', 'C', 'D+', 'D', 'F')),
    CONSTRAINT chk_hshp_diem CHECK (
        (diemQuaTrinh IS NULL OR (diemQuaTrinh BETWEEN 0 AND 10)) AND
        (diemCuoiKy IS NULL OR (diemCuoiKy BETWEEN 0 AND 10)) AND
        (diemTongKet IS NULL OR (diemTongKet BETWEEN 0 AND 10))
    )
);

-- Yêu Cầu Mở Lớp
CREATE TABLE YeuCauMoLop (
    maYeuCau VARCHAR(30) PRIMARY KEY,
    maSV VARCHAR(20) NOT NULL,
    maHocPhan VARCHAR(20) NOT NULL,
    lyDo TEXT NOT NULL,
    lichHocMongMuon VARCHAR(100),
    trangThai VARCHAR(20) NOT NULL DEFAULT 'DAGUI',
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_ycml_sinhvien FOREIGN KEY (maSV) REFERENCES SinhVien(maSV) ON DELETE CASCADE,
    CONSTRAINT fk_ycml_hocphan FOREIGN KEY (maHocPhan) REFERENCES HocPhan(maHP) ON DELETE RESTRICT,
    CONSTRAINT chk_ycml_trangthai CHECK (trangThai IN ('DAGUI', 'DATIEPNHAN', 'CHODUYET', 'DATUCHOI'))
);

-- Lịch Học chi tiết từng ngày của Lớp Học Phần
CREATE TABLE LichHoc (
    maLichHoc VARCHAR(30) PRIMARY KEY,
    maLHP VARCHAR(20) NOT NULL,
    ngayHoc DATE NOT NULL,
    phongHoc VARCHAR(50) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_lichhoc_lhp FOREIGN KEY (maLHP) REFERENCES LopHocPhan(maLHP) ON DELETE CASCADE
);

-- Điểm Danh
CREATE TABLE DiemDanh (
    maDiemDanh SERIAL PRIMARY KEY,
    maSV VARCHAR(20) NOT NULL,
    maLichHoc VARCHAR(30) NOT NULL,
    coMat BOOLEAN NOT NULL DEFAULT TRUE,
    ghiChu VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_dd_sinhvien FOREIGN KEY (maSV) REFERENCES SinhVien(maSV) ON DELETE CASCADE,
    CONSTRAINT fk_dd_lichhoc FOREIGN KEY (maLichHoc) REFERENCES LichHoc(maLichHoc) ON DELETE CASCADE
);

-- Thông Báo
CREATE TABLE ThongBao (
    maThongBao SERIAL PRIMARY KEY,
    tieuDe VARCHAR(255) NOT NULL,
    noiDung TEXT NOT NULL,
    phamVi VARCHAR(50) NOT NULL DEFAULT 'ALL',
    ngayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- ====================================================================
-- 4. TỐI ƯU RÀNG BUỘC DUY NHẤT & CHỈ MỤC CHO XÓA MỀM (PARTIAL UNIQUE INDEXES)
-- ====================================================================
-- Với cơ chế xóa mềm, chỉ kiểm tra tính duy nhất trên các bản ghi CHƯA BỊ XÓA (deleted = FALSE)

CREATE UNIQUE INDEX unq_active_nguoidung_email ON NguoiDung(email) WHERE deleted = FALSE;
CREATE UNIQUE INDEX unq_active_hshp_sv_lhp ON HoSoHocPhan(maSV, maLHP) WHERE deleted = FALSE;
CREATE UNIQUE INDEX unq_active_diemdanh_sv_lichhoc ON DiemDanh(maSV, maLichHoc) WHERE deleted = FALSE;

-- Indexes hỗ trợ lọc dữ liệu hoạt động
CREATE INDEX idx_hshp_sv_status ON HoSoHocPhan(maSV, trangThai) WHERE deleted = FALSE;
CREATE INDEX idx_lhp_thu_tiet ON LopHocPhan(thu, tietBatDau, soTiet) WHERE deleted = FALSE;
CREATE INDEX idx_diemdanh_sv ON DiemDanh(maSV) WHERE deleted = FALSE;

-- ====================================================================
-- 5. TRIGGER TỰ ĐỘNG HÓA NGHIỆP VỤ
-- ====================================================================

-- Trigger 1: Tự động tính Điểm Tổng Kết và Xếp Loại Kết Quả
CREATE OR REPLACE FUNCTION fn_tinh_diem_tong_ket()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.deleted = FALSE AND NEW.diemQuaTrinh IS NOT NULL AND NEW.diemCuoiKy IS NOT NULL THEN
        NEW.diemTongKet := ROUND((NEW.diemQuaTrinh * 0.3 + NEW.diemCuoiKy * 0.7), 1);
        
        IF NEW.diemTongKet >= 9.0 THEN
            NEW.ketQua := 'A+';
        ELSIF NEW.diemTongKet >= 8.5 THEN
            NEW.ketQua := 'A';
        ELSIF NEW.diemTongKet >= 8.0 THEN
            NEW.ketQua := 'B+';
        ELSIF NEW.diemTongKet >= 7.0 THEN
            NEW.ketQua := 'B';
        ELSIF NEW.diemTongKet >= 6.5 THEN
            NEW.ketQua := 'C+';
        ELSIF NEW.diemTongKet >= 5.5 THEN
            NEW.ketQua := 'C';
        ELSIF NEW.diemTongKet >= 5.0 THEN
            NEW.ketQua := 'D+';
        ELSIF NEW.diemTongKet >= 4.0 THEN
            NEW.ketQua := 'D';
        ELSE
            NEW.ketQua := 'F';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_tinh_diem_tong_ket
BEFORE INSERT OR UPDATE ON HoSoHocPhan
FOR EACH ROW
EXECUTE FUNCTION fn_tinh_diem_tong_ket();

-- Trigger 2: Đếm buổi vắng và tự động Đình Chỉ Thi khi điểm danh
CREATE OR REPLACE FUNCTION fn_cap_nhat_vang_va_dinh_chi()
RETURNS TRIGGER AS $$
DECLARE
    v_maLHP VARCHAR(20);
    v_soBuoiVangHienTai INT;
BEGIN
    -- Bỏ qua nếu bản ghi điểm danh đã bị xóa mềm
    IF NEW.deleted = TRUE THEN
        RETURN NEW;
    END IF;

    SELECT maLHP INTO v_maLHP FROM LichHoc WHERE maLichHoc = NEW.maLichHoc AND deleted = FALSE;

    -- Trường hợp 1: Điểm danh mới và sinh viên vắng mặt
    IF TG_OP = 'INSERT' AND NEW.coMat = FALSE THEN
        UPDATE HoSoHocPhan
        SET soBuoiVang = soBuoiVang + 1
        WHERE maSV = NEW.maSV AND maLHP = v_maLHP AND deleted = FALSE
        RETURNING soBuoiVang INTO v_soBuoiVangHienTai;

        IF v_soBuoiVangHienTai >= 3 THEN
            UPDATE HoSoHocPhan
            SET trangThai = 'DINHCHITHI', ketQua = 'F'
            WHERE maSV = NEW.maSV AND maLHP = v_maLHP AND deleted = FALSE;
        END IF;

    -- Trường hợp 2: Cập nhật bản ghi điểm danh
    ELSIF TG_OP = 'UPDATE' THEN
        -- Đổi từ Có mặt -> Vắng mặt
        IF OLD.coMat = TRUE AND NEW.coMat = FALSE THEN
            UPDATE HoSoHocPhan
            SET soBuoiVang = soBuoiVang + 1
            WHERE maSV = NEW.maSV AND maLHP = v_maLHP AND deleted = FALSE
            RETURNING soBuoiVang INTO v_soBuoiVangHienTai;

            IF v_soBuoiVangHienTai >= 3 THEN
                UPDATE HoSoHocPhan
                SET trangThai = 'DINHCHITHI', ketQua = 'F'
                WHERE maSV = NEW.maSV AND maLHP = v_maLHP AND deleted = FALSE;
            END IF;

        -- Đổi từ Vắng mặt -> Có mặt (chữa điểm danh nhầm)
        ELSIF OLD.coMat = FALSE AND NEW.coMat = TRUE THEN
            UPDATE HoSoHocPhan
            SET soBuoiVang = GREATEST(soBuoiVang - 1, 0)
            WHERE maSV = NEW.maSV AND maLHP = v_maLHP AND deleted = FALSE
            RETURNING soBuoiVang INTO v_soBuoiVangHienTai;

            IF v_soBuoiVangHienTai < 3 THEN
                UPDATE HoSoHocPhan
                SET trangThai = 'DANGHOC', ketQua = NULL
                WHERE maSV = NEW.maSV AND maLHP = v_maLHP AND trangThai = 'DINHCHITHI' AND deleted = FALSE;
            END IF;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cap_nhat_vang_va_dinh_chi
AFTER INSERT OR UPDATE ON DiemDanh
FOR EACH ROW
EXECUTE FUNCTION fn_cap_nhat_vang_va_dinh_chi();