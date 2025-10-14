-- ==========================
-- TABELAS PRINCIPAIS
-- ==========================

-- Usuário
CREATE TABLE Usuario (
    usuarioId   NUMBER PRIMARY KEY,
    cpf         CHAR(11) UNIQUE NOT NULL,
    nome        VARCHAR2(100) NOT NULL,
    sobrenome   VARCHAR2(100),
    username    VARCHAR2(50) UNIQUE NOT NULL,
    email       VARCHAR2(150) UNIQUE NOT NULL,
    cargo       VARCHAR2(100),
    deletado    CHAR(1) DEFAULT 'N' CHECK (deletado IN ('S','N'))
);

-- Região
CREATE TABLE Regiao (
    regiaoId    NUMBER PRIMARY KEY,
    nome        VARCHAR2(100) NOT NULL,
    deletado    CHAR(1) DEFAULT 'N' CHECK (deletado IN ('S','N'))
);

CREATE TABLE RegiaoPoligono (
    regiaoId NUMBER NOT NULL,
    pontoId NUMBER NOT NULL,
    coordX NUMBER NOT NULL,
    coordY NUMBER NOT NULL,
    PRIMARY KEY (regiaoId, pontoId),
    FOREIGN KEY (regiaoId) REFERENCES Regiao(regiaoId)
);

-- Radar
CREATE TABLE Radar (
    radarId             VARCHAR2(100) PRIMARY KEY,
    regiaoId            NUMBER,
    latitude            NUMBER(9,6) NOT NULL,
    longitude           NUMBER(9,6) NOT NULL,
    endereco            VARCHAR2(200),
    velocidadePermitida NUMBER(3) NOT NULL,
    deletado            CHAR(1) DEFAULT 'N' CHECK (deletado IN ('S','N')),
    CONSTRAINT fk_radar_regiao FOREIGN KEY (regiaoId)
        REFERENCES Regiao(regiaoId)
);

-- Registro de Velocidade
CREATE TABLE RegistroVelocidade (
    registroVelocidadeId NUMBER PRIMARY KEY,
    radarId              VARCHAR2(100),
    regiaoId             NUMBER,
    tipoVeiculo          VARCHAR2(50),
    velocidadeRegistrada NUMBER(3) NOT NULL,
    data                 DATE DEFAULT SYSDATE NOT NULL,
    deletado             CHAR(1) DEFAULT 'N' CHECK (deletado IN ('S','N')),
    CONSTRAINT fk_registro_radar FOREIGN KEY (radarId)
        REFERENCES Radar(radarId),
    CONSTRAINT fk_registro_regiao FOREIGN KEY (regiaoId)
        REFERENCES Regiao(regiaoId)
);

-- Indicador
CREATE TABLE Indicador (
    indicadorId NUMBER PRIMARY KEY,
    nome        VARCHAR2(100) NOT NULL,
    mnemonico   VARCHAR2(100),
    descricao   VARCHAR2(255),
    usuarioId   NUMBER,
    deletado    CHAR(1) DEFAULT 'N' CHECK (deletado IN ('S','N')),
    CONSTRAINT fk_indicador_usuario FOREIGN KEY (usuarioId)
        REFERENCES Usuario(usuarioId)
);

-- Evento
CREATE TABLE Evento (
    eventoId     NUMBER PRIMARY KEY,
    indicadorId  NUMBER NOT NULL,
    nome         VARCHAR2(100) NOT NULL,
    data         DATE DEFAULT SYSDATE NOT NULL,
    descricao    VARCHAR2(255),
    usuarioId    NUMBER,
    deletado     CHAR(1) DEFAULT 'N' CHECK (deletado IN ('S','N')),
    CONSTRAINT fk_evento_indicador FOREIGN KEY (indicadorId)
        REFERENCES Indicador(indicadorId),
    CONSTRAINT fk_evento_usuario FOREIGN KEY (usuarioId)
        REFERENCES Usuario(usuarioId)
);

-- Relação N:N entre Evento e Indicador
CREATE TABLE EventoIndicador (
    eventoId     NUMBER NOT NULL,
    indicadorId  NUMBER NOT NULL,
    CONSTRAINT pk_evento_indicador PRIMARY KEY (eventoId, indicadorId),
    CONSTRAINT fk_eventoindic_evento FOREIGN KEY (eventoId)
        REFERENCES Evento(eventoId),
    CONSTRAINT fk_eventoindic_indicador FOREIGN KEY (indicadorId)
        REFERENCES Indicador(indicadorId)
);

-- ==========================
-- TIMELINES (LOGS)
-- ==========================

CREATE TABLE IndicadoresTimeline (
    timelineId   NUMBER PRIMARY KEY,
    indicadorId  NUMBER NOT NULL,
    usuarioId    NUMBER,
    acao         VARCHAR2(20) NOT NULL, -- CRIACAO, ALTERACAO, EXCLUSAO
    descricao    VARCHAR2(500),
    data         DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_timeline_indicador FOREIGN KEY (indicadorId)
        REFERENCES Indicador(indicadorId),
    CONSTRAINT fk_timeline_usuario FOREIGN KEY (usuarioId)
        REFERENCES Usuario(usuarioId)
);

