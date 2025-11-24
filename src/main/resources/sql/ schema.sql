SET FOREIGN_KEY_CHECKS = 0;
CREATE DATABASE Quick_Zee;
USE  Quick_Zee;
-- Optional: drop tables if exist (useful during development)
-- DROP TABLE IF EXISTS quiz_result_answers;
-- DROP TABLE IF EXISTS quiz_results;
-- DROP TABLE IF EXISTS options;
-- DROP TABLE IF EXISTS questions;
-- DROP TABLE IF EXISTS quizzes;
-- DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- USERS
CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, -- not null+ unique
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,        -- TODO: hash passwords in production
  semester INT,
  role VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
select * from users;

-- QUIZZES
CREATE TABLE quizzes (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  semester INT,
  duration_minutes INT NOT NULL DEFAULT 15,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_quizzes_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
select * from quizzes;
-- QUESTIONS
CREATE TABLE questions (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  quiz_id BIGINT NOT NULL,
  ordinal INT NOT NULL,                 -- question order (1,2,3,...)
  text TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
  UNIQUE KEY uq_quiz_ordinal (quiz_id, ordinal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
select * from questions;

-- OPTIONS (answers)
CREATE TABLE options (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  question_id BIGINT NOT NULL,
  ordinal INT NOT NULL,                 -- option order (0,1,2,3)
  text VARCHAR(1024) NOT NULL,
  is_correct TINYINT(1) NOT NULL DEFAULT 0,  -- 1 = correct, 0 = incorrect
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_options_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
  UNIQUE KEY uq_question_option_ordinal (question_id, ordinal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
select * from options;
-- QUIZ RESULTS (summary row)
CREATE TABLE quiz_results (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  quiz_id BIGINT NOT NULL,
  score INT,
  total_questions INT,
  submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_results_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
  CONSTRAINT fk_results_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
  INDEX idx_results_user (user_id),
  INDEX idx_results_quiz (quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
select * from quiz_results;

-- QUIZ RESULT ANSWERS (per-question selected option)
CREATE TABLE quiz_result_answers (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  result_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  selected_option_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_answers_result FOREIGN KEY (result_id) REFERENCES quiz_results(id) ON DELETE CASCADE,
  CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE RESTRICT,
  CONSTRAINT fk_answers_option FOREIGN KEY (selected_option_id) REFERENCES options(id) ON DELETE SET NULL,
  INDEX idx_answers_result (result_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
select * from quiz_result_answers;