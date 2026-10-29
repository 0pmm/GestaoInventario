import http from './http'

export const observacoesApi = {
    listar: (bemId) => http.get(`/bens/${bemId}/observacoes`).then((r) => r.data),
    criar: (bemId, texto) => http.post(`/bens/${bemId}/observacoes`, { texto }).then((r) => r.data),
}