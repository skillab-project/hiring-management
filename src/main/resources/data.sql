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
(17, 'Senior Backend Engineer', 'Lead our backend team with Java expertise', '2025-06-15', 'Published', 6, 16),
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
INSERT INTO interview_report (id, interview_id) VALUES
(9, 7),
(10, 7),
(11, 7),
(12, 8),
(13, 8),
(14, 9),
(15, 9),
(16, 10),
(17, 10),
(18, 11),
(19, 11),
(20, 12),
(21, 12),
(22, 13),
(23, 13),
(24, 14),
(25, 14),
(26, 15),
(27, 15),
(28, 16),
(29, 16),
(30, 17),
(31, 17),
(32, 18),
(33, 18),
(34, 19),
(35, 19),
(36, 20),
(37, 20),
(38, 21),
(39, 21),
(40, 22),
(41, 22),
(42, 22),
(43, 23),
(44, 23),
(45, 24),
(46, 24),
(47, 24);

-- === CANDIDATE ===
INSERT INTO candidate (id, first_name, last_name, email, status, comments, job_ad_id, interview_report_id)
VALUES
-- JobAd 8: Backend Developer
(10, 'John', 'Doe', 'john@example.com', 'Approved', 'Strong coding skills', 8, 9),
(11, 'Alice', 'Smith', 'alice.smith@example.com', 'Pending', 'Good Spring Boot knowledge', 8, 10),
(12, 'Bob', 'Johnson', 'bob.j@example.com', 'Rejected', 'Weak in SQL', 8, 11),

-- JobAd 9: HR Specialist
(13, 'Maria', 'Papadopoulou', 'maria.p@example.com', 'Approved', 'Strong interpersonal skills', 9, 12),
(14, 'Nikos', 'Karas', 'nikos.kara@example.com', 'Pending', 'Enthusiastic but junior', 9, 13),

-- JobAd 10: Data Analyst
(15, 'Elena', 'Kostas', 'elena.k@example.com', 'Approved', 'SQL expert', 10, 14),
(16, 'George', 'Liakos', 'george.l@example.com', 'Rejected', 'Struggled in Python', 10, 15),

-- JobAd 11: Frontend Developer
(17, 'Sophia', 'Andreou', 'sophia.a@example.com', 'Pending', 'Good UI eye', 11, 16),
(18, 'Panagiotis', 'Dimitriou', 'panos.d@example.com', 'Rejected', 'Weak JavaScript fundamentals', 11,17),

-- JobAd 12: Recruiter
(19, 'Helen', 'Markou', 'helen.m@example.com', 'Approved', 'Great sourcing track record', 12, 18),
(20, 'Christos', 'Zafeiris', 'christos.z@example.com', 'Pending', 'Needs mentoring', 12, 19),

-- JobAd 13: Data Engineer
(21, 'Ioanna', 'Petrou', 'ioanna.p@example.com', 'Approved', 'Strong in pipelines', 13, 20),
(22, 'Dimitris', 'Alexiou', 'dimitris.a@example.com', 'Rejected', 'Weak in Spark', 13, 21),

-- JobAd 14: DevOps Engineer
(23, 'Giannis', 'Rallis', 'giannis.r@example.com', 'Approved', 'Solid Kubernetes skills', 14, 22),
(24, 'Katerina', 'Sotiropoulou', 'katerina.s@example.com', 'Pending', 'Good CI/CD basics', 14, 23),

-- JobAd 15: Training Coordinator
(25, 'Anna', 'Georgiou', 'anna.g@example.com', 'Approved', 'Strong coordination skills', 15, 24),
(26, 'Vasileios', 'Nikou', 'vasilis.n@example.com', 'Pending', 'Shows potential', 15, 25),

-- JobAd 16: ML Engineer
(27, 'Eleni', 'Pappa', 'eleni.p@example.com', 'Approved', 'Good ML model deployment', 16, 26),
(28, 'Stavros', 'Michailidis', 'stavros.m@example.com', 'Rejected', 'Weak coding practices', 16, 27),

