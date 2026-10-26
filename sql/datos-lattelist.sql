-- =====================================================================
--  LatteList - Datos de ejemplo para MySQL Workbench
--  Cafeterías REALES de Mar del Plata. Usuarios, reseñas y listas inventados.
--
--  ANTES de correr este script:
--    1) Borrá DatosDemo.java del back (o poné app.datos-demo=false).
--    2) Arrancá el back UNA vez: Hibernate crea las tablas y se crea el admin.
--    3) Abrí este archivo en Workbench y ejecutalo completo (rayito).
--  Se puede correr varias veces: limpia los datos demo y los vuelve a cargar.
--  Contraseña de todos los usuarios demo: Demo1234
-- =====================================================================
USE lattelist;
SET SQL_SAFE_UPDATES = 0;
 
-- ---------- 0) Limpieza de datos demo (NO toca al admin) ----------
DELETE FROM lista_cafe WHERE lista_id IN (SELECT id FROM listas_de_cafes WHERE usuario_id IN (SELECT id FROM usuarios WHERE email LIKE '%@lattelist.com' AND tipo_de_usuario <> 'ADMIN'));
DELETE FROM lista_cafe WHERE cafe_id IN (SELECT id FROM cafes WHERE duenio_id IN (SELECT id FROM usuarios WHERE email LIKE '%@lattelist.com' AND tipo_de_usuario <> 'ADMIN'));
DELETE FROM listas_de_cafes WHERE usuario_id IN (SELECT id FROM usuarios WHERE email LIKE '%@lattelist.com' AND tipo_de_usuario <> 'ADMIN');
DELETE FROM resenas WHERE usuario_id IN (SELECT id FROM usuarios WHERE email LIKE '%@lattelist.com' AND tipo_de_usuario <> 'ADMIN')
   OR cafe_id IN (SELECT id FROM cafes WHERE duenio_id IN (SELECT id FROM usuarios WHERE email LIKE '%@lattelist.com' AND tipo_de_usuario <> 'ADMIN'));
DELETE FROM cafe_etiquetas WHERE cafe_id IN (SELECT id FROM cafes WHERE duenio_id IN (SELECT id FROM usuarios WHERE email LIKE '%@lattelist.com' AND tipo_de_usuario <> 'ADMIN'));
DELETE FROM cafes WHERE duenio_id IN (SELECT id FROM usuarios WHERE email LIKE '%@lattelist.com' AND tipo_de_usuario <> 'ADMIN');
DELETE FROM usuarios WHERE email LIKE '%@lattelist.com' AND tipo_de_usuario <> 'ADMIN';
 
-- ---------- 1) Usuarios (contraseña Demo1234, guardada con BCrypt) ----------
INSERT INTO usuarios (nombre, apellido, email, contrasena, tipo_de_usuario, fecha_alta) VALUES
('Martina', 'Quiroga', 'duenio1@lattelist.com', '$2a$10$xkNj2D5ulYBJlJ/qeDOQC.MV853dNHMHIXJLUhapaoGGnUtakS7gm', 'DUENIO', CURDATE()),
('Federico', 'Ledesma', 'duenio2@lattelist.com', '$2a$10$xkNj2D5ulYBJlJ/qeDOQC.MV853dNHMHIXJLUhapaoGGnUtakS7gm', 'DUENIO', CURDATE()),
('Lucía', 'Fernández', 'cliente1@lattelist.com', '$2a$10$xkNj2D5ulYBJlJ/qeDOQC.MV853dNHMHIXJLUhapaoGGnUtakS7gm', 'CLIENTE', CURDATE()),
('Tomás', 'Benítez', 'cliente2@lattelist.com', '$2a$10$xkNj2D5ulYBJlJ/qeDOQC.MV853dNHMHIXJLUhapaoGGnUtakS7gm', 'CLIENTE', CURDATE()),
('Camila', 'Ríos', 'cliente3@lattelist.com', '$2a$10$xkNj2D5ulYBJlJ/qeDOQC.MV853dNHMHIXJLUhapaoGGnUtakS7gm', 'CLIENTE', CURDATE()),
('Joaquín', 'Pereyra', 'cliente4@lattelist.com', '$2a$10$xkNj2D5ulYBJlJ/qeDOQC.MV853dNHMHIXJLUhapaoGGnUtakS7gm', 'CLIENTE', CURDATE());
 
