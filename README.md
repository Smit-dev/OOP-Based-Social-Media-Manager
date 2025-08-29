# OOP-Based Social Media Manager

Console-based Java application demonstrating OOP design (inheritance, interfaces, composition), scheduling, analytics, and JFreeChart chart generation for two simulated platforms: Instagram & X.

## 1. Features
* Role-based access (Admin, Content Creator, Market Analyst)
* Post creation / deletion / commenting
* Follow / unfollow with follower/following updates
* Post Scheduling (queued & auto-published when due)
* Analytics engine (activity logs, trend + growth analysis)
* Report summaries (overview, growth trends, performance)
* JFreeChart line charts saved to `charts/`
* CSV-based lightweight persistence (no external DB)
* Single launcher script `START.bat`

## 2. Architecture Overview
```
socialmedia/
  User (abstract)
  Admin
  ContentCreator
  MarketAnalyst
  SocialMediaActions (interface)
  Profile (+ nested Post)
  PostScheduler / ScheduledPost
  AnalyticsEngine
  MarketAnalysis + InstagramAnalysis + XAnalysis
  SocialMediaManager (main)
```

### Design Highlights
| Concern | Approach | Rationale |
|---------|----------|-----------|
| Roles | Inheritance (`User` subclasses) | Shared core + specialized behavior |
| Actions | Interface (`SocialMediaActions`) | Clear contract & polymorphism |
| Scheduling | `Timer` + CSV queue | Simple & interview-friendly |
| Analytics | Aggregation + simple trend math | Demonstrates extensibility |
| Persistence | CSV files | Zero external dependencies |

## 3. Data Files
| File | Purpose | Commit? |
|------|---------|---------|
| profiles/user_auth.csv | Credentials + roles | NO (use sample) |
| profiles/user_auth.sample.csv | Template credentials | YES |
| profiles/profiles.csv | Platform stats & posts | YES (demo) |
| profiles/creator_data.csv | Creator sponsorship data | YES |
| profiles/logs/analytics_data.csv | Event log | NO |
| profiles/scheduled_posts.csv | Pending scheduled posts | NO |

Create a working auth file from sample (Windows):
```
copy profiles\user_auth.sample.csv profiles\user_auth.csv
```

## 4. Build & Run
Windows (already scripted):
```
START.bat
```
Manual commands:
```
javac -cp "lib/*" -d bin src\socialmedia\*.java
java  -cp "bin;lib/*" socialmedia.SocialMediaManager
```
Linux / macOS:
```
javac -cp "lib/*" -d bin src/socialmedia/*.java
java  -cp "bin:lib/*" socialmedia.SocialMediaManager
```

## 5. Roles
| Role | Code | Capabilities |
|------|------|--------------|
| Admin | 1 | Full management + scheduling + analytics/reports |
| Content Creator | 2 | Posting + brand/sponsor mgmt (inherits admin posting/report features) |
| Market Analyst | 3 | Analytics & charts only |

## 6. Scheduling Flow
1. User schedules content -> row in `scheduled_posts.csv`.
2. `PostScheduler` scans due rows.
3. If due: publish post, persist updates, remove from queue.

## 7. Analytics & Reports
`AnalyticsEngine` aggregates actions; growth = delta between earliest & latest snapshot. JFreeChart exports JPEG trend charts to `charts/`.

## 8. Extensibility Ideas
* Replace CSV with SQLite or embedded DB
* Add enums for platforms/roles
* Dependency injection & repository layer
* Switch Timer to ScheduledExecutorService
* Password hashing (BCrypt) + input sanitization
* Export analytics via REST or JSON API

## 9. Security
Plaintext passwords only for demo. For production: hash + salt, restrict file permissions, validate inputs, atomic writes.

## 10. License
MIT (see LICENSE).