-- JobAd 17: Senior Backend Engineer
(29, 'Petros', 'Anagnostou', 'petros.a@example.com', 'Pending', 'Architectural mindset', 17, 28),
(30, 'Despina', 'Lazarou', 'despina.l@example.com', 'Pending', 'Strong but prefers Python', 17, 29),

-- JobAd 18: HR Operations Assistant
(31, 'Olga', 'Mantzou', 'olga.m@example.com', 'Approved', 'Process oriented', 18, 30),
(32, 'Thanasis', 'Vergis', 'thanasis.v@example.com', 'Rejected', 'Limited HR knowledge', 18, 31),

-- JobAd 19: Junior Data Analyst
(33, 'Kalliopi', 'Xenou', 'kalliopi.x@example.com', 'Pending', 'Strong statistics, weak SQL', 19, 32),
(34, 'Leonidas', 'Fotiou', 'leonidas.f@example.com', 'Hired', 'Excel wizard', 19, 33),

-- JobAd 20: React Developer (Intern)
(35, 'Georgia', 'Alexi', 'georgia.a@example.com', 'Pending', 'React basics covered', 20, 34),
(36, 'Marios', 'Spanos', 'marios.s@example.com', 'Rejected', 'Needs mentoring in Git', 20, 35),

-- JobAd 21: Recruiter (Freelance)
(37, 'Eftychia', 'Marinou', 'eftychia.m@example.com', 'Approved', 'Independent worker', 21, 36),
(38, 'Kostas', 'Chatzis', 'kostas.c@example.com', 'Hired', 'Needs better sourcing tools', 21, 37),

-- JobAd 22: ETL Specialist
(39, 'Natalia', 'Karagianni', 'natalia.k@example.com', 'Approved', 'Excellent SQL pipelines', 22, 38),
(40, 'Michalis', 'Arvanitis', 'michalis.a@example.com', 'Rejected', 'Struggled in automation', 22, 39),

-- JobAd 23: Site Reliability Engineer
(41, 'Christina', 'Drosou', 'christina.d@example.com', 'Approved', 'Solid incident management', 23, 40),
(48, 'Maria', 'Papadopoulou', 'maria.p@example.com', 'Hired', 'Insufficient digital marketing skills', 23, 41),
(42, 'Dionysis', 'Panou', 'dionysis.p@example.com', 'Pending', 'Needs on-call experience', 23, 42),

-- JobAd 24: L&D Coordinator
(43, 'Ioannis', 'Tzanos', 'ioannis.t@example.com', 'Pending', 'Good program design', 24, 43),
(44, 'Sofia', 'Ntouka', 'sofia.n@example.com', 'Approved', 'Strong organization skills', 24, 44),

-- JobAd 25: AI Research Engineer
(45, 'Markos', 'Filippou', 'markos.f@example.com', 'Approved', 'Deep learning expert', 25, 45),
(46, 'Irini', 'Kallergi', 'irini.k@example.com', 'Rejected', 'Too junior for role', 25, 46),
(47, 'Eleni', 'Papadopoulou', 'eleni.p@example.com', 'Hired', 'Insufficient digital marketing skills', 25,47);


