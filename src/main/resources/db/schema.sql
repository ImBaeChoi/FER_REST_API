-- FER Database Schema
-- PostgreSQL

-- 1. Users
CREATE TABLE "Users" (
    "UserId"       VARCHAR(100)             NOT NULL,
    "Name"         VARCHAR(100)             NOT NULL,
    "PasswordHash" VARCHAR(255)             NOT NULL,
    "CreatedAt"    TIMESTAMP WITH TIME ZONE NOT NULL,
    "UpdatedAt"    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT "Users_pkey" PRIMARY KEY ("UserId")
);

-- 2. RefreshTokens
CREATE TABLE "RefreshTokens" (
    "Id"        BIGSERIAL                NOT NULL,
    "UserId"    VARCHAR(100)             NOT NULL,
    "Token"     VARCHAR(700)             NOT NULL,
    "ExpiresAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    "Revoked"   BOOLEAN                  NOT NULL DEFAULT FALSE,
    "CreatedAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT "RefreshTokens_pkey"  PRIMARY KEY ("Id"),
    CONSTRAINT "RefreshTokens_Token_key" UNIQUE ("Token"),
    CONSTRAINT "RefreshTokens_UserId_fkey" FOREIGN KEY ("UserId") REFERENCES "Users" ("UserId")
);

-- 3. Devices
CREATE TABLE "Devices" (
    "DeviceId"   BIGSERIAL                NOT NULL,
    "UserId"     VARCHAR(100)             NOT NULL,
    "DeviceType" VARCHAR(50)              NOT NULL,
    "CreatedAt"  TIMESTAMP WITH TIME ZONE NOT NULL,
    "UpdatedAt"  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT "Devices_pkey"        PRIMARY KEY ("DeviceId"),
    CONSTRAINT "Devices_UserId_fkey" FOREIGN KEY ("UserId") REFERENCES "Users" ("UserId")
);

-- 4. DriveSessions
CREATE TABLE "DriveSessions" (
    "DriveId"   BIGSERIAL                NOT NULL,
    "UserId"    VARCHAR(100)             NOT NULL,
    "DeviceId"  BIGINT                   NOT NULL,
    "StartedAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    "EndedAt"   TIMESTAMP WITH TIME ZONE,
    "CreatedAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT "DriveSessions_pkey"         PRIMARY KEY ("DriveId"),
    CONSTRAINT "DriveSessions_UserId_fkey"  FOREIGN KEY ("UserId")   REFERENCES "Users"   ("UserId"),
    CONSTRAINT "DriveSessions_DeviceId_fkey" FOREIGN KEY ("DeviceId") REFERENCES "Devices" ("DeviceId")
);

-- 5. EmotionTypes
CREATE TABLE "EmotionTypes" (
    "EmotionTypeId" SMALLINT    NOT NULL,
    "Name"          VARCHAR(20) NOT NULL,
    CONSTRAINT "EmotionTypes_pkey"     PRIMARY KEY ("EmotionTypeId"),
    CONSTRAINT "EmotionTypes_Name_key" UNIQUE ("Name")
);

-- 6. Emotions
CREATE TABLE "Emotions" (
    "EmotionId"     BIGSERIAL                NOT NULL,
    "UserId"        VARCHAR(100)             NOT NULL,
    "DeviceId"      BIGINT                   NOT NULL,
    "DriveId"       BIGINT,
    "EmotionTypeId" SMALLINT                 NOT NULL,
    "Confidence"    NUMERIC(4, 3)            NOT NULL,
    "Source"        VARCHAR(20)              NOT NULL,
    "ModelVersion"  VARCHAR(50)              NOT NULL,
    "OccurredAt"    TIMESTAMP WITH TIME ZONE NOT NULL,
    "CreatedAt"     TIMESTAMP WITH TIME ZONE NOT NULL,
    "Payload"       JSONB,
    CONSTRAINT "Emotions_pkey"              PRIMARY KEY ("EmotionId"),
    CONSTRAINT "Emotions_UserId_fkey"       FOREIGN KEY ("UserId")        REFERENCES "Users"        ("UserId"),
    CONSTRAINT "Emotions_DeviceId_fkey"     FOREIGN KEY ("DeviceId")      REFERENCES "Devices"      ("DeviceId"),
    CONSTRAINT "Emotions_DriveId_fkey"      FOREIGN KEY ("DriveId")       REFERENCES "DriveSessions" ("DriveId"),
    CONSTRAINT "Emotions_EmotionTypeId_fkey" FOREIGN KEY ("EmotionTypeId") REFERENCES "EmotionTypes" ("EmotionTypeId")
);

-- 7. EmotionReports
CREATE TABLE "EmotionReports" (
    "ReportId"    BIGSERIAL                NOT NULL,
    "UserId"      VARCHAR(100)             NOT NULL,
    "DriveId"     BIGINT,
    "WindowStart" TIMESTAMP WITH TIME ZONE NOT NULL,
    "WindowEnd"   TIMESTAMP WITH TIME ZONE NOT NULL,
    "SpecVersion" VARCHAR(50)              NOT NULL,
    "Summary"     JSONB                    NOT NULL,
    "GeneratedAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    "SourceHash"  VARCHAR(64),
    CONSTRAINT "EmotionReports_pkey"        PRIMARY KEY ("ReportId"),
    CONSTRAINT "EmotionReports_UserId_fkey" FOREIGN KEY ("UserId")  REFERENCES "Users"        ("UserId"),
    CONSTRAINT "EmotionReports_DriveId_fkey" FOREIGN KEY ("DriveId") REFERENCES "DriveSessions" ("DriveId")
);

