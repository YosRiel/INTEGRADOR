CREATE DATABASE integrador;

USE integrador;

CREATE TABLE `informe` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `grupo` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cantidad_imagenes` int NOT NULL,
  `descripcion` text,
  `nombre_grupo` varchar(255) NOT NULL,
  `informe_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9one2kb0tlbn45t0vbnsee7n` (`informe_id`),
  CONSTRAINT `FK9one2kb0tlbn45t0vbnsee7n` FOREIGN KEY (`informe_id`) REFERENCES `informe` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `imagen` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `grupo_id` bigint NOT NULL,
  `datos` longblob NOT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4epolic1t2oupw5pb4us3534o` (`grupo_id`),
  CONSTRAINT `FK4epolic1t2oupw5pb4us3534o` FOREIGN KEY (`grupo_id`) REFERENCES `grupo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pedido` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cliente` varchar(255) DEFAULT NULL,
  `servicio` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `empresa` varchar(255) DEFAULT NULL,
  `ruc` varchar(255) DEFAULT NULL,
  `estado` varchar(255) NOT NULL DEFAULT 'Pendiente',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `proforma` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `contenido_informe` text,
  `descripcion_servicio` varchar(255) DEFAULT NULL,
  `informe_id` bigint DEFAULT NULL,
  `pedido_id` bigint DEFAULT NULL,
  `valor_servicio` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `usuario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
