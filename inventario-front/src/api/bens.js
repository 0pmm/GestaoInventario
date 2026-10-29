import http from './http'
import { criarCrudApi } from './crudApi'

export const bensApi = {
    ...criarCrudApi('/bens'),
    inativar: (id, motivo) =>
        http
            .patch(`/bens/${id}/inativar`, null, { params: { motivo: motivo || undefined } })
            .then((r) => r.data),
}