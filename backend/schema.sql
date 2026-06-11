-- EduGuard backend schema (SQLite)

CREATE TABLE IF NOT EXISTS users (
    id              TEXT PRIMARY KEY,
    name            TEXT NOT NULL,
    email           TEXT NOT NULL UNIQUE,
    phone           TEXT,
    password_hash   TEXT NOT NULL,
    role            TEXT NOT NULL CHECK (role IN ('admin', 'mentor', 'student')),
    assigned_mentor TEXT,
    status          TEXT NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'disabled')),
    created_at      TEXT NOT NULL,
    FOREIGN KEY (assigned_mentor) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS policies (
    id            TEXT PRIMARY KEY,
    student_id    TEXT NOT NULL UNIQUE,
    allowed_apps  TEXT NOT NULL DEFAULT '[]',   -- JSON array of {packageName, dailyLimitMinutes, blocked}
    sleep_enabled INTEGER NOT NULL DEFAULT 0,
    sleep_start   TEXT,                          -- "22:00"
    sleep_end     TEXT,                          -- "06:00"
    theme         TEXT,
    updated_at    TEXT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests (
    id                      TEXT PRIMARY KEY,
    student_id              TEXT NOT NULL,
    mentor_id               TEXT,
    type                    TEXT NOT NULL DEFAULT 'extraTime',  -- extraTime | emergency
    message                 TEXT,
    requested_for_app       TEXT,
    requested_extra_minutes INTEGER,
    status                  TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    created_at              TEXT NOT NULL,
    resolved_at             TEXT,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (mentor_id)  REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS logs (
    id         TEXT PRIMARY KEY,
    student_id TEXT NOT NULL,
    date       TEXT NOT NULL,               -- "yyyy-MM-dd"
    app_usage  TEXT NOT NULL DEFAULT '[]',  -- JSON array of {packageName, duration}
    created_at TEXT NOT NULL,
    UNIQUE (student_id, date),
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_requests_student ON requests(student_id);
CREATE INDEX IF NOT EXISTS idx_requests_mentor  ON requests(mentor_id);
CREATE INDEX IF NOT EXISTS idx_logs_student     ON logs(student_id);
