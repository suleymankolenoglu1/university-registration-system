-- Yazilim Muhendisligi Bolumu - Ornek Veriler
-- Kocaeli Universitesi Teknoloji Fakultesi

-- =============================================
-- TRIGGERLARI GECICI OLARAK DEVRE DISI BIRAK
-- =============================================
ALTER TABLE sections DISABLE TRIGGER ALL;
ALTER TABLE enrollments DISABLE TRIGGER ALL;

-- Onceki verileri temizle (bagimliliklardan bagimsiz sirayla)
TRUNCATE TABLE waiting_list CASCADE;
TRUNCATE TABLE enrollments CASCADE;
TRUNCATE TABLE sections CASCADE;
TRUNCATE TABLE courses CASCADE;
TRUNCATE TABLE instructors CASCADE;
TRUNCATE TABLE students CASCADE;
TRUNCATE TABLE rooms CASCADE;

-- =============================================
-- 1. DERSLIKLER
-- =============================================
INSERT INTO rooms (room_id, room_code, building, capacity, room_type) VALUES
(1, 'D1', 'D Blok', 60, 'CLASSROOM'),
(2, 'D2', 'D Blok', 60, 'CLASSROOM'),
(3, 'D3', 'D Blok', 40, 'CLASSROOM'),
(4, 'D4', 'D Blok', 40, 'CLASSROOM'),
(5, 'LAB1', 'D Blok', 30, 'LABORATORY'),
(6, 'LAB2', 'D Blok', 30, 'LABORATORY'),
(7, 'LAB3', 'D Blok', 30, 'LABORATORY'),
(8, 'A101', 'A Blok', 100, 'AMPHITHEATER'),
(9, 'A102', 'A Blok', 80, 'AMPHITHEATER'),
(10, 'B201', 'B Blok', 50, 'CLASSROOM');

-- =============================================
-- 2. OGRETIM UYELERI
-- =============================================
INSERT INTO instructors (instructor_id, instructor_number, first_name, last_name, email, password, title, department) VALUES
(1, 'YZM001', 'Mehmet Zeki', 'Konyar', 'mzkonyar@kocaeli.edu.tr', 'hoca123', 'Doc. Dr.', 'Yazilim Muhendisligi'),
(2, 'YZM002', 'Hakan', 'Gunduz', 'hgunduz@kocaeli.edu.tr', 'hoca123', 'Doc. Dr.', 'Yazilim Muhendisligi'),
(3, 'YZM003', 'Kerem', 'Kucuk', 'kkucuk@kocaeli.edu.tr', 'hoca123', 'Prof. Dr.', 'Yazilim Muhendisligi'),
(4, 'YZM004', 'Levent', 'Bayindir', 'lbayindir@kocaeli.edu.tr', 'hoca123', 'Dr. Ogr. Uyesi', 'Yazilim Muhendisligi'),
(5, 'YZM005', 'Kaplan', 'Kaplan', 'kkaplan@kocaeli.edu.tr', 'hoca123', 'Doc. Dr.', 'Yazilim Muhendisligi'),
(6, 'YZM006', 'Irfan', 'Kosesoy', 'ikosesoy@kocaeli.edu.tr', 'hoca123', 'Dr. Ogr. Uyesi', 'Yazilim Muhendisligi');

-- Sekans guncelle
SELECT setval('instructors_instructor_id_seq', 6);

-- =============================================
-- 3. DERSLER (Yazilim Muhendisligi)
-- =============================================
-- 1. Sinif Dersleri (YZM1xx)
INSERT INTO courses (course_id, course_code, course_name, credits, description, prerequisite_course_id, department) VALUES
(1, 'YZM101', 'Bilgisayar Muhendisligine Giris', 4, 'Temel bilgisayar bilimleri ve muhendislik kavramlari', NULL, 'Yazilim Muhendisligi'),
(2, 'YZM103', 'Programlama I', 6, 'C programlama dili temelleri', NULL, 'Yazilim Muhendisligi'),
(3, 'YZM105', 'Programlama Laboratuvari I', 2, 'C programlama uygulamalari', NULL, 'Yazilim Muhendisligi'),
(4, 'YZM107', 'Ayrik Matematik', 5, 'Kumeler, baginti, fonksiyon, graflar', NULL, 'Yazilim Muhendisligi');

