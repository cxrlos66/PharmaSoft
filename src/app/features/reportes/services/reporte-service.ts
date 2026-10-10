import { Injectable,inject } from '@angular/core';
import { HttpClient,HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ProductoMasVendido,VentaPorCategoria } from '../models/reporte.model';
@Injectable({providedIn:'root'})
export class ReporteService {
  private readonly http=inject(HttpClient);
  private readonly url=`${environment.apiUrl}/reportes`;
  private parametros(desde: string,hasta: string): HttpParams {
    let parametros=new HttpParams();
    if(desde) parametros=parametros.set('desde',desde);
    if(hasta) parametros=parametros.set('hasta',hasta);
    return parametros;
  }
  categorias(desde: string,hasta: string):Observable<VentaPorCategoria[]> {return this.http.get<VentaPorCategoria[]>(`${this.url}/ventas-por-categoria`,{params:this.parametros(desde,hasta)});}
  productos(desde: string,hasta: string):Observable<ProductoMasVendido[]> {return this.http.get<ProductoMasVendido[]>(`${this.url}/productos-mas-vendidos`,{params:this.parametros(desde,hasta)});}
}