-- === STEP ===
INSERT INTO step (id, title, description, interview_id, position, score) VALUES
(11, 'Technical', 'Java & Spring Boot', 7, 0, 0),
(12, 'HR Round', 'Soft skills & culture fit', 7, 1, 0),
(13, 'HR Policies', 'HR processes & labor law basics', 8, 0, 0),
(14, 'Culture Fit', 'Values and teamwork', 8, 1, 0),
(15, 'SQL & Statistics', 'Joins, window functions, hypothesis testing', 9, 0, 0),
(16, 'Python/Analytics Case', 'Pandas/numpy case exercise', 9, 1, 0),
(17, 'System Design', 'Scalability, caching, trade-offs', 7, 2, 0),
(18, 'Case Scenarios', 'Practical HR scenarios & decisions', 8, 2, 0),
(19, 'Data Modeling', 'Star/Snowflake, normalization', 9, 2, 0),
(20, 'React Core', 'Components, state, hooks', 10, 0, 0),
(21, 'UI/UX', 'Accessibility & design systems', 10, 1, 0),
(22, 'Frontend Build', 'Tooling, testing, performance', 10, 2, 0),
(23, 'Sourcing', 'Channels & boolean search', 11, 0, 0),
(24, 'Screening', 'Structured interviews & bias', 11, 1, 0),
(25, 'Offer/Close', 'Negotiation & stakeholder mgmt', 11, 2, 0),
(26, 'ETL Design', 'Batch vs streaming', 12, 0, 0),
(27, 'Data Warehousing', 'Partitioning, orchestration', 12, 1, 0),
(28, 'Ops & Reliability', 'Monitoring & SLAs', 12, 2, 0),
(29, 'CI/CD', 'Pipelines & artifacts', 13, 0, 0),
(30, 'Containers', 'Docker/Kubernetes', 13, 1, 0),
(31, 'Observability', 'Metrics, logs, tracing', 13, 2, 0),
(32, 'Needs Analysis', 'Gap analysis & stakeholders', 14, 0, 0),
(33, 'Program Delivery', 'Scheduling & facilitation', 14, 1, 0),
(34, 'ML Fundamentals', 'Bias/variance, evaluation', 15, 0, 0),
(35, 'Production ML', 'Serving & monitoring', 15, 1, 0),
(36, 'Feature Engineering', 'Preprocessing & leakage', 15, 2, 0),
(37, 'Architecture', 'DDD, microservices', 16, 0, 0),
(38, 'Performance', 'Profiling & tuning', 16, 1, 0),
(39, 'Leadership', 'Mentoring & reviews', 16, 2, 0),
(40, 'Admin & Compliance', 'Docs, GDPR basics', 17, 0, 0),
(41, 'Communication', 'Ticketing & employee support', 17, 1, 0),
(42, 'Excel/Spreadsheets', 'Functions & pivots', 18, 0, 0),
(43, 'Basic SQL', 'SELECT, JOIN, GROUP BY', 18, 1, 0),
(44, 'HTML/CSS', 'Semantics & layout', 19, 0, 0),
(45, 'React Basics', 'Components & props', 19, 1, 0),
(46, 'Remote Sourcing', 'Async comms & tools', 20, 0, 0),
(47, 'Client Handling', 'Req intake & updates', 20, 1, 0),
(48, 'Data Cleansing', 'Quality & validation', 21, 0, 0),
(49, 'Pipelines', 'Scheduling & retries', 21, 1, 0),
(50, 'Reliability', 'SLI/SLO/Error budgets', 22, 0, 0),
(51, 'Incident Response', 'On-call & postmortems', 22, 1, 0),
(52, 'Performance Eng', 'Capacity & load', 22, 2, 0),
(53, 'Learning Strategy', 'KPIs & outcomes', 23, 0, 0),
(54, 'Content Design', 'Curricula & materials', 23, 1, 0),
(55, 'Research Methods', 'Reading & experimentation', 24, 0, 0),
(56, 'Advanced ML', 'DL architectures', 24, 1, 0),
(57, 'Paper Review', 'Critique & replication', 24, 2, 0);

