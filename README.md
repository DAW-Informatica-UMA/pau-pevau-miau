<div align="center">

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/-TiKKTTS)

# 𝑷𝒆𝑽𝑨𝑼 𝑴𝑰𝑨𝑼 ฅ(•- •マ

*Microservicio REST para la gestión de la información del estudiantado en las pruebas PEvAU.*

</div>

---

## ⏣ Descripción del Proyecto

Este repositorio contiene un microservicio REST desarrollado para la asignatura **Desarrollo de Aplicaciones Web (DAW)** de la Universidad de Málaga (UMA). 

> [!IMPORTANT]
> **⬡ Objetivo Principal:** > El sistema está diseñado específicamente para la gestión de la información del estudiantado y los centros (institutos) en el marco de las pruebas PEvAU, garantizando un acceso seguro según el rol del usuario.

**Funcionalidades principales:**

<div align="center">

![Importación](https://img.shields.io/badge/➤_Importación-CSV%2FExcel-cba6f7?style=for-the-badge&labelColor=1e1e2e)
![Listados](https://img.shields.io/badge/➤_Listados-Filtrables_y_Ordenables-cba6f7?style=for-the-badge&labelColor=1e1e2e)
![Seguridad](https://img.shields.io/badge/➤_Seguridad-Roles_y_JWT-cba6f7?style=for-the-badge&labelColor=1e1e2e)

</div>

---

## ⬡ Integrantes del Equipo

| ⏣ Nombre del Integrante |
| :--- |
| • Francisco Bravo Bravo |
| • Juan Víctor Palomo Martínez |
| • ~Patricia Jia Hoyo Marqués~ |
| • Javiér Coronado Torres-Pardo |
| • Mihai Cristian Mita |

---

## ❖ Tecnologías Principales

<div align="center">

![Java](https://img.shields.io/badge/Java%2017-f38ba8?style=for-the-badge&logo=openjdk&logoColor=1e1e2e)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%204.0.5-a6e3a1?style=for-the-badge&logo=springboot&logoColor=1e1e2e)
![Spring Security & JWT](https://img.shields.io/badge/Security%20&%20JWT-f9e2af?style=for-the-badge&logo=springsecurity&logoColor=1e1e2e)
![Data JPA](https://img.shields.io/badge/Data%20JPA-cba6f7?style=for-the-badge&logo=hibernate&logoColor=1e1e2e)
![H2 Database](https://img.shields.io/badge/H2%20Database-89b4fa?style=for-the-badge&logo=minutemailer&logoColor=1e1e2e)
![Swagger](https://img.shields.io/badge/Swagger-eba0ac?style=for-the-badge&logo=swagger&logoColor=1e1e2e)
![Lombok](https://img.shields.io/badge/Lombok-f5c2e7?style=for-the-badge&logo=lombok&logoColor=1e1e2e)

</div>

---
## ❖ Ejecucion

> [!NOTE]
> **⏣ Ejecución Rápida (Req: Java 17):**
> ```bash
> git clone [https://github.com/tu-usuario/pevau-miau.git](https://github.com/tu-usuario/pevau-miau.git) && cd pevau-miau && ./mvnw spring-boot:run
> ```

---

## ⏣ API REST (Autenticación JWT)

Prueba todas las rutas en la documentación de Swagger: `/swagger-ui/index.html`

➤ **`/estudiantes`**: Operaciones CRUD completas + `/upload` para importación masiva CSV.
➤ **`/institutos`**: Operaciones CRUD completas.

---

## ⬡ Arquitectura y Estado

➤ **Estructura:** Arquitectura clásica de Spring Boot (Controllers, Services, Models, Repositories, Security).
➤ **Pendiente:** Implementar tests de integración y mejorar la trazabilidad de errores.
