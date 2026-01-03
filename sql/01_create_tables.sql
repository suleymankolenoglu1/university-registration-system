-- Students tablosu
CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    student_number VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    semester INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Instructors tablosu
CREATE TABLE instructors (
    instructor_id SERIAL PRIMARY KEY,
    instructor_number VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    title VARCHAR(50) NOT NULL,
    department VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Courses tablosu
CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    description TEXT,
    prerequisite_course_id INT REFERENCES courses(course_id) ON DELETE SET NULL,
    department VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms tablosu (Derslikler)
CREATE TABLE rooms (
    room_id SERIAL PRIMARY KEY,
    room_code VARCHAR(20) UNIQUE NOT NULL,
    building VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    room_type VARCHAR(20) NOT NULL
);

-- Sections tablosu (Ders Şubeleri)
CREATE TABLE sections (
    section_id SERIAL PRIMARY KEY,
    course_id INT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    instructor_id INT NOT NULL REFERENCES instructors(instructor_id),
    room_id INT REFERENCES rooms(room_id),
    semester VARCHAR(20) NOT NULL,
    section_number INT NOT NULL DEFAULT 1,
    day_of_week VARCHAR(10) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    capacity INT NOT NULL,
    enrolled_count INT DEFAULT 0
);

-- Enrollments tablosu (Ders Kayıtları)
CREATE TABLE enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    section_id INT NOT NULL REFERENCES sections(section_id) ON DELETE CASCADE,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',
    grade VARCHAR(5),
    UNIQUE(student_id, section_id)
);
-- Waiting_List tablosu (Bekleme Listesi)
CREATE TABLE waiting_list (
    waiting_id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    section_id INT NOT NULL REFERENCES sections(section_id) ON DELETE CASCADE,
    position INT NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, section_id)
);


-- INDEXLER (Performans için)
CREATE INDEX idx_students_department ON students(department);
CREATE INDEX idx_students_email ON students(email);

CREATE INDEX idx_instructors_department ON instructors(department);

CREATE INDEX idx_courses_department ON courses(department);

CREATE INDEX idx_sections_course ON sections(course_id);
CREATE INDEX idx_sections_instructor ON sections(instructor_id);
CREATE INDEX idx_sections_semester ON sections(semester);

CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_section ON enrollments(section_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);

CREATE INDEX idx_waiting_section ON waiting_list(section_id, position);