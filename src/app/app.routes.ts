import { Routes } from '@angular/router';
export const rutas:Routes=[
 {path:'',pathMatch:'full',redirectTo:'ventas'},
 {path:'ventas',loadChildren:()=>import('./features/ventas/ventas.routes').then(m=>m.rutasVentas)},
 {path:'categorias',loadComponent:()=>import('./features/categorias/pages/categoria-list').then(m=>m.CategoriaList),title:'Categorías'},
 {path:'productos',loadComponent:()=>import('./features/productos/pages/producto-list').then(m=>m.ProductoList),title:'Productos'},
 {path:'clientes',loadComponent:()=>import('./features/clientes/pages/cliente-list').then(m=>m.ClienteList),title:'Clientes'},
 {path:'reportes',loadComponent:()=>import('./features/reportes/pages/reporte-list').then(m=>m.ReporteList),title:'Reportes'},
 {path:'**',redirectTo:'ventas'}
];