-- === QUESTION ===  (ΠΡΟΣΟΧΗ: με position)
INSERT INTO question (id, title, description, step_id, position) VALUES
(13, 'What is a HashMap?', 'Explain usage and performance', 11, 0),
(14, 'SOLID Principles', 'Define the 5 principles',        11, 1),
(15, 'Conflict Resolution', 'How do you resolve team conflicts?', 12, 0),
(16, 'Scale an API', 'Design a high-traffic read API', 17, 0),
(17, 'Caching Strategy', 'Where and what to cache', 17, 1),
(18, 'Conflict Case', 'Resolve conflict between teams', 18, 0),
(19, 'Policy Dilemma', 'Handle borderline policy breach', 18, 1),
(20, 'Dimensional Model', 'Design sales analytics model', 19, 0),
(21, 'Normalization', '3NF vs denormalization trade-offs', 19, 1),
(22, 'Hooks', 'useEffect pitfalls', 20, 0),
(23, 'State Mgmt', 'Lifting state vs context', 20, 1),
(24, 'Accessibility', 'ARIA and keyboard nav', 21, 0),
(25, 'Design Tokens', 'Scale a design system', 21, 1),
(26, 'Performance', 'Core Web Vitals improvements', 22, 0),
(27, 'Testing', 'Unit vs E2E in FE', 22, 1),
(28, 'Boolean Search', 'Build queries for LinkedIn', 23, 0),
(29, 'Sourcing Plan', '30-day plan for hard role', 23, 1),
(30, 'Structured Interview', 'Scorecards & consistency', 24, 0),
(31, 'Bias Mitigation', 'Reduce bias in interviews', 24, 1),
(32, 'Negotiation', 'Handle multiple offers', 25, 0),
(33, 'Stakeholders', 'Sync with hiring manager', 25, 1),
(34, 'Batch vs Stream', 'Choose for clickstream', 26, 0),
(35, 'Idempotency', 'Design safe re-runs', 26, 1),
(36, 'Partitioning', 'Strategy for large tables', 27, 0),
(37, 'Orchestration', 'Airflow vs alternatives', 27, 1),
(38, 'Monitoring', 'What to monitor in ETL', 28, 0),
(39, 'SLAs', 'Define SLAs for data jobs', 28, 1),
(40, 'Pipeline Stages', 'Build/test/deploy gates', 29, 0),
(41, 'Rollback', 'Safe rollback strategies', 29, 1),
(42, 'K8s Objects', 'Deployments vs StatefulSets', 30, 0),
(43, 'Networking', 'Ingress vs Service', 30, 1),
(44, 'Tracing', 'When to trace spans', 31, 0),
(45, 'Logging', 'Structure logs for value', 31, 1),
(46, 'TNA', 'Define training needs', 32, 0),
(47, 'Stakeholders', 'Align on objectives', 32, 1),
(48, 'Facilitation', 'Engagement techniques', 33, 0),
(49, 'Scheduling', 'Cohort vs on-demand', 33, 1),
(50, 'Overfitting', 'Detect and prevent', 34, 0),
(51, 'Metrics', 'Choose metrics per task', 34, 1),
(52, 'Model Serving', 'Batch vs online', 35, 0),
(53, 'Monitoring ML', 'Data drift & alerts', 35, 1),
(54, 'Leakage', 'Find and avoid leakage', 36, 0),
(55, 'Encoding', 'Categorical encoding choices', 36, 1),
(56, 'Bounded Contexts', 'Identify contexts', 37, 0),
(57, 'Service Boundaries', 'Split monolith safely', 37, 1),
(58, 'Profiling', 'Find hotspots', 38, 0),
(59, 'Caching Layers', 'DB vs app cache', 38, 1),
(60, 'Code Reviews', 'Set standards & coach', 39, 0),
(61, 'Roadmapping', 'Plan & communicate', 39, 1),
(62, 'GDPR Basics', 'Data requests handling', 40, 0),
(63, 'Records', 'Maintain employee files', 40, 1),
(64, 'Ticket Prioritization', 'SLA-based triage', 41, 0),
(65, 'Empathy', 'Handle sensitive cases', 41, 1),
(66, 'Formulas', 'VLOOKUP/XLOOKUP/INDEX-MATCH', 42, 0),
(67, 'Pivot Tables', 'Summarize datasets', 42, 1),
(68, 'Joins', 'Inner vs left with examples', 43, 0),
(69, 'Aggregation', 'GROUP BY & HAVING', 43, 1),
(70, 'Semantics', 'Accessible HTML', 44, 0),
(71, 'Layout', 'Flexbox vs Grid', 44, 1),
(72, 'Props vs State', 'Identify usage', 45, 0),
(73, 'Lists/Keys', 'Render lists correctly', 45, 1),
(74, 'Tools', 'ATS/CRM usage', 46, 0),
(75, 'Async', 'Status updates cadence', 46, 1),
(76, 'Intake', 'Clarify must-haves', 47, 0),
(77, 'Reporting', 'Progress & metrics', 47, 1),
(78, 'Validation', 'Detect anomalies', 48, 0),
(79, 'Standardization', 'Normalize inputs', 48, 1),
(80, 'Retries', 'Backoff strategies', 49, 0),
(81, 'Scheduling', 'Daily vs event-driven', 49, 1),
(82, 'SLOs', 'Define and track', 50, 0),
(83, 'Error Budgets', 'Use in decisions', 50, 1),
(84, 'Runbooks', 'Create effective runbooks', 51, 0),
(85, 'Postmortems', 'Blameless approach', 51, 1),
(86, 'Load Testing', 'Plan & execute', 52, 0),
(87, 'Capacity', 'Forecast & scale', 52, 1),
(88, 'KPIs', 'Define learning KPIs', 53, 0),
(89, 'ROI', 'Measure training impact', 53, 1),
(90, 'Curricula', 'Learning paths', 54, 0),
(91, 'Materials', 'Create reusable assets', 54, 1),
(92, 'Lit Review', 'Find & assess papers', 55, 0),
(93, 'Experiment Design', 'Hypotheses & controls', 55, 1),
(94, 'Architectures', 'CNN/RNN/Transformers', 56, 0),
(95, 'Regularization', 'Dropout/weight decay', 56, 1),
(96, 'Reproducibility', 'Replication plan', 57, 0),
(97, 'Critique', 'Assess assumptions', 57, 1);

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
(27, 'Artificial Intelligence', 'ai000', 0),
(28, 'Communication',        'comm001', 0),
(29, 'Problem Solving',      'prob001', 0),
(30, 'Conflict Management',  'conf001', 0);

