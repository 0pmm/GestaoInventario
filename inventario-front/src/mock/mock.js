import http from '../api/http'

const categorias = [
  { id: 1, nome: 'Informática', descricao: 'Computadores, monitores e periféricos' },
  { id: 2, nome: 'Mobiliário', descricao: 'Mesas, cadeiras e armários' },
  { id: 3, nome: 'Eletrodomésticos', descricao: 'Equipamentos de copa e climatização' },
  { id: 4, nome: 'Laboratório', descricao: 'Instrumentos e equipamentos de ensino' },
  { id: 5, nome: 'Audiovisual', descricao: 'Projetores, telas e som' },
]
const localizacoes = [
  { id: 1, nome: 'Bloco A - Sala 101', descricao: 'Sala de aula' },
  { id: 2, nome: 'Bloco A - Laboratório 2', descricao: 'Laboratório de informática' },
  { id: 3, nome: 'Bloco B - Secretaria', descricao: 'Atendimento' },
  { id: 4, nome: 'Almoxarifado', descricao: 'Depósito central' },
  { id: 5, nome: 'Auditório', descricao: 'Eventos' },
]
const usuarios = [
  { id: 1, nome: 'Gustavo Brito', email: 'gustavo@empresa.com', perfil: 'ADMINISTRADOR', ativo: true },
  { id: 2, nome: 'Marina Souza', email: 'marina@empresa.com', perfil: 'COMUM', ativo: true },
  { id: 3, nome: 'Carlos Lima', email: 'carlos@empresa.com', perfil: 'COMUM', ativo: false },
]
const c = (...ids) => ids.map((id) => categorias.find((x) => x.id === id))
const l = (id) => localizacoes.find((x) => x.id === id)

const bens = [
  ['PAT-0001', 'Notebook Dell Latitude 5440', 12, 5200, 'NOVO', 'ATIVO', 2, [1], '2026-03-14'],
  ['PAT-0002', 'Monitor LG 24" Full HD', 30, 890, 'BOM', 'ATIVO', 2, [1], '2025-08-02'],
  ['PAT-0003', 'Cadeira ergonômica Flexform', 45, 640, 'BOM', 'ATIVO', 1, [2], '2024-11-20'],
  ['PAT-0004', 'Mesa de reunião 12 lugares', 2, 3400, 'REGULAR', 'ATIVO', 3, [2], '2023-05-09'],
  ['PAT-0005', 'Projetor Epson PowerLite', 4, 3100, 'BOM', 'EM_MANUTENCAO', 5, [5], '2024-02-17'],
  ['PAT-0006', 'Ar-condicionado Split 18000 BTU', 6, 2750, 'BOM', 'ATIVO', 1, [3], '2024-09-30'],
  ['PAT-0007', 'Microscópio binocular', 8, 4200, 'NOVO', 'ATIVO', 4, [4], '2026-01-22'],
  ['PAT-0008', 'Impressora HP LaserJet', 3, 1850, 'RUIM', 'EM_MANUTENCAO', 3, [1], '2022-07-11'],
  ['PAT-0009', 'Geladeira Consul 340L', 1, 2300, 'REGULAR', 'ATIVO', 4, [3], '2021-12-05'],
  ['PAT-0010', 'Caixa de som JBL PartyBox', 2, 2900, 'BOM', 'ATIVO', 5, [5], '2025-04-18'],
  ['PAT-0011', 'Armário de aço 2 portas', 10, 780, 'REGULAR', 'ATIVO', 4, [2], '2020-10-01'],
  ['PAT-0012', 'Computador desktop legado', 5, 1900, 'RUIM', 'INATIVO', 4, [1], '2018-06-12'],
].map((b, i) => ({
  id: i + 1,
  codigoPatrimonio: b[0], nome: b[1], descricao: `${b[1]} — adquirido para uso institucional.`,
  quantidade: b[2], valorAquisicao: b[3], estadoConservacao: b[4], status: b[5],
  localizacao: l(b[6]), categorias: c(...b[7]), dataAquisicao: b[8],
}))

