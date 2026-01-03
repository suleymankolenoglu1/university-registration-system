-- ============================================
-- TRIGGER 1: Kontenjan Kontrolü
-- ============================================
CREATE OR REPLACE FUNCTION check_and_update_enrollment()
RETURNS TRIGGER AS $$
BEGIN
    -- Yeni kayıt ekleniyor
    IF TG_OP = 'INSERT' AND NEW.status = 'ENROLLED' THEN
        -- Kontenjan kontrolü
        IF (SELECT enrolled_count >= capacity FROM sections WHERE section_id = NEW.section_id) THEN
            RAISE EXCEPTION 'Kontenjan dolu!';
        END IF;
        
        -- enrolled_count artır
        UPDATE sections SET enrolled_count = enrolled_count + 1 
        WHERE section_id = NEW.section_id;
    END IF;
    
    -- Kayıt DROPPED yapılıyor
    IF TG_OP = 'UPDATE' AND OLD.status = 'ENROLLED' AND NEW.status = 'DROPPED' THEN
        UPDATE sections SET enrolled_count = enrolled_count - 1 
        WHERE section_id = NEW.section_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_enrollment_capacity
    BEFORE INSERT OR UPDATE ON enrollments
    FOR EACH ROW
    EXECUTE FUNCTION check_and_update_enrollment();

    -- ============================================
-- TRIGGER 2: Zaman Çakışması Kontrolü
-- ============================================
CREATE OR REPLACE FUNCTION check_time_conflict()
RETURNS TRIGGER AS $$
DECLARE
    new_section RECORD;
    conflict_count INT;
BEGIN
    -- Yeni kaydolunacak section bilgilerini al
    SELECT day_of_week, start_time, end_time, semester 
    INTO new_section
    FROM sections WHERE section_id = NEW.section_id;
    
    -- Çakışma kontrolü
    SELECT COUNT(*) INTO conflict_count
    FROM enrollments e
    JOIN sections s ON e.section_id = s.section_id
    WHERE e.student_id = NEW.student_id
      AND e.status = 'ENROLLED'
      AND s.semester = new_section.semester
      AND s.day_of_week = new_section.day_of_week
      AND s.section_id != NEW.section_id
      AND (s.start_time < new_section.end_time AND s.end_time > new_section.start_time);
    
    IF conflict_count > 0 THEN
        RAISE EXCEPTION 'Zaman çakışması! Bu saatte başka dersiniz var.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_time_conflict
    BEFORE INSERT ON enrollments
    FOR EACH ROW
    WHEN (NEW.status = 'ENROLLED')
    EXECUTE FUNCTION check_time_conflict();

    -- ============================================
-- TRIGGER 3: Ön Koşul Kontrolü
-- ============================================
CREATE OR REPLACE FUNCTION check_prerequisite()
RETURNS TRIGGER AS $$
DECLARE
    prereq_id INT;
    prereq_completed BOOLEAN;
BEGIN
    -- Dersin ön koşulunu al
    SELECT c.prerequisite_course_id INTO prereq_id
    FROM sections s
    JOIN courses c ON s.course_id = c.course_id
    WHERE s.section_id = NEW.section_id;
    
    -- Ön koşul yoksa devam et
    IF prereq_id IS NULL THEN
        RETURN NEW;
    END IF;
    
    -- Öğrenci ön koşulu tamamlamış mı?
    SELECT EXISTS (
        SELECT 1 FROM enrollments e
        JOIN sections s ON e.section_id = s.section_id
        WHERE e.student_id = NEW.student_id
          AND s.course_id = prereq_id
          AND e.status = 'COMPLETED'
          AND e.grade IS NOT NULL
          AND e.grade NOT IN ('F', 'FF')
    ) INTO prereq_completed;
    
    IF NOT prereq_completed THEN
        RAISE EXCEPTION 'Ön koşul dersi tamamlanmamış!';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prerequisite_check
    BEFORE INSERT ON enrollments
    FOR EACH ROW
    WHEN (NEW.status = 'ENROLLED')
    EXECUTE FUNCTION check_prerequisite();

    -- ============================================
-- TRIGGER 4: Bekleme Listesi Otomasyonu
-- ============================================
CREATE OR REPLACE FUNCTION process_waitlist()
RETURNS TRIGGER AS $$
DECLARE
    next_student RECORD;
BEGIN
    -- Sadece DROPPED durumuna geçişte çalış
    IF TG_OP = 'UPDATE' AND OLD.status = 'ENROLLED' AND NEW.status = 'DROPPED' THEN
        -- Bekleme listesindeki ilk öğrenciyi bul
        SELECT * INTO next_student
        FROM waiting_list
        WHERE section_id = NEW.section_id
        ORDER BY position ASC
        LIMIT 1;
        
        -- Bekleme listesinde öğrenci varsa
        IF FOUND THEN
            -- Yeni enrollment ekle
            INSERT INTO enrollments (student_id, section_id, status)
            VALUES (next_student.student_id, next_student.section_id, 'ENROLLED');
            
            -- Bekleme listesinden sil
            DELETE FROM waiting_list WHERE waiting_id = next_student.waiting_id;
            
            -- Diğer pozisyonları güncelle
            UPDATE waiting_list 
            SET position = position - 1 
            WHERE section_id = next_student.section_id 
              AND position > next_student.position;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_waitlist_automation
    AFTER UPDATE ON enrollments
    FOR EACH ROW
    EXECUTE FUNCTION process_waitlist();

    -- ============================================
-- TRIGGER 5: Derslik Çakışması Kontrolü
-- ============================================
CREATE OR REPLACE FUNCTION check_room_conflict()
RETURNS TRIGGER AS $$
DECLARE
    conflict_count INT;
BEGIN
    IF NEW.room_id IS NULL THEN
        RETURN NEW;
    END IF;
    
    SELECT COUNT(*) INTO conflict_count
    FROM sections
    WHERE room_id = NEW.room_id
      AND semester = NEW.semester
      AND day_of_week = NEW.day_of_week
      AND section_id != COALESCE(NEW.section_id, 0)
      AND (start_time < NEW.end_time AND end_time > NEW.start_time);
    
    IF conflict_count > 0 THEN
        RAISE EXCEPTION 'Derslik çakışması! Bu derslik bu saatte dolu.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_room_conflict
    BEFORE INSERT OR UPDATE ON sections
    FOR EACH ROW
    EXECUTE FUNCTION check_room_conflict();

-- ============================================
-- TRIGGER 6: Öğretim Elemanı Çakışması Kontrolü
-- ============================================
CREATE OR REPLACE FUNCTION check_instructor_conflict()
RETURNS TRIGGER AS $$
DECLARE
    conflict_count INT;
BEGIN
    SELECT COUNT(*) INTO conflict_count
    FROM sections
    WHERE instructor_id = NEW.instructor_id
      AND semester = NEW.semester
      AND day_of_week = NEW.day_of_week
      AND section_id != COALESCE(NEW.section_id, 0)
      AND (start_time < NEW.end_time AND end_time > NEW.start_time);
    
    IF conflict_count > 0 THEN
        RAISE EXCEPTION 'Hoca çakışması! Bu hoca bu saatte başka ders veriyor.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_instructor_conflict
    BEFORE INSERT OR UPDATE ON sections
    FOR EACH ROW
    EXECUTE FUNCTION check_instructor_conflict();