-- === question_skill ===
INSERT INTO question_skill (question_id, skill_id) VALUES
(13, 16),
(14,17),(15,28),(15,30),
(16,16),(16,17),(17,16),
(18,24),(18,25),(19,24),
(20,18),(20,26),(21,18),
(22,20),(22,24),(23,20),
(24,20),(24,24),(25,24),
(26,20),(26,21),(27,20),
(28,25),(28,24),(29,25),
(30,25),(30,24),(31,24),
(32,25),(32,24),(33,24),
(34,18),(34,26),(35,26),
(36,18),(36,26),(37,21),
(38,21),(38,26),(39,21),
(40,21),(40,24),(41,21),
(42,22),(42,21),(43,22),
(44,21),(44,22),(45,21),
(46,24),(46,23),(47,24),
(48,24),(48,23),(49,23),
(50,27),(50,26),(51,27),
(52,27),(52,21),(53,27),
(54,26),(54,27),(55,26),
(56,17),(56,16),(57,16),
(58,16),(58,21),(59,16),
(60,24),(61,24),
(62,24),(62,23),(63,24),
(64,24),(65,24),
(66,23),(66,26),(67,23),
(68,18),(69,18),
(70,20),(71,20),
(72,20),(73,20),
(74,25),(74,24),(75,24),
(76,25),(77,25),
(78,26),(78,18),(79,26),
(80,26),(81,26),
(82,21),(82,22),(83,21),
(84,21),(84,24),(85,24),
(86,21),(87,21),
(88,24),(89,24),
(90,24),(91,23),
(92,27),(92,23),(93,27),
(94,27),(95,27),
(96,27),(97,27);

-- === CANDIDATE_SKILL_SCORE ===

