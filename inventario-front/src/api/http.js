import axios from 'axios'

const http = axios.create({
    baseURL: 'http://localhost:8080/api',
    withCredentials: true,
})

let aoNaoAutorizado = () => { }
export function setUnauthorizedHandler(fn) {
    aoNaoAutorizado = fn
}

http.interceptors.response.use(
    (resposta) => resposta,
    (erro) => {
        const url = erro.config?.url ?? ''
        if (erro.response?.status === 401 && !url.includes('/auth/')) {
            aoNaoAutorizado()
        }
        return Promise.reject(erro)
    },
)

export default http