const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  
  devServer: {
    client: {
      overlay: false
    },
    proxy: {
      '/api': {
        target: 'http://5bd7091c.r7.cpolar.top',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        },
      }
      
    }
  }
})
