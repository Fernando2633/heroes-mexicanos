-- ============================================================
-- SCRIPT DE CREACIÓN Y CARGA DE BASE DE DATOS: heroes_mexicanos
-- Práctica I y II - Backend REST con Spring Boot y Arquitectura Hexagonal
-- ============================================================

CREATE DATABASE IF NOT EXISTS `heroes_mexicanos` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `heroes_mexicanos`;

DROP TABLE IF EXISTS `heroes`;

CREATE TABLE `heroes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `apellido` VARCHAR(100) NOT NULL,
  `fecha_nacimiento` DATE NOT NULL,
  `estado_nacimiento` VARCHAR(100) NOT NULL,
  `epoca` VARCHAR(100) NOT NULL,
  `movimiento` VARCHAR(150) NOT NULL,
  `descripcion` TEXT,
  PRIMARY KEY (`id`),
  CONSTRAINT `uk_heroe_nombre_apellido` UNIQUE (`nombre`, `apellido`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `heroes` (`id`, `nombre`, `apellido`, `fecha_nacimiento`, `estado_nacimiento`, `epoca`, `movimiento`, `descripcion`) VALUES
(1, 'Miguel', 'Hidalgo y Costilla', '1753-05-08', 'Guanajuato', 'Independencia', 'Movimiento Insurgente', 'Sacerdote y revolucionario considerado el Padre de la Patria mexicana por iniciar el Grito de Dolores.'),
(2, 'José María', 'Morelos y Pavón', '1765-09-30', 'Michoacán', 'Independencia', 'Movimiento Insurgente', 'Sacerdote y militar insurgente que organizó la segunda etapa de la Independencia y promulgó los Sentimientos de la Nación.'),
(3, 'Benito', 'Juárez García', '1806-03-21', 'Oaxaca', 'Reforma', 'Movimiento Liberal', 'Abogado y presidente mexicano de origen zapoteco que consolidó las Leyes de Reforma y la República.'),
(4, 'Emiliano', 'Zapata Salazar', '1879-08-08', 'Morelos', 'Revolución Mexicana', 'Ejército Libertador del Sur', 'Líder militar y campesino revolucionario, símbolo de la lucha agraria con el lema Tierra y Libertad.'),
(5, 'Doroteo', 'Arango (Pancho Villa)', '1878-06-05', 'Durango', 'Revolución Mexicana', 'División del Norte', 'General revolucionario famoso por liderar la División del Norte en las batallas del norte de México.'),
(6, 'Josefa', 'Ortiz de Domínguez', '1768-09-08', 'Michoacán', 'Independencia', 'Conspiración de Querétaro', 'Heroína de la Independencia conocida como la Corregidora, pieza clave para el inicio del movimiento insurgente.'),
(7, 'Leona', 'Vicario', '1789-04-10', 'Ciudad de México', 'Independencia', 'Movimiento Insurgente', 'Periodista y heroína de la Independencia, declarada Benemérita Madre de la Patria.'),
(8, 'Francisco I.', 'Madero', '1873-10-30', 'Coahuila', 'Revolución Mexicana', 'Anti-reeleccionismo', 'Empresario y político promotor de la Revolución Mexicana con el lema Sufragio efectivo, no reelección.'),
(9, 'Lázaro', 'Cárdenas del Río', '1895-05-21', 'Michoacán', 'México Contemporáneo', 'Cardenismo', 'Presidente de México que llevó a cabo la Expropiación Petrolera en 1938 y el reparto agrario.'),
(10, 'Sor Juana Inés', 'de la Cruz', '1648-11-12', 'Estado de México', 'Época Virreinal', 'Humanismo y Literatura', 'Religiosa y escritora novohispana, una de las mayores exponentes de la literatura en español.'),
(11, 'Vicente', 'Guerrero', '1782-08-10', 'Guerrero', 'Independencia', 'Resistencia Insurgente', 'General insurgente y segundo presidente de México que consumó la independencia junto a Iturbide.'),
(12, 'Ignacio', 'Allende', '1769-01-21', 'Guanajuato', 'Independencia', 'Movimiento Insurgente', 'Capitán de las milicias novohispanas que luchó junto a Hidalgo en la primera etapa insurgente.'),
(13, 'Agustín', 'de Iturbide', '1783-09-27', 'Michoacán', 'Independencia', 'Ejército Trigarante', 'Militar que proclamó el Plan de Iguala y consumó la Independencia de México en 1821.'),
(14, 'Porfirio', 'Díaz', '1830-09-15', 'Oaxaca', 'Porfiriato', 'Ejército Liberal', 'Militar destacado en la Intervención Francesa y presidente de México durante más de tres décadas.'),
(15, 'Venustiano', 'Carranza', '1859-12-29', 'Coahuila', 'Revolución Mexicana', 'Movimiento Constitucionalista', 'Político y militar impulsador de la Constitución Política de los Estados Unidos Mexicanos de 1917.'),
(16, 'Álvaro', 'Obregón', '1880-02-19', 'Sonora', 'Revolución Mexicana', 'Ejército Constitucionalista', 'General revolucionario triunfador y presidente que inició la reconstrucción posrevolucionaria.'),
(17, 'Plutarco Elías', 'Calles', '1877-09-25', 'Sonora', 'México Contemporáneo', 'Reconstrucción Nacional', 'Presidente de México y fundador del Partido Nacional Revolucionario (PNR).'),
(18, 'Nezahualcóyotl', 'Acolhua', '1402-04-28', 'Estado de México', 'Época Prehispánica', 'Señorío de Texcoco', 'Rey de Texcoco, poeta, erudito y arquitecto prehispánico de gran sabiduría.'),
(19, 'Cuauhtémoc', 'Ihuitemoc', '1496-01-01', 'Ciudad de México', 'Época Prehispánica', 'Resistencia Tenochca', 'Último tlatoani mexica de Tenochtitlan que defendedió valientemente la ciudad ante la conquista española.'),
(20, 'Guadalupe', 'Victoria', '1786-09-29', 'Durango', 'Independencia', 'Primer Gobierno Republicano', 'Insurgente destacado y el primer Presidente de los Estados Unidos Mexicanos.'),
(21, 'Carmen', 'Serdán Alatriste', '1875-11-11', 'Puebla', 'Revolución Mexicana', 'Movimiento Maderista', 'Heroína revolucionaria poblana que participó activamente en los preparativos del alzamiento maderista.');
