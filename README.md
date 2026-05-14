<div align="center">

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/-TiKKTTS)

# 𝑷𝒆𝑽𝑨𝑼 𝑴𝑰𝑨𝑼 ฅ(•- •マ

*Microservicio REST para la gestión de la información del estudiantado en las pruebas PEvAU.*

</div>

---

## ✦ Descripción del Proyecto

Este repositorio contiene un microservicio REST desarrollado para la asignatura **Desarrollo de Aplicaciones Web (DAW)** de la Universidad de Málaga (UMA). 

> [!IMPORTANT]
> **✧ Objetivo Principal:** > El sistema está diseñado específicamente para la gestión de la información del estudiantado y los centros (institutos) en el marco de las pruebas PEvAU, garantizando un acceso seguro según el rol del usuario.

**Funcionalidades principales:**

<div align="center">

![Importación](https://img.shields.io/badge/➤_Importación-CSV%2FExcel-cba6f7?style=for-the-badge&labelColor=1e1e2e)
![Listados](https://img.shields.io/badge/➤_Listados-Filtrables_y_Ordenables-cba6f7?style=for-the-badge&labelColor=1e1e2e)
![Seguridad](https://img.shields.io/badge/➤_Seguridad-Roles_y_JWT-cba6f7?style=for-the-badge&labelColor=1e1e2e)

</div>

---

## ✧ Integrantes del Equipo

| ✦ Nombre del Integrante |
| :--- |
| • Francisco Bravo Bravo |
| • Juan Victor Palomo Martinez |
| • Patricia Jia Hoyo Marqués |
| • Javier Coronado Torres-Pardo |
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

## ❖ Puesta en Marcha
> [!NOTE]
> **✦ Requisitos Previos:** Asegúrate de tener instalado Java 17 y Maven en tu sistema antes de compilar el proyecto.

**Pasos para ejecutar en local:**

1. Clona este repositorio:
   ```bash
   git clone [https://github.com/tu-usuario/pevau-miau.git](https://github.com/tu-usuario/pevau-miau.git)
```

2. Navega al directorio del proyecto:
```bash
cd pevau-miau

```


3. Construye el proyecto con el Wrapper de Maven (incluido en el repositorio):
```bash
./mvnw clean install

```


4. Levanta el servidor:
```bash
./mvnw spring-boot:run

```



---

## ✦ Endpoints de la API

El sistema está protegido mediante autenticación **JWT**. Las rutas requieren permisos específicos (roles como `ADMINISTRADOR` o `VICERRECTORADO`).

> [!TIP]
> **✦ Documentación Interactiva:** Puedes explorar, enviar el token y probar todos los endpoints accediendo a Swagger UI en la ruta `/swagger-ui/index.html` (o la ruta que genere Swagger en tu entorno local).

### ➤ Gestión de Estudiantes (`/estudiantes`)

| Método | Endpoint | Descripción |
| --- | --- | --- |
|  | `/` | Lista estudiantes (filtrable por sede y convocatoria). |
|  | `/` | Crea un nuevo estudiante manualmente. |
|  | `/{idEstudiante}` | Obtiene los detalles de un estudiante específico. |
|  | `/{idEstudiante}` | Actualiza los datos de un estudiante. |
|  | `/{idEstudiante}` | Elimina un estudiante (si no está bloqueado). |
|  | `/upload` | Importa estudiantes de forma masiva mediante archivo CSV. |

### ➤ Gestión de Institutos (`/institutos`)

| Método | Endpoint | Descripción |
| --- | --- | --- |
|  | `/` | Obtiene el listado de todos los institutos. |
|  | `/` | Registra un nuevo instituto. |
|  | `/{idInstituto}` | Obtiene los detalles de un instituto específico. |
|  | `/{idInstituto}` | Actualiza la información de un instituto. |
|  | `/{idInstituto}` | Elimina un instituto del sistema. |

---

## ✧ Arquitectura del Sistema

El proyecto sigue una arquitectura clásica de capas de Spring Boot para separar responsabilidades de manera limpia:

```text
src/main/java/es/uma/informatica/daw/miau/pau_pevau
 ├── 📂 controllers    # Controladores REST (Estudiantes, Institutos...)
 ├── 📂 services       # Lógica de negocio y procesamiento de CSVs
 ├── 📂 models         # DTOs y modelos de transferencia de datos
 ├── 📂 entities       # Entidades JPA mapeadas a la base de datos
 ├── 📂 repositories   # Interfaces de acceso a datos (Spring Data JPA)
 ├── 📂 security       # Configuración JWT y filtros de autenticación
 └── 📂 exceptions     # Manejo global de errores y excepciones personalizadas

```

---

## ❖ Próximos Pasos

* [x] Configuración inicial y conexión a BD H2.
* [x] Lógica de importación de estudiantes por CSV.
* [x] Refactorización e implementación de seguridad con JWT.
* [ ] Añadir tests de integración para asegurar los endpoints con seguridad.
* [ ] Mejorar la trazabilidad de errores durante la subida masiva de archivos.

```