-- ---------- 2) Cafés reales de Mar del Plata ----------
INSERT INTO cafes (nombre, direccion, costo_promedio, logo, instagram_url, duenio_id) VALUES
('Kersen', 'Garay 1755, Mar del Plata', '$$$', NULL, 'https://instagram.com/kersencafe', (SELECT id FROM usuarios WHERE email = 'duenio1@lattelist.com')),
('Dinette', 'Alvarado 1702, Mar del Plata', '$$$', NULL, 'https://instagram.com/muydinette', (SELECT id FROM usuarios WHERE email = 'duenio1@lattelist.com')),
('Niño Lobo', 'Roca 1244, Mar del Plata', '$$', NULL, NULL, (SELECT id FROM usuarios WHERE email = 'duenio1@lattelist.com')),
('ATC - Acá Tomamos Café', 'Rawson 2025, Mar del Plata', '$$', NULL, 'https://instagram.com/acatomamoscafe', (SELECT id FROM usuarios WHERE email = 'duenio1@lattelist.com')),
('Café Planta Baja', 'Rawson 1457, Mar del Plata', '$$', NULL, 'https://instagram.com/cafeplantabaja', (SELECT id FROM usuarios WHERE email = 'duenio1@lattelist.com')),
('No Coffee no Prana', '14 de Julio 2479, Mar del Plata', '$$', NULL, 'https://instagram.com/no_coffee__no__prana', (SELECT id FROM usuarios WHERE email = 'duenio2@lattelist.com')),
('Crumble Bakery', 'San Juan 540, Mar del Plata', '$$', NULL, 'https://instagram.com/crumblebakerymdp', (SELECT id FROM usuarios WHERE email = 'duenio2@lattelist.com')),
('Nina Pasticceria', 'Olavarría 2530, Mar del Plata', '$$', NULL, 'https://instagram.com/ninapasticceria', (SELECT id FROM usuarios WHERE email = 'duenio2@lattelist.com')),
('Atilio''s', 'Alvarado 2924, Mar del Plata', '$$', NULL, 'https://instagram.com/atiliosatilios', (SELECT id FROM usuarios WHERE email = 'duenio2@lattelist.com'));
 
