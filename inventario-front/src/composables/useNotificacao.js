import { reactive } from "vue";

const estado = reactive({aberto: false, texto: '', cor: 'success'})

export function useNotificacao() {
    function avisar(texto, cor = 'success') {
        Object.assign(estado, { aberto: true, texto, cor})
    }
    return { estado, avisar }
}