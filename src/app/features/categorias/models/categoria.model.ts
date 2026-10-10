export interface Categoria { id: number; nombre: string; descripcion: string | null; estado: boolean; }
export type CategoriaDatos = Omit<Categoria,'id'>;