-- 8. CoachingEvents
CREATE TABLE "CoachingEvents" (
    "CoachingId" BIGSERIAL                NOT NULL,
    "UserId"     VARCHAR(100)             NOT NULL,
    "EmotionId"  BIGINT,
    "Type"       VARCHAR(30)              NOT NULL,
    "Message"    TEXT                     NOT NULL,
    "Channel"    VARCHAR(20)              NOT NULL,
    "IssuedAt"   TIMESTAMP WITH TIME ZONE NOT NULL,
    "CreatedAt"  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT "CoachingEvents_pkey"          PRIMARY KEY ("CoachingId"),
    CONSTRAINT "CoachingEvents_UserId_fkey"   FOREIGN KEY ("UserId")    REFERENCES "Users"    ("UserId"),
    CONSTRAINT "CoachingEvents_EmotionId_fkey" FOREIGN KEY ("EmotionId") REFERENCES "Emotions" ("EmotionId")
);

-- 9. RelabelQueue
CREATE TABLE "RelabelQueue" (
    "QueueId"   BIGSERIAL                NOT NULL,
    "UserId"    VARCHAR(100)             NOT NULL,
    "EmotionId" BIGINT                   NOT NULL,
    "Reason"    VARCHAR(50)              NOT NULL,
    "Status"    VARCHAR(20)              NOT NULL,
    "CreatedAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT "RelabelQueue_pkey"          PRIMARY KEY ("QueueId"),
    CONSTRAINT "RelabelQueue_UserId_fkey"   FOREIGN KEY ("UserId")    REFERENCES "Users"    ("UserId"),
    CONSTRAINT "RelabelQueue_EmotionId_fkey" FOREIGN KEY ("EmotionId") REFERENCES "Emotions" ("EmotionId")
);

-- 10. BioSignals
CREATE TABLE "BioSignals" (
    "BioSignalId" BIGSERIAL                NOT NULL,
    "UserId"      VARCHAR(100)             NOT NULL,
    "DeviceId"    BIGINT                   NOT NULL,
    "DriveId"     BIGINT,
    "SignalType"  VARCHAR(20)              NOT NULL,
    "Value"       DOUBLE PRECISION         NOT NULL,
    "Unit"        VARCHAR(20)              NOT NULL,
    "MeasuredAt"  TIMESTAMP WITH TIME ZONE NOT NULL,
    "CreatedAt"   TIMESTAMP WITH TIME ZONE NOT NULL,
    "QualityFlag" VARCHAR(30),
    "Attr"        JSONB,
    CONSTRAINT "BioSignals_pkey"          PRIMARY KEY ("BioSignalId"),
    CONSTRAINT "BioSignals_UserId_fkey"   FOREIGN KEY ("UserId")   REFERENCES "Users"        ("UserId"),
    CONSTRAINT "BioSignals_DeviceId_fkey" FOREIGN KEY ("DeviceId") REFERENCES "Devices"      ("DeviceId"),
    CONSTRAINT "BioSignals_DriveId_fkey"  FOREIGN KEY ("DriveId")  REFERENCES "DriveSessions" ("DriveId")
);

-- 11. AuthSessions
CREATE TABLE "AuthSessions" (
    "SessionId" BIGSERIAL                NOT NULL,
    "UserId"    VARCHAR(100)             NOT NULL,
    "IssuedAt"  TIMESTAMP WITH TIME ZONE NOT NULL,
    "ExpiresAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    "IpAddress" VARCHAR(45),
    "UserAgent" TEXT,
    CONSTRAINT "AuthSessions_pkey"        PRIMARY KEY ("SessionId"),
    CONSTRAINT "AuthSessions_UserId_fkey" FOREIGN KEY ("UserId") REFERENCES "Users" ("UserId")
);

-- 12. MusicRecommendations
CREATE TABLE "MusicRecommendations" (
    "RecommendationId" BIGSERIAL                NOT NULL,
    "UserId"           VARCHAR(100)             NOT NULL,
    "EmotionId"        BIGINT,
    "DriveId"          BIGINT,
    "EmotionName"      VARCHAR(30)              NOT NULL,
    "Genre"            VARCHAR(50)              NOT NULL,
    "PlaylistId"       VARCHAR(100),
    "PlaylistName"     VARCHAR(255),
    "PlaylistUrl"      VARCHAR(500),
    "IssuedAt"         TIMESTAMP WITH TIME ZONE NOT NULL,
    "CreatedAt"        TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT "MusicRecommendations_pkey"          PRIMARY KEY ("RecommendationId"),
    CONSTRAINT "MusicRecommendations_UserId_fkey"   FOREIGN KEY ("UserId")    REFERENCES "Users"        ("UserId"),
    CONSTRAINT "MusicRecommendations_EmotionId_fkey" FOREIGN KEY ("EmotionId") REFERENCES "Emotions"     ("EmotionId"),
    CONSTRAINT "MusicRecommendations_DriveId_fkey"  FOREIGN KEY ("DriveId")   REFERENCES "DriveSessions" ("DriveId")
);

-- EmotionTypes 기본 데이터
INSERT INTO "EmotionTypes" ("EmotionTypeId", "Name") VALUES
    (0, 'neutral'),
    (1, 'happy'),
    (2, 'sad'),
    (3, 'angry'),
    (4, 'fear'),
    (5, 'disgust'),
    (6, 'surprise'),
    (7, 'contempt'),
    (8, 'drowsy');
