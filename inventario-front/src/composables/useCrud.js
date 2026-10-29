import { onMounted, reactive, ref, watch } from 'vue'
import { errosDeCampos, mensagemDeErro } from '../utils/erros'
import { useNotificacao } from './useNotificacao'

export function useCrud({
    api,
    vazio,
    rotulo = 'Registro',
    campoBusca = 'nome',
    filtros = null,
    paraForm = null,
    paraPayload = null,
}) {
    const { avisar } = useNotificacao()
    const itens = ref([])
    const carregando = ref(false)
    const busca = ref('')

    function parametros() {
        const params = { [campoBusca]: busca.value || undefined }
        if (filtros) {
            Object.entries(filtros).forEach(([chave, valor]) => {
                params[chave] = valor === '' || valor === null ? undefined : valor
            })
        }
        return params
    }

    async function carregar() {
        carregando.value = true
        try {
            itens.value = await api.listar(parametros())
        } catch (e) {
            avisar(mensagemDeErro(e), 'error')
        } finally {
            carregando.value = false
        }
    }


    let temporizador
    watch(busca, () => {
        clearTimeout(temporizador)
        temporizador = setTimeout(carregar, 300)
    })
    if (filtros) watch(filtros, carregar, { deep: true })

    onMounted(carregar)


    const dialogForm = ref(false)
    const salvando = ref(false)
    const editandoId = ref(null)
    const form = reactive(structuredClone(vazio))
    const errosCampos = ref({})
    const erroGeral = ref('')

    function limparErros() {
        errosCampos.value = {}
        erroGeral.value = ''
    }

    function abrirNovo() {
        editandoId.value = null
        Object.assign(form, structuredClone(vazio))
        limparErros()
        dialogForm.value = true
    }

    function abrirEdicao(item) {
        editandoId.value = item.id
        if (paraForm) {
            Object.assign(form, paraForm(item))
        } else {
            Object.keys(vazio).forEach((chave) => {
                form[chave] = item[chave] ?? vazio[chave]
            })
        }
        limparErros()
        dialogForm.value = true
    }

    async function salvar() {
        salvando.value = true
        limparErros()
        try {
            const corpo = paraPayload ? paraPayload(form, editandoId.value) : { ...form }
            if (editandoId.value) {
                await api.atualizar(editandoId.value, corpo)
            } else {
                await api.criar(corpo)
            }
            dialogForm.value = false
            avisar(`${rotulo} salvo(a) com sucesso`)
            await carregar()
        } catch (e) {
            const campos = errosDeCampos(e)
            if (Object.keys(campos).length > 0) {
                errosCampos.value = campos
            } else {
                erroGeral.value = mensagemDeErro(e)
            }
        } finally {
            salvando.value = false
        }
    }

    const dialogExcluir = ref(false)
    const alvoExclusao = ref(null)
    const excluindo = ref(false)

    function pedirExclusao(item) {
        alvoExclusao.value = item
        dialogExcluir.value = true
    }

    async function confirmarExclusao() {
        excluindo.value = true
        try {
            await api.excluir(alvoExclusao.value.id)
            dialogExcluir.value = false
            avisar(`${rotulo} excluído(a)`)
            await carregar()
        } catch (e) {
            dialogExcluir.value = false
            avisar(mensagemDeErro(e), 'error')
        } finally {
            excluindo.value = false
        }
    }

    return {
        itens, carregando, busca, carregar,
        dialogForm, salvando, editandoId, form, errosCampos, erroGeral, abrirNovo, abrirEdicao, salvar,
        dialogExcluir, alvoExclusao, excluindo, pedirExclusao, confirmarExclusao,
    }
}