-- 2. Sinif Dersleri (YZM2xx) - On kosullar kaldirildi (ornek veri icin)
INSERT INTO courses (course_id, course_code, course_name, credits, description, prerequisite_course_id, department) VALUES
(5, 'YZM201', 'Veri Yapilari', 5, 'Temel veri yapilari ve algoritmalar', NULL, 'Yazilim Muhendisligi'),
(6, 'YZM203', 'Programlama II', 6, 'Nesne yonelimli programlama - Java', NULL, 'Yazilim Muhendisligi'),
(7, 'YZM205', 'Programlama Laboratuvari II', 2, 'Java programlama uygulamalari', NULL, 'Yazilim Muhendisligi'),
(8, 'YZM207', 'Bilgisayar Organizasyonu', 4, 'Bilgisayar mimarisi temelleri', NULL, 'Yazilim Muhendisligi'),
(9, 'YZM209', 'Olasilik ve Istatistik', 4, 'Muhendisler icin istatistik', NULL, 'Yazilim Muhendisligi'),
(10, 'YZM211', 'Veritabani Yonetim Sistemleri', 5, 'SQL, veritabani tasarimi', NULL, 'Yazilim Muhendisligi');

-- 3. Sinif Dersleri (YZM3xx) - On kosullar kaldirildi (ornek veri icin)
INSERT INTO courses (course_id, course_code, course_name, credits, description, prerequisite_course_id, department) VALUES
(11, 'YZM301', 'Yazilim Muhendisligi', 5, 'Yazilim gelistirme surecleri ve metodolojileri', NULL, 'Yazilim Muhendisligi'),
(12, 'YZM303', 'Programlama III', 6, 'Ileri programlama teknikleri', NULL, 'Yazilim Muhendisligi'),
(13, 'YZM305', 'Programlama Laboratuvari III', 2, 'Ileri programlama uygulamalari', NULL, 'Yazilim Muhendisligi'),
(14, 'YZM307', 'Isletim Sistemleri', 5, 'Isletim sistemleri temelleri ve tasarimi', NULL, 'Yazilim Muhendisligi'),
(15, 'YZM309', 'Bilgisayar Aglari', 4, 'Ag protokolleri ve mimarisi', NULL, 'Yazilim Muhendisligi'),
(16, 'YZM313', 'Web Programlama', 4, 'HTML, CSS, JavaScript, backend gelistirme', NULL, 'Yazilim Muhendisligi');

-- Sekans guncelle
SELECT setval('courses_course_id_seq', 16);

