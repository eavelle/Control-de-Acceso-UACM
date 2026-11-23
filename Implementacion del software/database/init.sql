-- Base de datos para el Sistema de Control de Acceso UACM (Etapa 1).
-- Ejecutar como usuario root de MySQL/MariaDB:
--   mysql -u root -p < database/init.sql

CREATE DATABASE IF NOT EXISTS control_acceso_uacm
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'uacm'@'localhost' IDENTIFIED BY 'uacm123';
GRANT ALL PRIVILEGES ON control_acceso_uacm.* TO 'uacm'@'localhost';
FLUSH PRIVILEGES;
