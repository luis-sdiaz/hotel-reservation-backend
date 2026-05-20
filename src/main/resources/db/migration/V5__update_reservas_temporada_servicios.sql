ALTER TABLE reservas
    ADD COLUMN IF NOT EXISTS temporada_reserva VARCHAR(30) NOT NULL DEFAULT 'TEMPORADA_BAJA';

CREATE TABLE IF NOT EXISTS reserva_servicios_adicionales (
    reserva_id BIGINT NOT NULL,
    servicio_adicional_id BIGINT NOT NULL,
    PRIMARY KEY (reserva_id, servicio_adicional_id),
    CONSTRAINT fk_reserva_servicio_reserva
        FOREIGN KEY (reserva_id) REFERENCES reservas(id),
    CONSTRAINT fk_reserva_servicio_servicio
        FOREIGN KEY (servicio_adicional_id) REFERENCES servicios_adicionales(id)
);

CREATE INDEX IF NOT EXISTS idx_reserva_servicios_reserva
    ON reserva_servicios_adicionales(reserva_id);

CREATE INDEX IF NOT EXISTS idx_reserva_servicios_servicio
    ON reserva_servicios_adicionales(servicio_adicional_id);
