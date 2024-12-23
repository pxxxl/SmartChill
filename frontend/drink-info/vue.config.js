const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  
	devServer: {
    client: {
      overlay: false
    },
    proxy: {
      '/api': {
        target: 'http://2ce8d2f4.r7.cpolar.top',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        },
      }
    }
  }
})
