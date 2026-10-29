const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })
const numero = new Intl.NumberFormat('pt-BR')
const dataHora = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' })

export const formatarMoeda = (valor) => (valor == null ? '—' : moeda.format(valor))

export const formatarNumero = (valor) => (valor == null ? '—' : numero.format(valor))

export function formatarData(iso) {
    if (!iso) return '—'
    const [ano, mes, dia] = iso.split('-')
    return `${dia}/${mes}/${ano}`
}

export function formatarDataHora(iso) {
    return iso ? dataHora.format(new Date(iso)) : '—'
}