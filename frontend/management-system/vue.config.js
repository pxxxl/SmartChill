const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,

  devServer: {
    client: {
      overlay: false,
    },
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:18080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '',
        },
      },
    },
  },
})
