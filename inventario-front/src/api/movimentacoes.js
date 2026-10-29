import http from './http'

function limpar(params = {}) {
    return Object.fromEntries(
        Object.entries(params).filter(([, v]) => v !== null && v !== undefined && v !== ''),
    )
}

export const movimentacoesApi = {
    listar: (params) => http.get('/movimentacoes', { params: limpar(params) }).then((r) => r.data),

    listarDoBem: (bemId, params) =>
        http.get(`/bens/${bemId}/movimentacoes`, { params: limpar(params) }).then((r) => r.data),

    buscar: (id) => http.get(`/movimentacoes/${id}`).then((r) => r.data),
}