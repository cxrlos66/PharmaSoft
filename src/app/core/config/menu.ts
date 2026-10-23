
import { Permiso } from '../auth/auth.model';

export interface OpcionMenu {
  etiqueta: string;
  ruta: string;
  icono: string;
  permiso?: Permiso;
}

export const menu: OpcionMenu[] = [
  {
    etiqueta: 'Inicio',
    ruta: '/inicio',
    icono: '🏠'
  },
  {
    etiqueta: 'Ventas',
    ruta: '/ventas',
    icono: '🧾',
    permiso: 'VENTAS_LEER'
  },
  {
    etiqueta: 'Productos',
    ruta: '/productos',
    icono: '💊',
    permiso: 'CATALOGO_LEER'
  },
  {
    etiqueta: 'Categorías',
    ruta: '/categorias',
    icono: '🗂️',
    permiso: 'CATALOGO_LEER'
  },
  {
    etiqueta: 'Clientes',
    ruta: '/clientes',
    icono: '👥',
    permiso: 'CLIENTES_LEER'
  },
  {
    etiqueta: 'Reportes',
    ruta: '/reportes',
    icono: '📊',
    permiso: 'REPORTES_VER'
  },
  {
    etiqueta: 'Mi cuenta',
    ruta: '/mi-cuenta',
    icono: '👤'
  }
];
