export interface Producto {
  id: number; codigo: string; nombre: string; descripcion: string | null;
  precio: number; stock: number; estado: boolean; categoriaId: number; categoriaNombre: string;
}
export interface ProductoDatos {
  codigo: string; nombre: string; descripcion: string;
  precio: number; stock: number; estado: boolean; categoriaId: number;
}
