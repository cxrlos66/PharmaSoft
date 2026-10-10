import { Component,inject,OnInit,signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ReactiveFormsModule,NonNullableFormBuilder,Validators } from '@angular/forms';
import { Categoria } from '../models/categoria.model';
import { CategoriaService } from '../services/categoria-service';
import { mensajeError } from '../../../core/utils/http-error';
@Component({selector:'app-categoria-list',imports:[ReactiveFormsModule],template:`
<section class="page">
 <header class="page-header"><h1>Categorías</h1></header>
 @if(mensaje()){<div class="exito">{{ mensaje() }}</div>}
 @if(error()){<div class="alerta">{{ error() }}</div>}
 <div class="dos-columnas">
  <form class="card" [formGroup]="formulario" (ngSubmit)="guardar()">
   <h2>{{ editando()===null?'Nueva categoría':'Modificar categoría' }}</h2>
   <label>Nombre <input class="input" formControlName="nombre" maxlength="100" required></label>
   <label>Descripción <textarea class="input" formControlName="descripcion" rows="3"></textarea></label>
   <label class="check"><input type="checkbox" formControlName="estado"> Activa</label>
   <div class="acciones"><button type="button" class="btn" (click)="limpiar()">Limpiar</button><button class="btn btn-primary" [disabled]="guardando()">Guardar</button></div>
  </form>
  <div class="card"><h2>Listado de categorías</h2>
   <div class="tabla-contenedor"><table class="tabla"><thead><tr><th>Nombre</th><th>Estado</th><th>Acciones</th></tr></thead>
    <tbody>@for(categoria of categorias();track categoria.id){<tr><td>{{ categoria.nombre }}</td><td>{{ categoria.estado?'Activa':'Inactiva' }}</td>
     <td class="acciones-fila"><button class="btn" (click)="editar(categoria)">Editar</button><button class="btn btn-peligro" (click)="eliminar(categoria)">Eliminar</button></td></tr>}
     @empty{<tr><td colspan="3">Sin categorías.</td></tr>}</tbody></table></div>
  </div>
 </div>
</section>`,styles:[`.card h2{margin-top:0}.card label{display:flex;flex-direction:column;gap:6px;margin:12px 0}.card .check{flex-direction:row;align-items:center}.acciones{display:flex;gap:9px;justify-content:flex-end;margin-top:15px}.acciones-fila{display:flex;gap:6px;flex-wrap:wrap}`]})
export class CategoriaList implements OnInit {
 private readonly fb=inject(NonNullableFormBuilder);
 private readonly servicio=inject(CategoriaService);
 protected readonly categorias=signal<Categoria[]>([]);
 protected readonly error=signal<string|null>(null);
 protected readonly mensaje=signal<string|null>(null);
 protected readonly guardando=signal(false);
 protected readonly editando=signal<number|null>(null);
 protected readonly formulario=this.fb.group({nombre:['',[Validators.required,Validators.minLength(2)]],descripcion:[''],estado:[true]});
 ngOnInit():void {this.cargar();}
 private cargar():void {this.servicio.listar().subscribe({next:pagina=>this.categorias.set(pagina.contenido),error:(error:HttpErrorResponse)=>this.error.set(mensajeError(error))});}
 editar(categoria:Categoria):void {this.editando.set(categoria.id);this.formulario.setValue({nombre:categoria.nombre,descripcion:categoria.descripcion??'',estado:categoria.estado});this.mensaje.set(null);}
 limpiar():void {this.editando.set(null);this.formulario.reset({nombre:'',descripcion:'',estado:true});}
 guardar():void {
  if(this.formulario.invalid){this.formulario.markAllAsTouched();return;}
  this.error.set(null);this.mensaje.set(null);this.guardando.set(true);
  const datos=this.formulario.getRawValue();const id=this.editando();
  const solicitud=id===null?this.servicio.registrar(datos):this.servicio.actualizar(id,datos);
  solicitud.subscribe({next:()=>{this.guardando.set(false);this.mensaje.set('Categoría guardada.');this.limpiar();this.cargar();},error:(error:HttpErrorResponse)=>{this.error.set(mensajeError(error));this.guardando.set(false);}});
 }
 eliminar(categoria:Categoria):void {
  if(!window.confirm(`¿Eliminar la categoría ${categoria.nombre}?`))return;
  this.servicio.eliminar(categoria.id).subscribe({next:()=>{this.mensaje.set('Categoría eliminada.');this.cargar();},error:(error:HttpErrorResponse)=>this.error.set(mensajeError(error))});
 }
}