--INSERT INTO skill_score (candidate_id, question_id, skill_id, score) VALUES
---- JobAd 8 -> Interview 7
--(10, 13, 16, 86), (10, 14, 17, 84), (10, 15, 28, 82), (10, 15, 30, 80),
--(11, 13, 16, 75), (11, 14, 17, 66),
--(12, 13, 16, 52), (12, 14, 17, 48), (12, 15, 28, 45),
---- JobAd 9 -> Interview 8
--(13, 18, 24, 82), (13, 18, 25, 84),
--(14, 18, 24, 68),
---- JobAd 10 -> Interview 9
--(15, 20, 18, 88), (15, 20, 26, 85),
--(16, 20, 18, 54),
---- JobAd 11 -> Interview 10
--(17, 22, 20, 72),
--(18, 22, 20, 49),
---- JobAd 12 -> Interview 11
--(19, 28, 25, 85), (19, 28, 24, 82),
--(20, 28, 25, 63),
---- JobAd 13 -> Interview 12
--(21, 34, 18, 86), (21, 34, 26, 83), (21, 37, 21, 81),
--(22, 34, 18, 51), (22, 37, 21, 47),
---- JobAd 14 -> Interview 13
--(23, 40, 21, 87), (23, 40, 24, 84), (23, 42, 22, 85),
--(24, 40, 21, 69),
---- JobAd 15 -> Interview 14
--(25, 46, 24, 85), (25, 46, 23, 83),
--(26, 46, 24, 70),
---- JobAd 16 -> Interview 15
--(27, 50, 27, 88), (27, 50, 26, 84),
--(28, 50, 27, 53),
---- JobAd 17 -> Interview 16
--(29, 56, 16, 77), (29, 60, 24, 68),
--(30, 58, 21, 71),
---- JobAd 18 -> Interview 17
--(31, 62, 24, 84), (31, 62, 23, 82),
--(32, 62, 24, 49),
---- JobAd 19 -> Interview 18
--(34, 66, 23, 94), (34, 66, 26, 92), (34, 68, 18, 90),
--(33, 66, 23, 62), (33, 68, 18, 58),
---- JobAd 20 -> Interview 19
--(35, 70, 20, 73),
--(36, 70, 20, 46),
---- JobAd 21 -> Interview 20
--(38, 76, 25, 93), (38, 75, 24, 90),
--(37, 76, 25, 85), (37, 75, 24, 82),
---- JobAd 22 -> Interview 21
--(39, 78, 26, 86), (39, 78, 18, 84),
--(40, 78, 26, 52),
---- JobAd 23 -> Interview 22
--(48, 82, 21, 92), (48, 82, 22, 90),
--(41, 82, 21, 85), (41, 82, 22, 83),
--(42, 82, 21, 71),
---- JobAd 24 -> Interview 23
--(44, 88, 24, 84), (44, 91, 23, 82),
--(43, 88, 24, 69),
---- JobAd 25 -> Interview 24
--(47, 92, 27, 93), (47, 92, 23, 88),
--(45, 92, 27, 86), (45, 92, 23, 82),
--(46, 92, 27, 52);
INSERT INTO skill_score (candidate_id, question_id, skill_id, score) VALUES
-- JobAd 8 -> Interview 7
-- Approved 10: συμπλήρωση System Design (Q16, Q17)
(10, 16, 16, 85), (10, 16, 17, 83), (10, 17, 16, 84),
-- Rejected 12: περισσότερα & χαμηλά
(12, 15, 30, 42), (12, 16, 16, 40), (12, 17, 16, 28),

-- JobAd 9 -> Interview 8
-- Approved 13: Q19 έλειπε
(13, 19, 24, 81),

-- JobAd 10 -> Interview 9
-- Approved 15: Q21 έλειπε
(15, 21, 18, 86),
-- Rejected 16: περισσότερα & χαμηλά
(16, 21, 18, 52), (16, 20, 26, 10),

-- JobAd 11 -> Interview 10
-- Rejected 18: περισσότερα & χαμηλά
(18, 22, 24, 45), (18, 23, 20, 47),