-- =============================================
-- 4. OGRENCILER
-- =============================================
-- 1. Sinif (2024 girisliler)
INSERT INTO students (student_id, student_number, first_name, last_name, email, password, department, semester) VALUES
(1, '240201001', 'Ahmet', 'Yilmaz', 'ahmet.yilmaz@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(2, '240201002', 'Mehmet', 'Demir', 'mehmet.demir@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(3, '240201003', 'Ayse', 'Kaya', 'ayse.kaya@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(4, '240201004', 'Fatma', 'Sahin', 'fatma.sahin@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(5, '240201005', 'Mustafa', 'Celik', 'mustafa.celik@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(6, '240201006', 'Zeynep', 'Ozturk', 'zeynep.ozturk@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(7, '240201007', 'Ali', 'Acar', 'ali.acar@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(8, '240201008', 'Elif', 'Arslan', 'elif.arslan@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(9, '240201009', 'Hasan', 'Korkmaz', 'hasan.korkmaz@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1),
(10, '240201010', 'Merve', 'Yildiz', 'merve.yildiz@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 1);

-- 2. Sinif (2023 girisliler)
INSERT INTO students (student_id, student_number, first_name, last_name, email, password, department, semester) VALUES
(11, '230201001', 'Emre', 'Aktas', 'emre.aktas@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(12, '230201002', 'Selin', 'Yildirim', 'selin.yildirim@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(13, '230201003', 'Burak', 'Erdogan', 'burak.erdogan@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(14, '230201004', 'Deniz', 'Koc', 'deniz.koc@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(15, '230201005', 'Ceren', 'Aydin', 'ceren.aydin@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(16, '230201006', 'Onur', 'Ozkan', 'onur.ozkan@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(17, '230201007', 'Seda', 'Tas', 'seda.tas@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(18, '230201008', 'Kaan', 'Duman', 'kaan.duman@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(19, '230201009', 'Ece', 'Kilic', 'ece.kilic@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3),
(20, '230201010', 'Murat', 'Sen', 'murat.sen@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 3);

-- 3. Sinif (2022 girisliler)
INSERT INTO students (student_id, student_number, first_name, last_name, email, password, department, semester) VALUES
(21, '220201001', 'Oguz', 'Polat', 'oguz.polat@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(22, '220201002', 'Gamze', 'Kurt', 'gamze.kurt@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(23, '220201003', 'Ufuk', 'Aksoy', 'ufuk.aksoy@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(24, '220201004', 'Buse', 'Tekin', 'buse.tekin@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(25, '220201005', 'Taha', 'Kara', 'taha.kara@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(26, '220201006', 'Sibel', 'Dogan', 'sibel.dogan@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(27, '220201007', 'Kerem', 'Aslan', 'kerem.aslan@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(28, '220201008', 'Tugce', 'Ozdemir', 'tugce.ozdemir@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(29, '220201009', 'Ilker', 'Bozkurt', 'ilker.bozkurt@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5),
(30, '220201010', 'Pinar', 'Gunes', 'pinar.gunes@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 5);

-- 4. Sinif (2021 girisliler)
INSERT INTO students (student_id, student_number, first_name, last_name, email, password, department, semester) VALUES
(31, '210201001', 'Tolga', 'Cakir', 'tolga.cakir@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(32, '210201002', 'Ipek', 'Ucar', 'ipek.ucar@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(33, '210201003', 'Serkan', 'Bulut', 'serkan.bulut@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(34, '210201004', 'Didem', 'Uslu', 'didem.uslu@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(35, '210201005', 'Baran', 'Erdem', 'baran.erdem@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(36, '210201006', 'Hilal', 'Sezer', 'hilal.sezer@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(37, '210201007', 'Arda', 'Yalcin', 'arda.yalcin@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(38, '210201008', 'Naz', 'Akin', 'naz.akin@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(39, '210201009', 'Selim', 'Eren', 'selim.eren@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7),
(40, '210201010', 'Derya', 'Keskin', 'derya.keskin@kou.edu.tr', 'ogrenci123', 'Yazilim Muhendisligi', 7);

-- Sekans guncelle
SELECT setval('students_student_id_seq', 40);

-- =============================================
-- 5. DERS SUBELERI (2024-2025 Guz Donemi)
-- =============================================
-- Not: Programlama Lab I, II, III -> Irfan Kosesoy (instructor_id = 6)
-- Ayni sinif dersleri cakismayacak sekilde planlanmistir.

-- 1. SINIF DERSLERI (Pazartesi-Carsamba agirlikli)
-- YZM101 - Bilgisayar Muh. Giris (Mehmet Zeki Konyar)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (1, 1, 1, 1, '2024-2025-GUZ', 1, 'PAZARTESI', '09:00', '11:00', 30, 5);
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (2, 1, 1, 1, '2024-2025-GUZ', 2, 'PAZARTESI', '11:00', '13:00', 30, 5);

-- YZM103 - Programlama I (Hakan Gunduz)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (3, 2, 2, 2, '2024-2025-GUZ', 1, 'SALI', '09:00', '12:00', 30, 5);
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (4, 2, 2, 2, '2024-2025-GUZ', 2, 'SALI', '13:00', '16:00', 30, 5);

-- YZM105 - Programlama Laboratuvari I (Irfan Kosesoy)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (5, 3, 6, 5, '2024-2025-GUZ', 1, 'CARSAMBA', '09:00', '11:00', 30, 5);
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (6, 3, 6, 5, '2024-2025-GUZ', 2, 'CARSAMBA', '11:00', '13:00', 30, 5);

-- YZM107 - Ayrik Matematik (Kerem Kucuk)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (7, 4, 3, 1, '2024-2025-GUZ', 1, 'PERSEMBE', '09:00', '12:00', 30, 5);
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (8, 4, 3, 1, '2024-2025-GUZ', 2, 'PERSEMBE', '13:00', '16:00', 30, 5);

-- 2. SINIF DERSLERI (Sali-Persembe agirlikli)
-- YZM201 - Veri Yapilari (Kerem Kucuk)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (9, 5, 3, 3, '2024-2025-GUZ', 1, 'PAZARTESI', '13:00', '16:00', 30, 5);
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (10, 5, 4, 3, '2024-2025-GUZ', 2, 'CUMA', '09:00', '12:00', 30, 5);

-- YZM203 - Programlama II (Hakan Gunduz)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (11, 6, 2, 2, '2024-2025-GUZ', 1, 'CARSAMBA', '13:00', '16:00', 30, 5);
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (12, 6, 2, 4, '2024-2025-GUZ', 2, 'CUMA', '13:00', '16:00', 30, 5);

-- YZM205 - Programlama Laboratuvari II (Irfan Kosesoy)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (13, 7, 6, 6, '2024-2025-GUZ', 1, 'PERSEMBE', '09:00', '11:00', 30, 5);
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (14, 7, 6, 6, '2024-2025-GUZ', 2, 'PERSEMBE', '11:00', '13:00', 30, 5);

-- YZM207 - Bilgisayar Organizasyonu (Mehmet Zeki Konyar)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (15, 8, 1, 1, '2024-2025-GUZ', 1, 'SALI', '13:00', '15:00', 30, 5);

-- YZM209 - Olasilik ve Istatistik (Levent Bayindir)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (16, 9, 4, 4, '2024-2025-GUZ', 1, 'SALI', '09:00', '11:00', 30, 5);

-- YZM211 - Veritabani Yonetim Sistemleri (Kaplan Kaplan)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (17, 10, 5, 3, '2024-2025-GUZ', 1, 'CUMA', '13:00', '16:00', 30, 5);

-- 3. SINIF DERSLERI (Carsamba-Cuma agirlikli)
-- YZM301 - Yazilim Muhendisligi (Mehmet Zeki Konyar)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (18, 11, 1, 8, '2024-2025-GUZ', 1, 'CUMA', '09:00', '12:00', 30, 5);

-- YZM303 - Programlama III (Hakan Gunduz)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (19, 12, 2, 4, '2024-2025-GUZ', 1, 'PERSEMBE', '09:00', '12:00', 30, 5);

-- YZM305 - Programlama Laboratuvari III (Irfan Kosesoy)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (20, 13, 6, 7, '2024-2025-GUZ', 1, 'CARSAMBA', '14:00', '16:00', 30, 5);

-- YZM307 - Isletim Sistemleri (Levent Bayindir)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (21, 14, 4, 3, '2024-2025-GUZ', 1, 'PERSEMBE', '13:00', '16:00', 30, 5);

-- YZM309 - Bilgisayar Aglari (Kaplan Kaplan)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (22, 15, 5, 4, '2024-2025-GUZ', 1, 'CUMA', '09:00', '11:00', 30, 5);

-- YZM313 - Web Programlama (Kaplan Kaplan)
INSERT INTO sections (section_id, course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity, enrolled_count)
VALUES (23, 16, 5, 6, '2024-2025-GUZ', 1, 'CUMA', '11:00', '13:00', 30, 5);

-- Sekans guncelle
SELECT setval('sections_section_id_seq', 23);

-- =============================================
-- 6. DERS KAYITLARI (Her subede 5 ogrenci)
-- =============================================
-- 1. Sinif ogrencileri (id: 1-10) -> 1. sinif derslerine
-- Her sube 5 kisi: 1-5 -> sube 1, 6-10 -> sube 2

-- YZM101 subeleri
INSERT INTO enrollments (student_id, section_id, status) VALUES (1, 1, 'ENROLLED'), (2, 1, 'ENROLLED'), (3, 1, 'ENROLLED'), (4, 1, 'ENROLLED'), (5, 1, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (6, 2, 'ENROLLED'), (7, 2, 'ENROLLED'), (8, 2, 'ENROLLED'), (9, 2, 'ENROLLED'), (10, 2, 'ENROLLED');

-- YZM103 subeleri
INSERT INTO enrollments (student_id, section_id, status) VALUES (1, 3, 'ENROLLED'), (2, 3, 'ENROLLED'), (3, 3, 'ENROLLED'), (4, 3, 'ENROLLED'), (5, 3, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (6, 4, 'ENROLLED'), (7, 4, 'ENROLLED'), (8, 4, 'ENROLLED'), (9, 4, 'ENROLLED'), (10, 4, 'ENROLLED');

-- YZM105 subeleri
INSERT INTO enrollments (student_id, section_id, status) VALUES (1, 5, 'ENROLLED'), (2, 5, 'ENROLLED'), (3, 5, 'ENROLLED'), (4, 5, 'ENROLLED'), (5, 5, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (6, 6, 'ENROLLED'), (7, 6, 'ENROLLED'), (8, 6, 'ENROLLED'), (9, 6, 'ENROLLED'), (10, 6, 'ENROLLED');

-- YZM107 subeleri
INSERT INTO enrollments (student_id, section_id, status) VALUES (1, 7, 'ENROLLED'), (2, 7, 'ENROLLED'), (3, 7, 'ENROLLED'), (4, 7, 'ENROLLED'), (5, 7, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (6, 8, 'ENROLLED'), (7, 8, 'ENROLLED'), (8, 8, 'ENROLLED'), (9, 8, 'ENROLLED'), (10, 8, 'ENROLLED');

-- 2. Sinif ogrencileri (id: 11-20) -> 2. sinif derslerine
INSERT INTO enrollments (student_id, section_id, status) VALUES (11, 9, 'ENROLLED'), (12, 9, 'ENROLLED'), (13, 9, 'ENROLLED'), (14, 9, 'ENROLLED'), (15, 9, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (16, 10, 'ENROLLED'), (17, 10, 'ENROLLED'), (18, 10, 'ENROLLED'), (19, 10, 'ENROLLED'), (20, 10, 'ENROLLED');

INSERT INTO enrollments (student_id, section_id, status) VALUES (11, 11, 'ENROLLED'), (12, 11, 'ENROLLED'), (13, 11, 'ENROLLED'), (14, 11, 'ENROLLED'), (15, 11, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (16, 12, 'ENROLLED'), (17, 12, 'ENROLLED'), (18, 12, 'ENROLLED'), (19, 12, 'ENROLLED'), (20, 12, 'ENROLLED');

INSERT INTO enrollments (student_id, section_id, status) VALUES (11, 13, 'ENROLLED'), (12, 13, 'ENROLLED'), (13, 13, 'ENROLLED'), (14, 13, 'ENROLLED'), (15, 13, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (16, 14, 'ENROLLED'), (17, 14, 'ENROLLED'), (18, 14, 'ENROLLED'), (19, 14, 'ENROLLED'), (20, 14, 'ENROLLED');

INSERT INTO enrollments (student_id, section_id, status) VALUES (11, 15, 'ENROLLED'), (12, 15, 'ENROLLED'), (13, 15, 'ENROLLED'), (14, 15, 'ENROLLED'), (15, 15, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (11, 16, 'ENROLLED'), (12, 16, 'ENROLLED'), (13, 16, 'ENROLLED'), (14, 16, 'ENROLLED'), (15, 16, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (16, 17, 'ENROLLED'), (17, 17, 'ENROLLED'), (18, 17, 'ENROLLED'), (19, 17, 'ENROLLED'), (20, 17, 'ENROLLED');

-- 3. Sinif ogrencileri (id: 21-30) -> 3. sinif derslerine
INSERT INTO enrollments (student_id, section_id, status) VALUES (21, 18, 'ENROLLED'), (22, 18, 'ENROLLED'), (23, 18, 'ENROLLED'), (24, 18, 'ENROLLED'), (25, 18, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (21, 19, 'ENROLLED'), (22, 19, 'ENROLLED'), (23, 19, 'ENROLLED'), (24, 19, 'ENROLLED'), (25, 19, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (26, 20, 'ENROLLED'), (27, 20, 'ENROLLED'), (28, 20, 'ENROLLED'), (29, 20, 'ENROLLED'), (30, 20, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (21, 21, 'ENROLLED'), (22, 21, 'ENROLLED'), (23, 21, 'ENROLLED'), (24, 21, 'ENROLLED'), (25, 21, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (26, 22, 'ENROLLED'), (27, 22, 'ENROLLED'), (28, 22, 'ENROLLED'), (29, 22, 'ENROLLED'), (30, 22, 'ENROLLED');
INSERT INTO enrollments (student_id, section_id, status) VALUES (21, 23, 'ENROLLED'), (22, 23, 'ENROLLED'), (23, 23, 'ENROLLED'), (24, 23, 'ENROLLED'), (25, 23, 'ENROLLED');

-- =============================================
-- OZET BILGI
-- =============================================
-- Ogrenci Girisi: 240201001 / ogrenci123 (1. sinif), 230201001 / ogrenci123 (2. sinif), 220201001 / ogrenci123 (3. sinif)
-- Hoca Girisi: ikosesoy@kocaeli.edu.tr / hoca123 (Irfan Kosesoy - Lab dersleri)
-- Her derse 30 kisi kapasiteli subeler, 5 kisi kayitli -> 25 bos kontenjan
-- Programlama Lab I, II, III -> Irfan Kosesoy

-- =============================================
-- TRIGGERLARI TEKRAR ETKINLESTIR
-- =============================================
ALTER TABLE sections ENABLE TRIGGER ALL;
ALTER TABLE enrollments ENABLE TRIGGER ALL;