-- ---------- 3) Etiquetas de cada café (tabla cafe_etiquetas) ----------
INSERT INTO cafe_etiquetas (cafe_id, etiqueta) VALUES
((SELECT id FROM cafes WHERE nombre = 'Kersen'), 'CAFE_ESPECIALIDAD'),
((SELECT id FROM cafes WHERE nombre = 'Kersen'), 'TOSTADO_PROPIO'),
((SELECT id FROM cafes WHERE nombre = 'Kersen'), 'BRUNCH'),
((SELECT id FROM cafes WHERE nombre = 'Kersen'), 'MINIMALISTA'),
((SELECT id FROM cafes WHERE nombre = 'Kersen'), 'ZONA_CENTRICA'),
((SELECT id FROM cafes WHERE nombre = 'Kersen'), 'WIFI'),
((SELECT id FROM cafes WHERE nombre = 'Kersen'), 'PAREJAS'),
((SELECT id FROM cafes WHERE nombre = 'Dinette'), 'CAFE_ESPECIALIDAD'),
((SELECT id FROM cafes WHERE nombre = 'Dinette'), 'PASTELERIA_FRANCESA'),
((SELECT id FROM cafes WHERE nombre = 'Dinette'), 'BRUNCH'),
((SELECT id FROM cafes WHERE nombre = 'Dinette'), 'TEMATICO'),
((SELECT id FROM cafes WHERE nombre = 'Dinette'), 'ZONA_CENTRICA'),
((SELECT id FROM cafes WHERE nombre = 'Dinette'), 'PAREJAS'),
((SELECT id FROM cafes WHERE nombre = 'Niño Lobo'), 'CAFE_ESPECIALIDAD'),
((SELECT id FROM cafes WHERE nombre = 'Niño Lobo'), 'ARTE'),
((SELECT id FROM cafes WHERE nombre = 'Niño Lobo'), 'ZONA_CENTRICA'),
((SELECT id FROM cafes WHERE nombre = 'Niño Lobo'), 'ESTUDIANTES'),
((SELECT id FROM cafes WHERE nombre = 'Niño Lobo'), 'TEMATICO'),
((SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café'), 'CAFE_ESPECIALIDAD'),
((SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café'), 'BRUNCH'),
((SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café'), 'VINTAGE'),
((SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café'), 'TEMATICO'),
((SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café'), 'PET_FRIENDLY'),
((SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café'), 'VEGANO'),
((SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café'), 'PATIO'),
((SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café'), 'ESTUDIANTES'),
((SELECT id FROM cafes WHERE nombre = 'Café Planta Baja'), 'CAFE_ESPECIALIDAD'),
((SELECT id FROM cafes WHERE nombre = 'Café Planta Baja'), 'ESTILO_COWORKING'),
((SELECT id FROM cafes WHERE nombre = 'Café Planta Baja'), 'WIFI'),
((SELECT id FROM cafes WHERE nombre = 'Café Planta Baja'), 'ENCHUFES_DISPONIBLES'),
((SELECT id FROM cafes WHERE nombre = 'Café Planta Baja'), 'ZONA_CENTRICA'),
((SELECT id FROM cafes WHERE nombre = 'Café Planta Baja'), 'ESTUDIANTES'),
((SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana'), 'CAFE_ESPECIALIDAD'),
((SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana'), 'VEGANO'),
((SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana'), 'VEGETARIANO'),
((SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana'), 'SIN_TACC'),
((SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana'), 'ORGANICO'),
((SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana'), 'PET_FRIENDLY'),
((SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana'), 'PATIO'),
((SELECT id FROM cafes WHERE nombre = 'Crumble Bakery'), 'CAFE_ESPECIALIDAD'),
((SELECT id FROM cafes WHERE nombre = 'Crumble Bakery'), 'BRUNCH'),
((SELECT id FROM cafes WHERE nombre = 'Crumble Bakery'), 'PASTELERIA_FRANCESA'),
((SELECT id FROM cafes WHERE nombre = 'Crumble Bakery'), 'FAMILIAR'),
((SELECT id FROM cafes WHERE nombre = 'Crumble Bakery'), 'ESTUDIANTES'),
((SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria'), 'CAFE_COMERCIAL'),
((SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria'), 'PASTELERIA_ITALIANA'),
((SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria'), 'TERRAZA'),
((SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria'), 'FAMILIAR'),
((SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria'), 'PAREJAS'),
((SELECT id FROM cafes WHERE nombre = 'Atilio''s'), 'BRUNCH'),
((SELECT id FROM cafes WHERE nombre = 'Atilio''s'), 'VEGETARIANO'),
((SELECT id FROM cafes WHERE nombre = 'Atilio''s'), 'CAFE_COMERCIAL'),
((SELECT id FROM cafes WHERE nombre = 'Atilio''s'), 'ZONA_CENTRICA'),
((SELECT id FROM cafes WHERE nombre = 'Atilio''s'), 'GRUPOS_GRANDES');
 
-- ---------- 4) Reseñas (de los clientes sobre los cafés) ----------
INSERT INTO resenas (puntuacion_precio, puntuacion_atencion, puntuacion_general, comentario, fecha, usuario_id, cafe_id) VALUES
(4, 5, 5, 'Gran variedad y precios razonables.', DATE_SUB(CURDATE(), INTERVAL 39 DAY), (SELECT id FROM usuarios WHERE email = 'cliente1@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Kersen')),
(3, 5, 5, 'Ideal para ir a estudiar un rato.', DATE_SUB(CURDATE(), INTERVAL 0 DAY), (SELECT id FROM usuarios WHERE email = 'cliente2@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Kersen')),
(3, 2, 3, 'Me encantó el lugar, súper recomendable.', DATE_SUB(CURDATE(), INTERVAL 0 DAY), (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Kersen')),
(4, 5, 4, 'La pastelería es increíble.', DATE_SUB(CURDATE(), INTERVAL 22 DAY), (SELECT id FROM usuarios WHERE email = 'cliente1@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Dinette')),
(3, 2, 3, 'Tardaron un poco en atender pero el café estaba muy rico.', DATE_SUB(CURDATE(), INTERVAL 34 DAY), (SELECT id FROM usuarios WHERE email = 'cliente2@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Niño Lobo')),
(5, 5, 5, 'Excelente café, volvería sin dudas.', DATE_SUB(CURDATE(), INTERVAL 54 DAY), (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Niño Lobo')),
(2, 5, 4, 'Muy buena atención y lindo ambiente.', DATE_SUB(CURDATE(), INTERVAL 25 DAY), (SELECT id FROM usuarios WHERE email = 'cliente4@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Niño Lobo')),
(1, 4, 3, 'El café es correcto, nada del otro mundo.', DATE_SUB(CURDATE(), INTERVAL 58 DAY), (SELECT id FROM usuarios WHERE email = 'cliente2@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café')),
(3, 4, 5, 'Gran variedad y precios razonables.', DATE_SUB(CURDATE(), INTERVAL 14 DAY), (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café')),
(1, 4, 3, 'Un poco caro pero vale la pena.', DATE_SUB(CURDATE(), INTERVAL 30 DAY), (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Café Planta Baja')),
(3, 5, 5, 'Tardaron un poco en atender pero el café estaba muy rico.', DATE_SUB(CURDATE(), INTERVAL 25 DAY), (SELECT id FROM usuarios WHERE email = 'cliente2@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana')),
(2, 5, 4, 'El café es correcto, nada del otro mundo.', DATE_SUB(CURDATE(), INTERVAL 12 DAY), (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana')),
(3, 3, 4, 'Un poco caro pero vale la pena.', DATE_SUB(CURDATE(), INTERVAL 41 DAY), (SELECT id FROM usuarios WHERE email = 'cliente4@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana')),
(4, 5, 4, 'Me encantó el lugar, súper recomendable.', DATE_SUB(CURDATE(), INTERVAL 32 DAY), (SELECT id FROM usuarios WHERE email = 'cliente1@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria')),
(4, 5, 5, 'Buen lugar para ir en pareja.', DATE_SUB(CURDATE(), INTERVAL 29 DAY), (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria')),
(5, 5, 5, 'Un poco caro pero vale la pena.', DATE_SUB(CURDATE(), INTERVAL 27 DAY), (SELECT id FROM usuarios WHERE email = 'cliente1@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Atilio''s')),
(3, 4, 3, 'Buen lugar para ir en pareja.', DATE_SUB(CURDATE(), INTERVAL 6 DAY), (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Atilio''s')),
(5, 5, 5, 'Excelente café, volvería sin dudas.', DATE_SUB(CURDATE(), INTERVAL 5 DAY), (SELECT id FROM usuarios WHERE email = 'cliente4@lattelist.com'), (SELECT id FROM cafes WHERE nombre = 'Atilio''s'));
 
-- ---------- 5) Listas de los clientes (Favoritos + una propia) ----------
INSERT INTO listas_de_cafes (nombre, fecha_creacion, favoritos, usuario_id) VALUES
('Favoritos', CURDATE(), 1, (SELECT id FROM usuarios WHERE email = 'cliente1@lattelist.com')),
('Para estudiar', CURDATE(), 0, (SELECT id FROM usuarios WHERE email = 'cliente1@lattelist.com')),
('Favoritos', CURDATE(), 1, (SELECT id FROM usuarios WHERE email = 'cliente2@lattelist.com')),
('Salidas de fin de semana', CURDATE(), 0, (SELECT id FROM usuarios WHERE email = 'cliente2@lattelist.com')),
('Favoritos', CURDATE(), 1, (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com')),
('Para ir con amigas', CURDATE(), 0, (SELECT id FROM usuarios WHERE email = 'cliente3@lattelist.com')),
('Favoritos', CURDATE(), 1, (SELECT id FROM usuarios WHERE email = 'cliente4@lattelist.com')),
('Meriendas', CURDATE(), 0, (SELECT id FROM usuarios WHERE email = 'cliente4@lattelist.com'));
 
-- Cafés dentro de cada lista (tabla lista_cafe)
INSERT INTO lista_cafe (lista_id, cafe_id) VALUES
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente1@lattelist.com' AND l.nombre = 'Favoritos'), (SELECT id FROM cafes WHERE nombre = 'Kersen')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente1@lattelist.com' AND l.nombre = 'Favoritos'), (SELECT id FROM cafes WHERE nombre = 'Café Planta Baja')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente1@lattelist.com' AND l.nombre = 'Para estudiar'), (SELECT id FROM cafes WHERE nombre = 'Dinette')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente1@lattelist.com' AND l.nombre = 'Para estudiar'), (SELECT id FROM cafes WHERE nombre = 'Niño Lobo')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente1@lattelist.com' AND l.nombre = 'Para estudiar'), (SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente2@lattelist.com' AND l.nombre = 'Favoritos'), (SELECT id FROM cafes WHERE nombre = 'Dinette')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente2@lattelist.com' AND l.nombre = 'Favoritos'), (SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente2@lattelist.com' AND l.nombre = 'Salidas de fin de semana'), (SELECT id FROM cafes WHERE nombre = 'Niño Lobo')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente2@lattelist.com' AND l.nombre = 'Salidas de fin de semana'), (SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente2@lattelist.com' AND l.nombre = 'Salidas de fin de semana'), (SELECT id FROM cafes WHERE nombre = 'Crumble Bakery')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente3@lattelist.com' AND l.nombre = 'Favoritos'), (SELECT id FROM cafes WHERE nombre = 'Niño Lobo')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente3@lattelist.com' AND l.nombre = 'Favoritos'), (SELECT id FROM cafes WHERE nombre = 'Crumble Bakery')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente3@lattelist.com' AND l.nombre = 'Para ir con amigas'), (SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente3@lattelist.com' AND l.nombre = 'Para ir con amigas'), (SELECT id FROM cafes WHERE nombre = 'Café Planta Baja')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente3@lattelist.com' AND l.nombre = 'Para ir con amigas'), (SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente4@lattelist.com' AND l.nombre = 'Favoritos'), (SELECT id FROM cafes WHERE nombre = 'ATC - Acá Tomamos Café')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente4@lattelist.com' AND l.nombre = 'Favoritos'), (SELECT id FROM cafes WHERE nombre = 'Nina Pasticceria')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente4@lattelist.com' AND l.nombre = 'Meriendas'), (SELECT id FROM cafes WHERE nombre = 'Café Planta Baja')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente4@lattelist.com' AND l.nombre = 'Meriendas'), (SELECT id FROM cafes WHERE nombre = 'No Coffee no Prana')),
((SELECT l.id FROM listas_de_cafes l JOIN usuarios u ON u.id = l.usuario_id WHERE u.email = 'cliente4@lattelist.com' AND l.nombre = 'Meriendas'), (SELECT id FROM cafes WHERE nombre = 'Atilio''s'));
 
SET SQL_SAFE_UPDATES = 1;
 
-- ---------- 6) Verificación rápida ----------
SELECT 'usuarios' AS tabla, COUNT(*) AS filas FROM usuarios
UNION ALL SELECT 'cafes', COUNT(*) FROM cafes
UNION ALL SELECT 'cafe_etiquetas', COUNT(*) FROM cafe_etiquetas
UNION ALL SELECT 'resenas', COUNT(*) FROM resenas
UNION ALL SELECT 'listas_de_cafes', COUNT(*) FROM listas_de_cafes
UNION ALL SELECT 'lista_cafe', COUNT(*) FROM lista_cafe;
 
-- Promedio de puntuación por café (útil para mostrar en la defensa)
SELECT c.nombre, c.direccion, ROUND(AVG(r.puntuacion_general),2) AS promedio, COUNT(r.id) AS resenas
FROM cafes c LEFT JOIN resenas r ON r.cafe_id = c.id
GROUP BY c.id, c.nombre, c.direccion ORDER BY promedio DESC;
 