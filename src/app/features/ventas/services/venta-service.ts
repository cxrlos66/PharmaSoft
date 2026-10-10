import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PaginaResponse } from '../../../core/models/pagina-response';
import { FiltroVentas, Venta, VentaRequest } from '../models/venta.model';
@Injectable({providedIn:'root'})
export class VentaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/ventas`;
  registrar(datos: VentaRequest): Observable<Venta> {return this.http.post<Venta>(this.url,datos);}
  obtener(id: number): Observable<Venta> {return this.http.get<Venta>(`${this.url}/${id}`);}
  anular(id: number): Observable<Venta> {return this.http.patch<Venta>(`${this.url}/${id}/anular`,{});}
  buscar(filtro: FiltroVentas, pagina: number, tamanio: number): Observable<PaginaResponse<Venta>> {
    let parametros = new HttpParams().set('pagina',pagina).set('tamanio',tamanio).set('ordenarPor','fechaRegistro').set('direccion','desc');
    if (filtro.clienteId !== null) parametros = parametros.set('clienteId',filtro.clienteId);
    if (filtro.estado) parametros = parametros.set('estado',filtro.estado);
    if (filtro.desde) parametros = parametros.set('desde',filtro.desde);
    if (filtro.hasta) parametros = parametros.set('hasta',filtro.hasta);
    return this.http.get<PaginaResponse<Venta>>(`${this.url}/buscar`,{params:parametros});
  }
}
