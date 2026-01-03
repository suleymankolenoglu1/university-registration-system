-- ============================================
-- PROCEDURE 1: Öğrenci Ders Programı
-- ============================================
CREATE OR REPLACE FUNCTION get_student_schedule(p_student_id INT)
RETURNS TABLE (
    course_code VARCHAR,
    course_name VARCHAR,
    instructor_name VARCHAR,
    room_code VARCHAR,
    day_of_week VARCHAR,
    start_time TIME,
    end_time TIME
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        c.course_code,
        c.course_name,
        CONCAT(i.title, ' ', i.first_name, ' ', i.last_name)::VARCHAR,
        r.room_code,
        s.day_of_week,
        s.start_time,
        s.end_time
    FROM enrollments e
    JOIN sections s ON e.section_id = s.section_id
    JOIN courses c ON s.course_id = c.course_id
    JOIN instructors i ON s.instructor_id = i.instructor_id
    LEFT JOIN rooms r ON s.room_id = r.room_id
    WHERE e.student_id = p_student_id
      AND e.status = 'ENROLLED'
    ORDER BY 
        CASE s.day_of_week 
            WHEN 'PAZARTESI' THEN 1
            WHEN 'SALI' THEN 2
            WHEN 'CARSAMBA' THEN 3
            WHEN 'PERSEMBE' THEN 4
            WHEN 'CUMA' THEN 5
        END,
        s.start_time;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- PROCEDURE 2: Müsait Derslikler
-- ============================================
CREATE OR REPLACE FUNCTION get_available_rooms(
    p_day VARCHAR,
    p_start_time TIME,
    p_end_time TIME,
    p_semester VARCHAR
)
RETURNS TABLE (
    room_id INT,
    room_code VARCHAR,
    building VARCHAR,
    capacity INT,
    room_type VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT r.room_id, r.room_code, r.building, r.capacity, r.room_type
    FROM rooms r
    WHERE r.room_id NOT IN (
        SELECT s.room_id 
        FROM sections s
        WHERE s.room_id IS NOT NULL
          AND s.semester = p_semester
          AND s.day_of_week = p_day
          AND (s.start_time < p_end_time AND s.end_time > p_start_time)
    )
    ORDER BY r.capacity DESC;
END;
$$ LANGUAGE plpgsql;


-- ============================================
-- PROCEDURE 3: Zaman Çakışması Kontrolü
-- ============================================
CREATE OR REPLACE FUNCTION check_time_conflict_fn(
    p_student_id INT,
    p_section_id INT
)
RETURNS TABLE (
    has_conflict BOOLEAN,
    conflicting_course VARCHAR,
    conflict_day VARCHAR,
    conflict_time VARCHAR
) AS $$
DECLARE
    new_section RECORD;
BEGIN
    SELECT day_of_week, start_time, end_time, semester 
    INTO new_section
    FROM sections WHERE section_id = p_section_id;
    
    RETURN QUERY
    SELECT 
        TRUE,
        (c.course_code || ' - ' || c.course_name)::VARCHAR,
        s.day_of_week::VARCHAR,
        (TO_CHAR(s.start_time, 'HH24:MI') || '-' || TO_CHAR(s.end_time, 'HH24:MI'))::VARCHAR
    FROM enrollments e
    JOIN sections s ON e.section_id = s.section_id
    JOIN courses c ON s.course_id = c.course_id
    WHERE e.student_id = p_student_id
      AND e.status = 'ENROLLED'
      AND s.semester = new_section.semester
      AND s.day_of_week = new_section.day_of_week
      AND s.section_id != p_section_id
      AND (s.start_time < new_section.end_time AND s.end_time > new_section.start_time);
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- PROCEDURE 4: Ders İstatistikleri
-- ============================================
CREATE OR REPLACE FUNCTION get_course_statistics()
RETURNS TABLE (
    course_code VARCHAR,
    course_name VARCHAR,
    section_count BIGINT,
    total_capacity BIGINT,
    total_enrolled BIGINT,
    occupancy_rate NUMERIC,
    waitlist_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        c.course_code,
        c.course_name,
        COUNT(DISTINCT s.section_id),
        COALESCE(SUM(s.capacity), 0)::BIGINT,
        COALESCE(SUM(s.enrolled_count), 0)::BIGINT,
        ROUND((SUM(s.enrolled_count)::NUMERIC / NULLIF(SUM(s.capacity), 0)) * 100, 2),
        (SELECT COUNT(*) FROM waiting_list w 
         JOIN sections sec ON w.section_id = sec.section_id 
         WHERE sec.course_id = c.course_id)::BIGINT
    FROM courses c
    LEFT JOIN sections s ON c.course_id = s.course_id
    GROUP BY c.course_id, c.course_code, c.course_name
    ORDER BY occupancy_rate DESC NULLS LAST;
END;
$$ LANGUAGE plpgsql;