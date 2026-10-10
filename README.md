# PharmaSoft — Frontend Angular 22

## Requisitos

- Node.js compatible con Angular 22 y npm.
- PharmaBackend funcionando en `http://localhost:8080` y conectado a Oracle.
- Configuración CORS del backend que permita `http://localhost:4200`.

## Abrir el proyecto

Abra la carpeta `PharmaSoft_Frontend` en Visual Studio Code y ejecute:

```bash
npm install
npm start
```

Abra `http://localhost:4200` en el navegador.

## Conexión

La URL base de la API está en `src/environments/environment.ts`:

```ts
export const environment = { apiUrl: 'http://localhost:8080/api/v1' };
```

## Módulos incluidos

- Categorías: listado, registro, edición y eliminación.
- Clientes: listado, registro, edición y eliminación.
- Productos: listado, registro, edición y eliminación, con selección de categoría.
- Ventas: registro cabecera-detalle con `FormArray`, buscadores, `computed`, validaciones de stock y confirmación.
- Comprobante de venta, impresión manual o automática y anulación.
- Consulta de ventas paginada con filtros opcionales, fechas y estado.
- Reportes: ventas por categoría y productos más vendidos, excluyendo anuladas desde el backend.

El servidor determina precios y totales; el frontend solo envía `clienteId`, `productoId` y `cantidad`.

## Comprobación

1. Mantenga PharmaBackend iniciado en STS.
2. Arranque Angular con `npm start`.
3. Abra la sección Productos y verifique que aparecen los registros cargados en Oracle.
4. Abra Ventas > Nueva venta; seleccione cliente activo y uno o varios productos, ajuste cantidades y revise el total.
5. Revise, confirme y compruebe el comprobante.
6. Vuelva a Ventas para filtrar e imprimir consultas.
7. Consulte los reportes por rango de fechas.

**No se adjuntan capturas ni historial Git simulados.** Las evidencias y los commits deben generarse cuando se pruebe el programa en su computadora.

Las búsquedas de cliente/producto del formulario cargan hasta 100 registros, según el ejemplo de la guía de la sesión 9. Para una base de datos mayor se recomienda un endpoint de búsqueda en el backend.
