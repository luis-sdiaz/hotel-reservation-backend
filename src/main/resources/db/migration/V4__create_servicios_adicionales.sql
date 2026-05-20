CREATE TABLE IF NOT EXISTS servicios_adicionales (
                                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                     nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
    );