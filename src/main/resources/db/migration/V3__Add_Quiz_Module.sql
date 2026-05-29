ALTER TABLE quizzes
    ADD COLUMN IF NOT EXISTS module_id BIGINT;

ALTER TABLE quizzes
    ADD CONSTRAINT fk_quizzes_module
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE;
