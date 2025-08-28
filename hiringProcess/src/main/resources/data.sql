-- === ORGANISATION ===
INSERT INTO organisation (id, name, description) VALUES (3, 'TechCorp', 'A global tech company');

-- === DEPARTMENT ===
INSERT INTO department (id, name, location, description, organisation_id) VALUES
(4, 'Engineering', 'Athens', 'Handles product dev', 3),
(5, 'HR', 'Thessaloniki', 'Manages HR stuff', 3),
(6, 'Data Science', 'Patra', 'Data and AI department', 3);

-- === OCCUPATION ===
INSERT INTO occupation (id, title, esco_id) VALUES
(6, 'Software Engineer', 'esco_1234'),
(7, 'HR Specialist', 'esco_5678'),
(8, 'Data Analyst', 'esco_9012'),
(9, 'Frontend Developer', 'esco_3456'),
(10, 'Recruiter', 'esco_7890'),
(11, 'Data Engineer', 'esco_1122'),
(12, 'DevOps Engineer', 'esco_3344'),
(13, 'Training Coordinator', 'esco_5566'),
(14, 'Machine Learning Engineer', 'esco_7788');

-- === INTERVIEW ===
INSERT INTO interview (id, title, description) VALUES
(7, 'Backend Interview', 'Spring Boot and APIs'),
(8, 'HR Interview', 'HR processes'),
(9, 'Data Interview', 'SQL, Python'),
(10, 'Frontend Interview', 'React and UI design'),
(11, 'Recruitment Interview', 'Hiring & sourcing'),
(12, 'Data Eng. Interview', 'ETL pipelines & DBs'),
(13, 'DevOps Interview', 'CI/CD, Kubernetes'),
(14, 'Training Interview', 'Internal training processes'),
(15, 'ML Interview', 'ML models & deployment'),
(16, 'Senior Backend Interview', 'Advanced Java and architecture'),
(17, 'HR Assistant Interview', 'Supportive HR operations'),
(18, 'Junior Data Interview', 'Basic analytics and Excel'),
(19, 'Frontend Intern Interview', 'React basics and growth'),
(20, 'Freelance Recruiter Interview', 'Remote sourcing case study'),
(21, 'ETL Specialist Interview', 'Data cleaning and pipelines'),
(22, 'SRE Interview', 'Site reliability scenarios'),
(23, 'L&D Interview', 'Learning strategy alignment'),
(24, 'AI Research Interview', 'Advanced ML knowledge');

-- === JOB_AD ===
INSERT INTO job_ad (id, title, description, publish_date, status, occupation_id, interview_id) VALUES
(8, 'Backend Developer', 'We are hiring a backend Java developer', '2025-08-01', 'Published', 6, 7),
(9, 'HR Specialist', 'Join our HR department', '2025-08-01', 'Published', 7, 8),
(10, 'Data Analyst', 'Hiring data analyst with SQL skills', '2025-08-06', 'Published', 8, 9),
(11, 'Frontend Developer', 'Seeking React developer for dynamic team', '2025-08-06', 'Published', 9, 10),
(12, 'Recruiter', 'Looking for tech recruiter', '2025-08-06', 'Published', 10, 11),
(13, 'Data Engineer', 'Build data pipelines and manage ETL', '2025-08-06', 'Published', 11, 12),
(14, 'DevOps Engineer', 'Manage CI/CD pipelines and infrastructure', '2025-08-06', 'Published', 12, 13),
(15, 'Training Coordinator', 'Plan and manage employee training', '2025-08-06', 'Published', 13, 14),
(16, 'ML Engineer', 'Develop and deploy machine learning models', '2025-08-06', 'Published', 14, 15),
(17, 'Senior Backend Engineer', 'Lead our backend team with Java expertise', '2025-06-15', 'Complete', 6, 16),
(18, 'HR Operations Assistant', 'Assist in HR operations and tasks', '2025-07-10', 'Pending', 7, 17),
(19, 'Junior Data Analyst', 'Assist with data cleansing and reporting', '2025-06-20', 'Complete', 8, 18),
(20, 'React Developer (Intern)', 'Frontend internship with mentorship', '2025-07-01', 'Pending', 9, 19),
(21, 'Recruiter (Freelance)', 'Remote recruiter needed short-term', '2025-06-25', 'Complete', 10, 20),
(22, 'ETL Specialist', 'Build automated data pipelines', '2025-07-15', 'Pending', 11, 21),
(23, 'Site Reliability Engineer', 'DevOps for global-scale apps', '2025-06-10', 'Complete', 12, 22),
(24, 'L&D Coordinator', 'Support learning & development programs', '2025-07-20', 'Pending', 13, 23),
(25, 'AI Research Engineer', 'Work on cutting-edge ML solutions', '2025-06-05', 'Complete', 14, 24);

