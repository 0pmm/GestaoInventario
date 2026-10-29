import { criarCrudApi } from "./crudApi";

export const usuariosApi = {
    ...criarCrudApi('/usuarios'),
    inativar: (id) => http.patch('/usuarios/${id}/inativar').then((r) => r.data),
    ativar: (id) => http.patch('/usuarios/${id}/reativar').then((r) => r.data)
} 