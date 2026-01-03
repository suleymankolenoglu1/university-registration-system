-- ============================================
-- TEST VERİLERİ
-- ============================================

-- Öğrenciler (şifre: password123)
INSERT INTO students (student_number, first_name, last_name, email, password, department, semester) VALUES
('2021001001', 'Ahmet', 'Yılmaz', 'ahmet.yilmaz@uni.edu.tr', '$2a$10$N9qo8uLOickgx2ZMRZoHK.uS8/nP7YScCa.F8w.KmEuRxqxCZqjte', 'Bilgisayar Mühendisliği', 4),
('2021001002', 'Ayşe', 'Demir', 'ayse.demir@uni.edu.tr', '$2a$10$N9qo8uLOickgx2ZMRZoHK.uS8/nP7YScCa.F8w.KmEuRxqxCZqjte', 'Bilgisayar Mühendisliği', 4),
('2022001003', 'Mehmet', 'Kaya', 'mehmet.kaya@uni.edu.tr', '$2a$10$N9qo8uLOickgx2ZMRZoHK.uS8/nP7YScCa.F8w.KmEuRxqxCZqjte', 'Elektrik Elektronik', 2);

-- Öğretim Elemanları
INSERT INTO instructors (instructor_number, first_name, last_name, email, password, title, department) VALUES
('INS001', 'Kemal', 'Aydın', 'kemal.aydin@uni.edu.tr', '$2a$10$N9qo8uLOickgx2ZMRZoHK.uS8/nP7YScCa.F8w.KmEuRxqxCZqjte', 'Prof. Dr.', 'Bilgisayar Mühendisliği'),
('INS002', 'Zeynep', 'Arslan', 'zeynep.arslan@uni.edu.tr', '$2a$10$N9qo8uLOickgx2ZMRZoHK.uS8/nP7YScCa.F8w.KmEuRxqxCZqjte', 'Doç. Dr.', 'Bilgisayar Mühendisliği'),
('INS003', 'Mustafa', 'Şahin', 'mustafa.sahin@uni.edu.tr', '$2a$10$N9qo8uLOickgx2ZMRZoHK.uS8/nP7YScCa.F8w.KmEuRxqxCZqjte', 'Dr. Öğr. Üyesi', 'Matematik');

-- Derslikler
INSERT INTO rooms (room_code, building, capacity, room_type) VALUES
('A101', 'A Blok', 50, 'SINIF'),
('A102', 'A Blok', 50, 'SINIF'),
('B101', 'B Blok', 30, 'LAB'),
('C201', 'C Blok', 150, 'AMFI');

-- Dersler
INSERT INTO courses (course_code, course_name, credits, description, department) VALUES
('MAT101', 'Matematik I', 4, 'Temel matematik', 'Matematik'),
('CS101', 'Programlamaya Giriş', 4, 'Python temelleri', 'Bilgisayar Mühendisliği'),
('CS102', 'Nesne Yönelimli Programlama', 4, 'Java ile OOP', 'Bilgisayar Mühendisliği'),
('CS201', 'Veri Yapıları', 4, 'Algoritmalar', 'Bilgisayar Mühendisliği');

-- Ön koşul ilişkileri
UPDATE courses SET prerequisite_course_id = (SELECT course_id FROM courses WHERE course_code = 'CS101') WHERE course_code = 'CS102';
UPDATE courses SET prerequisite_course_id = (SELECT course_id FROM courses WHERE course_code = 'CS102') WHERE course_code = 'CS201';

-- Şubeler
INSERT INTO sections (course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity) VALUES
(1, 3, 4, '2025-BAHAR', 1, 'PAZARTESI', '09:00', '12:00', 100),
(2, 1, 3, '2025-BAHAR', 1, 'PAZARTESI', '13:00', '16:00', 30),
(2, 2, 3, '2025-BAHAR', 2, 'CARSAMBA', '09:00', '12:00', 30),
(3, 1, 3, '2025-BAHAR', 1, 'PERSEMBE', '13:00', '16:00', 30);