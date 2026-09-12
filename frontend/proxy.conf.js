/**
 * Proxy di sviluppo per Angular CLI (ng serve).
 *
 * Inoltra le chiamate /api al backend su localhost:8083.
 * Rimuove l'header "Origin" in arrivo dal browser: il backend vede una
 * richiesta server-to-server (niente controlli CORS) ed evita l'errore
 * "Invalid CORS request" quando il frontend è aperto da un'altra origine
 * (es. http://85.235.148.177:4800 oppure l'IP LAN del server).
 *
 * Nota: usiamo l'opzione `configure` di Vite (non `onProxyReq` come
 * opzione diretta, che la versione di http-proxy bundlata ignora) per
 * agganciare l'evento 'proxyReq' sull'istanza del proxy.
 */
module.exports = {
  '/api': {
    target: 'http://localhost:8083',
    secure: false,
    changeOrigin: true,
    configure(proxy) {
      proxy.on('proxyReq', (proxyReq) => {
        proxyReq.removeHeader('origin');
      });
    }
  }
};