import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PaginaResponse } from '../../../core/models/pagina-response';
import { Categoria, CategoriaDatos } from '../models/categoria.model';
@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/categorias`;
  listar(pagina = 0, tamanio = 100): Observable<PaginaResponse<Categoria>> {
    return this.http.get<PaginaResponse<Categoria>>(this.url,{params:new HttpParams().set('pagina',pagina).set('tamanio',tamanio)});
  }
  registrar(datos: CategoriaDatos): Observable<Categoria> {return this.http.post<Categoria>(this.url, datos);}
  actualizar(id: number, datos: CategoriaDatos): Observable<Categoria> {return this.http.put<Categoria>(`${this.url}/${id}`, datos);}
  eliminar(id: number): Observable<void> {return this.http.delete<void>(`${this.url}/${id}`);}
}
