import { Component,inject,OnInit,signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ReactiveFormsModule,NonNullableFormBuilder,Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Producto, ProductoDatos } from '../models/producto.model';
import { Categoria } from '../../categorias/models/categoria.model';
import { CategoriaService } from '../../categorias/services/categoria-service';
import { ProductoService } from '../services/producto-service';
import { mensajeError } from '../../../core/utils/http-error';
@Component({selector:'app-producto-list',imports:[ReactiveFormsModule,CurrencyPipe],template:`
<section class="page"><header class="page-header"><h1>Productos</h1></header>
 @if(mensaje()){<div class="exito">{{ mensaje() }}</div>}
 @if(error()){<div class="alerta" role="alert">{{ error() }}</div>}
 <div class="dos-columnas">
  <form class="card" [formGroup]="formulario" (ngSubmit)="guardar()"><h2>{{ editando()===null?'Nuevo producto':'Modificar producto' }}</h2>
   <label>Código <input class="input" formControlName="codigo" required></label>
   <label>Nombre <input class="input" formControlName="nombre" required></label>
   <label>Descripción <input class="input" formControlName="descripcion"></label>
   <label>Categoría <select class="input" formControlName="categoriaId" required><option [ngValue]="null">Seleccione categoría</option>
    @for(categoria of categorias();track categoria.id){<option [ngValue]="categoria.id">{{ categoria.nombre }}</option>}
   </select></label>
   <label>Precio (S/) <input class="input" formControlName="precio" type="number" step="0.01" min="0.01" required></label>
   <label>Stock <input class="input" formControlName="stock" type="number" step="1" min="0" required></label>
   <label class="check"><input type="checkbox" formControlName="estado"> Activo</label>
   <div class="acciones"><button class="btn" type="button" (click)="limpiar()">Limpiar</button><button class="btn btn-primary" [disabled]="guardando()">Guardar</button></div>
  </form>
  <div class="card"><h2>Listado de productos</h2>
   <div class="tabla-contenedor"><table class="tabla"><thead><tr><th>Código</th><th>Producto</th><th>Precio</th><th>Stock</th><th>Acciones</th></tr></thead>
    <tbody>@for(producto of productos();track producto.id){<tr><td>{{ producto.codigo }}</td><td>{{ producto.nombre }}<small class="secundario">{{ producto.categoriaNombre }}</small></td>
     <td>{{ producto.precio | currency:'PEN':'S/ ' }}</td><td>{{ producto.stock }}</td>
     <td class="acciones-fila"><button class="btn" (click)="editar(producto)">Editar</button><button class="btn btn-peligro" (click)="eliminar(producto)">Eliminar</button></td></tr>}
     @empty{<tr><td colspan="5">Sin productos.</td></tr>}</tbody></table></div>
  </div>
 </div></section>`,styles:[`.card h2{margin-top:0}.card label{display:flex;flex-direction:column;gap:6px;margin:11px 0}.card .check{flex-direction:row;align-items:center}.acciones{display:flex;gap:9px;justify-content:flex-end;margin-top:15px}.acciones-fila{display:flex;gap:6px;flex-wrap:wrap}.secundario{display:block;color:#67768a;margin-top:3px}`]})
export class ProductoList implements OnInit {
 private readonly fb=inject(NonNullableFormBuilder);
 private readonly servicio=inject(ProductoService);
 private readonly categoriasServicio=inject(CategoriaService);
 protected readonly productos=signal<Producto[]>([]);
 protected readonly categorias=signal<Categoria[]>([]);
 protected readonly error=signal<string|null>(null);
 protected readonly mensaje=signal<string|null>(null);
 protected readonly guardando=signal(false);
 protected readonly editando=signal<number|null>(null);
 protected readonly formulario=this.fb.group({
   codigo:['',Validators.required],nombre:['',Validators.required],descripcion:[''],
   precio:[0,[Validators.required,Validators.min(0.01)]],stock:[0,[Validators.required,Validators.min(0),Validators.pattern(/^\d+$/)]],
   estado:[true],categoriaId:this.fb.control<number|null>(null,Validators.required)
 });
 ngOnInit():void {this.cargar();}
 private cargar():void {forkJoin({productos:this.servicio.listar(),categorias:this.categoriasServicio.listar()}).subscribe({
   next:datos=>{this.productos.set(datos.productos.contenido);this.categorias.set(datos.categorias.contenido);},
   error:(error:HttpErrorResponse)=>this.error.set(mensajeError(error))
 });}
 editar(producto:Producto):void {this.editando.set(producto.id);this.formulario.setValue({codigo:producto.codigo,nombre:producto.nombre,descripcion:producto.descripcion??'',precio:producto.precio,stock:producto.stock,estado:producto.estado,categoriaId:producto.categoriaId});this.mensaje.set(null);}
 limpiar():void {this.editando.set(null);this.formulario.reset({codigo:'',nombre:'',descripcion:'',precio:0,stock:0,estado:true,categoriaId:null});}
 guardar():void {
  if(this.formulario.invalid){this.formulario.markAllAsTouched();this.error.set('Completa el nombre, código, categoría, precio y stock válidos.');return;}
  const datos=this.formulario.getRawValue();if(datos.categoriaId===null)return;
  const solicitud:ProductoDatos={codigo:datos.codigo,nombre:datos.nombre,descripcion:datos.descripcion,precio:Number(datos.precio),stock:Number(datos.stock),estado:datos.estado,categoriaId:datos.categoriaId};
  this.guardando.set(true);this.error.set(null);this.mensaje.set(null);
  const id=this.editando();const operacion=id===null?this.servicio.registrar(solicitud):this.servicio.actualizar(id,solicitud);
  operacion.subscribe({next:()=>{this.guardando.set(false);this.mensaje.set('Producto guardado.');this.limpiar();this.cargar();},error:(error:HttpErrorResponse)=>{this.error.set(mensajeError(error));this.guardando.set(false);}});
 }
 eliminar(producto:Producto):void {
  if(!window.confirm(`¿Eliminar el producto ${producto.nombre}?`))return;
  this.servicio.eliminar(producto.id).subscribe({next:()=>{this.mensaje.set('Producto eliminado.');this.cargar();},error:(error:HttpErrorResponse)=>this.error.set(mensajeError(error))});
 }
}
