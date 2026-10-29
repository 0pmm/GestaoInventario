import { computed, onMounted, reactive, ref, watch } from 'vue'
import { bensApi } from '../api/bens'
import { mensagemDeErro } from '../utils/erros'
import { ESTADOS, STATUS_BEM, rotulo } from '../utils/enums'
import { useNotificacao } from './useNotificacao'

const valorDoBem = (bem) => (bem.valorAquisicao ?? 0) * bem.quantidade

function limpar(params) {
    return Object.fromEntries(
        Object.entries(params).filter(([, v]) => v !== null && v !== undefined && v !== ''),
    )
}

export function useEstatisticas() {
    const { avisar } = useNotificacao()

    const bens = ref([])
    const carregando = ref(false)

    const filtros = reactive({
        categoriaId: null,
        localizacaoId: null,
        status: null,
        aquisicaoDe: '',
        aquisicaoAte: '',
    })

    const incluirInativos = ref(false)

    async function carregar() {
        carregando.value = true
        try {
            const params = limpar(filtros)
            let lista = await bensApi.listar(params)

            if (incluirInativos.value && !filtros.status) {
                const inativos = await bensApi.listar({ ...params, status: 'INATIVO' })
                lista = [...lista, ...inativos]
            }
            bens.value = lista
        } catch (e) {
            avisar(mensagemDeErro(e), 'error')
        } finally {
            carregando.value = false
        }
    }

    watch([filtros, incluirInativos], carregar, { deep: true })
    onMounted(carregar)

    function limparFiltros() {
        Object.assign(filtros, {
            categoriaId: null,
            localizacaoId: null,
            status: null,
            aquisicaoDe: '',
            aquisicaoAte: '',
        })
        incluirInativos.value = false
    }


    const resumo = computed(() => ({
        bens: bens.value.length,
        unidades: bens.value.reduce((total, b) => total + b.quantidade, 0),
        valorTotal: bens.value.reduce((total, b) => total + valorDoBem(b), 0),
        semValor: bens.value.filter((b) => b.valorAquisicao == null).length,
    }))

    function agrupar(chaves) {
        const mapa = new Map()
        for (const bem of bens.value) {
            for (const nome of chaves(bem)) {
                const grupo = mapa.get(nome) ?? { nome, bens: 0, unidades: 0, valor: 0 }
                grupo.bens += 1
                grupo.unidades += bem.quantidade
                grupo.valor += valorDoBem(bem)
                mapa.set(nome, grupo)
            }
        }
        return [...mapa.values()].sort((a, b) => b.bens - a.bens || a.nome.localeCompare(b.nome))
    }

    const porCategoria = computed(() => agrupar((b) => b.categorias.map((c) => c.nome)))
    const porLocalizacao = computed(() => agrupar((b) => [b.localizacao.nome]))
    const porStatus = computed(() => agrupar((b) => [rotulo(STATUS_BEM, b.status)]))
    const porEstado = computed(() => agrupar((b) => [rotulo(ESTADOS, b.estadoConservacao)]))

    const semDados = computed(() => !carregando.value && bens.value.length === 0)

    function paraGrafico(grupos, campo = 'bens') {
        return {
            labels: grupos.map((g) => g.nome),
            datasets: [{ data: grupos.map((g) => g[campo]) }],
        }
    }

    return {
        bens, carregando, filtros, incluirInativos, carregar, limparFiltros,
        resumo, porCategoria, porLocalizacao, porStatus, porEstado, semDados, paraGrafico,
    }
}