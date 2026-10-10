import { HttpErrorResponse } from '@angular/common/http';
export function mensajeError(error: HttpErrorResponse): string {
  const cuerpo: unknown = error.error;
  if (typeof cuerpo === 'string' && cuerpo.trim()) return cuerpo;
  if (cuerpo && typeof cuerpo === 'object') {
    const respuesta = cuerpo as {mensaje?: string; message?: string; validationErrors?: Record<string,string>};
    if (respuesta.validationErrors && Object.keys(respuesta.validationErrors).length) {
      return Object.values(respuesta.validationErrors).join('. ');
    }
    if (respuesta.mensaje) return respuesta.mensaje;
    if (respuesta.message) return respuesta.message;
  }
  return error.status === 0 ? 'No se pudo conectar con PharmaBackend.' : `Error al procesar la operación (${error.status}).`;
}