CREATE TABLE EventosTimeline (
    timelineId   NUMBER PRIMARY KEY,
    eventoId     NUMBER NOT NULL,
    usuarioId    NUMBER,
    acao         VARCHAR2(20) NOT NULL,
    descricao    VARCHAR2(500),
    data         DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_timeline_evento FOREIGN KEY (eventoId)
        REFERENCES Evento(eventoId),
    CONSTRAINT fk_timeline_usuario_evento FOREIGN KEY (usuarioId)
        REFERENCES Usuario(usuarioId)
);

-- ==========================
-- SEQUÊNCIAS
-- ==========================
CREATE SEQUENCE seq_usuario START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_regiao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_radar START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_registro START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_indicador START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_evento START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_indicadores_timeline START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_eventos_timeline START WITH 1 INCREMENT BY 1;

-- ==========================
-- TRIGGERS DE AUTO-INCREMENTO
-- ==========================
CREATE OR REPLACE TRIGGER trg_usuario_pk
BEFORE INSERT ON Usuario
FOR EACH ROW
BEGIN
    IF :NEW.usuarioId IS NULL THEN
        SELECT seq_usuario.NEXTVAL INTO :NEW.usuarioId FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_regiao_pk
BEFORE INSERT ON Regiao
FOR EACH ROW
BEGIN
    IF :NEW.regiaoId IS NULL THEN
        SELECT seq_regiao.NEXTVAL INTO :NEW.regiaoId FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_radar_pk
BEFORE INSERT ON Radar
FOR EACH ROW
BEGIN
    IF :NEW.radarId IS NULL THEN
        SELECT seq_radar.NEXTVAL INTO :NEW.radarId FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_registro_pk
BEFORE INSERT ON RegistroVelocidade
FOR EACH ROW
BEGIN
    IF :NEW.registroVelocidadeId IS NULL THEN
        SELECT seq_registro.NEXTVAL INTO :NEW.registroVelocidadeId FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_indicador_pk
BEFORE INSERT ON Indicador
FOR EACH ROW
BEGIN
    IF :NEW.indicadorId IS NULL THEN
        SELECT seq_indicador.NEXTVAL INTO :NEW.indicadorId FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_evento_pk
BEFORE INSERT ON Evento
FOR EACH ROW
BEGIN
    IF :NEW.eventoId IS NULL THEN
        SELECT seq_evento.NEXTVAL INTO :NEW.eventoId FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_indicadores_timeline_pk
BEFORE INSERT ON IndicadoresTimeline
FOR EACH ROW
BEGIN
    IF :NEW.timelineId IS NULL THEN
        SELECT seq_indicadores_timeline.NEXTVAL INTO :NEW.timelineId FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_eventos_timeline_pk
BEFORE INSERT ON EventosTimeline
FOR EACH ROW
BEGIN
    IF :NEW.timelineId IS NULL THEN
        SELECT seq_eventos_timeline.NEXTVAL INTO :NEW.timelineId FROM dual;
    END IF;
END;
/

-- ==========================
-- TRIGGERS DE AUDITORIA
-- ==========================

-- Indicadores
CREATE OR REPLACE TRIGGER trg_indicador_log
AFTER INSERT OR UPDATE OR DELETE ON Indicador
FOR EACH ROW
DECLARE
    v_acao VARCHAR2(20);
BEGIN
    IF INSERTING THEN
        v_acao := 'CRIACAO';
    ELSIF UPDATING THEN
        v_acao := 'ALTERACAO';
    ELSIF DELETING THEN
        v_acao := 'EXCLUSAO';
    END IF;

    INSERT INTO IndicadoresTimeline (indicadorId, usuarioId, acao, descricao)
    VALUES (
        NVL(:NEW.indicadorId, :OLD.indicadorId),
        NVL(:NEW.usuarioId, :OLD.usuarioId),
        v_acao,
        'Alteração automática registrada por trigger'
    );
END;
/

-- Eventos
CREATE OR REPLACE TRIGGER trg_evento_log
AFTER INSERT OR UPDATE OR DELETE ON Evento
FOR EACH ROW
DECLARE
    v_acao VARCHAR2(20);
BEGIN
    IF INSERTING THEN
        v_acao := 'CRIACAO';
    ELSIF UPDATING THEN
        v_acao := 'ALTERACAO';
    ELSIF DELETING THEN
        v_acao := 'EXCLUSAO';
    END IF;

    INSERT INTO EventosTimeline (eventoId, usuarioId, acao, descricao)
    VALUES (
        NVL(:NEW.eventoId, :OLD.eventoId),
        NVL(:NEW.usuarioId, :OLD.usuarioId),
        v_acao,
        'Alteração automática registrada por trigger'
    );
END;
/

-- ==========================
-- ÍNDICES OPCIONAIS
-- ==========================
CREATE INDEX idx_radar_regiao ON Radar(regiaoId);
CREATE INDEX idx_registro_radar ON RegistroVelocidade(radarId);
CREATE INDEX idx_registro_regiao ON RegistroVelocidade(regiaoId);
CREATE INDEX idx_evento_indicador ON Evento(indicadorId);