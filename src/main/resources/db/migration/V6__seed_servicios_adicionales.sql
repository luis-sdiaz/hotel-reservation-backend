INSERT INTO servicios_adicionales (nombre, descripcion, precio, activo)
SELECT 'DESAYUNO', 'Desayuno tipo buffet', 35000.00, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM servicios_adicionales WHERE nombre = 'DESAYUNO'
);

INSERT INTO servicios_adicionales (nombre, descripcion, precio, activo)
SELECT 'PARQUEADERO', 'Servicio de parqueadero por reserva', 20000.00, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM servicios_adicionales WHERE nombre = 'PARQUEADERO'
);

INSERT INTO servicios_adicionales (nombre, descripcion, precio, activo)
SELECT 'SPA', 'Acceso a zona de spa', 80000.00, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM servicios_adicionales WHERE nombre = 'SPA'
);

INSERT INTO servicios_adicionales (nombre, descripcion, precio, activo)
SELECT 'TRANSPORTE', 'Transporte desde o hacia el aeropuerto', 60000.00, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM servicios_adicionales WHERE nombre = 'TRANSPORTE'
);