-- === jobad_department ===
INSERT INTO jobad_department (jobad_id, department_id) VALUES
(8, 4), (9, 5), (10, 6),
(11, 4), (12, 5), (13, 6),
(14, 4), (15, 5), (16, 6),
(17, 4), (18, 5), (19, 6),
(20, 4), (21, 5), (22, 6),
(23, 4), (24, 5), (25, 6);

-- === INTERVIEW_REPORT ===
INSERT INTO interview_report (id, interview_id) VALUES (9, 7);

-- === CANDIDATE ===
INSERT INTO candidate (id, first_name, last_name, email, info, status, comments, job_ad_id, interview_report_id)
VALUES
(10, 'John', 'Doe', 'john@example.com', 'Experienced dev', 'Approved', 'Strong coding skills', 8, 9),
-- JobAd 8: Backend Developer
(11, 'Alice', 'Smith', 'alice.smith@example.com', 'Backend dev with 3y exp', 'Pending', 'Good Spring Boot knowledge', 8, NULL),
(12, 'Bob', 'Johnson', 'bob.j@example.com', 'Fullstack dev, prefers backend', 'Rejected', 'Weak in SQL', 8, NULL),

-- JobAd 9: HR Specialist
(13, 'Maria', 'Papadopoulou', 'maria.p@example.com', 'HR MSc, 2y exp', 'Approved', 'Strong interpersonal skills', 9, NULL),
(14, 'Nikos', 'Karas', 'nikos.kara@example.com', 'Recent HR graduate', 'Pending', 'Enthusiastic but junior', 9, NULL),

-- JobAd 10: Data Analyst
(15, 'Elena', 'Kostas', 'elena.k@example.com', 'Data analyst, 4y exp in finance', 'Approved', 'SQL expert', 10, NULL),
(16, 'George', 'Liakos', 'george.l@example.com', 'Statistician, 1y exp', 'Rejected', 'Struggled in Python', 10, NULL),

-- JobAd 11: Frontend Developer
(17, 'Sophia', 'Andreou', 'sophia.a@example.com', 'Frontend dev, 2y React', 'Pending', 'Good UI eye', 11, NULL),
(18, 'Panagiotis', 'Dimitriou', 'panos.d@example.com', 'Frontend intern', 'Rejected', 'Weak JavaScript fundamentals', 11, NULL),

-- JobAd 12: Recruiter
(19, 'Helen', 'Markou', 'helen.m@example.com', 'Recruiter, 5y exp', 'Approved', 'Great sourcing track record', 12, NULL),
(20, 'Christos', 'Zafeiris', 'christos.z@example.com', 'HR recruiter, 1y exp', 'Pending', 'Needs mentoring', 12, NULL),

-- JobAd 13: Data Engineer
(21, 'Ioanna', 'Petrou', 'ioanna.p@example.com', 'Data engineer, 3y ETL exp', 'Approved', 'Strong in pipelines', 13, NULL),
(22, 'Dimitris', 'Alexiou', 'dimitris.a@example.com', 'DBA turned data engineer', 'Rejected', 'Weak in Spark', 13, NULL),

-- JobAd 14: DevOps Engineer
(23, 'Giannis', 'Rallis', 'giannis.r@example.com', 'DevOps 4y exp', 'Approved', 'Solid Kubernetes skills', 14, NULL),
(24, 'Katerina', 'Sotiropoulou', 'katerina.s@example.com', 'Sysadmin migrating to DevOps', 'Pending', 'Good CI/CD basics', 14, NULL),

-- JobAd 15: Training Coordinator
(25, 'Anna', 'Georgiou', 'anna.g@example.com', 'Trainer, 6y exp', 'Approved', 'Strong coordination skills', 15, NULL),
(26, 'Vasileios', 'Nikou', 'vasilis.n@example.com', 'Training assistant, 1y exp', 'Pending', 'Shows potential', 15, NULL),

-- JobAd 16: ML Engineer
(27, 'Eleni', 'Pappa', 'eleni.p@example.com', 'ML engineer, 2y exp', 'Approved', 'Good ML model deployment', 16, NULL),
(28, 'Stavros', 'Michailidis', 'stavros.m@example.com', 'Data scientist transitioning to ML', 'Rejected', 'Weak coding practices', 16, NULL),

-- JobAd 17: Senior Backend Engineer
(29, 'Petros', 'Anagnostou', 'petros.a@example.com', 'Senior Java dev, 10y exp', 'Approved', 'Architectural mindset', 17, NULL),
(30, 'Despina', 'Lazarou', 'despina.l@example.com', 'Backend lead, 7y exp', 'Pending', 'Strong but prefers Python', 17, NULL),

-- JobAd 18: HR Operations Assistant
(31, 'Olga', 'Mantzou', 'olga.m@example.com', 'HR ops assistant, 2y exp', 'Approved', 'Process oriented', 18, NULL),
(32, 'Thanasis', 'Vergis', 'thanasis.v@example.com', 'Business admin graduate', 'Rejected', 'Limited HR knowledge', 18, NULL),

