# VeteicaMovil

Credenciales de acceso
Campo	Valor
 Correo electrónico	zian@veteica.com
 Contraseña	123456
 Cédula profesional	12345678

 Características principales
Módulos completos
Módulo	Funcionalidades
Autenticación	Login, Registro, Cierre de sesión
Dashboard	Estadísticas, gráficas de pastel, accesos rápidos
Pacientes	CRUD completo, fotos, búsqueda, historial clínico
Dueños	CRUD completo, fotos, asociación de mascotas
Citas	CRUD completo, selección de mascota, auto-completar dueño
Cobros	Tabla de cobros pendientes, CRUD de productos y servicios
Perfil	Información del usuario

Gráficas interactivas

Tipos de mascotas: Perros 60%, Gatos 25%, Otros 15%

Generación de PDFs
Ficha del dueño (descargable)

Ficha médica de la mascota

Ticket de cobro


Navegación de la app



Tecnologías utilizadas
Tecnología	Versión
Kotlin	1.9.0
Android SDK	35
Retrofit	2.9.0
Glide	4.16.0
Material Design	1.12.0
PDF	PdfDocument nativo


Conexión con el backend
La aplicación se conecta a la API en:

text
https://veteicamovil-production.up.railway.app/api/
Endpoints principales
Método	Endpoint	Descripción
POST	/api/auth/login	Iniciar sesión
GET	/api/pets	Listar mascotas
POST	/api/pets	Crear mascota
GET	/api/owners	Listar dueños
GET	/api/appointments	Listar citas
PUT	/api/appointments/{id}/complete	Completar cita
GET	/api/payments/pending	Cobros pendientes
POST	/api/upload/pet/{id}	Subir foto de mascota

Colores de la app

Elemento	Color	Código
Barra superior	Azul	#0c607d
Botones principales	Verde	#2E7D32
Botones de acción	Verde claro	#95C681
Fondo general	Gris claro	#F5F5F5


Estructura del proyecto
text
VeteicaApp/
├── app/src/main/
│   ├── java/com/example/veteica/
│   │   ├── activities/          # 26 pantallas
│   │   ├── adapters/            # 12 adapters
│   │   ├── models/              # 11 modelos
│   │   ├── network/             # API Service
│   │   ├── utils/               # Utilidades
│   │   └── views/               # Vistas personalizadas
│   └── res/
│       ├── layout/              # 35+ layouts
│       ├── drawable/            # 30+ íconos
│       ├── menu/                # Menús
│       └── values/              # Colores, strings
└── build.gradle



Requisitos del sistema

Requisito	Mínimo
Android	7.0 (Nougat) o superior
RAM	2 GB
Espacio	50 MB
Internet	Wi-Fi o datos móviles


Desarrolladores

Zian Vázquez	Backend Developer	@zianvazquez
Miguel Ángel	Frontend Developer	@migueahuanta09
Fransisco Miguel Navarro Crespo
ia de alta calidad con un enfoque ético, compasivo y científico"

*© 2025 Veterinaria Amor y lealtad - Todos los derechos reservados*
