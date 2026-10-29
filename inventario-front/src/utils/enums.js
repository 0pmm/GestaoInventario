export const STATUS_BEM = [
    { value: 'ATIVO', title: 'Ativo', cor: 'success' },
    { value: 'EM_MANUTENCAO', title:'Em manutenção', cor: 'warning'},
    { value: 'INATIVO', title: 'Inativo', cor: 'grey'},
    { value: 'VENDIDO', title: 'Vendido', cor: 'info'}
]

export const ESTADOS = [
    { value: 'NOVO', title: 'Novo', cor: 'success' },
    { value: 'BOM', title:'Bom', cor: 'info'},
    { value: 'REGULAR', title: 'Regular', cor: 'warning'},
    { value: 'RUIM', title: 'Ruim', cor: 'error'}
]

export const PERFIS = [
    { value: 'COMUM', title: 'Usuário comum', cor: 'grey' },
    { value: 'ADMINISTRADOR', title:'Administrador', cor: 'primary'},
]

export const TIPOS_MOVIMENTACAO = [
  { value: 'CADASTRO', title: 'Cadastro', cor: 'success', visivelParaComum: false },
  { value: 'EDICAO_DADOS', title: 'Edição de dados', cor: 'grey', visivelParaComum: false },
  { value: 'INATIVACAO', title: 'Inativação', cor: 'grey', visivelParaComum: false },
  { value: 'TRANSFERENCIA', title: 'Transferência', cor: 'info', visivelParaComum: true },
  { value: 'TROCA_CATEGORIA', title: 'Troca de categoria', cor: 'info', visivelParaComum: true },
  { value: 'AVARIA', title: 'Avaria', cor: 'error', visivelParaComum: true },
  { value: 'MUDANCA_ESTADO', title: 'Mudança de estado', cor: 'warning', visivelParaComum: true },
  { value: 'MANUTENCAO', title: 'Manutenção', cor: 'warning', visivelParaComum: true },
  { value: 'VENDA', title: 'Venda', cor: 'primary', visivelParaComum: true },
  { value: 'MUDANCA_STATUS', title: 'Mudança de status', cor: 'info', visivelParaComum: true },
]

export function rotulo(opcoes, valor) {
    return opcoes.find((o) => o.value === valor)?.title ?? valor
}