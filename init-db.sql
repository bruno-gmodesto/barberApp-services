-- Script de inicialização do banco de dados
-- Cria os dois databases necessários para os microserviços

CREATE DATABASE IF NOT EXISTS barberApp_users;
CREATE DATABASE IF NOT EXISTS barberApp_schedules;

GRANT ALL PRIVILEGES ON barberApp_users.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON barberApp_schedules.* TO 'root'@'%';

FLUSH PRIVILEGES;
