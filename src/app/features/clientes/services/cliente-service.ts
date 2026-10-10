import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PaginaResponse } from '../../../core/models/pagina-response';
import { Cliente, ClienteDatos } from '../models/cliente.model';
@Injectable({ providedIn: 'root' })
export class ClienteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/clientes`;
  listar(pagina = 0, tamanio = 100, ordenarPor = 'apellidos', direccion: 'asc' | 'desc' = 'asc'): Observable<PaginaResponse<Cliente>> {
    const parametros = new HttpParams().set('pagina', pagina).set('tamanio', tamanio).set('ordenarPor', ordenarPor).set('direccion', direccion);
    return this.http.get<PaginaResponse<Cliente>>(this.url, {params: parametros});
  }
  obtener(id: number): Observable<Cliente> {return this.http.get<Cliente>(`${this.url}/${id}`);}
  registrar(datos: ClienteDatos): Observable<Cliente> {return this.http.post<Cliente>(this.url, datos);}
  actualizar(id: number, datos: ClienteDatos): Observable<Cliente> {return this.http.put<Cliente>(`${this.url}/${id}`, datos);}
  eliminar(id: number): Observable<void> {return this.http.delete<void>(`${this.url}/${id}`);}
}
