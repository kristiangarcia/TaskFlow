# Documentacion del CRUD - TaskFlow

## Resumen

Se ha implementado el CRUD completo para las entidades principales del proyecto TaskFlow:
- Usuarios
- Tareas
- Asignaciones

## Estructura Implementada

### 1. DataManager (src/main/java/com/taskflow/util/DataManager.java)

#### Operaciones CREATE (Insercion)
- `insertarUsuario(Usuario usuario)`: Inserta un nuevo usuario en la base de datos
- `insertarTarea(Tarea tarea)`: Inserta una nueva tarea en la base de datos
- `insertarAsignacion(Asignacion asignacion)`: Inserta una nueva asignacion en la base de datos

#### Operaciones READ (Lectura)
- `getUsuarios()`: Obtiene la lista observable de todos los usuarios
- `getTareas()`: Obtiene la lista observable de todas las tareas
- `getAsignaciones()`: Obtiene la lista observable de todas las asignaciones
- `obtenerUsuarioPorId(int id)`: Busca un usuario especifico por ID
- `obtenerTareaPorId(int id)`: Busca una tarea especifica por ID
- `obtenerAsignacionPorId(int id)`: Busca una asignacion especifica por ID

#### Operaciones UPDATE (Actualizacion)
- `actualizarUsuario(Usuario usuario)`: Actualiza un usuario existente
- `actualizarTarea(Tarea tarea)`: Actualiza una tarea existente
- `actualizarAsignacion(Asignacion asignacion)`: Actualiza una asignacion existente

#### Operaciones DELETE (Eliminacion)
- `eliminarUsuario(int idUsuario)`: Elimina un usuario por ID
- `eliminarTarea(int idTarea)`: Elimina una tarea por ID
- `eliminarAsignacion(int idAsignacion)`: Elimina una asignacion por ID

#### Metodos Auxiliares
- `cargarDatos()`: Recarga todos los datos desde la base de datos
- `recargarDatos()`: Metodo publico para recargar datos
- `countTareasByEstado(EstadoTarea estado)`: Cuenta tareas por estado
- `countUsuariosActivos()`: Cuenta usuarios activos

### 2. Controladores con Validaciones

#### ModalNuevoUsuarioController
Implementa la creacion de usuarios con las siguientes validaciones:
- Nombre: Obligatorio
- Email: Obligatorio y formato valido
- Telefono: Obligatorio y formato numerico (9-15 digitos)
- Contraseña: Obligatoria, minimo 6 caracteres
- Rol: Obligatorio (admin/empleado)

#### ModalNuevaTareaController
Implementa la creacion de tareas con las siguientes validaciones:
- Titulo: Obligatorio
- Descripcion: Obligatoria
- Categoria: Obligatoria
- Prioridad: Obligatoria (alta/media/baja)
- Estado: Obligatorio (abierta/en_progreso/completada/retrasada)
- Fecha limite: Obligatoria, no puede ser anterior a hoy
- Tiempo estimado: Opcional, debe ser numero positivo si se proporciona

### 3. Clase de Validaciones (src/main/java/com/taskflow/util/Validaciones.java)

Metodos utilitarios para validacion de datos:
- `esTextoVacio(String texto)`: Valida texto vacio
- `esEmailValido(String email)`: Valida formato de email
- `esTelefonoValido(String telefono)`: Valida formato de telefono
- `esNumeroPositivo(String numero)`: Valida numero entero positivo
- `esDecimalPositivo(String numero)`: Valida numero decimal positivo
- `longitudMinima(String texto, int minimo)`: Valida longitud minima
- `longitudMaxima(String texto, int maximo)`: Valida longitud maxima

## Flujo de Operaciones

### Crear un registro:
1. El usuario completa el formulario
2. Se ejecuta la validacion de campos
3. Si la validacion es exitosa, se crea el objeto del modelo
4. Se llama al metodo insertar correspondiente en DataManager
5. DataManager ejecuta el INSERT en la base de datos
6. Se recargan los datos desde la base de datos
7. Se muestra mensaje de exito/error al usuario

### Actualizar un registro:
1. Se obtiene el objeto existente por ID
2. Se modifican los campos necesarios
3. Se llama al metodo actualizar correspondiente en DataManager
4. DataManager ejecuta el UPDATE en la base de datos
5. Se recargan los datos desde la base de datos

### Eliminar un registro:
1. Se obtiene el ID del registro a eliminar
2. Se llama al metodo eliminar correspondiente en DataManager
3. DataManager ejecuta el DELETE en la base de datos
4. Se recargan los datos desde la base de datos

### Leer registros:
1. Los datos se cargan automaticamente al iniciar DataManager
2. Se puede acceder a las listas observables mediante los getters
3. Se pueden buscar registros especificos por ID

## Manejo de Errores

- Todos los metodos CRUD retornan boolean indicando exito/fallo
- Los errores SQL se capturan y se imprimen en consola
- Los controladores muestran alertas al usuario en caso de error
- Las validaciones de campos muestran mensajes especificos de error

## Recarga Automatica de Datos

Despues de cada operacion de escritura (INSERT, UPDATE, DELETE), se recargan automaticamente los datos desde la base de datos para mantener la sincronizacion entre la aplicacion y la base de datos.

## Uso de Prepared Statements

Todas las operaciones utilizan PreparedStatement para:
- Prevenir inyeccion SQL
- Mejorar rendimiento
- Facilitar el manejo de tipos de datos

## Notas Importantes

- Se utiliza el patron Singleton para DataManager
- Las listas son ObservableList para integracion con JavaFX
- Los modelos utilizan Properties de JavaFX para binding automatico
