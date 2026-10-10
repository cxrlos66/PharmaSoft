import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { menu } from './core/config/menu';
@Component({
 selector:'app-root',imports:[RouterLink,RouterLinkActive,RouterOutlet],
 template:`<div class="shell">
  <header class="area-header"><strong>PharmaSoft</strong><span>Botica · Inventario y ventas</span></header>
  <aside class="area-sidebar"><nav aria-label="Navegación principal">
   @for(opcion of menu; track opcion.ruta){<a [routerLink]="opcion.ruta" routerLinkActive="activo">{{ opcion.icono }} {{ opcion.etiqueta }}</a>}
  </nav></aside>
  <main class="area-main"><router-outlet /></main>
 </div>`,styles:[`@media print {.shell{display:block;min-height:0}.area-header,.area-sidebar{display:none !important}.area-main{padding:0;background:#fff}}`]
})
export class App {readonly menu=menu;}
