export function mensagemDeErro(e) {
    if (!e.response) return 'Não foi possível conectar ao servidor'
    const d = e.response.data
    return d?.message || d?.mensagem || 'Erro inesperado'
}

export function errosDeCampos(e) {
    const d = e.response?.data
    const mapa = {}
    if (Array.isArray(d?.errors)) {
        d.errors.forEach((x) => {
            if (x.field) mapa[x.field] = x.defaultMessage
        })
    }
    if (d?.campos) Object.assign(mapa, d.campos)
    return mapa
}