-- JobAd 12 -> Interview 11
-- Approved 19: συμπληρώνω όλες τις ερωτήσεις/skills του interview
(19, 29, 25, 83),
(19, 30, 25, 84), (19, 30, 24, 82),
(19, 31, 24, 81),
(19, 32, 25, 83),
(19, 33, 24, 82),

-- JobAd 13 -> Interview 12
-- Approved 21: συμπλήρωση όλων
(21, 35, 26, 82),
(21, 36, 18, 83),
(21, 38, 21, 80),
(21, 39, 21, 79),
-- Rejected 22: ένα ακόμη χαμηλό
(22, 35, 26, 32),

-- JobAd 14 -> Interview 13
-- Approved 23: συμπλήρωση όλων
(23, 41, 21, 82),
(23, 44, 21, 82),
(23, 45, 21, 81),

-- JobAd 15 -> Interview 14
-- Approved 25: συμπλήρωση όλων
(25, 47, 24, 83),
(25, 48, 24, 82),
(25, 49, 23, 81),

-- JobAd 16 -> Interview 15
-- Approved 27: συμπλήρωση όλων
(27, 51, 27, 86),
(27, 52, 27, 85), (27, 52, 21, 82),
(27, 53, 27, 84),
(27, 54, 26, 83), (27, 54, 27, 85),
(27, 55, 26, 82),
-- Rejected 28: ένα ακόμη χαμηλό
(28, 52, 27, 27),

-- JobAd 17 -> Interview 16
-- (Pending μόνο – καμία αλλαγή)

-- JobAd 18 -> Interview 17
-- Approved 31: συμπλήρωση όλων
(31, 63, 24, 81),
(31, 64, 24, 80),
(31, 65, 24, 79),
-- Rejected 32: ένα ακόμη χαμηλό
(32, 63, 24, 15),

-- JobAd 19 -> Interview 18
-- Hired 34: συμπλήρωση Pivot & Aggregation
(34, 67, 23, 93),
(34, 69, 18, 91),

-- JobAd 20 -> Interview 19
-- Rejected 36: λίγο ακόμη, χαμηλά
(36, 71, 20, 25),

-- JobAd 21 -> Interview 20
-- Hired 38: όλες οι ερωτήσεις (74 & 77 έλειπαν)
(38, 74, 25, 92), (38, 74, 24, 90), (38, 77, 25, 91),
-- Approved 37: όλες με ελαφρώς χαμηλότερα από τον Hired
(37, 74, 25, 80), (37, 74, 24, 78), (37, 77, 25, 81),

-- JobAd 22 -> Interview 21
-- Approved 39: συμπλήρωση όλων
(39, 79, 26, 84),
(39, 80, 26, 83),
(39, 81, 26, 82),
-- Rejected 40: ένα ακόμη χαμηλό
(40, 79, 26, 10),

-- JobAd 23 -> Interview 22
-- Hired 48: συμπλήρωση 83–87
(48, 83, 21, 91),
(48, 84, 21, 89), (48, 84, 24, 90),
(48, 85, 24, 88),
(48, 86, 21, 90),
(48, 87, 21, 89),
-- Approved 41: συμπλήρωση 83–87 με καλές τιμές
(41, 83, 21, 82),
(41, 84, 21, 81), (41, 84, 24, 80),
(41, 85, 24, 79),
(41, 86, 21, 81),
(41, 87, 21, 80),

-- JobAd 24 -> Interview 23
-- Approved 44: συμπλήρωση 89 και 90
(44, 89, 24, 80),
(44, 90, 24, 79),

-- JobAd 25 -> Interview 24
-- Hired 47: συμπλήρωση 93–97
(47, 93, 27, 94),
(47, 94, 27, 95),
(47, 95, 27, 94),
(47, 96, 27, 93),
(47, 97, 27, 93),
-- Approved 45: συμπλήρωση 93–97 (λίγο χαμηλότερα)
(45, 93, 27, 84),
(45, 94, 27, 85),
(45, 95, 27, 84),
(45, 96, 27, 83),
(45, 97, 27, 83),
-- Rejected 46: ένα ακόμη χαμηλό
(46, 93, 27, 20);