-- JobAd 19: Junior Data Analyst
(33, 'Kalliopi', 'Xenou', 'kalliopi.x@example.com', 'Math graduate', 'Pending', 'Strong statistics, weak SQL', 19, NULL),
(34, 'Leonidas', 'Fotiou', 'leonidas.f@example.com', 'Economics graduate', 'Approved', 'Excel wizard', 19, NULL),

-- JobAd 20: React Developer (Intern)
(35, 'Georgia', 'Alexi', 'georgia.a@example.com', 'CS student', 'Pending', 'React basics covered', 20, NULL),
(36, 'Marios', 'Spanos', 'marios.s@example.com', 'Self-taught React dev', 'Rejected', 'Needs mentoring in Git', 20, NULL),

-- JobAd 21: Recruiter (Freelance)
(37, 'Eftychia', 'Marinou', 'eftychia.m@example.com', 'Freelance recruiter, 3y exp', 'Approved', 'Independent worker', 21, NULL),
(38, 'Kostas', 'Chatzis', 'kostas.c@example.com', 'Remote HR recruiter', 'Pending', 'Needs better sourcing tools', 21, NULL),

-- JobAd 22: ETL Specialist
(39, 'Natalia', 'Karagianni', 'natalia.k@example.com', 'ETL specialist, 4y exp', 'Approved', 'Excellent SQL pipelines', 22, NULL),
(40, 'Michalis', 'Arvanitis', 'michalis.a@example.com', 'Data analyst moving to ETL', 'Rejected', 'Struggled in automation', 22, NULL),

-- JobAd 23: Site Reliability Engineer
(41, 'Christina', 'Drosou', 'christina.d@example.com', 'SRE 5y exp', 'Approved', 'Solid incident management', 23, NULL),
(42, 'Dionysis', 'Panou', 'dionysis.p@example.com', 'Junior DevOps', 'Pending', 'Needs on-call experience', 23, NULL),

-- JobAd 24: L&D Coordinator
(43, 'Ioannis', 'Tzanos', 'ioannis.t@example.com', 'Trainer, 3y exp', 'Pending', 'Good program design', 24, NULL),
(44, 'Sofia', 'Ntouka', 'sofia.n@example.com', 'L&D admin', 'Approved', 'Strong organization skills', 24, NULL),

-- JobAd 25: AI Research Engineer
(45, 'Markos', 'Filippou', 'markos.f@example.com', 'PhD ML researcher', 'Approved', 'Deep learning expert', 25, NULL),
(46, 'Irini', 'Kallergi', 'irini.k@example.com', 'AI MSc student', 'Rejected', 'Too junior for role', 25, NULL);


-- === STEP ===
INSERT INTO step (id, title, description, interview_id, position, score) VALUES
(11, 'Technical', 'Java & Spring Boot', 7, 0, 0),
(12, 'HR Round', 'Soft skills & culture fit', 7, 1, 0),
(13, 'HR Policies', 'HR processes & labor law basics', 8, 0, 0),
(14, 'Culture Fit', 'Values and teamwork', 8, 1, 0),
(15, 'SQL & Statistics', 'Joins, window functions, hypothesis testing', 9, 0, 0),
(16, 'Python/Analytics Case', 'Pandas/numpy case exercise', 9, 1, 0);

-- === QUESTION ===  (ΠΡΟΣΟΧΗ: με position)
INSERT INTO question (id, title, description, step_id, position) VALUES
(13, 'What is a HashMap?', 'Explain usage and performance', 11, 0),
(14, 'SOLID Principles', 'Define the 5 principles',        11, 1),
(15, 'Conflict Resolution', 'How do you resolve team conflicts?', 12, 0);

-- === SKILL ===
INSERT INTO skill (id, title, esco_id, score) VALUES
(16, 'Java', 'java001', 0),
(17, 'OOP Principles', 'oop002', 0),
(18, 'SQL', 'sql999', 0),
(19, 'Python Programming', 'py888', 0),
(20, 'ReactJS Framework', 'react777', 0),
(21, 'Continuous Integration & Deployment', 'cicd666', 0),
(22, 'Container Orchestration', 'k8s555', 0),
(23, 'Spreadsheet Software', 'excel444', 0),
(24, 'Collaboration and Teamwork', 'team333', 0),
(25, 'Talent Acquisition Skills', 'recruit222', 0),
(26, 'Data Preprocessing', 'clean111', 0),
(27, 'Artificial Intelligence', 'ai000', 0);

-- === question_skill ===
INSERT INTO question_skill (question_id, skill_id) VALUES
(13, 16),
(14, 17);

-- === STEP_RESULTS ===
INSERT INTO step_results (id, step_id, interview_report_id) VALUES
(18, 11, 9),
(19, 12, 9);

-- === QUESTION_SCORE ===
INSERT INTO question_score (id, question_id, step_results_id, score) VALUES
(20, 13, 18, 9.0),
(21, 14, 18, 8.5),
(22, 15, 19, 7.0);

