-- 1. Bảng users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL, -- LEARNER, INSTRUCTOR, ADMIN
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng courses
CREATE TABLE courses (
                         id BIGSERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         thumbnail_url VARCHAR(255),
                         status VARCHAR(50) DEFAULT 'DRAFT', -- DRAFT, PUBLISHED
                         instructor_id BIGINT REFERENCES users(id),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Bảng modules
CREATE TABLE modules (
                         id BIGSERIAL PRIMARY KEY,
                         course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
                         title VARCHAR(255) NOT NULL,
                         order_index INT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Bảng lessons
CREATE TABLE lessons (
                         id BIGSERIAL PRIMARY KEY,
                         module_id BIGINT REFERENCES modules(id) ON DELETE CASCADE,
                         title VARCHAR(255) NOT NULL,
                         content_type VARCHAR(50) NOT NULL, -- VIDEO, TEXT, DOCUMENT
                         content_url VARCHAR(255),
                         content_body TEXT,
                         order_index INT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Bảng enrollments
CREATE TABLE enrollments (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                             course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
                             enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             status VARCHAR(50) DEFAULT 'IN_PROGRESS', -- IN_PROGRESS, COMPLETED
                             progress_percentage FLOAT DEFAULT 0.0,
                             UNIQUE(user_id, course_id)
);

-- 6. Bảng lesson_progress
CREATE TABLE lesson_progress (
                                 id BIGSERIAL PRIMARY KEY,
                                 enrollment_id BIGINT REFERENCES enrollments(id) ON DELETE CASCADE,
                                 lesson_id BIGINT REFERENCES lessons(id) ON DELETE CASCADE,
                                 is_completed BOOLEAN DEFAULT FALSE,
                                 completed_at TIMESTAMP,
                                 UNIQUE(enrollment_id, lesson_id)
);

-- 7. Bảng quizzes
CREATE TABLE quizzes (
                         id BIGSERIAL PRIMARY KEY,
                         course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
                         title VARCHAR(255) NOT NULL,
                         passing_score INT NOT NULL,
                         time_limit_minutes INT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Bảng questions
CREATE TABLE questions (
                           id BIGSERIAL PRIMARY KEY,
                           quiz_id BIGINT REFERENCES quizzes(id) ON DELETE CASCADE,
                           content TEXT NOT NULL,
                           options JSONB NOT NULL,
                           correct_answer VARCHAR(50) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. Bảng attempts
CREATE TABLE attempts (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                          quiz_id BIGINT REFERENCES quizzes(id) ON DELETE CASCADE,
                          score INT NOT NULL,
                          user_answers JSONB NOT NULL,
                          status VARCHAR(50) NOT NULL, -- PASSED, FAILED
                          submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Bảng certificates
CREATE TABLE certificates (
                              id BIGSERIAL PRIMARY KEY,
                              user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                              course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
                              certificate_code VARCHAR(100) UNIQUE NOT NULL,
                              pdf_url VARCHAR(255) NOT NULL,
                              issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);