const hoje = Date.now()
const dia = 86400000
const mov = (id, bemId, tipo, diasAtras, extra = {}) => ({
  id, bem: bens.find((b) => b.id === bemId), tipo,
  dataHora: new Date(hoje - diasAtras * dia - id * 3600000).toISOString(),
  responsavel: 'Gustavo Brito', valorAnterior: null, valorNovo: null, origem: null, destino: null, motivo: null,
  ...extra,
})
const movimentacoes = [
  mov(1, 5, 'MANUTENCAO', 0, { valorAnterior: 'ATIVO', valorNovo: 'EM_MANUTENCAO', motivo: 'Lâmpada queimada' }),
  mov(2, 1, 'TRANSFERENCIA', 1, { origem: 'Almoxarifado', destino: 'Bloco A - Laboratório 2' }),
  mov(3, 8, 'AVARIA', 2, { valorAnterior: 'REGULAR', valorNovo: 'RUIM', motivo: 'Atolamento recorrente' }),
  mov(4, 7, 'CADASTRO', 4),
  mov(5, 4, 'MUDANCA_ESTADO', 6, { valorAnterior: 'BOM', valorNovo: 'REGULAR' }),
  mov(6, 12, 'INATIVACAO', 9, { motivo: 'Equipamento obsoleto' }),
  mov(7, 2, 'TRANSFERENCIA', 12, { origem: 'Bloco B - Secretaria', destino: 'Bloco A - Laboratório 2' }),
  mov(8, 6, 'TROCA_CATEGORIA', 15, { valorAnterior: 'Mobiliário', valorNovo: 'Eletrodomésticos' }),
]
const observacoes = [
  { id: 1, texto: 'Enviado à assistência técnica, previsão de retorno em 5 dias.', autor: 'Gustavo Brito', dataHora: new Date(hoje - dia).toISOString() },
  { id: 2, texto: 'Verificado em inventário anual, sem divergências.', autor: 'Marina Souza', dataHora: new Date(hoje - 40 * dia).toISOString() },
]

const sessao = { usuario: new URLSearchParams(location.search).has('logado') ? usuarios[0] : null }

function responder(config, data, status = 200) {
  return Promise.resolve({ data, status, statusText: 'OK', headers: {}, config })
}
function erro(config, status, mensagem) {
  const e = new Error(mensagem)
  e.config = config
  e.response = { status, data: { mensagem }, config }
  return Promise.reject(e)
}

const norm = (s) => (s ?? '').toString().toLowerCase()

export function instalarMock() {
  http.defaults.adapter = async (config) => {
    const url = config.url.replace(/^https?:\/\/[^/]+\/api/, '')
    const metodo = (config.method ?? 'get').toLowerCase()
    const p = config.params ?? {}
    await new Promise((r) => setTimeout(r, 120))

    if (url === '/auth/me') return sessao.usuario ? responder(config, sessao.usuario) : erro(config, 401, 'Não autenticado')
    if (url === '/auth/login') { sessao.usuario = usuarios[0]; return responder(config, sessao.usuario) }
    if (url === '/auth/logout') { sessao.usuario = null; return responder(config, null) }

    if (url === '/categorias') return responder(config, categorias.filter((x) => !p.busca || norm(x.nome).includes(norm(p.busca))))
    if (url === '/localizacoes') return responder(config, localizacoes.filter((x) => !p.busca || norm(x.nome).includes(norm(p.busca))))
    if (url === '/usuarios') return responder(config, usuarios.filter((x) => !p.busca || norm(x.nome).includes(norm(p.busca))))

    if (url === '/bens') {
      if (metodo !== 'get') return responder(config, JSON.parse(config.data))
      const lista = bens.filter((b) =>
        (!p.busca || norm(b.nome).includes(norm(p.busca)) || norm(b.codigoPatrimonio).includes(norm(p.busca))) &&
        (!p.categoriaId || b.categorias.some((x) => x.id === Number(p.categoriaId))) &&
        (!p.localizacaoId || b.localizacao.id === Number(p.localizacaoId)) &&
        (p.status ? b.status === p.status : b.status !== 'INATIVO'),
      )
      return responder(config, lista)
    }
    let m = url.match(/^\/bens\/(\d+)\/observacoes$/)
    if (m) return responder(config, observacoes)
    m = url.match(/^\/bens\/(\d+)$/)
    if (m) {
      const b = bens.find((x) => x.id === Number(m[1]))
      return b ? responder(config, b) : erro(config, 404, 'Bem não encontrado')
    }
    if (url === '/movimentacoes') {
      return responder(config, movimentacoes.filter((x) => (!p.bemId || x.bem.id === Number(p.bemId)) && (!p.tipo || x.tipo === p.tipo)))
    }
    return responder(config, [])
  }
}