-- === EXTRA DEPARTMENTS (no job ads) ===
INSERT INTO department (id, name, location, description, organisation_id) VALUES
(7,  'Product',            'Athens',       'Product management & discovery',           3),
(8,  'Quality Assurance',  'Thessaloniki', 'Manual & automated testing',               3),
(9,  'IT Support',         'Athens',       'Helpdesk, device mgmt & internal tools',   3),
(10, 'Security',           'Athens',       'AppSec, SecOps, compliance',               3),
(11, 'Research',           'Patra',        'Applied research & prototyping',           3),
(12, 'Marketing',          'Remote',       'Tech marketing & communications',          3);

-- === EXTRA OCCUPATIONS (no job ads) ===
INSERT INTO occupation (id, title, esco_id) VALUES
(15, 'Product Manager',           'esco_9901'),
(16, 'QA Engineer',               'esco_9902'),
(17, 'UX/UI Designer',            'esco_9903'),
(18, 'IT Support Specialist',     'esco_9904'),
(19, 'Security Analyst',          'esco_9905'),
(20, 'Cloud Architect',           'esco_9906'),
(21, 'Database Administrator',    'esco_9907'),
(22, 'Business Analyst',          'esco_9908');


-- TOP-UP: συμπλήρωσε Ο,ΤΙ λείπει για Approved/Hired ώστε ΟΛΑ τα skills να είναι rated
-- (Χωρίς διπλοεγγραφές: εισάγει μόνο όσα δεν υπάρχουν)
INSERT INTO skill_score (candidate_id, question_id, skill_id, score, comment)
SELECT
    c.id                                        AS candidate_id,
    q.id                                        AS question_id,
    qs.skill_id                                 AS skill_id,
    /* προαιρετικά CAST για να ταιριάζει με INTEGER */
    CAST(
        CASE
            WHEN c.status = 'Hired'    THEN 90 + MOD(q.id + c.id, 8)   -- 90–97
            WHEN c.status = 'Approved' THEN 82 + MOD(q.id + c.id, 8)   -- 82–89
            ELSE 0
        END
    AS INT)                                     AS score,
    NULL                                        AS comment
FROM candidate c
JOIN job_ad ja         ON c.job_ad_id = ja.id
JOIN interview i       ON ja.interview_id = i.id
JOIN step s            ON s.interview_id = i.id
JOIN question q        ON q.step_id = s.id
JOIN question_skill qs ON qs.question_id = q.id
WHERE c.status IN ('Approved','Hired')
  AND NOT EXISTS (
        SELECT 1
        FROM skill_score ss
        WHERE ss.candidate_id = c.id
          AND ss.question_id  = q.id
          AND ss.skill_id     = qs.skill_id
  );

-- === H2: bump sequences so new rows won't collide ===
-- (ΠΡΕΠΕΙ να είναι στο τέλος του αρχείου)

ALTER SEQUENCE IF EXISTS candidate_sequence        RESTART WITH 1000;
ALTER SEQUENCE IF EXISTS interview_report_sequence RESTART WITH 1000;

-- === Minimal fixes (append after your INSERTs) ===

-- 1) JobAds με candidates σε "Pending" -> "Published" για συνέπεια
UPDATE job_ad SET status = 'Published' WHERE id IN (18, 20, 22, 24);

-- 2) Unique email για τη Maria (SRE Hired, id=48)
UPDATE candidate
SET email = 'maria.p.sre@example.com'
WHERE id = 48;

-- 3) Ρεαλιστικά comments για Hired υποψηφίους (χωρίς «περίεργες» παρατηρήσεις)
UPDATE candidate
SET comments = 'Strong SRE fundamentals; incident response under pressure; ownership mindset'
WHERE id = 48;  -- SRE (JobAd 23)

UPDATE candidate
SET comments = 'Published papers; solid DL implementations; reproducible experiments'
WHERE id = 47;  -- AI Research (JobAd 25)
