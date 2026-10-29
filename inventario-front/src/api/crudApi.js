import http from './http'

export function criarCrudApi(rota) {
  return {
    listar: (params) => http.get(rota, { params }).then((r) => r.data),
    buscar: (id) => http.get(`${rota}/${id}`).then((r) => r.data),
    criar: (dto) => http.post(rota, dto).then((r) => r.data),
    atualizar: (id, dto) => http.put(`${rota}/${id}`, dto).then((r) => r.data),
    excluir: (id) => http.delete(`${rota}/${id}`),
  }
}